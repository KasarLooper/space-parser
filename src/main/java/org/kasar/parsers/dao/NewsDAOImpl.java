package org.kasar.parsers.dao;

import org.kasar.parsers.utils.JDBCConnector;
import java.util.Date;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NewsDAOImpl implements NewsDAO {
    private long lastId;

    @Override
    public long put(String name, String summary, String caption, String link, Date date, List<String> keywords, List<Long> photoes) throws SQLException, IOException {
        if (name == null) throw new IllegalArgumentException("Name can't be null");
        if (summary == null) throw new IllegalArgumentException("Caption can't be null");
        if (caption == null) throw new IllegalArgumentException("Caption can't be null");
        if (link == null) throw new IllegalArgumentException("Link can't be null");
        if (date == null) throw new IllegalArgumentException("Date can't be null");
        if (keywords == null) throw new IllegalArgumentException("Keywords can't be null");
        if (keywords.isEmpty()) throw new IllegalArgumentException("Keywords can't be empty");
        if (photoes == null) throw new IllegalArgumentException("Photoes can't be null");
        if (photoes.isEmpty()) throw new IllegalArgumentException("Photoes can' be empty");
        String query1 = "INSERT INTO hubble_news (name, summary, caption, link, date) VALUES (?, ?, ?, ?, ?)";
        Connection connection = JDBCConnector.getConnection();
        try (PreparedStatement preparedStatement1 = connection.prepareStatement(query1, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement1.setString(1, name);
            preparedStatement1.setString(2, summary);
            preparedStatement1.setString(3, caption);
            preparedStatement1.setString(4, link);
            preparedStatement1.setDate(5, new java.sql.Date(date.getTime()));
            preparedStatement1.execute();
            ResultSet resultSet = preparedStatement1.getGeneratedKeys();
            resultSet.next();
            long id = resultSet.getLong("id");
            String query2 = "INSERT INTO keywords_of_hubble_news (news_id, word) VALUES (?, ?)";
            try (PreparedStatement preparedStatement2 = connection.prepareStatement(query2)) {
                for (String keyword : keywords) {
                    preparedStatement2.setLong(1, id);
                    preparedStatement2.setString(2, keyword);
                    preparedStatement2.addBatch();
                }
                preparedStatement2.executeBatch();
            }
            String query3 = "INSERT INTO photoes_of_hubble_news (news_id, photo_id) VALUES (?, ?)";
            try (PreparedStatement preparedStatement3 = connection.prepareStatement(query3)) {
                for (long photoId : photoes) {
                    preparedStatement3.setLong(1, id);
                    preparedStatement3.setLong(2, photoId);
                    preparedStatement3.addBatch();
                }
                preparedStatement3.executeBatch();
            }
            lastId = id;
            return id;
        }
    }

    @Override
    public void putNotParsed(String link, String reason) throws SQLException, IOException {
        if (link == null) throw new IllegalArgumentException("Link can't be null");
        String query;
        if (reason == null) query = "INSERT INTO hubble_news_not_parsed (link) VALUES (?)";
        else query = "INSERT INTO hubble_news_not_parsed (link, reason) VALUES (?, ?)";
        Connection connection = JDBCConnector.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, link);
            if (reason != null) preparedStatement.setString(2, reason);
            preparedStatement.execute();
        }
    }

    public void deleteNotParsed(String link) throws SQLException, IOException {
        if (link == null) throw new IllegalArgumentException("Link can't be null");
        String query = "DELETE FROM hubble_news_not_parsed WHERE link = ?";
        Connection connection = JDBCConnector.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, link);
            preparedStatement.execute();
        }
    }

    @Override
    public long getIdWithName(String name) throws SQLException, IOException {
        if (name == null) throw new IllegalArgumentException("Name can' be null");
        String query = "SELECT id FROM hubble_news WHERE name = ?";
        Connection connection = JDBCConnector.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getLong("id");
        }
    }

    @Override
    public long getIdWithLink(String pattern) throws SQLException, IOException {
        if (pattern == null) throw new IllegalArgumentException("Link can't be null");
        String query = "SELECT id FROM hubble_news WHERE link LIKE ?";
        Connection connection = JDBCConnector.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, pattern);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getLong("id");
        }
    }

    @Override
    public long getIdWithDate(Date date) throws SQLException, IOException {
        if (date == null) throw new IllegalArgumentException("Date can't be null");
        String query = "SELECT id FROM hubble_news WHERE date = ?";
        Connection connection = JDBCConnector.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setDate(1, new java.sql.Date(date.getTime()));
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getLong("id");
        }
    }

    @Override
    public List<Long> getIdWithKeyword(List<String> keywords) throws SQLException, IOException {
        if (keywords == null) throw new IllegalArgumentException("Keywords can't be null");
        if (keywords.isEmpty()) throw new IllegalArgumentException("Keywords can't be empty");
        Connection connection = JDBCConnector.getConnection();
        keywords.replaceAll(s -> "'" + s + "'");
        String keywordsString = String.join(",", keywords);
        String query = "SELECT news_id FROM (SELECT * FROM keywords_of_hubble_news WHERE word IN (?)) needed_news GROUP BY news_id ORDER BY COUNT(word) DESC";
        query = query.replace("?", keywordsString);
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Long> res = new ArrayList<>();
            while (resultSet.next()) {
                res.add(resultSet.getLong("news_id"));
            }
            return res;
        }
    }

    @Override
    public String getName(long id) throws SQLException, IOException {
        Connection connection = JDBCConnector.getConnection();
        String query = "SELECT name FROM hubble_news WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getString("name");
        }
    }

    @Override
    public String getSummary(long id) throws SQLException, IOException {
        Connection connection = JDBCConnector.getConnection();
        String query = "SELECT summary FROM hubble_news WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getString("summary");
        }
    }

    @Override
    public String getCaption(long id) throws SQLException, IOException {
        Connection connection = JDBCConnector.getConnection();
        String query = "SELECT caption FROM hubble_news WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getString("caption");
        }
    }

    @Override
    public String getLink(long id) throws SQLException, IOException {
        Connection connection = JDBCConnector.getConnection();
        String query = "SELECT link FROM hubble_news WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getString("link");
        }
    }

    @Override
    public Date getDate(long id) throws SQLException, IOException {
        Connection connection = JDBCConnector.getConnection();
        String query = "SELECT date FROM hubble_news WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return new Date(resultSet.getDate("date").getTime());
        }
    }

    @Override
    public List<String> getKeywords(long id) throws SQLException, IOException {
        List<String> res = new ArrayList<>();
        Connection connection = JDBCConnector.getConnection();
        String query = "SELECT word FROM keywords_of_hubble_news WHERE news_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                res.add(resultSet.getString("word"));
            }
            return res;
        }
    }

    @Override
    public List<Long> getPhotoes(long id) throws SQLException, IOException {
        List<Long> res = new ArrayList<>();
        Connection connection = JDBCConnector.getConnection();
        String query = "SELECT photo_id FROM photoes_of_hubble_news WHERE news_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                res.add(resultSet.getLong("photo_id"));
            }
            return res;
        }
    }

    @Override
    public List<String> getNotParsed() throws SQLException, IOException {
        List<String> res = new ArrayList<>();
        Connection connection = JDBCConnector.getConnection();
        String query = "SELECT * FROM hubble_news_not_parsed";
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                res.add(resultSet.getString("link"));
            }
            return res;
        }
    }

    @Override
    public String getReason(String link) throws SQLException, IOException {
        Connection connection = JDBCConnector.getConnection();
        String query = "SELECT reason FROM hubble_news_not_parsed WHERE link = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, link);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getString("reason");
        }
    }

    @Override
    public void deleteLast() throws SQLException, IOException {
        Connection connection = JDBCConnector.getConnection();
        String query = "DELETE FROM hubble_news WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, lastId);
            preparedStatement.execute();
        }
    }
}
