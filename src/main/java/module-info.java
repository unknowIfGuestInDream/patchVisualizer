/*
 * MIT License
 *
 * Copyright (c) 2026 unknowIfGuestInDream
 */

module com.tlcsdm.patchvisualizer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires io.github.javadiffutils;
    requires com.dlsc.preferencesfx;
    requires org.slf4j;
    requires java.prefs;

    opens com.tlcsdm.patchvisualizer to javafx.fxml, com.dlsc.preferencesfx;
    opens com.tlcsdm.patchvisualizer.util to javafx.fxml;
    opens com.tlcsdm.patchvisualizer.preferences to com.dlsc.preferencesfx;
    
    exports com.tlcsdm.patchvisualizer;
    exports com.tlcsdm.patchvisualizer.util;
    exports com.tlcsdm.patchvisualizer.preferences;
}
