package com.zebrunner.automation.util;

import com.zebrunner.agent.core.webdriver.RemoteWebDriverFactory;
import com.zebrunner.carina.utils.R;
import com.zebrunner.carina.utils.report.ReportContext;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.support.decorators.Decorated;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
public class FileUtils {

    public static Optional<File> getFileFromZip(String zipFilePath, String fileNameToFind) {
        try (FileInputStream fis = new FileInputStream(zipFilePath);
             ZipInputStream zis = new ZipInputStream(fis)) {

            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                log.info("Find file with name " + entry.getName());

                if (entry.getName().equals(fileNameToFind)) {

                    // Create a temporary file and copy the content from the archive into it
                    File tempFile = Files.createTempFile(null, null).toFile();
                    try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = zis.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                        }
                    }
                    log.info("File {} found in the archive.", fileNameToFind);
                    return Optional.of(tempFile);
                }
            }
            log.info("File {} not found in the archive.", fileNameToFind);
            return Optional.empty();
        } catch (IOException e) {
            log.error("Error occurred while extracting file from zip: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public static List<File> findFilesByPattern(String zipFilePath, String pattern) {
        List<File> foundFiles = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(zipFilePath);
             ZipInputStream zis = new ZipInputStream(fis)) {

            Pattern filePattern = Pattern.compile(pattern);
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                log.info("Find file with name " + entry.getName());

                Matcher matcher = filePattern.matcher(entry.getName());
                if (matcher.matches()) {

                    File tempFile = Files.createTempFile(null, null).toFile();
                    try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = zis.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                        }
                    }
                    foundFiles.add(tempFile);
                }
            }
        } catch (IOException e) {
            log.error("Error occurred while searching files in zip: {}", e.getMessage());
        }
        return foundFiles;
    }

    public static Optional<String> getContentFromFile(File file) {
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            String content = new String(bytes, StandardCharsets.UTF_8);

            log.debug(content);

            return Optional.of(content);
        } catch (IOException e) {
            log.error("Error occurred while reading content from file: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public static Optional<File> getFileFromURL(String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            URLConnection connection = url.openConnection();

            String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);

            File tempFile = Files.createTempFile(fileName, "").toFile();

            try (InputStream inputStream = connection.getInputStream();
                 OutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                return Optional.of(tempFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static boolean isRemoteFileAvailable(URL url) {

        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            log.info("Remote file response code = {}", responseCode);
            return 200 <= responseCode && responseCode <= 399;
        } catch (IOException e) {
            log.info("Remote file is not available. Message: {}", e.getMessage(), e);
            return false;
        }
    }

    public static boolean isLocalFileAvailable(URL url) {
        try {
            String filePath = url.getPath();
            File file = new File(filePath);
            return file.exists() && file.length() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Optional<File> waitFile(URL url, long timeoutInSeconds) {
        log.info("Waiting file with URL  " + url);
        long endTime = System.currentTimeMillis() + timeoutInSeconds * 1000;
        long waitInterval = 1000;

        while (System.currentTimeMillis() < endTime) {
            if (isZebrunnerExecutor()) {
                if (isRemoteFileAvailable(url)) {
                    return getFileFromURL(url.toString());
                }
            } else {
                if (isLocalFileAvailable(url)) {
                    return getFileFromURL(url.toString());
                }
            }

            try {
                Thread.sleep(waitInterval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    public static URL copyFileFromURL(URL urlString, String destinationPath) {
        if (!new File(urlString.getPath()).exists()) {
            try {
                URL url = new URL(String.valueOf(urlString));
                InputStream inputStream = url.openStream();

                Path targetPath = Paths.get(destinationPath);

                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);

                inputStream.close();

                log.info("File successfully copied to   " + targetPath);
                return url;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        return urlString;
    }

    public static boolean isZebrunnerExecutor() {
        return RemoteWebDriverFactory.getSeleniumHubUrl() != null || R.CONFIG.get("selenium_url").contains("engine.zebrunner");
    }

    public static boolean isPngImage(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] header = new byte[8];
            // Read the first 8 bytes from the file
            fis.read(header);
            // PNG signature: 89 50 4E 47 0D 0A 1A 0A
            byte[] pngSignature = {(byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1A, '\n'};
            // Compare the read bytes with the PNG signature
            for (int i = 0; i < pngSignature.length; i++) {
                if (header[i] != pngSignature[i]) {
                    return false; // If any byte doesn't match, the file is not a PNG
                }
            }
            return true; // If all bytes match, the file is a PNG
        } catch (IOException e) {
            // In case of an error while reading the file, return false
            e.printStackTrace();
            return false;
        }
    }

    @SneakyThrows
    public static URL getFileUrl(WebDriver driver, String fileName) {
        log.debug("Trying to create URL for remote downloaded file = {}", fileName);

        if (isZebrunnerExecutor()) {
            WebDriver drv = driver;
            SessionId drvSessionId;

            if (drv instanceof Decorated<?>) {
                drvSessionId = ((RemoteWebDriver) (((Decorated<?>) drv).getOriginal())).getSessionId();
            } else {
                drvSessionId = ((RemoteWebDriver) drv).getSessionId();
            }

            String seleniumUrl = RemoteWebDriverFactory.getSeleniumHubUrl() != null ?
                    RemoteWebDriverFactory.getSeleniumHubUrl().toString() : R.CONFIG.get("selenium_url");

            String endpoint = String.format("%s/%s/%s", seleniumUrl.replace("wd/hub", "download"),
                    drvSessionId, fileName);

            log.debug("Created remote file URL: {}", endpoint);
            try {
                return new URL(endpoint);
            } catch (MalformedURLException e) {
                throw new UncheckedIOException(e);
            }
        } else {
            Path path = ReportContext.getBaseDirectory().resolve("downloads");
            String uriString = path.toUri() + fileName;

            return new URL(uriString);
        }
    }
}


