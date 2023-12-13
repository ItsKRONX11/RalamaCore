package net.ralama.file;

import lombok.Getter;
import net.lingala.zip4j.ZipFile;

import java.io.File;
import java.io.IOException;

public class FileManager {
    @Getter
    private final File dataFolder;

    public FileManager() {
        this.dataFolder = new File(System.getProperty("user.dir"));

        if (dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        File serversFolder = get("servers");
        if (!serversFolder.exists()) {
            serversFolder.mkdirs();
        }

        File templatesFolder = get("templates");
        if (!templatesFolder.exists()) {
            templatesFolder.mkdirs();
        }

        File logsFolder = get("logs");
        if (!logsFolder.exists()) {
            logsFolder.mkdirs();
        }
    }

    public File get(String path) {
        return new File(dataFolder, path);
    }

    public void unzip(String zipFilePath, String targetPath) {

        try (ZipFile zipFile = new ZipFile(zipFilePath)) {
            zipFile.extractAll(targetPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
