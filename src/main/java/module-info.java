module com.salmon.distancevector {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires guru.nidi.graphviz;
    requires org.slf4j;
    requires svgSalamander;
    requires com.fasterxml.jackson.core;
    requires java.naming;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;
    requires org.jetbrains.annotations;

    opens Controller to javafx.fxml;
    exports Controller;
    exports Model;
    opens Model to javafx.fxml;
    exports Model.Data;
    opens Model.Data to javafx.fxml;
}