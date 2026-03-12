package com.zebrunner.automation.legacy;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Deprecated
public class UrlUtils {

    public static String extractValueBetweenSegments(String url, String startSegment, String endSegment) {
        try {
            URI uri = new URI(url);
            String path = uri.getPath(); // Gets the path part of the URL

            String patternString = "/" + startSegment + "/([^/?]+)/" + endSegment;
            Pattern pattern = Pattern.compile(patternString);
            Matcher matcher = pattern.matcher(path);

            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null; // Value not found or invalid URL
    }

    public static String extractValueAfterSegment(String url, String segment) {
        try {
            URI uri = new URI(url);
            String path = uri.getPath(); // Gets the path part of the URL

            String patternString = "/" + segment + "/([^/?]+)";
            Pattern pattern = Pattern.compile(patternString);
            Matcher matcher = pattern.matcher(path);

            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null; // Value not found or invalid URL
    }

}

