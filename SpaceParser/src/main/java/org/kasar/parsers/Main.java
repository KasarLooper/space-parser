package org.kasar.parsers;

import org.kasar.parsers.dao.NewsDAO;
import org.kasar.parsers.dao.NewsDAOImpl;
import org.kasar.parsers.dao.PhotoDAO;
import org.kasar.parsers.dao.PhotoDAOImpl;
import org.kasar.parsers.parsers.TelescopeParserNews;
import org.kasar.parsers.parsers.TelescopeParserPhotoes;
import org.kasar.parsers.utils.DBInfo;
import org.kasar.parsers.utils.PhotoDownloader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        initJamesWebb();

        PhotoDAO photoDAO = new PhotoDAOImpl();
        List<String> links0 = TelescopeParserPhotoes.getLinksToHubblePhotoes();
        for (String link : links0)
            photoDAO.putNotParsed(link, "This photo hasn't been parsed yet");

        TelescopeParserPhotoes.parseHubblePhotos();

        NewsDAO newsDAO = new NewsDAOImpl();
        List<String> links1 = TelescopeParserNews.parseLinksToNews();
        for (String link : links1)
            newsDAO.putNotParsed(link, "This news hasn't been parsed yet");

        TelescopeParserNews.parseNews();


        //TelescopeParserPhotoes.getLinksToHubblePhotoes();

        //List<String> links = TelescopeParserNews.parseLinksToNews();

        //TelescopeParserNews.parseNewsAndSave("https://hubblesite.org/contents/news-releases/2024/news-2024-004?itemsPerPage=100&page=1&filterUUID=2771e7af-5f1f-4c2d-b4d7-faf8bcb16673",
        //        new PhotoDAOImpl(), new NewsDAOImpl(), new DBInfo());

        //TelescopeParserPhotoes.parseHubblePhotoes();
        //TelescopeParser.parsePhotoAndSave("https://hubblesite.org/contents/media/images/2023/023/01HGXC3Q4K3ZSWZ0SW19R7NR1N", new PhotoDAOImpl(), new PhotoDownloader(), new DBInfo());
    }

    public static void initJamesWebb() {
        TelescopeParserPhotoes.numberOfPhotos = 481;
        TelescopeParserPhotoes.site = "https://webbtelescope.org";
        TelescopeParserPhotoes.filterUUID = "91dfa083-c258-4f9f-bef1-8f40c26f4c97";
        TelescopeParserNews.numberOfNews = 130;
        TelescopeParserNews.site = "https://webbtelescope.org";
        TelescopeParserNews.filterUUID = "d252bcd2-d0eb-4fae-83f4-e58d64e1e282";
    }
}