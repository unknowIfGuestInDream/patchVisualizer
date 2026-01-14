/*
 * MIT License
 *
 * Copyright (c) 2026 梦里不知身是客
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

import com.tlcsdm.patchvisualizer.util.DiffHandleUtil;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * Main JavaFX application for visualizing diff and patch files.
 *
 * @author unknowIfGuestInDream
 */
public class PatchVisualizerApp extends Application {

    private WebView webView;
    private TabPane tabPane;
    private Stage primaryStage;
    private TextField originalFileField;
    private TextField revisedFileField;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Patch Visualizer");

        BorderPane root = new BorderPane();

        // Create menu bar
        MenuBar menuBar = createMenuBar();
        root.setTop(menuBar);

        // Create main content
        tabPane = new TabPane();

        // File comparison tab
        Tab compareTab = createCompareTab();
        tabPane.getTabs().add(compareTab);

        // Import tab (for diff/patch files)
        Tab importTab = createImportTab();
        tabPane.getTabs().add(importTab);

        root.setCenter(tabPane);

        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        // File menu
        Menu fileMenu = new Menu("File");
        MenuItem openOriginal = new MenuItem("Open Original File...");
        openOriginal.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        openOriginal.setOnAction(e -> selectOriginalFile());

        MenuItem openRevised = new MenuItem("Open Revised File...");
        openRevised.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
        openRevised.setOnAction(e -> selectRevisedFile());

        MenuItem importDiff = new MenuItem("Import Diff/Patch File...");
        importDiff.setAccelerator(new KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN));
        importDiff.setOnAction(e -> importDiffFile());

        MenuItem exit = new MenuItem("Exit");
        exit.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));
        exit.setOnAction(e -> primaryStage.close());

        fileMenu.getItems().addAll(openOriginal, openRevised, new SeparatorMenuItem(), importDiff,
                new SeparatorMenuItem(), exit);

        // Help menu
        Menu helpMenu = new Menu("Help");
        MenuItem about = new MenuItem("About");
        about.setOnAction(e -> showAboutDialog());
        helpMenu.getItems().add(about);

        menuBar.getMenus().addAll(fileMenu, helpMenu);
        return menuBar;
    }

    private Tab createCompareTab() {
        Tab tab = new Tab("Compare Files");
        tab.setClosable(false);

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        // File selection
        GridPane fileGrid = new GridPane();
        fileGrid.setHgap(10);
        fileGrid.setVgap(10);

        Label originalLabel = new Label("Original File:");
        originalFileField = new TextField();
        originalFileField.setEditable(false);
        originalFileField.setPrefWidth(500);
        Button originalButton = new Button("Browse...");
        originalButton.setOnAction(e -> selectOriginalFile());

        Label revisedLabel = new Label("Revised File:");
        revisedFileField = new TextField();
        revisedFileField.setEditable(false);
        revisedFileField.setPrefWidth(500);
        Button revisedButton = new Button("Browse...");
        revisedButton.setOnAction(e -> selectRevisedFile());

        fileGrid.add(originalLabel, 0, 0);
        fileGrid.add(originalFileField, 1, 0);
        fileGrid.add(originalButton, 2, 0);
        fileGrid.add(revisedLabel, 0, 1);
        fileGrid.add(revisedFileField, 1, 1);
        fileGrid.add(revisedButton, 2, 1);

        GridPane.setHgrow(originalFileField, Priority.ALWAYS);
        GridPane.setHgrow(revisedFileField, Priority.ALWAYS);

        // Compare button
        HBox buttonBox = new HBox(10);
        Button compareButton = new Button("Compare");
        compareButton.setOnAction(e -> compareFiles());
        Button clearButton = new Button("Clear");
        clearButton.setOnAction(e -> clearComparison());
        buttonBox.getChildren().addAll(compareButton, clearButton);

        // WebView for displaying diff
        webView = new WebView();
        VBox.setVgrow(webView, Priority.ALWAYS);

        content.getChildren().addAll(fileGrid, buttonBox, webView);

        tab.setContent(content);
        return tab;
    }

    private Tab createImportTab() {
        Tab tab = new Tab("Import Diff/Patch");
        tab.setClosable(false);

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        // Import button
        HBox importBox = new HBox(10);
        Button importButton = new Button("Import Diff/Patch File");
        importButton.setOnAction(e -> importDiffFile());
        Label helpLabel = new Label("Supports .diff, .patch files (unified diff format)");
        helpLabel.setStyle("-fx-text-fill: gray;");
        importBox.getChildren().addAll(importButton, helpLabel);

        // WebView for displaying imported diff
        WebView importWebView = new WebView();
        importWebView.setId("importWebView");
        VBox.setVgrow(importWebView, Priority.ALWAYS);

        content.getChildren().addAll(importBox, importWebView);

        tab.setContent(content);
        return tab;
    }

    private void selectOriginalFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Original File");
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            originalFileField.setText(file.getAbsolutePath());
        }
    }

    private void selectRevisedFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Revised File");
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            revisedFileField.setText(file.getAbsolutePath());
        }
    }

    private void compareFiles() {
        String originalPath = originalFileField.getText();
        String revisedPath = revisedFileField.getText();

        if (originalPath.isEmpty() || revisedPath.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select both original and revised files.");
            return;
        }

        try {
            List<String> diffResult = DiffHandleUtil.diffString(originalPath, revisedPath);
            String html = DiffHandleUtil.getDiffHtml(List.of(diffResult));
            webView.getEngine().loadContent(html);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to compare files: " + e.getMessage());
        }
    }

    private void clearComparison() {
        originalFileField.clear();
        revisedFileField.clear();
        webView.getEngine().loadContent("");
    }

    private void importDiffFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Diff/Patch File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Diff Files", "*.diff", "*.patch"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        File file = fileChooser.showOpenDialog(primaryStage);

        if (file != null) {
            try {
                List<String> content = Files.readAllLines(file.toPath());
                String html = DiffHandleUtil.getDiffHtml(List.of(content));

                // Switch to import tab and display
                tabPane.getSelectionModel().select(1);
                Tab importTab = tabPane.getTabs().get(1);
                VBox vbox = (VBox) importTab.getContent();
                WebView importWebView = (WebView) vbox.getChildren().stream()
                        .filter(node -> node instanceof WebView)
                        .findFirst()
                        .orElse(null);

                if (importWebView != null) {
                    importWebView.getEngine().loadContent(html);
                }
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to read file: " + e.getMessage());
            }
        }
    }

    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Patch Visualizer");
        alert.setHeaderText("Patch Visualizer v1.0.0");
        alert.setContentText("A JavaFX application for visualizing diff and patch files.\n\n"
                + "Features:\n"
                + "- Compare two files side by side\n"
                + "- Import and visualize diff/patch files\n"
                + "- Support for unified diff format\n\n"
                + "Built with Java 21, JavaFX, and java-diff-utils.");
        alert.showAndWait();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
