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

import com.github.difflib.DiffUtils;
import com.github.difflib.UnifiedDiffUtils;
import com.github.difflib.patch.Patch;
import com.github.difflib.patch.PatchFailedException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Diff handling utility for generating and visualizing file differences.
 *
 * @author unknowIfGuestInDream
 */
public class DiffHandleUtil {

    private static final int MAX_BINARY_LINES = 100;
    private static final String BINARY_MARKER = "GIT binary patch";
    private static final String BINARY_DIFF_MARKER = "Binary files";

    private DiffHandleUtil() {
    }

    /**
     * Compare two files and return the difference in original file + diff format.
     *
     * @param original original file content
     * @param revised  compared file content
     * @return list of diff strings
     */
    public static List<String> diffString(List<String> original, List<String> revised) {
        return diffString(original, revised, null, null);
    }

    /**
     * Compare two files and return the difference in original file + diff format.
     *
     * @param original         original file content
     * @param revised          compared file content
     * @param originalFileName original file name
     * @param revisedFileName  compared file name
     * @return list of diff strings
     */
    public static List<String> diffString(List<String> original, List<String> revised, String originalFileName,
                                          String revisedFileName) {
        originalFileName = originalFileName == null ? "Original" : originalFileName;
        revisedFileName = revisedFileName == null ? "Revised" : revisedFileName;
        // Generate diff patch
        Patch<String> patch = DiffUtils.diff(original, revised);
        // Generate unified diff format
        List<String> unifiedDiff = UnifiedDiffUtils.generateUnifiedDiff(originalFileName, revisedFileName, original,
                patch, 0);
        int diffCount = unifiedDiff.size();
        if (diffCount == 0) {
            // If no difference, insert placeholder
            unifiedDiff.add("--- " + originalFileName);
            unifiedDiff.add("+++ " + revisedFileName);
            unifiedDiff.add("@@ -0,0 +0,0 @@");
        } else if (diffCount >= 3 && !unifiedDiff.get(2).contains("@@ -1,")) {
            unifiedDiff.set(1, unifiedDiff.get(1));
            // If first line unchanged, insert placeholder
            unifiedDiff.add(2, "@@ -0,0 +0,0 @@");
        }
        // Add space prefix to original lines
        List<String> original1 = original.stream().map(v -> " " + v).collect(Collectors.toList());
        // Insert diff into original file
        return insertOrig(original1, unifiedDiff);
    }

