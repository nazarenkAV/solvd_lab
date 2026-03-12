package com.zebrunner.automation.legacy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zebrunner.automation.api.iam.domain.User;

@Deprecated
public class StringUtil {

    public static String randomChangeLetterCase(String string) {
        return string.chars()
                .mapToObj(c -> Math.random() < 0.5 ? Character.toUpperCase((char) c) : (char) c)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    public static String getByPattern(String s, String pattern) {
        Pattern regexPattern = Pattern.compile(pattern);
        Matcher matcher = regexPattern.matcher(s);
        String lastNumberString = null;
        while (matcher.find()) {
            lastNumberString = matcher.group(1);
        }
        if (lastNumberString != null) {
            return lastNumberString;
        } else {
            throw new RuntimeException("Pattern not found in the input string.");
        }
    }

    public static String getExpectedAuthor(User user) {
        String username = user.getUsername();
        String fName = user.getFirstName();
        String lName = user.getLastName();

        if ((fName != null && !fName.isEmpty()) && (lName != null && !lName.isEmpty())) {
            return fName + " " + lName;
        } else if (lName != null && !lName.isEmpty()) {
            return lName;
        } else if (fName != null && !fName.isEmpty()) {
            return fName;
        } else {
            return username;
        }
    }

    public static String replaceSpaceWithHyphen(String text) {
        String[] words = text.split(" ");

        if (words.length > 1) {
            return String.join("_", words);
        } else {
            return text;
        }
    }

    public static int convertToSeconds(String time) {
        int totalSeconds = 0;

        String[] parts = time.split("\\s+");
        for (String part : parts) {
            if (part.endsWith("s")) {
                int seconds = Integer.parseInt(part.substring(0, part.length() - 1));
                totalSeconds += seconds;
            } else if (part.endsWith("m")) {
                int minutes = Integer.parseInt(part.substring(0, part.length() - 1));
                totalSeconds += minutes * 60;
            }
        }

        return totalSeconds;
    }

    public static String extractToken(String url) {
        String regex = "token=(\\w+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    public static String getTemporaryUsernameFromEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex != -1) { // Check if '@' exists in the email
            return email.substring(0, atIndex);
        } else {
            // Handle cases where '@' is not present in the email
            throw new RuntimeException("Email is not valid !");
        }
    }

    public static String trimProjectKey(String projectKey) {
        if (projectKey.length() > 4) {
            projectKey = projectKey.substring(0, 4);
        }
        return projectKey;
    }
}