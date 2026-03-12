package com.zebrunner.automation.legacy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PageUrlEnum {

    DOCUMENTATION("Documentation", "https://zebrunner.com/documentation/"),
    DOC_REPORTING_CONCEPTS("Reporting concepts - Documentation", "https://zebrunner.com/documentation/reporting/"),
    DOC_GUIDE_LAUNCHERS("Launchers", "https://zebrunner.com/documentation/guide/launchers/"),
    DOC_REPORTING_API_GUIDE("Automation Reporting API guide - Documentation", "https://zebrunner.com/documentation/reporting/api/");

    private final String pageTitle;
    private final String pageUrl;

}
