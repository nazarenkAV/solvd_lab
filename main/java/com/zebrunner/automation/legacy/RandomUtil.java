package com.zebrunner.automation.legacy;

import java.util.Random;

@Deprecated
public class RandomUtil {

    public static int generateRandomNumber(int start, int end) {
        if (start > end) {
            throw new IllegalArgumentException("The start value must be less than or equal to the end value.");
        }
        Random random = new Random();
        int randomValue = random.nextInt(end - start + 1) + start;
        return randomValue;
    }
}
