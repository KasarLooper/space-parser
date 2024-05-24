package org.kasar.parsers.dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface PhotoDAO {
    long put(String name, String caption, String link, List<String> keywords) throws SQLException, IOException;
    void putNotParsed(String link, String reason) throws SQLException, IOException;
    void deleteNotParsed(String link) throws SQLException, IOException;
    long getIdWithName(String name) throws SQLException, IOException;
    long getIdWithLink(String link) throws SQLException, IOException;
    List<Long> getIdWithKeyword(List<String> keywords) throws SQLException, IOException;
    String getName(long id) throws SQLException, IOException;
    String getCaption(long id) throws SQLException, IOException;
    String getLink(long id) throws SQLException, IOException;
    List<String> getKeywords(long id) throws SQLException, IOException;
    List<String> getNotParsed() throws SQLException, IOException;
    String getReason(String link) throws SQLException, IOException;
    void deleteLast() throws SQLException, IOException;
}
