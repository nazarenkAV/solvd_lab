package com.zebrunner.automation.api.tcm.domain;

import lombok.*;
import org.apache.commons.lang3.RandomStringUtils;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Suite implements Cloneable {

    private String suiteName;
    private String runName;
    private Repository refersToRepo;

    public Suite getDeepCopy() {
        Suite newSuite = new Suite();
        newSuite.setSuiteName(suiteName);
        newSuite.setRunName(runName);
        newSuite.refersToRepo = refersToRepo;
        return newSuite;
    }

    @Override
    public Suite clone() throws CloneNotSupportedException {
        Suite suite = null;
        try {
            suite = (Suite) super.clone();
        } catch (CloneNotSupportedException e) {
            suite = new Suite();
            suite.setSuiteName(suiteName);
            suite.setRunName(runName);
        }
        suite.refersToRepo = (Repository) this.refersToRepo.clone();
        return suite;
    }

    public Suite getCopyWithRandomSuiteName() throws CloneNotSupportedException {
        final String randomSuiteName = RandomStringUtils.randomAlphabetic(6);

        Suite suite = this.clone();
        suite.setSuiteName(randomSuiteName);
        return suite;
    }
}
