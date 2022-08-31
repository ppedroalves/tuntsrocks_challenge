module com.ppedroalves.spreadsheet_challenge {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.api.services.sheets;
    requires com.google.api.client;
    requires com.google.api.client.json.gson;
    requires google.api.client;
    requires com.google.api.client.extensions.jetty.auth;
    requires com.google.api.client.extensions.java6.auth;
    requires com.google.api.client.auth;
    requires com.google.gson;
    requires jdk.httpserver;


    opens com.ppedroalves.spreadsheet_challenge to javafx.fxml;
    exports com.ppedroalves.spreadsheet_challenge;
}