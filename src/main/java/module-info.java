module com.example.turinmachine {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.example.turinmachine to javafx.fxml;
    exports com.example.turinmachine;
}