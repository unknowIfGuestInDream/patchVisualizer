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

    opens com.tlcsdm.patchvisualizer to javafx.fxml;
    opens com.tlcsdm.patchvisualizer.util to javafx.fxml;
    
    exports com.tlcsdm.patchvisualizer;
    exports com.tlcsdm.patchvisualizer.util;
}
