/**
 * This project is the Discrete math final project
 */
module project.md {
    requires javafx.fxml;
    requires javafx.controls;
    requires java.base;// For socket connection
    requires ch.qos.logback.classic;
    requires ch.qos.logback.core;
    requires org.slf4j;
    
    opens com.neoterux.pmd;
    opens com.neoterux.pmd.components;
    opens com.neoterux.pmd.controllers;
    
    exports com.neoterux.pmd to javafx.fxml, javafx.controls;
    exports com.neoterux.pmd.components to javafx.controls;
    exports com.neoterux.pmd.controllers to javafx.fxml, javafx.controls;
    exports com.neoterux.pmd.utils to javafx.controls, javafx.fxml;
    exports com.neoterux.server.api;
}
