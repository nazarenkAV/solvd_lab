package com.zebrunner.automation.legacy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@Deprecated
public class Logs {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * @param url     full URL to the log file on the server.
     * @param matches strings to find in the log file.
     *
     * @return boolean value dependent from the file content.
     */
    public static Boolean checkLogStream(String url, String... matches) {

        List<String> patterns = Arrays.stream(matches).collect(Collectors.toList());

        try (
                ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(url).openStream());
                Scanner scanner = new Scanner(readableByteChannel)
        ) {
            LOGGER.info("Start reading file.");
            q:
            while (scanner.hasNextLine()) {
                String step = scanner.nextLine();
                LOGGER.debug("Line is    "+step);
                for (String match : matches) {
                    if (patterns.contains(match)) {
                        if (step.contains(match)) {
                            LOGGER.info("Found element: '" + match + "'");
                            patterns.remove(match);
                            if (patterns.isEmpty()) {
                                break q;
                            }
                        }
                    }
                }
            }
            LOGGER.info("Stop reading file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return patterns.isEmpty();
    }
}
