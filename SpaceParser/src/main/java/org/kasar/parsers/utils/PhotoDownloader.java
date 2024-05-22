package org.kasar.parsers.utils;

import org.apache.commons.io.FileUtils;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class PhotoDownloader {
    private Path lastPath;

    public String download(String dir, long id, String extension, String site, int attempts) throws IOException {
        try {
            return download0(dir, id, extension, site);
        } catch (IOException e) {
            if (attempts <= 0) throw e;
            else return download(dir, id, extension, site, attempts - 1);
        }
    }

    private String download0(String dir, long id, String extension, String site) throws IOException {
        URL url = new URL(site);
        Path path = Path.of(new DBInfo().getPathToTelescopeImages())
                .resolve(Path.of(dir)).resolve(Path.of(id + "." + extension));
        lastPath = path;
        FileUtils.copyURLToFile(url, path.toFile(), 0, 0);
        return path.toString();
    }

    public void deleteLast() throws IOException {
        Files.delete(lastPath);
    }
}
