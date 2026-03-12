package com.zebrunner.automation.legacy;

import org.testng.annotations.DataProvider;

@Deprecated
public class TestingPlatformsDataProvider {

    @DataProvider(name = "ZEBRUNNER_SELENIUM_GRID")
    public static Object[][] zebrunnerGrid() {
        return new Object[][] {
                { LauncherDataEnum.NATIVE.get(), LauncherDataEnum.ANDROID.get(), LauncherDataEnum.REDROID.get(),
                        new String[] { "ZTP-3363", "ZTP-3370" } },//ZTP-3363, ZTP-3370
                { LauncherDataEnum.WEB.get(), LauncherDataEnum.ANDROID.get(), LauncherDataEnum.REDROID.get(),
                        new String[] { "ZTP-3364", "ZTP-3369" } },//ZTP-3364, ZTP-3369
                { LauncherDataEnum.WEB.get(), LauncherDataEnum.LINUX.get(), LauncherDataEnum.CHROME.get(), new String[] { "ZTP-3365", "ZTP-3366" } },
                //ZTP-3365,ZTP-3366
                { LauncherDataEnum.WEB.get(), LauncherDataEnum.LINUX.get(), LauncherDataEnum.FIREFOX.get(), new String[] { "ZTP-3368" } },//ZTP-3368
                { LauncherDataEnum.WEB.get(), LauncherDataEnum.LINUX.get(), LauncherDataEnum.EDGE.get(), new String[] { "ZTP-3367" } },//ZTP-3367
        };
    }

    @DataProvider(name = "ZEBRUNNER_SELENIUM_GRID_OS_WITH_BROWSERS")
    public static Object[][] zebrunnerSeleniumGridOSsWithBrowsers() {
        return new Object[][] {
                { LauncherDataEnum.LINUX.get(), LauncherDataEnum.CHROME.get(), new String[] { "ZTP-3365", "ZTP-3366" } },//ZTP-3365,ZTP-3366
                { LauncherDataEnum.LINUX.get(), LauncherDataEnum.FIREFOX.get(), new String[] { "ZTP-3368" } },//ZTP-3368
                { LauncherDataEnum.LINUX.get(), LauncherDataEnum.EDGE.get(), new String[] { "ZTP-3367" } },//ZTP-3367
        };
    }

    @DataProvider(name = "ZEBRUNNER_SELENIUM_GRID_OS_WITH_DEVICES")
    public static Object[][] zebrunnerSeleniumGridOSsWithDevices() {
        return new Object[][] {
                { LauncherDataEnum.NATIVE.get(), LauncherDataEnum.ANDROID.get(),
                        new String[] { "ZTP-3363", "ZTP-3370" } },//ZTP-3363, ZTP-3370
                { LauncherDataEnum.WEB.get(), LauncherDataEnum.ANDROID.get(),
                        new String[] { "ZTP-3364", "ZTP-3369" } },//ZTP-3364, ZTP-3369
        };
    }

    @DataProvider(name = "BROWSER_STACK_OS_WITH_DEVICES")
    public static Object[][] browserStackOSsWithDevices() {
        return new Object[][] {
                { LauncherDataEnum.NATIVE.get(), LauncherDataEnum.ANDROID.get(), new String[] { "ZTP-3374", "ZTP-3390" } },//ZTP-3374, ZTP-3390
                { LauncherDataEnum.WEB.get(), LauncherDataEnum.ANDROID.get(), new String[] { "ZTP-3373", "ZTP-3389" } },//ZTP-3373, ZTP-3389
                { LauncherDataEnum.NATIVE.get(), LauncherDataEnum.IOS.get(), new String[] { "ZTP-3375", "ZTP-3391" } },//ZTP-3375, ZTP-3391
                { LauncherDataEnum.WEB.get(), LauncherDataEnum.IOS.get(), new String[] { "ZTP-3376", "ZTP-3392" } }//ZTP-3376, ZTP-3392
        };
    }

