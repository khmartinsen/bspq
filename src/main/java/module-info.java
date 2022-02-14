module bspq.bspqtools {
    requires javafx.controls;
    requires javafx.fxml;


    opens bspq.viewer to javafx.fxml;
    exports bspq.tools;
    exports bspq.viewer;
}