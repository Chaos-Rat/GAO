module com.test.tps {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;

    opens com.test.tps to javafx.fxml;
    exports com.test.tps;
}