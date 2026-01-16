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

package com.tlcsdm.patchvisualizer.util;

import java.io.File;
import java.util.Locale;
import java.util.prefs.Preferences;

/**
 * Utility class for managing application preferences and settings.
 * Uses Java Preferences API to persist settings across sessions.
 *
 * @author unknowIfGuestInDream
 */
public class PreferencesUtil {

    private static final Preferences PREFS = Preferences.userNodeForPackage(PreferencesUtil.class);

    private static final String KEY_LAST_IMPORT_DIRECTORY = "lastImportDirectory";
    private static final String KEY_LAST_ORIGINAL_DIRECTORY = "lastOriginalDirectory";
    private static final String KEY_LAST_REVISED_DIRECTORY = "lastRevisedDirectory";
    private static final String KEY_LANGUAGE = "language";

    private PreferencesUtil() {
        // Utility class
    }

    /**
     * Get the last directory used for importing diff/patch files.
     *
     * @return the last directory, or null if not set
     */
    public static File getLastImportDirectory() {
        String path = PREFS.get(KEY_LAST_IMPORT_DIRECTORY, null);
        if (path != null) {
            File dir = new File(path);
            if (dir.exists() && dir.isDirectory()) {
                return dir;
            }
        }
        return null;
    }

    /**
     * Set the last directory used for importing diff/patch files.
     *
     * @param directory the directory to save
     */
    public static void setLastImportDirectory(File directory) {
        if (directory != null && directory.exists()) {
            File dir = directory.isDirectory() ? directory : directory.getParentFile();
            if (dir != null) {
                PREFS.put(KEY_LAST_IMPORT_DIRECTORY, dir.getAbsolutePath());
            }
        }
    }

    /**
     * Get the last directory used for selecting original files.
     *
     * @return the last directory, or null if not set
     */
    public static File getLastOriginalDirectory() {
        String path = PREFS.get(KEY_LAST_ORIGINAL_DIRECTORY, null);
        if (path != null) {
            File dir = new File(path);
            if (dir.exists() && dir.isDirectory()) {
                return dir;
            }
        }
        return null;
    }

    /**
     * Set the last directory used for selecting original files.
     *
     * @param directory the directory to save
     */
    public static void setLastOriginalDirectory(File directory) {
        if (directory != null && directory.exists()) {
            File dir = directory.isDirectory() ? directory : directory.getParentFile();
            if (dir != null) {
                PREFS.put(KEY_LAST_ORIGINAL_DIRECTORY, dir.getAbsolutePath());
            }
        }
    }

    /**
     * Get the last directory used for selecting revised files.
     *
     * @return the last directory, or null if not set
     */
    public static File getLastRevisedDirectory() {
        String path = PREFS.get(KEY_LAST_REVISED_DIRECTORY, null);
        if (path != null) {
            File dir = new File(path);
            if (dir.exists() && dir.isDirectory()) {
                return dir;
            }
        }
        return null;
    }

    /**
     * Set the last directory used for selecting revised files.
     *
     * @param directory the directory to save
     */
    public static void setLastRevisedDirectory(File directory) {
        if (directory != null && directory.exists()) {
            File dir = directory.isDirectory() ? directory : directory.getParentFile();
            if (dir != null) {
                PREFS.put(KEY_LAST_REVISED_DIRECTORY, dir.getAbsolutePath());
            }
        }
    }

    /**
     * Get the saved language preference.
     *
     * @return the saved locale, or system default if not set
     */
    public static Locale getLanguage() {
        String language = PREFS.get(KEY_LANGUAGE, null);
        if (language != null) {
            return Locale.forLanguageTag(language);
        }
        return Locale.getDefault();
    }

    /**
     * Save the language preference.
     *
     * @param locale the locale to save
     */
    public static void setLanguage(Locale locale) {
        if (locale != null) {
            PREFS.put(KEY_LANGUAGE, locale.toLanguageTag());
        }
    }
}
