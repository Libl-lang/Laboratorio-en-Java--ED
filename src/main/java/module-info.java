module com.example.grafosjava {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.brunomnsilva.smartgraph;

    opens com.example.grafosjava to javafx.fxml;
    exports com.example.grafosjava;
    exports com.example.grafosjava.model;
    exports com.example.grafosjava.logic;
    exports com.example.grafosjava.ui;
    opens com.example.grafosjava.ui to javafx.fxml;
}
