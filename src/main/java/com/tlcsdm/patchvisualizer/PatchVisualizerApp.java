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
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
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
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Main JavaFX application for visualizing diff and patch files.
 *
 * @author unknowIfGuestInDream
 */
public class PatchVisualizerApp extends Application {

    private static final String BUNDLE_BASE_NAME = "com.tlcsdm.patchvisualizer.i18n.messages";

    private WebView webView;
    private TabPane tabPane;
    private Stage primaryStage;
    private TextField originalFileField;
    private TextField revisedFileField;
    private ResourceBundle bundle;
    private Locale currentLocale;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.currentLocale = Locale.getDefault();
        this.bundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, currentLocale);

        initializeUI();
    }

    private void initializeUI() {
        primaryStage.setTitle(bundle.getString("app.title"));

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

        // Calculate window size based on screen dimensions (80% of screen size)
        javafx.geometry.Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double width = screenBounds.getWidth() * 0.8;
        double height = screenBounds.getHeight() * 0.8;

        Scene scene = new Scene(root, width, height);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void changeLanguage(Locale locale) {
        this.currentLocale = locale;
        this.bundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale);

        // Rebuild UI with new locale
        Scene oldScene = primaryStage.getScene();
        double width = oldScene.getWidth();
        double height = oldScene.getHeight();

        BorderPane root = new BorderPane();
        MenuBar menuBar = createMenuBar();
        root.setTop(menuBar);

        tabPane = new TabPane();
        Tab compareTab = createCompareTab();
        tabPane.getTabs().add(compareTab);
        Tab importTab = createImportTab();
        tabPane.getTabs().add(importTab);
        root.setCenter(tabPane);

        Scene scene = new Scene(root, width, height);
        primaryStage.setScene(scene);
        primaryStage.setTitle(bundle.getString("app.title"));
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        // File menu
        Menu fileMenu = new Menu(bundle.getString("menu.file"));
        MenuItem openOriginal = new MenuItem(bundle.getString("menu.file.openOriginal"));
        openOriginal.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        openOriginal.setOnAction(e -> selectOriginalFile());

        MenuItem openRevised = new MenuItem(bundle.getString("menu.file.openRevised"));
        openRevised.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
        openRevised.setOnAction(e -> selectRevisedFile());

        MenuItem importDiff = new MenuItem(bundle.getString("menu.file.importDiff"));
        importDiff.setAccelerator(new KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN));
        importDiff.setOnAction(e -> importDiffFile());

        MenuItem exit = new MenuItem(bundle.getString("menu.file.exit"));
        exit.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));
        exit.setOnAction(e -> primaryStage.close());

        fileMenu.getItems().addAll(openOriginal, openRevised, new SeparatorMenuItem(), importDiff,
                new SeparatorMenuItem(), exit);

        // Language menu
        Menu languageMenu = new Menu(bundle.getString("menu.language"));
        ToggleGroup languageGroup = new ToggleGroup();

        RadioMenuItem englishItem = new RadioMenuItem(bundle.getString("menu.language.english"));
        englishItem.setToggleGroup(languageGroup);
        englishItem.setSelected(currentLocale.getLanguage().equals("en"));
        englishItem.setOnAction(e -> changeLanguage(Locale.ENGLISH));

        RadioMenuItem chineseItem = new RadioMenuItem(bundle.getString("menu.language.chinese"));
        chineseItem.setToggleGroup(languageGroup);
        chineseItem.setSelected(currentLocale.getLanguage().equals("zh"));
        chineseItem.setOnAction(e -> changeLanguage(Locale.CHINESE));

        RadioMenuItem japaneseItem = new RadioMenuItem(bundle.getString("menu.language.japanese"));
        japaneseItem.setToggleGroup(languageGroup);
        japaneseItem.setSelected(currentLocale.getLanguage().equals("ja"));
        japaneseItem.setOnAction(e -> changeLanguage(Locale.JAPANESE));

        languageMenu.getItems().addAll(englishItem, chineseItem, japaneseItem);

        // Help menu
        Menu helpMenu = new Menu(bundle.getString("menu.help"));
        MenuItem about = new MenuItem(bundle.getString("menu.help.about"));
        about.setOnAction(e -> showAboutDialog());
        helpMenu.getItems().add(about);

        menuBar.getMenus().addAll(fileMenu, languageMenu, helpMenu);
        return menuBar;
    }

    private Tab createCompareTab() {
        Tab tab = new Tab(bundle.getString("tab.compare"));
        tab.setClosable(false);

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        // File selection
        GridPane fileGrid = new GridPane();
        fileGrid.setHgap(10);
        fileGrid.setVgap(10);

        Label originalLabel = new Label(bundle.getString("label.originalFile"));
        originalFileField = new TextField();
        originalFileField.setEditable(false);
        originalFileField.setPrefWidth(500);
        Button originalButton = new Button(bundle.getString("button.browse"));
        originalButton.setOnAction(e -> selectOriginalFile());

        Label revisedLabel = new Label(bundle.getString("label.revisedFile"));
        revisedFileField = new TextField();
        revisedFileField.setEditable(false);
        revisedFileField.setPrefWidth(500);
        Button revisedButton = new Button(bundle.getString("button.browse"));
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
        Button compareButton = new Button(bundle.getString("button.compare"));
        compareButton.setOnAction(e -> compareFiles());
        Button clearButton = new Button(bundle.getString("button.clear"));
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
        Tab tab = new Tab(bundle.getString("tab.import"));
        tab.setClosable(false);

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        // Import button
        HBox importBox = new HBox(10);
        Button importButton = new Button(bundle.getString("button.importFile"));
        importButton.setOnAction(e -> importDiffFile());
        Label helpLabel = new Label(bundle.getString("label.help"));
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
        fileChooser.setTitle(bundle.getString("fileChooser.selectOriginal"));
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            originalFileField.setText(file.getAbsolutePath());
        }
    }

    private void selectRevisedFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(bundle.getString("fileChooser.selectRevised"));
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            revisedFileField.setText(file.getAbsolutePath());
        }
    }

    private void compareFiles() {
        String originalPath = originalFileField.getText();
        String revisedPath = revisedFileField.getText();

        if (originalPath.isEmpty() || revisedPath.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, bundle.getString("message.warning"),
                    bundle.getString("message.selectBothFiles"));
            return;
        }

        try {
            List<String> diffResult = DiffHandleUtil.diffString(originalPath, revisedPath);
            String html = DiffHandleUtil.getDiffHtml(List.of(diffResult));
            webView.getEngine().loadContent(html);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, bundle.getString("message.error"),
                    MessageFormat.format(bundle.getString("message.failedCompare"), e.getMessage()));
        }
    }

    private void clearComparison() {
        originalFileField.clear();
        revisedFileField.clear();
        webView.getEngine().loadContent("");
    }

    private void importDiffFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(bundle.getString("fileChooser.importDiff"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(bundle.getString("fileChooser.filterDiff"), "*.diff", "*.patch"),
                new FileChooser.ExtensionFilter(bundle.getString("fileChooser.filterAll"), "*.*"));
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
                showAlert(Alert.AlertType.ERROR, bundle.getString("message.error"),
                        MessageFormat.format(bundle.getString("message.failedRead"), e.getMessage()));
            }
        }
    }

    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(bundle.getString("app.about.title"));
        alert.setHeaderText(bundle.getString("app.about.header"));
        alert.setContentText(bundle.getString("app.about.content"));
        alert.showAndWait();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
