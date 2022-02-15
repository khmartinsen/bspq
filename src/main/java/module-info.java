module bspq.viewer {
    requires javafx.controls;
    requires javafx.fxml;


    opens bspq.viewer to javafx.fxml;
    exports bspq.viewer;
}