    @DataProvider(name = "BROWSER_STACK_OS_WITH_BROWSERS")
    public static Object[][] browserStackOSsWithBrowsers() {
        return new Object[][] {
                { "macOS", LauncherDataEnum.CHROME.get(), new String[] { "ZTP-3381", "ZTP-3371" } },//ZTP-3381,ZTP-3372
                { "macOS", LauncherDataEnum.FIREFOX.get(), "ZTP-3383" },//ZTP-3383
                { "macOS", LauncherDataEnum.EDGE.get(), "ZTP-3382" },//ZTP-3382
                { "macOS", LauncherDataEnum.SAFARI.get(), "ZTP-3384" },//ZTP-3384

                { "Windows", LauncherDataEnum.CHROME.get(), new String[] { "ZTP-3385", "ZTP-3372" } },//ZTP-3385,ZTP-3372
                { "Windows", LauncherDataEnum.FIREFOX.get(), "ZTP-3387" },//ZTP-3387
                { "Windows", LauncherDataEnum.EDGE.get(), "ZTP-3386" },//ZTP-3386
                { "Windows", LauncherDataEnum.IE.get(), "ZTP-3388" }//ZTP-3388
        };
    }

    @DataProvider(name = "LAMBDATEST_OS_WITH_DEVICES")
    public static Object[][] lambdaTestOSsWithDevices() {
        return new Object[][] {
                { LauncherDataEnum.WEB.get(), LauncherDataEnum.ANDROID.get(), new String[] { "ZTP-3432", "ZTP-3418" } },
                { LauncherDataEnum.WEB.get(), LauncherDataEnum.IOS.get(), new String[] { "ZTP-3434", "ZTP-3419" } }
        };
    }

    @DataProvider(name = "LAMBDATEST_OS_WITH_BROWSERS")
    public static Object[][] lambdaTestOSsWithBrowsers() {
        return new Object[][] {
                { "macOS", LauncherDataEnum.CHROME.get(), new String[] { "ZTP-3420", "ZTP-3416" } },//ZTP-3420,ZTP-3416
                { "macOS", LauncherDataEnum.FIREFOX.get(), "ZTP-3421" },//ZTP-3421
                { "macOS", LauncherDataEnum.EDGE.get(), "ZTP-3422" },//ZTP-3422
                { "macOS", LauncherDataEnum.SAFARI.get(), "ZTP-3424" },//ZTP-3424
                { "Windows", LauncherDataEnum.OPERA.get(), "ZTP-3423" },//ZTP-3423

                { "Windows", LauncherDataEnum.CHROME.get(), new String[] { "ZTP-3425", "ZTP-3417" } },//ZTP-3425,ZTP-3417
                { "Windows", LauncherDataEnum.FIREFOX.get(), "ZTP-3426" },//ZTP-3426
                { "Windows", LauncherDataEnum.EDGE.get(), "ZTP-3428" },//ZTP-3428
                { "Windows", LauncherDataEnum.IE.get(), "ZTP-3427" },//ZTP-3427
                { "Windows", LauncherDataEnum.OPERA.get(), "ZTP-3430" }//ZTP-3430
        };
    }

    @DataProvider(name = "SAUCE_LABS_OS_WITH_DEVICES")
    public static Object[][] sauceLabsOSsWithDevices() {
        return new Object[][] {
                { LauncherDataEnum.NATIVE.get(), LauncherDataEnum.ANDROID.get(), new String[] { "ZTP-3439", "ZTP-3453" } },//ZTP-3439, ZTP-3453
                { LauncherDataEnum.WEB.get(), LauncherDataEnum.ANDROID.get(), new String[] { "ZTP-3438", "ZTP-3452" } },//ZTP-3438, ZTP-3452
                { LauncherDataEnum.NATIVE.get(), LauncherDataEnum.IOS.get(), new String[] { "ZTP-3441", "ZTP-3455" } },//ZTP-3441, ZTP-3455
                { LauncherDataEnum.WEB.get(), LauncherDataEnum.IOS.get(), new String[] { "ZTP-3440", "ZTP-3454" } }//ZTP-3440, ZTP-3454
        };
    }

