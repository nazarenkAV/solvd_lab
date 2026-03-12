package com.zebrunner.automation.util;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static com.zebrunner.carina.utils.common.CommonUtils.pause;

public class VideoDelimiterUtil {

    /**
     * @param driver        WebDriver instance
     * @param result        ITestResult instance
     * @param secondsToShow how long to show delimiter screen
     * @param timeZone      timeZone in str representation like "GMT+3:00"
     */
    public static void delimit(WebDriver driver, ITestResult result, Integer secondsToShow, String timeZone) {
        String script =
                "var canv=document.createElement(\"canvas\");\n"
                        + "canv.innerText = \"inner\";\n"
                        + "canv.setAttribute(\"id\", \"canvasID\");\n"
                        + "canv.setAttribute(\"width\", window.innerWidth);\n"
                        + "canv.setAttribute(\"height\", window.innerHeight);\n"
                        + "canv.setAttribute(\"style\", \"border:1px solid #000000;\");\n"
                        + "canv.style.position = \"absolute\";\n"
                        + "canv.style.top = 0;\n"
                        + "canv.style.left = 0;\n"
                        + "canv.style.zIndex = 5000;\n"
                        + "document.body.appendChild(canv);\n"
                        + "var ctx = canv.getContext(\"2d\");\n"
                        + "ctx.fillStyle = arguments[0];\n"
                        + "ctx.fillRect(0, 0, window.innerWidth, window.innerHeight);\n"
                        + "ctx.fillStyle = \"#000000\";\n"
                        + "ctx.font = \"30px Arial\";\n"
                        + "ctx.textAlign = \"center\";\n"
                        + "var widthCenter = window.innerWidth/2;\n"
                        + "var cellSize = window.innerHeight/10;\n"
                        + "ctx.fillText(arguments[1], widthCenter, cellSize*2);\n"
                        + "ctx.fillText(arguments[2], widthCenter, cellSize*3);\n"
                        + "ctx.fillText(arguments[3], widthCenter, cellSize*4);\n"
                        + "ctx.fillText(arguments[4], widthCenter, cellSize*5);\n"
                        + "ctx.fillText(arguments[5], widthCenter, cellSize*6);\n"
                        + "ctx.fillText(arguments[6], widthCenter, cellSize*7);\n"
                        + "ctx.fillText(arguments[7], widthCenter, cellSize*8);\n"
                        + "ctx.fillText(arguments[8], widthCenter, cellSize*9);\n";

        String backgroundColour = "#666a6e";
        String status = "status unknown";
        switch (result.getStatus()) {
            case (ITestResult.SUCCESS):
                backgroundColour = "#52a49a";
                status = "SUCCESS";
                break;
            case (ITestResult.FAILURE):
                backgroundColour = "#d95259";
                status = "FAILED";
                break;
            case (ITestResult.SKIP):
                backgroundColour = "#f6c85c";
                status = "SKIPPED";
                break;
        }

        DateFormat df = new SimpleDateFormat("dd.MM.yyyy - kk:mm:ss.SSS X");
        df.setTimeZone(TimeZone.getTimeZone(timeZone));
        long millis = result.getEndMillis() - result.getStartMillis();
        String duration = String.format("%02d:%02d:%02d:%03d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)),
                TimeUnit.MILLISECONDS.toMillis(millis) - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(millis)));

        String testName = "testName - " + result.getName();
        String testStatus = "status - " + status;
        String testContextName = "testContextName - " + result.getTestContext().getName();
        String testInstanceName = "instanceName - " + result.getInstanceName();
        String testId = "testId - " + result.id();
        String testStarted = "started - " + df.format(new Date(result.getStartMillis()));
        String testFinished = "finished - " + df.format(new Date(result.getEndMillis()));
        String testDuration = "duration - " + duration;


        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(script, backgroundColour, testName, testStatus,
                testContextName, testInstanceName, testId, testStarted,
                testFinished, testDuration);
        pause(secondsToShow);
        js.executeScript("canv = document.getElementById(\"canvasID\");\n" + "canv.remove();");
        pause(1);
    }

    /**
     * Timezone set to "GMT+0:00"
     *
     * @param driver        WebDriver instance
     * @param result        ITestResult instance
     * @param secondsToShow how long to show delimiter screen
     */
    public static void delimit(WebDriver driver, ITestResult result, Integer secondsToShow) {
        delimit(driver, result, secondsToShow, "GMT+0:00");
    }

}
