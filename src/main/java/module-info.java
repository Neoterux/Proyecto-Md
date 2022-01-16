/**
 * This project is the Discrete math final project
 */
module project.md {
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.swing;
    requires java.base;// For socket connection
    requires logback.classic;
    requires logback.core;
    requires org.slf4j;

    opens com.neoterux.pmd;
    opens com.neoterux.server.api;
    opens com.neoterux.pmd.controllers;

    exports com.neoterux.pmd to javafx.fxml, javafx.controls;
    exports com.neoterux.pmd.controllers to javafx.controls, javafx.fxml;
    exports com.neoterux.server.api;
}
