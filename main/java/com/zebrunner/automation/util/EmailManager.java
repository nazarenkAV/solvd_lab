package com.zebrunner.automation.util;

import org.openqa.selenium.support.ui.FluentWait;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import jakarta.mail.Authenticator;
import jakarta.mail.BodyPart;
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.search.AndTerm;
import jakarta.mail.search.RecipientTerm;
import jakarta.mail.search.SearchTerm;
import jakarta.mail.search.SubjectTerm;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import com.zebrunner.automation.config.ConfigHelper;

@Slf4j
@RequiredArgsConstructor
public class EmailManager {

    private static final String JOIN_WORKSPACE_EMAIL_SUBJECT = "Join the workspace";
    private static final String WORKSPACE_READY_EMAIL_SUBJECT = "Your Zebrunner workspace is ready";
    private static final String GITHUB_VERIFY_DEVICE_EMAIL_SUBJECT = "[GitHub] Please verify your device";

    private static final Duration DEFAULT_EMAIL_POLLING_INTERVAL = Duration.ofSeconds(5);
    private static final Duration DEFAULT_EMAIL_DELIVERY_FAILED_TIMEOUT = Duration.ofSeconds(30);

    public static final EmailManager primaryInstance = new EmailManager(
            ConfigHelper.getEmailAccountProperties().getUsername(),
            ConfigHelper.getEmailAccountProperties().getPassword()
    );

    private final String email;
    private final String password;

    static {
        Runtime.getRuntime()
               .addShutdownHook(new Thread(primaryInstance::clearInbox));
    }

    private Session getSession() {
        Properties properties = new Properties();
        PasswordAuthenticator passwordAuthenticator = new PasswordAuthenticator(email, password);

        properties.putAll(Map.of(
                "mail.user", email,
                "mail.host", "imap.gmail.com",
                "mail.store.protocol", "imaps",
                "mail.imap.socketFactory.fallback", "false"
        ));

        return Session.getInstance(properties, passwordAuthenticator);
    }

    public void waitUntilEmailDelivered(String subject) {
        new FluentWait<>(false)
                .pollingEvery(DEFAULT_EMAIL_POLLING_INTERVAL)
                .withTimeout(DEFAULT_EMAIL_DELIVERY_FAILED_TIMEOUT)
                .ignoring(RuntimeException.class)

                .until(
                        $ -> this.getOptionalEmailMessageContent(subject)
                                 .isPresent()
                );
    }

    public String pollWorkspaceReadyInvitationLink() {
        this.waitUntilEmailDelivered(WORKSPACE_READY_EMAIL_SUBJECT);

        String linkPrefix = "If the button is not clickable, use the <a href=\"";
        String emailContent = this.getEmailMessageContent(WORKSPACE_READY_EMAIL_SUBJECT);

        if (!emailContent.contains(linkPrefix)) {
            throw new RuntimeException("Could not find new workspace invitation link for email '" + email + "'. Raw html content:\n" + emailContent);
        }

        int linkStartIndex = emailContent.indexOf(linkPrefix) + linkPrefix.length();
        int linkEndIndex = emailContent.indexOf("\"", linkStartIndex);

        return emailContent.substring(linkStartIndex, linkEndIndex);
    }

    public String pollWorkspaceInvitationLink() {
        this.waitUntilEmailDelivered(JOIN_WORKSPACE_EMAIL_SUBJECT);

        String linkPrefix = "<a href=\"";
        String emailContent = this.getEmailMessageContent(JOIN_WORKSPACE_EMAIL_SUBJECT);

        if (!emailContent.contains(linkPrefix)) {
            throw new RuntimeException("Could not find workspace invitation link for email '" + email + "'. Raw html content:\n" + emailContent);
        }

        int linkStartIndex = emailContent.indexOf(linkPrefix) + linkPrefix.length();
        int linkEndIndex = emailContent.indexOf("\"", linkStartIndex);

        return emailContent.substring(linkStartIndex, linkEndIndex);
    }

    public String pollLaunchLink(String launchEmailSubject) {
        this.waitUntilEmailDelivered(launchEmailSubject);

        String linkPrefix = "<a href=\"";
        String emailContent = this.getEmailMessageContent(launchEmailSubject);

        if (!emailContent.contains(linkPrefix)) {
            throw new RuntimeException("Could not find launch link for email '" + email + "'. Raw html content:\n" + emailContent);
        }

        int linkStartIndex = emailContent.indexOf(linkPrefix) + linkPrefix.length();
        int linkEndIndex = emailContent.indexOf("\"", linkStartIndex);

        return emailContent.substring(linkStartIndex, linkEndIndex);
    }

    public String pollGitHubCode() {
        this.waitUntilEmailDelivered(GITHUB_VERIFY_DEVICE_EMAIL_SUBJECT);

        String codePrefix = "Verification code: ";
        String emailContent = this.getEmailMessageContent(GITHUB_VERIFY_DEVICE_EMAIL_SUBJECT);

        if (!emailContent.contains(codePrefix)) {
            throw new RuntimeException("Could not find Github verification code for email '" + email + "'. Raw html content:\n" + emailContent);
        }

        int codeStartIndex = emailContent.indexOf(codePrefix) + codePrefix.length();
        int codeEndIndex = emailContent.indexOf("\r", codeStartIndex);

        return emailContent.substring(codeStartIndex, codeEndIndex);
    }

    public String getEmailMessageContent(String subject) {
        return this.getOptionalEmailMessageContent(subject)
                   .orElseThrow(() -> new RuntimeException("Couldn't find email by subject '" + subject + "'"));
    }

    @SneakyThrows
    public Optional<String> getOptionalEmailMessageContent(String subject) {
        Session session = this.getSession();
        try (
                Store store = session.getStore();
                Folder inbox = this.connectAndOpenInbox(store, Folder.READ_ONLY)
        ) {
            SearchTerm searchTerm = new AndTerm(
                    new SubjectTerm(subject),
                    new RecipientTerm(Message.RecipientType.TO, new InternetAddress(email))
            );
            Message[] messages = inbox.search(searchTerm);

            return messages.length > 0
                    ? Optional.of(this.getHtmlContent(messages[0]))
                    : Optional.empty();
        }
    }

    @SneakyThrows
    private String getHtmlContent(Message message) {
        MimeMultipart multipart = (MimeMultipart) message.getContent();
        BodyPart bodyPart = multipart.getBodyPart(0);

        while (!bodyPart.isMimeType("text/html")) {
            multipart = (MimeMultipart) bodyPart.getContent();
            bodyPart = multipart.getBodyPart(0);
        }

        return (String) bodyPart.getContent();
    }

    @SneakyThrows
    private Folder connectAndOpenInbox(Store store, int openMode) {
        store.connect();
        Folder inbox = store.getFolder("INBOX");
        inbox.open(openMode);

        return inbox;
    }

    @SneakyThrows
    private void clearInbox() {
        Session session = this.getSession();
        try (
                Store store = session.getStore();
                Folder inbox = this.connectAndOpenInbox(store, Folder.READ_WRITE)
        ) {
            for (Message message : inbox.getMessages()) {
                message.setFlag(Flags.Flag.DELETED, true);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Getter
    @RequiredArgsConstructor
    private static class PasswordAuthenticator extends Authenticator {

        private final String email;
        private final String password;

        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(email, password);
        }

    }

}
