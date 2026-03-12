package com.zebrunner.automation.util;

import com.zebrunner.carina.utils.config.Configuration;
import com.zebrunner.carina.webdriver.config.WebDriverConfiguration;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import lombok.SneakyThrows;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class WaitUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final Duration WAITING_TIME = Duration.ofSeconds(5);
    private static final Duration INTERVAL_TIME = Duration.ofSeconds(1);
    private static final String LOG_WAITING_TIME = "Total waiting time: {} minutes or {} seconds or {} milliseconds";

    /**
     * Used to expect that the list is not empty. In particular, it can be used to wait for
     * the initialization of the list of interface elements
     *
     * @param list of elements
     * @return true if list is not empty and false otherwise
     */
    public static boolean waitCheckListIsNotEmpty(List<?> list) {
        return waitCheckListIsNotEmpty(list, WAITING_TIME);
    }

    /**
     * Used to expect that the list is not empty with custom timeout. In particular, it can be used to wait for
     * the initialization of the list of interface elements
     *
     * @param list    of elements
     * @param timeout waiting time if after which the list remains empty an exception will be thrown
     * @return true if list is not empty and false otherwise
     */
    @Deprecated
    public static boolean waitCheckListIsNotEmpty(List<?> list, Duration timeout) {
        Clock clock = Clock.systemDefaultZone();
        Duration interval = Duration.ofSeconds(Math.min(30, timeout.toSeconds() / 8));
        Instant start = clock.instant();
        Instant end = clock.instant().plusSeconds(timeout.toSeconds());
        Sleeper sleeper = Sleeper.SYSTEM_SLEEPER;
        while (list.isEmpty() && end.isAfter(clock.instant())) {
            try {
                sleeper.sleep(interval);
            } catch (InterruptedException | StaleElementReferenceException e) {
                e.printStackTrace();
            }
        }
        LOGGER.info(LOG_WAITING_TIME, Duration.between(clock.instant(), start).toMinutes());

        return !list.isEmpty();
    }

    public static boolean waitCheckListIsNotEmpty(List<?> list,
                                                  String successLogMessage, String failLogMessage) {
        return waitCheckListIsNotEmpty(list, WAITING_TIME,
                INTERVAL_TIME,
                successLogMessage, failLogMessage);
    }

    public static boolean waitCheckListIsNotEmpty(List<?> list, Duration timeout, Duration interval,
                                                  String successLogMessage, String failLogMessage) {
        Clock clock = Clock.systemDefaultZone();
        Duration waitingInterval = Duration.ofSeconds(Math.min(30, interval.toSeconds()));
        Instant start = clock.instant();
        Instant end = clock.instant().plusSeconds(timeout.toSeconds());
        Sleeper sleeper = Sleeper.SYSTEM_SLEEPER;
        while (list.isEmpty() && end.isAfter(clock.instant())) {
            try {
                sleeper.sleep(waitingInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        LOGGER.info(LOG_WAITING_TIME, Duration.between(clock.instant(), start).toMinutes());

        if (list.isEmpty()) {
            LOGGER.info(failLogMessage);
        } else {
            LOGGER.info(successLogMessage);
        }
        return !list.isEmpty();
    }

    public static <T> List<T> waitElementListIsNotEmpty(List<T> elementList, String errorLogMessage) {
        return waitElementListIsNotEmpty(elementList, WAITING_TIME, errorLogMessage);
    }

    public static <T> List<T> waitElementListIsNotEmpty(List<T> elementList, Duration timeout, String errorLogMessage) {
        Clock clock = Clock.systemDefaultZone();
        Duration interval = Duration.ofSeconds(Math.min(30, timeout.toSeconds() / 8));
        Instant start = clock.instant();
        Instant end = clock.instant().plusSeconds(timeout.toSeconds());
        Sleeper sleeper = Sleeper.SYSTEM_SLEEPER;
        while (elementList.isEmpty() && end.isAfter(clock.instant())) {
            try {
                sleeper.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Duration allWaitingTime = Duration.between(clock.instant(), start);
        LOGGER.info(LOG_WAITING_TIME, allWaitingTime.toMinutes(), allWaitingTime.toSeconds(), allWaitingTime.toMillis());

        if (elementList.isEmpty()) {
            throw new NoSuchElementException(errorLogMessage);
        }

        return elementList;
    }

    /**
     * Waits for an element to appear in the list
     *
     * @param elementList the list of ui elements
     */
    public static <T> boolean waitAndCheckElementAppearedInListByCondition(List<T> elementList,
                                                                           Function<T, Boolean> condition) {
        Clock clock = Clock.systemDefaultZone();
        Duration interval = Duration.ofSeconds(Math.min(30, WAITING_TIME.toSeconds() / 5));
        Instant start = clock.instant();
        Instant end = clock.instant().plusSeconds(WAITING_TIME.toSeconds());
        Sleeper sleeper = Sleeper.SYSTEM_SLEEPER;

        boolean isPresent = false;

        while (end.isAfter(clock.instant())) {
            try {
                Optional<T> optionalElement = elementList.stream()
                        .filter(condition::apply)
                        .findFirst();
                if (optionalElement.isPresent()) {
                    isPresent = true;
                    break;
                }
            } catch (StaleElementReferenceException e) {
                LOGGER.info("Catched 'StaleElementReferenceException' while lambda proceed.");
            }
            try {
                sleeper.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Duration allWaitingTime = Duration.between(clock.instant(), start);
        LOGGER.info(LOG_WAITING_TIME, allWaitingTime.toMinutes(), allWaitingTime.toSeconds(), allWaitingTime.toMillis());
        return isPresent;
    }

    public static <T> T waitElementAppearedInListByCondition(List<T> elementList,
                                                             Function<T, Boolean> condition, String elementFoundLogMessage, String elementNotFoundErrorMessage) {

        return waitElementAppearedInListByCondition(elementList, condition, elementFoundLogMessage, elementNotFoundErrorMessage,
                WAITING_TIME);
    }

    /**
     * Search element in list by condition
     */
    public static <T> T waitElementAppearedInListByCondition(List<T> elementList,
                                                             Function<T, Boolean> condition, String elementFoundLogMessage, String elementNotFoundErrorMessage,
                                                             Duration timeout) {
        Duration waitingInterval = Duration.ofSeconds(timeout.toSeconds() / 8);

        return waitElementAppearedInListByCondition(elementList, condition, timeout, waitingInterval,
                elementFoundLogMessage, elementNotFoundErrorMessage);
    }

    public static <T> T waitElementAppearedInListByCondition(List<T> elementList,
                                                             Function<T, Boolean> condition, Duration timeout, Duration interval, String elementFoundLogMessage, String elementNotFoundErrorMessage) {
        Clock clock = Clock.systemDefaultZone();
        // Used 30 sec as max min because Driver Connection Refuse
        Duration waitingInterval = Duration.ofSeconds(Math.min(30, interval.toSeconds()));
        Instant start = clock.instant();
        Instant end = clock.instant().plusSeconds(timeout.toSeconds());
        Sleeper sleeper = Sleeper.SYSTEM_SLEEPER;

        Optional<T> optionalElement = Optional.empty();

        while (end.isAfter(clock.instant())) {
            Optional<T> tempOptionalElement = elementList.stream()
                    .filter(condition::apply)
                    .findFirst();
            if (tempOptionalElement.isPresent()) {
                optionalElement = tempOptionalElement;
                break;
            }
            try {
                sleeper.sleep(waitingInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Duration allWaitingTime = Duration.between(clock.instant(), start);
        LOGGER.info(LOG_WAITING_TIME, allWaitingTime.toMinutes(), allWaitingTime.toSeconds(), allWaitingTime.toMillis());

        if (optionalElement.isEmpty()) {
            throw new NoSuchElementException(elementNotFoundErrorMessage);
        } else {
            LOGGER.info(elementFoundLogMessage);
        }
        return optionalElement.get();
    }

    public static <T> boolean waitCheckElementMatchByCondition(T element, Function<T, Boolean> condition, Duration timeout, Duration interval) {
        Clock clock = Clock.systemDefaultZone();
        // Used 30 sec as max min because Driver Connection Refuse
        Duration waitingInterval = Duration.ofSeconds(Math.min(30, interval.toSeconds()));
        Instant start = clock.instant();
        Instant end = clock.instant().plusSeconds(timeout.toSeconds());
        Sleeper sleeper = Sleeper.SYSTEM_SLEEPER;

        boolean isConditionSuccess = false;

        while (end.isAfter(clock.instant())) {
            if (condition.apply(element)) {
                isConditionSuccess = true;
                break;
            }
            try {
                sleeper.sleep(waitingInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Duration allWaitingTime = Duration.between(clock.instant(), start);
        LOGGER.info(LOG_WAITING_TIME, allWaitingTime.toMinutes(), allWaitingTime.toSeconds(), allWaitingTime.toMillis());

        return isConditionSuccess;
    }

    public static <T> T waitElementMatchByCondition(T element, Function<T, Boolean> condition, Duration timeout, Duration interval,
                                                    String conditionSuccessLogMessage, String conditionNotSuccessErrorMessage) {
        Clock clock = Clock.systemDefaultZone();
        // Used 30 sec as max min because Driver Connection Refuse
        Duration waitingInterval = Duration.ofSeconds(Math.min(30, interval.toSeconds()));
        Instant start = clock.instant();
        Instant end = clock.instant().plusSeconds(timeout.toSeconds());
        Sleeper sleeper = Sleeper.SYSTEM_SLEEPER;

        boolean isConditionSuccess = false;

        while (end.isAfter(clock.instant())) {
            if (condition.apply(element)) {
                isConditionSuccess = true;
                break;
            }
            try {
                sleeper.sleep(waitingInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        Duration allWaitingTime = Duration.between(clock.instant(), start);
        LOGGER.info(LOG_WAITING_TIME, allWaitingTime.toMinutes(), allWaitingTime.toSeconds(), allWaitingTime.toMillis());

        if (!isConditionSuccess) {
            throw new NoSuchElementException(conditionNotSuccessErrorMessage);
        } else {
            LOGGER.info(conditionSuccessLogMessage);
        }
        return element;
    }

    // TODO Refactor
    @Beta
    @SneakyThrows
    public static Alert waitForAlert(WebDriver driver) {
        int i = 0;
        while (i++ < 5) {
            try {
                Alert alert = driver.switchTo().alert();
                return alert;
            } catch (NoAlertPresentException e) {
                Thread.sleep(1000);
                continue;
            }
        }
        return null;
    }

    /**
     * Use this function on component level only
     */
    public static <T> Optional<T> waitComponentInListByCondition(List<T> elementList,
                                                                 Function<T, Boolean> condition) {
        return waitComponentInListByCondition(elementList,
                condition, WAITING_TIME, INTERVAL_TIME);
    }

    /**
     * Use this function on component level only
     */
    public static <T> Optional<T> waitComponentInListByCondition(List<T> elementList,
                                                                 Function<T, Boolean> condition, Duration timeout, Duration interval) {
        Clock clock = Clock.systemDefaultZone();
        // Used 30 sec as max min because Driver Connection Refuse
        Duration waitingInterval = Duration.ofSeconds(Math.min(30, interval.toSeconds()));
        Instant end = clock.instant().plusSeconds(timeout.toSeconds());
        Sleeper sleeper = Sleeper.SYSTEM_SLEEPER;

        Optional<T> optionalElement = Optional.empty();

        while (end.isAfter(clock.instant())) {
            Optional<T> tempOptionalElement = elementList.stream()
                    .filter(condition::apply)
                    .findFirst();
            if (tempOptionalElement.isPresent()) {
                optionalElement = tempOptionalElement;
                break;
            }
            try {
                sleeper.sleep(waitingInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return optionalElement;
    }

    /**
     * Use this function on component level only
     *
     * @since 03.03.2021
     */
    public static <T> Optional<T> waitComponentByCondition(T element, Function<T, Boolean> condition) {
        return waitComponentByCondition(element, condition, WAITING_TIME, INTERVAL_TIME);

    }

    /**
     * Use this function on component level only
     */
    public static <T> Optional<T> waitComponentByCondition(T element, Function<T, Boolean> condition, Duration timeout, Duration interval) {
        Clock clock = Clock.systemDefaultZone();
        // Used 30 sec as max min because Driver Connection Refuse
        Duration waitingInterval = Duration.ofSeconds(Math.min(30, interval.toSeconds()));
        Instant end = clock.instant().plusSeconds(timeout.toSeconds());
        Sleeper sleeper = Sleeper.SYSTEM_SLEEPER;

        Optional<T> optionalElement = Optional.empty();

        while (end.isAfter(clock.instant())) {
            if (condition.apply(element)) {
                optionalElement = Optional.of(element);
            }
            try {
                sleeper.sleep(waitingInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return optionalElement;
    }

    public static <T> Optional<List<T>> waitComponentList(List<T> elementList) {
        return waitComponentList(elementList, WAITING_TIME, INTERVAL_TIME);
    }

    public static <T> Optional<List<T>> waitComponentList(List<T> elementList, Duration timeout, Duration interval) {
        Clock clock = Clock.systemDefaultZone();
        Duration waitInterval = Duration.ofSeconds(Math.min(30, interval.toSeconds()));
        Instant end = clock.instant().plusSeconds(timeout.toSeconds());
        Sleeper sleeper = Sleeper.SYSTEM_SLEEPER;
        while (end.isAfter(clock.instant())) {
            try {
                if (!elementList.isEmpty()) {
                    return Optional.of(elementList);
                }
            } catch (NoSuchElementException e) {
                LOGGER.debug(e.getMessage());
            }

            try {
                sleeper.sleep(waitInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }

    public static Optional<List<ExtendedWebElement>> wailListSize(List<ExtendedWebElement> list, Integer size) {
        LOGGER.info("Waiting list with size " + size);
        Wait<List<ExtendedWebElement>> wait = new FluentWait<>(list)
                .pollingEvery(Duration.ofMillis(5000)) // there is no sense to refresh url address too often
                .withTimeout(Duration.ofSeconds(Configuration.getRequired(WebDriverConfiguration.Parameter.EXPLICIT_TIMEOUT, Integer.class)));
        try {
            return Optional.of(wait.until(l ->
                    l.size() >= size ? l : null));
        } catch (TimeoutException e) {
            return Optional.empty();
        }
    }

    public static void waitNotEmptyListOfElements(WebDriver webDriver, String xpath) {
        try {
            new WebDriverWait(webDriver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(xpath), 0));
        } catch (TimeoutException | java.util.NoSuchElementException ignored) {
        }
    }
}