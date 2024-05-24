package org.kasar.parsers.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.print.Doc;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TelescopeSitesConnector {
    private static Logger logger = LoggerFactory.getLogger(TelescopeSitesConnector.class);

    public static Document getPage(String pathToPage, String filterUUID, int page, int itemsPerPage, int timeout, int attempts) throws IOException {
        try {
            return getPage0(pathToPage, filterUUID, page, itemsPerPage, timeout);
        } catch (IOException e) {
            if (attempts <= 0) throw e;
            else return getPage(pathToPage, filterUUID, page, itemsPerPage, timeout, attempts - 1);
        }
    }

    //Not recommended to use
    public static Document getPageFast(String pathToPage, String filterUUID, int page, int itemsPerPage, int timeout) throws IOException {
        String url = pathToPage + "?";
        if (filterUUID != null)
            url += "filterUUID=" + filterUUID + "&";
        if (page != -1)
            url += "page=" + page + "&";
        if (itemsPerPage != -1)
            url += "itemsPerPage=" + itemsPerPage + "&";
        Map<String, String> headers = getHeaders();
        return Jsoup.connect(url)
                .timeout(timeout)
                .headers(headers)
                .get();
    }

    private static Document getPage0(String pathToPage, String filterUUID, int page, int itemsPerPage, int timeout) throws IOException {
        String url = pathToPage + "?";
        if (filterUUID != null)
            url += "filterUUID=" + filterUUID + "&";
        if (page != -1)
            url += "page=" + page + "&";
        if (itemsPerPage != -1)
            url += "itemsPerPage=" + itemsPerPage + "&";
        long leftLimit = 60_000L;
        long rightLimit = 180_000L;
        long sleep = leftLimit + (long) (Math.random() * (rightLimit - leftLimit));
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException ignored) {
        }
        Map<String, String> headers = getHeaders();
        return Jsoup.connect(url)
                .timeout(timeout)
                .headers(headers)
                .get();
    }

    private static Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        // I put there my headers
        return headers;
    }
}
