package org.kasar.parsers.parsers;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.kasar.parsers.dao.PhotoDAO;
import org.kasar.parsers.dao.PhotoDAOImpl;
import org.kasar.parsers.utils.DBInfo;
import org.kasar.parsers.utils.PhotoDownloader;
import org.kasar.parsers.utils.TelescopeSitesConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TelescopeParserPhotoes {
    private static Logger logger = LoggerFactory.getLogger(TelescopeParserPhotoes.class);
    public static int numberOfPhotos = 4500;
    public static String site = "https://hubblesite.org";
    public static String filterUUID = "5a370ecc-f605-44dd-8096-125e4e623945";

    public static void parseHubblePhotos() throws IOException, SQLException {
        logger.info("Parsing  photoes"); //!!!
        DBInfo dbInfo = new DBInfo();
        PhotoDAO dao = new PhotoDAOImpl();
        PhotoDownloader downloader = new PhotoDownloader();
        List<String> list = dao.getNotParsed();
        //List<String> list = getLinksToHubblePhotoes();
        for (String link : list) {
            try {
                parsePhotoAndSave(link, dao, downloader, dbInfo);
                dao.deleteNotParsed(link);
            } catch (PhotoLinkParseException | IOException e) {
                //dao.deleteLast();
                //downloader.deleteLast();
                dao.deleteNotParsed(link);
                String error = "Couldn't parse photo with link '" + link + "': " + e.getMessage();
                dao.putNotParsed(link, error);
                logger.error(error);
            }
        }
    }

    public static void parsePhotoAndSave(String link, PhotoDAO dao, PhotoDownloader photoDownloader, DBInfo dbInfo) throws IOException, SQLException, PhotoLinkParseException {
        logger.debug("Parsing photo from link: " + link);
        String name;
        String caption;
        String linkToPhoto = null;
        String extension;
        List<String> keywords = new ArrayList<>();

        logger.debug("Making query");
        Document doc = TelescopeSitesConnector.getPage(link, null, -1, -1, 0, 4);

        logger.debug("Parsing name");
        Elements nameElements = doc.select("h1#page-title.interior-page-title");
        name = nameElements.first().text();

        logger.debug("Parsing caption");
        StringBuilder captionBuilder = new StringBuilder();
        Elements captionElements = doc.select("h3, p");
        boolean isFoundCaption = false;
        for (Element el : captionElements) {
            if (isFoundCaption) {
                if (el.is("p")) {
                    captionBuilder.append(el.text());
                    captionBuilder.append("\n\n");
                } else break;
            }
            if (el.is("h3") && el.text().equals("Caption"))
                isFoundCaption = true;
        }
        caption = captionBuilder.toString().trim();

        logger.debug("Parsing link to photo");
        Elements ulElements = doc.select("ul");
        ulElements.removeIf(el -> !(el.hasAttr("role") && el.attributes().get("role").equals("list")));
        Element ulElement = ulElements.first();
        Elements linkElements = ulElement.children();
        List<Element> aElements = new ArrayList<>();
        for (Element el : linkElements) {
            aElements.add(el.children().select("a").first());
        }
        List<String> links = new ArrayList<>();
        for (Element el : aElements) {
            links.add(el.text());
        }
        Item item = getBestPicture(links, dbInfo);
        for (Element el : aElements) {
            if (el.text().equals(item.name))
                linkToPhoto = "https:" + el.attributes().get("href").split("\\?")[0];
        }
        extension = item.format.toLowerCase();

        logger.debug("Parsing keywords");
        Elements keywordsElements = doc.select("a.keyword-tag");
        for (Element el : keywordsElements) {
            keywords.add(el.text());
        }

        /*
        System.out.println(name);
        System.out.println(caption);
        System.out.println(linkToPhoto);
        System.out.println(keywords);
         */


        logger.debug("Saving photo to DB");
        long id = dao.put(name, caption, link, keywords);
        try {
            logger.debug("Saving photo to files");
            String path = photoDownloader.download("photoes", id, extension, linkToPhoto, 4);
            logger.debug("Photo saved to: " + path);
        } catch (IOException e) {
            dao.deleteLast();
            throw e;
        }
    }

    public static List<String> getLinksToHubblePhotoes() throws IOException {
        logger.info("Parsing links");
        final int itemsPerPage = 500;
        final int numberOfQueries = numberOfPhotos / itemsPerPage + (numberOfPhotos % itemsPerPage == 0 ? 0 : 1);
        final String pathToPage = site + "/images"; //!!!
        List<String> res = new ArrayList<>(numberOfPhotos);
        for (int i = 1; i <= numberOfQueries; i++) {
            logger.debug("Making request: " + i);
            long time = System.currentTimeMillis();
            Document doc = TelescopeSitesConnector.getPage(pathToPage, filterUUID, i, itemsPerPage, 0, 4);
            logger.debug("The request was gotten in " + (System.currentTimeMillis() - time) + " milliseconds");
            logger.debug("Parsing links: " + i);
            Elements elements = doc.select("a.card-link");
            for (Element element : elements) {
                res.add(site + element.attr("href")); //!!!
                //System.out.println(site + element.attr("href"));
            }
            logger.debug("Links were parsed: " + i);
        }
        return res;
    }

    public static Item getBestPicture(List<String> pictures, DBInfo dbInfo) throws PhotoLinkParseException {
        if (pictures == null) throw new IllegalArgumentException("Pictures can't be null");
        if (pictures.isEmpty()) throw new IllegalArgumentException("Pictures can't be empty");
        //Pattern pattern0 = Pattern.compile("((\\w)|( )|(.))*");
        Pattern pattern = Pattern.compile("(.* )?(\\d+) X (\\d+), ([A-Z]{3}).*\\((((\\w)|( )|(.))*)\\).*");
        List<Item> items = new ArrayList<>();
        List<Exception> exceptions = new ArrayList<>();
        for (int i = 0; i < pictures.size(); i++) {
            String s = pictures.get(i);
            try {
                Matcher matcher = pattern.matcher(s);
                matcher.matches();
                int height = Integer.parseInt(matcher.group(2));
                int width = Integer.parseInt(matcher.group(3));
                String format = matcher.group(4);
                String size = matcher.group(5);
                if (size.contains("GB"))
                    continue;
                if (size.contains("MB") && Double.parseDouble(size.replace(" MB", "")) > dbInfo.getMaxPhotoSizeInMB())
                    continue;
                items.add(new Item(i + 1, height, width, format, s));
            } catch (IllegalStateException e) {
                exceptions.add(e);
            }
        }
        if (items.size() == 0) {
            StringBuilder s = new StringBuilder();
            for (Exception current : exceptions) {
                s.append(current.getMessage()).append(" ");
            }
            throw new PhotoLinkParseException("The list of items is empty: " + s);
        }
        try {
            final String format1 = "JPG";
            if (containsFormat(items, format1))
                items.removeIf(item -> !item.format.equals(format1));
            else {
                final String format2 = "PNG";
                if (containsFormat(items, format2))
                    items.removeIf(item -> !item.format.equals(format2));
            }
            if (exceptions.size() != 0) {
                StringBuilder s = new StringBuilder();
                for (Exception current : exceptions) {
                    s.append(current.getMessage()).append(" ");
                }
                logger.warn("Problems with current photo: " + s);
            }
            return Collections.max(items, Comparator.comparingInt(item1 -> item1.height * item1.width));
        } catch (Exception e) {
            StringBuilder s = new StringBuilder();
            for (Exception current : exceptions) {
                s.append(current.getMessage()).append(" ");
            }
            throw new PhotoLinkParseException("Couldn't parse photo: " + e.getMessage() + ". Causes: " + s);
        }
    }

    public static class PhotoLinkParseException extends Exception {
        public PhotoLinkParseException(String message) {
            super(message);
        }
        //public PhotoLinkParseException(String message, Exception cause) {super(message, cause);}
    }

    private static boolean containsFormat(List<Item> list, String format) {
        for (Item item : list) {
            if (item.format.equals(format))
                return true;
        }
        return false;
    }

    private static class Item {
        public Item(int index, int height, int width, String format, String name) {
            this.index = index;
            this.height = height;
            this.width = width;
            this.format = format;
            this.name = name;
        }
        int index;
        int height;
        int width;
        String format;
        String name;
    }
}
