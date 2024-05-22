package org.kasar.parsers.tests;

import org.kasar.parsers.Main;
import org.kasar.parsers.dao.NewsDAO;
import org.kasar.parsers.dao.NewsDAOImpl;
import org.kasar.parsers.dao.PhotoDAO;
import org.kasar.parsers.dao.PhotoDAOImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class HubbleTests {
    /*
    private static Logger logger = LoggerFactory.getLogger(Main.class);
    private static final String BIG_STRING = "This is ".repeat(10_000);
    private static final int NUMBER_OF_KEYWORDS = 5;
    private static final List<String> ALL_KEYWORDS = new ArrayList<>(NUMBER_OF_KEYWORDS);
    private static final Random RANDOM = new Random();

    static {
        for (int i = 0; i < NUMBER_OF_KEYWORDS; i++) {
            ALL_KEYWORDS.add("keyword" + i);
        }
    }

    public static void hubbleNewsTest3() throws SQLException, IOException {
        NewsDAO dao = new NewsDAOImpl();
        List<String> keywords = new ArrayList<>();
        keywords.add("keyword0");
        keywords.add("keyword1");
        keywords.add("keyword2");
        System.out.println(dao.getIdWithName("news0"));
        System.out.println(dao.getIdWithLink("example.org/news/news1"));
        System.out.println(dao.getIdWithKeyword(keywords));
        long id = 5;
        System.out.println(dao.getName(id));
        System.out.println(dao.getSummary(id));
        System.out.println(dao.getCaption(id));
        System.out.println(dao.getDate(id));
        System.out.println(dao.getLink(id));
        System.out.println(dao.getKeywords(id));
        System.out.println(dao.getPhotoes(id));
    }

    public static void hubbleNewsTest2() throws SQLException, IOException {
        NewsDAO dao = new NewsDAOImpl();
        for (int i = 0; i < 5; i++) {
            String reason = null;
            if (i % 2 == 0) reason = "Reason is: " + i;
            dao.putNotParsed("example.org/news/notParsed" + i, reason);
            logger.info("Putting news" + i + " was successful");
        }
        System.out.println(dao.getNotParsed());
        for (int i = 0; i < 5; i++) {
            String link = "example.org/news/notParsed" + i;
            System.out.println(dao.getReason(link));
        }
        for (int i = 0; i < 5; i++) {
            dao.deleteNotParsed("example.org/news/notParsed" + i);
            logger.info("Deleting news" + i + " was successful");
        }
        System.out.println(dao.getNotParsed());
    }

    public static void hubbleNewsTest1() {
        NewsDAO newsDAO = new NewsDAOImpl();
        for (int i = 0; i < 3; i++) {
            int n = RANDOM.nextInt(1, NUMBER_OF_KEYWORDS);
            String name = "news" + i;
            String summary = "This is about " + name;
            String caption = BIG_STRING + name;
            String link = "example.org/news/" + name;
            Date date = new Date();
            List<String> keywords = new ArrayList<>(n);
            List<Long> photoes = new ArrayList<>(3);
            for (long j = 0; j < 3; j++) {
                photoes.add(1 + i * 3L + j);
            }
            for (int j = 0; j < n; j++) {
                keywords.add(ALL_KEYWORDS.get(j));
            }
            try {
                logger.info("Writing to database " + name + " with keywords " + n);
                newsDAO.put(name, summary, caption, link, date, keywords, photoes);
                logger.info(name + " was put successfully");
            } catch (SQLException | IOException e) {
                logger.error("Couldn't write to database " + name + ": " + e.getMessage());
            }
        }
    }

    public static void hubblePhotoesTest3() throws SQLException, IOException {
        PhotoDAO dao = new PhotoDAOImpl();
        List<String> keywords = new ArrayList<>();
        keywords.add("keyword0");
        keywords.add("keyword1");
        System.out.println(dao.getIdWithName("photo0"));
        System.out.println(dao.getIdWithLink("example.org/photoes/photo1"));
        System.out.println(dao.getIdWithKeyword(keywords));
        long id = 1;
        System.out.println(dao.getName(id));
        System.out.println(dao.getCaption(id));
        System.out.println(dao.getLink(id));
        System.out.println(dao.getKeywords(id));
    }

    public static void hubblePhotoesTest2() throws SQLException, IOException {
        PhotoDAO dao = new PhotoDAOImpl();
        for (int i = 0; i < 5; i++) {
            String reason = null;
            if (i % 2 == 0) reason = "Reason is: " + i;
            dao.putNotParsed("example.org/photoes/notParsed" + i, reason);
            logger.info("Putting photo" + i + " was successful");
        }
        System.out.println(dao.getNotParsed());for (int i = 0; i < 5; i++) {
            String link = "example.org/photoes/notParsed" + i;
            System.out.println(dao.getReason(link));
        }

        for (int i = 0; i < 5; i++) {
            dao.deleteNotParsed("example.org/photoes/notParsed" + i);
            logger.info("Deleting photo" + i + " was successful");
        }
        System.out.println(dao.getNotParsed());
    }

    public static void hubblePhotoesTest1() {
        PhotoDAO photoDAO = new PhotoDAOImpl();
        for (int i = 0; i < 3; i++) {
            int n = RANDOM.nextInt(1, NUMBER_OF_KEYWORDS);
            String name = "photo" + i;
            String caption = BIG_STRING + name;
            String link = "example.org/photoes/" + name;
            List<String> keywords = new ArrayList<>(n);
            for (int j = 0; j < n; j++) {
                keywords.add(ALL_KEYWORDS.get(j));
            }
            try {
                logger.info("Writing to database " + name + " with keywords " + n);
                photoDAO.put(name, caption, link, keywords);
                logger.info(name + " was put successfully");
            } catch (SQLException | IOException e) {
                logger.error("Couldn't write to database " + name + ": " + e.getMessage());
            }
        }
    }
     */
}
