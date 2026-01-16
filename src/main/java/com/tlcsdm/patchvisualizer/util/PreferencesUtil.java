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
 * Utility class for managing application preferences.
 * Uses Java Preferences API to persist user settings across sessions.
 *
 * @author unknowIfGuestInDream
 */
public class PreferencesUtil {

    private static final String PREF_LAST_IMPORT_DIR = "lastImportDirectory";
    private static final String PREF_LAST_COMPARE_DIR = "lastCompareDirectory";
    private static final String PREF_LANGUAGE = "language";

    private static final Preferences prefs = Preferences.userNodeForPackage(PreferencesUtil.class);

    private PreferencesUtil() {
    }

    /**
     * Get the last directory used for importing diff/patch files.
     *
     * @return the last import directory, or user home if not set
     */
    public static File getLastImportDirectory() {
        String path = prefs.get(PREF_LAST_IMPORT_DIR, System.getProperty("user.home"));
        File dir = new File(path);
        return dir.exists() && dir.isDirectory() ? dir : new File(System.getProperty("user.home"));
    }

    /**
     * Set the last directory used for importing diff/patch files.
     *
     * @param directory the directory to save
     */
    public static void setLastImportDirectory(File directory) {
        if (directory != null) {
            File parent = directory.isDirectory() ? directory : directory.getParentFile();
            if (parent != null && parent.exists()) {
                prefs.put(PREF_LAST_IMPORT_DIR, parent.getAbsolutePath());
            }
        }
    }

    /**
     * Get the last directory used for file comparison.
     *
     * @return the last compare directory, or user home if not set
     */
    public static File getLastCompareDirectory() {
        String path = prefs.get(PREF_LAST_COMPARE_DIR, System.getProperty("user.home"));
        File dir = new File(path);
        return dir.exists() && dir.isDirectory() ? dir : new File(System.getProperty("user.home"));
    }

    /**
     * Set the last directory used for file comparison.
     *
     * @param directory the directory to save
     */
    public static void setLastCompareDirectory(File directory) {
        if (directory != null) {
            File parent = directory.isDirectory() ? directory : directory.getParentFile();
            if (parent != null && parent.exists()) {
                prefs.put(PREF_LAST_COMPARE_DIR, parent.getAbsolutePath());
            }
        }
    }

    /**
     * Get the saved language preference.
     *
     * @return the saved locale, or system default if not set
     */
    public static Locale getLanguage() {
        String language = prefs.get(PREF_LANGUAGE, null);
        if (language == null) {
            return Locale.getDefault();
        }
        return Locale.forLanguageTag(language);
    }

    /**
     * Set the language preference.
     *
     * @param locale the locale to save
     */
    public static void setLanguage(Locale locale) {
        if (locale != null) {
            prefs.put(PREF_LANGUAGE, locale.toLanguageTag());
        }
    }
}
