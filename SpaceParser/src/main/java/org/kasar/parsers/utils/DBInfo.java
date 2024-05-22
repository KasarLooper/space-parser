package org.kasar.parsers.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class DBInfo {
    Properties db;

    public DBInfo() throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try (InputStream dbInput = classloader.getResourceAsStream("db.properties")) {
            db = new Properties();
            db.load(dbInput);
        }
    }

    public String getHost() {
        return db.getProperty("host");
    }

    public String getUser() {
        return db.getProperty("user");
    }

    public String getPassword() {
        return db.getProperty("password");
    }

    public String getDBName() {
        return db.getProperty("dbname");
    }

    public String getPathToTelescopeImages() {
        return db.getProperty("pathToTelescopeImages");
    }

    public int getMaxPhotoSizeInMB() {
        return Integer.parseInt(db.getProperty("maxPhotoSizeInMB"));
    }
}
