package org.kasar.parsers.parsers;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.kasar.parsers.dao.NewsDAO;
import org.kasar.parsers.dao.NewsDAOImpl;
import org.kasar.parsers.dao.PhotoDAO;
import org.kasar.parsers.dao.PhotoDAOImpl;
import org.kasar.parsers.utils.DBInfo;
import org.kasar.parsers.utils.TelescopeSitesConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class TelescopeParserNews {
    private static Logger logger = LoggerFactory.getLogger(TelescopeParserNews.class);
    public static String site = "https://hubblesite.org";
    public static String filterUUID = "2771e7af-5f1f-4c2d-b4d7-faf8bcb16673";
    public static int numberOfNews = 1326;

    public static void parseNews() throws Exception {
        logger.info("Parsing news");
        PhotoDAO photoDAO = new PhotoDAOImpl();
        NewsDAO newsDAO = new NewsDAOImpl();
        DBInfo dbInfo = new DBInfo();
        List<String> links = newsDAO.getNotParsed();
        for (String link : links) {
            try {
                parseNewsAndSave(link, photoDAO, newsDAO, dbInfo);
                newsDAO.deleteNotParsed(link);
            } catch (Exception e) {
                logger.error("Couldn't parse news with link: " + link + " - " + e.getMessage());
                newsDAO.deleteNotParsed(link);
                newsDAO.putNotParsed(link, e.getMessage());
            }
        }
    }

    public static List<String> parseLinksToNews() throws Exception {
        logger.info("Parsing links to news");
        final int itemsPerPage = 100;
        final int numberOfQueries = numberOfNews / itemsPerPage + (numberOfNews % itemsPerPage == 0 ? 0 : 1);
        final String pathToPage = site + "/news/news-releases";
        List<String> res = new ArrayList<>(numberOfNews);
        for (int page = 1; page <= numberOfQueries; page++) {
            logger.info("Making request: " + page);
            Document doc = TelescopeSitesConnector.getPage(pathToPage, filterUUID, page, itemsPerPage, 0, 4);
            Elements links = doc.select("a.card-link");
            for (Element el : links) {
                res.add(site + el.attr("href"));
            }
        }
        return res;
    }

    public static void parseNewsAndSave(String link, PhotoDAO photoDAO, NewsDAO newsDAO, DBInfo info) throws Exception {
        logger.info("Parsing news with link: " + link);
        logger.info("Making query");
        Document doc = TelescopeSitesConnector.getPage(link, null, -1, -1, 30_000, 4);

        logger.info("Parsing name");
        String name;
        Elements nameElements = doc.select("h1#page-title.interior-page-title");
        name = nameElements.first().text();

        logger.info("Parsing date");
        Date date;
        Elements dateElements = doc.select("span.news-release-date");
        DateFormat dateFormat = new SimpleDateFormat("MMMMdd,yyyy", Locale.CANADA);
        String[] dateTokens = dateElements.text().split(" ");
        date = dateFormat.parse(dateTokens[0] + dateTokens[1] + dateTokens[2]);

        logger.info("Parsing summary");
        StringBuilder summaryBuilder = new StringBuilder();
        Elements summaryElements = doc.select("div.page-intro__main").first().children();
        for (Element el : summaryElements)
            summaryBuilder.append(el.text()).append("\n");
        String summary = summaryBuilder.toString().trim();

        Elements keywordsElements = null;
        logger.info("Parsing caption");
        String caption = null;
        try {
            StringBuilder captionBuilder = new StringBuilder();
            Elements captionElements = doc.select("div.custom-columns").first().children();
            captionElements.removeIf(el -> el.hasAttr("class"));
            captionElements = captionElements.first().children();
            Iterator<Element> captionIterator = captionElements.iterator();
            while (captionIterator.hasNext()) {
                Element el = captionIterator.next();
                if (el.hasAttr("class") && el.attr("class").equals("news-footer")) {
                    keywordsElements = el.children().select("ul.inline").first().children();
                    captionIterator.remove();
                }
            }
            captionElements.removeIf(el -> el.hasAttr("class"));
            captionElements = captionElements.select("p");
            for (Element el : captionElements)
                captionBuilder.append(el.text()).append("\n");
            caption = captionBuilder.toString().trim();
        } catch (RuntimeException e) {
            logger.warn("No caption: " + e.getMessage());
        }

        logger.info("Parsing keywords");
        List<String> keywords = new ArrayList<>(keywordsElements.size());
        for (Element element : keywordsElements)
            keywords.add(element.children().first().text());

        logger.info("Parsing images");
        List<String> links = new ArrayList<>();
        boolean hasPhotos = true;
        try {
            Elements imageElements = doc.select("ul.custom-columns").first().children();
            for (Element el : imageElements)
                links.add(site + el.children().first().children().select("p").first().children()
                        .first().attr("href").split("\\?")[0]);
        } catch (RuntimeException e) {
            logger.warn("No images: " + e.getMessage());
            hasPhotos = false;
        }

        /*
        System.out.println(name);
        System.out.println(date);
        System.out.println(summary);
        System.out.println(caption);
        System.out.println(keywords);
        System.out.println(links);
         */

        logger.info("Searching photos");
        List<Long> ids = new ArrayList<>(links.size());
        for (String s : links) {
            try {
                ids.add(photoDAO.getIdWithLink(s + "%"));
            } catch (SQLException e) {
                logger.warn("Problems with photo with link: " + link + " - " + e.getMessage());
            }
        }
        if (hasPhotos && ids.isEmpty())
            throw new Exception("No photos found");

        if (caption == null) {
            caption = "";
        }

        newsDAO.put(name, summary, caption, link, date, keywords, ids);
    }
}
