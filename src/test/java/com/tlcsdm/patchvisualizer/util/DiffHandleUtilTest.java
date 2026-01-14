/*
 * MIT License
 *
 * Copyright (c) 2026 unknowIfGuestInDream
 */

package com.tlcsdm.patchvisualizer.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DiffHandleUtil.
 */
class DiffHandleUtilTest {

    @TempDir
    Path tempDir;

    @Test
    void testDiffStringWithLists() {
        List<String> original = Arrays.asList("line 1", "line 2", "line 3");
        List<String> revised = Arrays.asList("line 1", "line 2 modified", "line 3");

        List<String> result = DiffHandleUtil.diffString(original, revised);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(s -> s.contains("---")));
        assertTrue(result.stream().anyMatch(s -> s.contains("+++")));
    }

    @Test
    void testDiffStringWithFilePaths() throws IOException {
        Path originalFile = tempDir.resolve("original.txt");
        Path revisedFile = tempDir.resolve("revised.txt");

        Files.write(originalFile, Arrays.asList("line 1", "line 2", "line 3"));
        Files.write(revisedFile, Arrays.asList("line 1", "line 2 modified", "line 3"));

        List<String> result = DiffHandleUtil.diffString(
                originalFile.toAbsolutePath().toString(),
                revisedFile.toAbsolutePath().toString());

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testDiffStringWithNoChanges() {
        List<String> original = Arrays.asList("line 1", "line 2", "line 3");
        List<String> revised = Arrays.asList("line 1", "line 2", "line 3");

        List<String> result = DiffHandleUtil.diffString(original, revised);

        assertNotNull(result);
        assertTrue(result.stream().anyMatch(s -> s.contains("@@ -0,0 +0,0 @@")));
    }

    @Test
    void testParsePatchFile() {
        List<String> patchContent = Arrays.asList(
                "--- a/file.txt",
                "+++ b/file.txt",
                "@@ -1,3 +1,3 @@",
                " line 1",
                "-line 2",
                "+line 2 modified",
                " line 3"
        );

        List<String> result = DiffHandleUtil.parsePatchFile(patchContent);

        assertNotNull(result);
        assertEquals(patchContent.size(), result.size());
    }

    @Test
    void testParsePatchFileWithNull() {
        List<String> result = DiffHandleUtil.parsePatchFile(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testParsePatchFileWithEmpty() {
        List<String> result = DiffHandleUtil.parsePatchFile(new ArrayList<>());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testApplyPatch() {
        List<String> original = Arrays.asList("line 1", "line 2", "line 3");
        List<String> patchContent = Arrays.asList(
                "--- a/file.txt",
                "+++ b/file.txt",
                "@@ -1,3 +1,3 @@",
                " line 1",
                "-line 2",
                "+line 2 modified",
                " line 3"
        );

        List<String> result = DiffHandleUtil.applyPatch(original, patchContent);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("line 2 modified", result.get(1));
    }

    @Test
    void testGetDiffHtml() {
        List<String> diff = Arrays.asList(
                "--- original.txt",
                "+++ revised.txt",
                "@@ -1,3 +1,3 @@",
                " line 1",
                "-line 2",
                "+line 2 modified",
                " line 3"
        );

        String html = DiffHandleUtil.getDiffHtml(List.of(diff));

        assertNotNull(html);
        assertTrue(html.contains("<!DOCTYPE html>"));
        assertTrue(html.contains("Diff2HtmlUI"));
    }

    @Test
    void testGenerateDiffHtml() throws IOException {
        Path htmlFile = tempDir.resolve("output.html");
        List<String> diff = Arrays.asList(
                "--- original.txt",
                "+++ revised.txt",
                "@@ -1,3 +1,3 @@",
                " line 1",
                "-line 2",
                "+line 2 modified",
                " line 3"
        );

        DiffHandleUtil.generateDiffHtml(diff, htmlFile.toAbsolutePath().toString());

        assertTrue(Files.exists(htmlFile));
        String content = Files.readString(htmlFile);
        assertTrue(content.contains("<!DOCTYPE html>"));
    }

    @Test
    void testReadStream() throws IOException {
        String testContent = "test content line 1\ntest content line 2";
        InputStream stream = new ByteArrayInputStream(testContent.getBytes(StandardCharsets.UTF_8));

        String result = DiffHandleUtil.readStream(stream);

        assertNotNull(result);
        assertTrue(result.contains("test content line 1"));
        assertTrue(result.contains("test content line 2"));
    }

    @Test
    void testReadStreamWithNull() throws IOException {
        String result = DiffHandleUtil.readStream(null);
        assertEquals("", result);
    }

    @Test
    void testGetRowMap() {
        String str = "@@ -10,5 +10,5 @@";
        Map<String, Integer> result = DiffHandleUtil.getRowMap(str);

        assertNotNull(result);
        assertEquals(10, result.get("orgRow"));
        assertEquals(5, result.get("orgDel"));
    }

    @Test
    void testGetRowMapWithNonAtLine() {
        String str = "not an @@ line";
        Map<String, Integer> result = DiffHandleUtil.getRowMap(str);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetOrigList() {
        List<String> original = Arrays.asList(" line 0", " line 1", " line 2", " line 3", " line 4");

        List<String> result = DiffHandleUtil.getOrigList(original, 1, 3);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(" line 1", result.get(0));
        assertEquals(" line 3", result.get(2));
    }

    @Test
    void testGetOrigListWithInvalidRange() {
        List<String> original = Arrays.asList(" line 0", " line 1", " line 2");

        List<String> result = DiffHandleUtil.getOrigList(original, 5, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetOrigListWithEmptyList() {
        List<String> result = DiffHandleUtil.getOrigList(new ArrayList<>(), 0, 5);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testInsert() {
        List<String> result = new ArrayList<>();
        result.add("existing");

        List<String> toInsert = Arrays.asList("new1", "new2");
        DiffHandleUtil.insert(result, toInsert);

        assertEquals(3, result.size());
        assertEquals("existing", result.get(0));
        assertEquals("new1", result.get(1));
        assertEquals("new2", result.get(2));
    }

    @Test
    void testDiffStringWithFileNames() {
        List<String> original = Arrays.asList("line 1", "line 2", "line 3");
        List<String> revised = Arrays.asList("line 1", "line 2 modified", "line 3");

        List<String> result = DiffHandleUtil.diffString(original, revised, "myOriginal.txt", "myRevised.txt");

        assertNotNull(result);
        assertTrue(result.stream().anyMatch(s -> s.contains("myOriginal.txt")));
        assertTrue(result.stream().anyMatch(s -> s.contains("myRevised.txt")));
    }

    // ==================== Large File Tests ====================

    @Test
    void testDiffStringWithLargeFile() throws IOException {
        // Test with ~1000+ line files with many modifications
        Path originalFile = tempDir.resolve("large_original.txt");
        Path revisedFile = tempDir.resolve("large_revised.txt");

        // Create large original file
        List<String> originalLines = new ArrayList<>();
        for (int i = 1; i <= 1200; i++) {
            originalLines.add("line " + i + ": original content for testing purposes");
        }
        Files.write(originalFile, originalLines);

        // Create revised file with modifications and additions
        List<String> revisedLines = new ArrayList<>();
        // Lines 1-100: unchanged
        for (int i = 1; i <= 100; i++) {
            revisedLines.add("line " + i + ": original content for testing purposes");
        }
        // Lines 101-200: modified
        for (int i = 101; i <= 200; i++) {
            revisedLines.add("line " + i + ": MODIFIED content - this line was changed");
        }
        // Lines 201-400: unchanged
        for (int i = 201; i <= 400; i++) {
            revisedLines.add("line " + i + ": original content for testing purposes");
        }
        // New lines inserted
        for (int i = 1; i <= 100; i++) {
            revisedLines.add("NEW LINE " + i + ": This is a newly inserted line");
        }
        // Continue with rest
        for (int i = 401; i <= 1200; i++) {
            revisedLines.add("line " + i + ": original content for testing purposes");
        }
        Files.write(revisedFile, revisedLines);

        List<String> result = DiffHandleUtil.diffString(
                originalFile.toAbsolutePath().toString(),
                revisedFile.toAbsolutePath().toString());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        // Verify it contains modification markers
        assertTrue(result.stream().anyMatch(s -> s.contains("MODIFIED")));
        assertTrue(result.stream().anyMatch(s -> s.contains("NEW LINE")));
    }

    @Test
    void testDiffStringWithLargeFileLists() {
        // Create large lists programmatically
        List<String> original = new ArrayList<>();
        List<String> revised = new ArrayList<>();

        // Original: 1000 lines
        for (int i = 0; i < 1000; i++) {
            original.add("Original line " + i + " with some content");
        }

        // Revised: modifications scattered throughout
        for (int i = 0; i < 1000; i++) {
            if (i % 10 == 0) {
                // Every 10th line is modified
                revised.add("MODIFIED line " + i + " with changed content");
            } else if (i >= 500 && i < 600) {
                // Lines 500-599 are completely new
                revised.add("INSERTED line " + i + " brand new content");
            } else {
                revised.add("Original line " + i + " with some content");
            }
        }
        // Add 200 new lines at the end
        for (int i = 1000; i < 1200; i++) {
            revised.add("APPENDED line " + i + " at end of file");
        }

        List<String> result = DiffHandleUtil.diffString(original, revised, "large_original.java", "large_revised.java");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(s -> s.contains("large_original.java")));
        assertTrue(result.stream().anyMatch(s -> s.contains("large_revised.java")));
    }

    // ==================== Multi-File Diff Tests ====================

    @Test
    void testParsePatchFileWithMultipleFiles() throws IOException {
        // Read multi-file diff from test resources
        List<String> multiFileDiff = Arrays.asList(
                "diff --git a/file1.java b/file1.java",
                "index abc123..def456 100644",
                "--- a/file1.java",
                "+++ b/file1.java",
                "@@ -1,5 +1,7 @@",
                " package com.example;",
                " ",
                "-public class File1 {",
                "+public class File1 implements Serializable {",
                "+    private static final long serialVersionUID = 1L;",
                " }",
                "diff --git a/file2.java b/file2.java",
                "index 111222..333444 100644",
                "--- a/file2.java",
                "+++ b/file2.java",
                "@@ -1,10 +1,15 @@",
                " package com.example;",
                " ",
                "+import java.util.*;",
                "+",
                " public class File2 {",
                "-    private String name;",
                "+    private final String name;",
                "+    private final List<String> items;",
                " ",
                "     public File2(String name) {",
                "         this.name = name;",
                "+        this.items = new ArrayList<>();",
                "     }",
                "+",
                "+    public List<String> getItems() {",
                "+        return Collections.unmodifiableList(items);",
                "+    }",
                " }"
        );

        List<String> result = DiffHandleUtil.parsePatchFile(multiFileDiff);

        assertNotNull(result);
        assertEquals(multiFileDiff.size(), result.size());
        // Verify both files are present
        assertTrue(result.stream().anyMatch(s -> s.contains("file1.java")));
        assertTrue(result.stream().anyMatch(s -> s.contains("file2.java")));
    }

    @Test
    void testGetDiffHtmlWithMultipleFiles() {
        List<String> multiFileDiff = Arrays.asList(
                "diff --git a/src/UserService.java b/src/UserService.java",
                "--- a/src/UserService.java",
                "+++ b/src/UserService.java",
                "@@ -1,10 +1,20 @@",
                " public class UserService {",
                "-    public User find(Long id) { return null; }",
                "+    public Optional<User> find(Long id) {",
                "+        return repository.findById(id);",
                "+    }",
                " }",
                "diff --git a/src/UserRepository.java b/src/UserRepository.java",
                "--- a/src/UserRepository.java",
                "+++ b/src/UserRepository.java",
                "@@ -1,5 +1,10 @@",
                " public interface UserRepository {",
                "-    User findById(Long id);",
                "+    Optional<User> findById(Long id);",
                "+    List<User> findAll();",
                "+    User save(User user);",
                "+    void delete(Long id);",
                " }",
                "diff --git a/pom.xml b/pom.xml",
                "--- a/pom.xml",
                "+++ b/pom.xml",
                "@@ -10,6 +10,11 @@",
                "     <dependencies>",
                "+        <dependency>",
                "+            <groupId>org.springframework</groupId>",
                "+            <artifactId>spring-core</artifactId>",
                "+        </dependency>",
                "     </dependencies>"
        );

        String html = DiffHandleUtil.getDiffHtml(List.of(multiFileDiff));

        assertNotNull(html);
        assertTrue(html.contains("<!DOCTYPE html>"));
        assertTrue(html.contains("UserService.java"));
        assertTrue(html.contains("UserRepository.java"));
        assertTrue(html.contains("pom.xml"));
    }

    // ==================== Edge Case Tests ====================

    @Test
    void testDiffStringWithOnlyAdditions() {
        List<String> original = new ArrayList<>();
        List<String> revised = new ArrayList<>();

        // Original is empty, revised has content (all additions)
        for (int i = 0; i < 50; i++) {
            revised.add("New line " + i);
        }

        List<String> result = DiffHandleUtil.diffString(original, revised);

        assertNotNull(result);
        // All lines should be additions - expect exactly 50 added lines
        long addedLines = result.stream().filter(s -> s.startsWith("+") && !s.startsWith("+++")).count();
        assertEquals(50, addedLines, "Expected exactly 50 added lines");
    }

    @Test
    void testDiffStringWithOnlyDeletions() {
        List<String> original = new ArrayList<>();
        List<String> revised = new ArrayList<>();

        // Original has content, revised is empty (all deletions)
        for (int i = 0; i < 50; i++) {
            original.add("Line to delete " + i);
        }

        List<String> result = DiffHandleUtil.diffString(original, revised);

        assertNotNull(result);
        // All lines should be deletions - expect exactly 50 deleted lines
        long deletedLines = result.stream().filter(s -> s.startsWith("-") && !s.startsWith("---")).count();
        assertEquals(50, deletedLines, "Expected exactly 50 deleted lines");
    }

    @Test
    void testDiffStringWithSpecialCharacters() {
        List<String> original = Arrays.asList(
                "Line with \"double quotes\"",
                "Line with 'single quotes'",
                "Line with special: @#$%^&*()",
                "中文测试 Japanese: 日本語",
                "Emoji: 🎉 🚀 ✨",
                "HTML: <div>&amp;</div>",
                "Path: C:\\Users\\test"
        );

        List<String> revised = Arrays.asList(
                "Line with \"modified quotes\"",
                "Line with 'single quotes'",
                "Line with special: @#$%^&*()[]{}",
                "中文测试修改 Japanese: 日本語テスト",
                "Emoji: 🎉 🚀 ✨ 💻 🔥",
                "HTML: <span>&amp;&nbsp;</span>",
                "Path: /home/user/test"
        );

        List<String> result = DiffHandleUtil.diffString(original, revised);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testDiffStringWithEmptyLines() {
        List<String> original = Arrays.asList(
                "Line 1",
                "",
                "",
                "Line 4",
                "",
                "Line 6"
        );

        List<String> revised = Arrays.asList(
                "Line 1",
                "",
                "New line inserted",
                "",
                "Line 4 modified",
                "Line 6"
        );

        List<String> result = DiffHandleUtil.diffString(original, revised);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testDiffStringWithWhitespaceChanges() {
        List<String> original = Arrays.asList(
                "    indented with spaces",
                "\tindented with tab",
                "trailing spaces   ",
                "mixed\t   whitespace"
        );

        List<String> revised = Arrays.asList(
                "        more spaces",
                "    now spaces instead of tab",
                "trailing spaces removed",
                "normalized whitespace"
        );

        List<String> result = DiffHandleUtil.diffString(original, revised);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testDiffStringWithLongLines() {
        // Test with very long lines
        StringBuilder longLine = new StringBuilder();
        for (int i = 0; i < 500; i++) {
            longLine.append("word").append(i).append(" ");
        }

        List<String> original = Arrays.asList(
                "Short line",
                longLine.toString(),
                "Another short line"
        );

        List<String> revised = Arrays.asList(
                "Short line modified",
                longLine + " APPENDED TEXT",
                "Another short line"
        );

        List<String> result = DiffHandleUtil.diffString(original, revised);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testDiffStringWithRepeatedContent() {
        // Test with many identical lines
        List<String> original = new ArrayList<>();
        List<String> revised = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            original.add("Repeated line content");
            revised.add("Repeated line content");
        }
        // Modify one line in the middle
        revised.set(50, "Modified line in the middle");

        List<String> result = DiffHandleUtil.diffString(original, revised);

        assertNotNull(result);
        assertTrue(result.stream().anyMatch(s -> s.contains("Modified line in the middle")));
    }

    @Test
    void testGetDiffHtmlWithBinaryFileNotation() {
        List<String> binaryDiff = Arrays.asList(
                "diff --git a/image.png b/image.png",
                "index abc123..def456 100644",
                "Binary files a/image.png and b/image.png differ",
                "diff --git a/README.md b/README.md",
                "--- a/README.md",
                "+++ b/README.md",
                "@@ -1,3 +1,5 @@",
                " # Project",
                "+",
                "+![Logo](./image.png)",
                " Description here"
        );

        String html = DiffHandleUtil.getDiffHtml(List.of(binaryDiff));

        assertNotNull(html);
        assertTrue(html.contains("<!DOCTYPE html>"));
    }

    @Test
    void testDiffStringWithMixedLineEndings() throws IOException {
        // Create files with different line endings
        Path originalFile = tempDir.resolve("unix_endings.txt");
        Path revisedFile = tempDir.resolve("mixed_endings.txt");

        // Unix line endings
        Files.writeString(originalFile, "Line 1\nLine 2\nLine 3\n");
        // Mixed content
        Files.writeString(revisedFile, "Line 1 modified\nLine 2\nLine 3\nLine 4 added\n");

        List<String> result = DiffHandleUtil.diffString(
                originalFile.toAbsolutePath().toString(),
                revisedFile.toAbsolutePath().toString());

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testGenerateDiffHtmlWithLargeContent() throws IOException {
        Path htmlFile = tempDir.resolve("large_output.html");

        // Create large diff content
        List<String> largeDiff = new ArrayList<>();
        largeDiff.add("--- large_original.txt");
        largeDiff.add("+++ large_revised.txt");
        largeDiff.add("@@ -1,500 +1,600 @@");
        for (int i = 0; i < 500; i++) {
            if (i % 5 == 0) {
                largeDiff.add("-Original line " + i);
                largeDiff.add("+Modified line " + i);
            } else {
                largeDiff.add(" Unchanged line " + i);
            }
        }

        DiffHandleUtil.generateDiffHtml(largeDiff, htmlFile.toAbsolutePath().toString());

        assertTrue(Files.exists(htmlFile));
        String content = Files.readString(htmlFile);
        assertTrue(content.contains("<!DOCTYPE html>"));
        assertTrue(content.length() > 10000); // Should be a large file
    }
}
