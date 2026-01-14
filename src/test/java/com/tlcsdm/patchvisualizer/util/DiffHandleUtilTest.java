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
}
