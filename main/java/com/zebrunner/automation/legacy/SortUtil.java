package com.zebrunner.automation.legacy;

import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Deprecated
public class SortUtil {

    public static <T> boolean isSorted(List<T> items, Comparator<T> comparator, boolean isAscending) {
        if (items == null || items.isEmpty()) {
            log.error("List with items is empty!");
            return false;
        }

        if (!isAscending) {
            comparator = comparator.reversed();
        }

        for (int i = 1; i < items.size(); i++) {
            if (comparator.compare(items.get(i - 1), items.get(i)) > 0) {
                log.error("Error for items");
                log.info("Item " + (i - 1) + " " + items.get(i - 1));
                log.info("Item " + (i) + " " + items.get(i));
                return false;
            }
        }

        return true;
    }

}