    /**
     * Compare two files and return the difference in original file + diff format.
     *
     * @param filePathOriginal original file path
     * @param filePathRevised  compared file path
     * @return list of diff strings
     */
    public static List<String> diffString(String filePathOriginal, String filePathRevised) {
        List<String> original = null;
        List<String> revised = null;
        File originalFile = new File(filePathOriginal);
        File revisedFile = new File(filePathRevised);
        try {
            original = Files.readAllLines(originalFile.toPath());
            revised = Files.readAllLines(revisedFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read files", e);
        }
        return diffString(original, revised, originalFile.getName(), revisedFile.getName());
    }

    /**
     * Parse a patch file content.
     *
     * @param patchContent the content of the patch file as lines
     * @return list of diff strings for visualization
     */
    public static List<String> parsePatchFile(List<String> patchContent) {
        if (patchContent == null || patchContent.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(patchContent);
    }

    /**
     * Filter and optimize patch content by truncating binary sections.
     * This prevents large binary patches from freezing the UI.
     *
     * @param patchContent the original patch content
     * @return optimized patch content
     */
    public static List<String> optimizePatchContent(List<String> patchContent) {
        if (patchContent == null || patchContent.isEmpty()) {
            return patchContent;
        }

        List<String> optimized = new ArrayList<>();
        boolean inBinarySection = false;
        int binaryLineCount = 0;

        for (String line : patchContent) {
            // Detect binary section start
            if (!inBinarySection && (line.contains(BINARY_MARKER) || line.contains(BINARY_DIFF_MARKER))) {
                inBinarySection = true;
                binaryLineCount = 0;
                optimized.add(line);
                continue;
            }

            // In binary section
            if (inBinarySection) {
                binaryLineCount++;
                
                // Only keep first MAX_BINARY_LINES lines of binary content
                if (binaryLineCount <= MAX_BINARY_LINES) {
                    optimized.add(line);
                } else if (binaryLineCount == MAX_BINARY_LINES + 1) {
                    optimized.add("... (binary content truncated for performance) ...");
                }

                // Detect binary section end (next file or diff marker)
                if (line.startsWith("diff --git") || line.startsWith("---") && binaryLineCount > 5) {
                    inBinarySection = false;
                    // Don't skip this line, it's the start of the next diff
                    if (binaryLineCount > MAX_BINARY_LINES) {
                        optimized.add(line);
                    }
                }
            } else {
                optimized.add(line);
            }
        }

        return optimized;
    }

    /**
     * Parse a patch file and apply it to original content.
     *
     * @param original     original file content
     * @param patchContent the content of the patch file as lines
     * @return the patched content
     */
    public static List<String> applyPatch(List<String> original, List<String> patchContent) {
        try {
            Patch<String> patch = UnifiedDiffUtils.parseUnifiedDiff(patchContent);
            return DiffUtils.patch(original, patch);
        } catch (PatchFailedException e) {
            throw new RuntimeException("Failed to apply patch", e);
        }
    }

    /**
     * Generate diff HTML content.
     *
     * @param diffString diff strings
     * @param htmlPath   HTML output path
     */
    public static void generateDiffHtml(List<String> diffString, String htmlPath) {
        generateDiffHtml(htmlPath, List.of(diffString));
    }

    /**
     * Generate diff HTML content from multiple diffs.
     *
     * @param htmlPath       HTML output path
     * @param diffStringList list of diff strings
     */
    public static void generateDiffHtml(String htmlPath, List<List<String>> diffStringList) {
        String template = getDiffHtml(diffStringList);
        try (FileWriter f = new FileWriter(htmlPath);
             BufferedWriter buf = new BufferedWriter(f)) {
            buf.write(template);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate HTML", e);
        }
    }

    /**
     * Get diff HTML content.
     *
     * @param diffStringList list of diff strings
     * @return HTML content
     */
    public static String getDiffHtml(List<List<String>> diffStringList) {
        Map<String, Object> map = new HashMap<>(8);
        try {
            map.put("highlightCss", readStream(
                    DiffHandleUtil.class.getResourceAsStream("/com/tlcsdm/patchvisualizer/static/diff2html/github.min.css")));
            map.put("diff2htmlCss", readStream(
                    DiffHandleUtil.class.getResourceAsStream("/com/tlcsdm/patchvisualizer/static/diff2html/diff2html.min.css")));
            map.put("diff2htmlJs", readStream(
                    DiffHandleUtil.class.getResourceAsStream("/com/tlcsdm/patchvisualizer/static/diff2html/diff2html-ui.min.js")));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load resources", e);
        }

        String template = """
                <!DOCTYPE html>
                <html lang="en-us">
                <head>
                  <meta charset="utf-8" />
                  <meta name="google" content="notranslate" />
                  <meta name="author" content="unknowIfGuestInDream">
                </head>
                <style type="text/css">
                {highlightCss}
                </style>
                <style type="text/css">
                {diff2htmlCss}
                </style>
                <script type="text/javascript">
                {diff2htmlJs}
                </script>
                <script>
                  const diffString = `
                {diffString}
                  `;

                  document.addEventListener('DOMContentLoaded', function () {
                    var targetElement = document.getElementById('myDiffElement');
                    var configuration = {
                      drawFileList: true,
                      fileListToggle: true,
                      fileListStartVisible: true,
                      fileContentToggle: true,
                      matching: 'lines',
                      outputFormat: 'side-by-side',
                      synchronisedScroll: true,
                      highlight: true,
                      renderNothingWhenEmpty: true,
                    };
                    var diff2htmlUi = new Diff2HtmlUI(targetElement, diffString, configuration);
                    diff2htmlUi.draw();
                    diff2htmlUi.highlightCode();
                  });
                </script>
                <body>
                  <div id="myDiffElement"></div>
                </body>
                </html>
                """;

        StringJoiner diffStringJoiner = new StringJoiner("\n");
        for (List<String> diffString : diffStringList) {
            StringBuilder builder = new StringBuilder();
            for (String line : diffString) {
                // Escape $ character
                builder.append(line.replace("$", "\\$"));
                builder.append("\n");
            }
            diffStringJoiner.add(builder.toString());
        }
        map.put("diffString", diffStringJoiner.toString());
        return formatTemplate(template, map);
    }

    /**
     * Read stream content as string.
     *
     * @param inputStream input stream
     * @return string content
     * @throws IOException if reading fails
     */
    public static String readStream(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Format template with placeholders.
     *
     * @param template the template string with {key} placeholders
     * @param map      the map of values
     * @return formatted string
     */
    private static String formatTemplate(String template, Map<String, Object> map) {
        String result = template;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
        }
        return result;
    }

    /**
     * Insert unified diff into original file content.
     *
     * @param original    original file content (with space prefix)
     * @param unifiedDiff unified diff content
     * @return merged content
     */
    public static List<String> insertOrig(List<String> original, List<String> unifiedDiff) {
        List<String> result = new ArrayList<>();
        // Split unifiedDiff by @@ markers into diffList
        List<List<String>> diffList = new ArrayList<>();
        List<String> d = new ArrayList<>();
        for (int i = 0; i < unifiedDiff.size(); i++) {
            String u = unifiedDiff.get(i);
            if (u.startsWith("@@") && !"@@ -0,0 +0,0 @@".equals(u) && !u.contains("@@ -1,")) {
                List<String> twoList = new ArrayList<>(d);
                diffList.add(twoList);
                d.clear();
                d.add(u);
                continue;
            }
            if (i == unifiedDiff.size() - 1) {
                d.add(u);
                List<String> twoList = new ArrayList<>(d);
                diffList.add(twoList);
                d.clear();
                break;
            }
            d.add(u);
        }

        // Merge diffList with original into result
        for (int i = 0; i < diffList.size(); i++) {
            List<String> diff = diffList.get(i);
            List<String> nexDiff = i == diffList.size() - 1 ? null : diffList.get(i + 1);
            // Line containing @@
            String simb = i == 0 ? diff.get(2) : diff.get(0);
            String nexSimb = nexDiff == null ? null : nexDiff.get(0);
            // Insert into result
            insert(result, diff);
            // Parse @@ line to get row information
            Map<String, Integer> map = getRowMap(simb);
            if (null != nexSimb) {
                Map<String, Integer> nexMap = getRowMap(nexSimb);
                int start = 0;
                if (map.get("orgRow") != 0) {
                    start = map.get("orgRow") + map.get("orgDel") - 1;
                }
                int end = nexMap.get("revRow") - 2;
                // Insert unchanged content
                insert(result, getOrigList(original, start, end));
            }

            int start = (map.get("orgRow") + map.get("orgDel") - 1);
            start = start == -1 ? 0 : start;
            if (simb.contains("@@ -1,") && null == nexSimb && map.get("orgDel") != original.size()) {
                insert(result, getOrigList(original, start, original.size() - 1));
            } else if (null == nexSimb && (map.get("orgRow") + map.get("orgDel") - 1) < original.size()) {
                insert(result, getOrigList(original, start, original.size() - 1));
            }
        }
        int diffCount = diffList.size() - 1;
        if (!"@@ -0,0 +0,0 @@".equals(unifiedDiff.get(2))) {
            diffCount = Math.max(diffList.size(), 1);
        }
        result.set(1, result.get(1) + " ( " + diffCount + " different )");
        return result;
    }

    /**
     * Insert content into result list.
     *
     * @param result          result list
     * @param noChangeContent content to insert
     */
    public static void insert(List<String> result, List<String> noChangeContent) {
        result.addAll(noChangeContent);
    }

    /**
     * Parse @@ line to get row modification info.
     *
     * @param str the @@ line
     * @return map with orgRow, orgDel, revRow, revAdd
     */
    public static Map<String, Integer> getRowMap(String str) {
        Map<String, Integer> map = new HashMap<>();
        if (str.startsWith("@@")) {
            String[] sp = str.split(" ");
            String org = sp[1];
            String[] orgSp = org.split(",");
            // Original file row number to delete
            map.put("orgRow", Integer.valueOf(orgSp[0].substring(1)));
            // Number of lines deleted from original
            map.put("orgDel", Integer.valueOf(orgSp[1]));

            String[] revSp = org.split(",");
            // Revised file row number to add
            map.put("revRow", Integer.valueOf(revSp[0].substring(1)));
            map.put("revAdd", Integer.valueOf(revSp[1]));
        }
        return map;
    }

    /**
     * Get a range of lines from original list.
     *
     * @param original1 original list
     * @param start     start index
     * @param end       end index
     * @return sublist
     */
    public static List<String> getOrigList(List<String> original1, int start, int end) {
        List<String> list = new ArrayList<>();
        if (!original1.isEmpty() && start <= end && end < original1.size()) {
            for (; start <= end; start++) {
                list.add(original1.get(start));
            }
        }
        return list;
    }
}
