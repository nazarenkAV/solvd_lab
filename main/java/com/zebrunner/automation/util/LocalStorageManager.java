package com.zebrunner.automation.util;

import lombok.SneakyThrows;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import com.zebrunner.automation.legacy.ObjectMapperFactory;

/**
 * Utility class for interacting with local storage using JavascriptExecutor.
 */
public class LocalStorageManager {

    private final JavascriptExecutor js;

    /**
     * Constructor to initialize the JavascriptExecutor.
     *
     * @param webDriver The WebDriver instance to use for executing Javascript.
     * @throws IllegalArgumentException If the WebDriver is null or does not implement JavascriptExecutor.
     */
    public LocalStorageManager(WebDriver webDriver) {
        if (webDriver == null) {
            throw new IllegalArgumentException("WebDriver cannot be null");
        }
        if (!(webDriver instanceof JavascriptExecutor)) {
            throw new IllegalArgumentException("WebDriver must implement JavascriptExecutor");
        }
        this.js = (JavascriptExecutor) webDriver;
    }

    /**
     * Removes an item from local storage by its key.
     *
     * @param item The key of the item to remove.
     */
    public void removeItem(String item) {
        js.executeScript(String.format("localStorage.removeItem('%s');", item));
    }

    /**
     * Checks if an item is present in local storage by its key.
     *
     * @param item The key of the item to check.
     * @return True if the item is present, false otherwise.
     */
    public boolean isItemPresent(String item) {
        Object result = js.executeScript(String.format("return localStorage.getItem('%s');", item));
        return result != null;
    }

    /**
     * Gets the value of an item from local storage by its key.
     *
     * @param key The key of the item to get.
     * @return The value of the item, or null if the item does not exist.
     */
    public String getItem(String key) {
        return (String) js.executeScript(String.format("return localStorage.getItem('%s');", key));
    }

    /**
     * Gets the key of an item from local storage by its index.
     *
     * @param index The index of the item.
     * @return The key of the item, or null if the index is out of bounds.
     */
    public String getKey(int index) {
        return (String) js.executeScript(String.format("return localStorage.key(%d);", index));
    }

    /**
     * Gets the number of items in local storage.
     *
     * @return The number of items in local storage.
     */
    public Long getLength() {
        return (Long) js.executeScript("return localStorage.length;");
    }

    /**
     * Sets the value of an item in local storage.
     *
     * @param key   The key of the item.
     * @param value The value to set.
     */
    public void setItem(String key, String value) {
        js.executeScript(String.format("localStorage.setItem('%s','%s');", key, value));
    }

    /**
     * Sets the value of an item in local storage.
     *
     * @param key    The key of the item.
     * @param object The value to set.
     */
    @SneakyThrows
    public void setItem(String key, Object object) {
        String value = ObjectMapperFactory.buildNew().writeValueAsString(object);
        js.executeScript(String.format("localStorage.setItem('%s','%s');", key, value));
    }

    /**
     * Clears all items from local storage.
     */
    public void clear() {
        js.executeScript("localStorage.clear();");
    }

}
