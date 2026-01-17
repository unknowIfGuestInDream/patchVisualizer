/*
 * MIT License
 *
 * Copyright (c) 2026 unknowIfGuestInDream
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.tlcsdm.patchvisualizer.preferences;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Locale;
import java.util.prefs.Preferences;

/**
 * Application preferences manager using Java Preferences API.
 *
 * @author unknowIfGuestInDream
 */
public class AppPreferences {

    private static final String LAST_DIRECTORY_KEY = "lastDirectory";
    private static final String LANGUAGE_KEY = "language";
    private static final Preferences prefs = Preferences.userNodeForPackage(AppPreferences.class);

    private static final AppPreferences INSTANCE = new AppPreferences();

    private final StringProperty lastDirectory = new SimpleStringProperty();
    private final StringProperty language = new SimpleStringProperty();

    private AppPreferences() {
        // Load preferences
        lastDirectory.set(prefs.get(LAST_DIRECTORY_KEY, System.getProperty("user.home")));
        language.set(prefs.get(LANGUAGE_KEY, Locale.getDefault().getLanguage()));

        // Add listeners to save changes
        lastDirectory.addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                prefs.put(LAST_DIRECTORY_KEY, newVal);
            }
        });

        language.addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                prefs.put(LANGUAGE_KEY, newVal);
            }
        });
    }

    public static AppPreferences getInstance() {
        return INSTANCE;
    }

    public String getLastDirectory() {
        return lastDirectory.get();
    }

    public void setLastDirectory(String directory) {
        this.lastDirectory.set(directory);
    }

    public StringProperty lastDirectoryProperty() {
        return lastDirectory;
    }

    public String getLanguage() {
        return language.get();
    }

    public void setLanguage(String language) {
        this.language.set(language);
    }

    public StringProperty languageProperty() {
        return language;
    }

    public Locale getLocale() {
        String lang = getLanguage();
        return switch (lang) {
            case "zh" -> Locale.CHINESE;
            case "ja" -> Locale.JAPANESE;
            default -> Locale.ENGLISH;
        };
    }
}
