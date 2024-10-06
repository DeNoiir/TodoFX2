module com.example.todofx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.databind;

    opens com.example.todofx to javafx.fxml;
    exports com.example.todofx;
    exports com.example.todofx.entity;
    opens com.example.todofx.entity to javafx.fxml;
    exports com.example.todofx.dao;
    opens com.example.todofx.dao to javafx.fxml;
    exports com.example.todofx.ui;
    opens com.example.todofx.ui to javafx.fxml;
    exports com.example.todofx.util;
    opens com.example.todofx.util to javafx.fxml;
    exports com.example.todofx.service;
    opens com.example.todofx.service to javafx.fxml;
}