    @DataProvider(name = "SAUCE_LABS_OS_WITH_BROWSERS")
    public static Object[][] sauceLabsOSsWithBrowsers() {
        return new Object[][] {
                { "macOS", LauncherDataEnum.CHROME.get(), new String[] { "ZTP-3436", "ZTP-3444" } },//ZTP-3436,ZTP-3372
                //  { "macOS", LauncherDataEnum.FIREFOX.get(), "ZTP-3383" },//ZTP-3383
                //   { "macOS", LauncherDataEnum.EDGE.get(), "ZTP-3382" },//ZTP-3382
                //   { "macOS", LauncherDataEnum.SAFARI.get(), "ZTP-3384" },//ZTP-3384

                { "Windows", LauncherDataEnum.CHROME.get(), new String[] { "ZTP-3437", "ZTP-3448" } },//ZTP-3385,ZTP-3448
                { "Windows", LauncherDataEnum.FIREFOX.get(), "ZTP-3450" },//ZTP-3450
                { "Windows", LauncherDataEnum.EDGE.get(), "ZTP-3449" },//ZTP-3449
                { "Windows", LauncherDataEnum.IE.get(), "ZTP-3451" },//ZTP-3451

                { LauncherDataEnum.LINUX.get(), LauncherDataEnum.CHROME.get(), new String[] { "ZTP-3435", "ZTP-3442" } },//ZTP-3435,ZTP-3442
                { LauncherDataEnum.LINUX.get(), LauncherDataEnum.FIREFOX.get(), "ZTP-3443" },//ZTP-3443
                //  { LauncherDataEnum.LINUX.get(), LauncherDataEnum.EDGE.get(), "ZTP-3445" },//ZTP-3445 (not supported now)
                //  { LauncherDataEnum.LINUX.get(), LauncherDataEnum.SAFARI.get(), "ZTP-3447" }//ZTP-3447 (not supported now)
        };
    }

    @DataProvider(name = "TESTING_BOT_OS_WITH_DEVICES")
    public static Object[][] testingBotPlatformOSsWithDevices() {
        return new Object[][] {
                { LauncherDataEnum.NATIVE.get(), LauncherDataEnum.ANDROID.get(), new String[] { "ZTP-3397", "ZTP-3412" } },
                { LauncherDataEnum.WEB.get(), LauncherDataEnum.ANDROID.get(), new String[] { "ZTP-3396", "ZTP-3413" } },
                { LauncherDataEnum.NATIVE.get(), LauncherDataEnum.IOS.get(), new String[] { "ZTP-3399", "ZTP-3414" } },
                { LauncherDataEnum.WEB.get(), LauncherDataEnum.IOS.get(), new String[] { "ZTP-3398", "ZTP-3415" } }
        };
    }

    @DataProvider(name = "TESTING_BOT_OS_WITH_BROWSERS")
    public static Object[][] testingBotOSsWithBrowsers() {
        return new Object[][] {
                { "macOS", LauncherDataEnum.CHROME.get(), new String[] { "ZTP-3400", "ZTP-3393" } },
                { "macOS", LauncherDataEnum.FIREFOX.get(), "ZTP-3401" },
                { "macOS", LauncherDataEnum.EDGE.get(), "ZTP-3402" },
                { "macOS", LauncherDataEnum.OPERA.get(), "ZTP-3403" },
                { "macOS", LauncherDataEnum.SAFARI.get(), "ZTP-3404" },

                { "Windows", LauncherDataEnum.CHROME.get(), new String[] { "ZTP-3395", "ZTP-3408" } },
                { "Windows", LauncherDataEnum.FIREFOX.get(), "ZTP-3409" },
                { "Windows", LauncherDataEnum.OPERA.get(), "ZTP-3411" },
                { "Windows", LauncherDataEnum.IE.get(), "ZTP-3410" },

                { LauncherDataEnum.LINUX.get(), LauncherDataEnum.CHROME.get(), new String[] { "ZTP-3394", "ZTP-3405" } },
                { LauncherDataEnum.LINUX.get(), LauncherDataEnum.FIREFOX.get(), "ZTP-3406" },
                { LauncherDataEnum.LINUX.get(), LauncherDataEnum.OPERA.get(), "ZTP-3407" },
        };
    }

}
