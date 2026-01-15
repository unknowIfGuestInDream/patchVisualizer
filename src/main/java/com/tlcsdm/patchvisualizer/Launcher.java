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

package com.tlcsdm.patchvisualizer;

/**
 * Launcher class for running the JavaFX application from a fat JAR.
 * <p>
 * This class is needed because JavaFX does not allow a class that extends
 * {@link javafx.application.Application} to be the main class of an executable
 * fat JAR. The JavaFX runtime checks the module path for JavaFX components,
 * and when running from a shaded JAR, this check fails with:
 * "Error: JavaFX runtime components are missing, and are required to run this application"
 * <p>
 * By using this launcher class (which does NOT extend Application), we bypass
 * this check and allow the application to start correctly from the fat JAR.
 *
 * @author unknowIfGuestInDream
 */
public class Launcher {

    /**
     * Main entry point for the application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        PatchVisualizerApp.main(args);
    }
}
