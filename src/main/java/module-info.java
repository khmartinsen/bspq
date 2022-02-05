module bspq.bspqtools {
    requires javafx.controls;
    requires javafx.fxml;


    opens bspq.bsviewer to javafx.fxml;
    exports bspq.bspqtools;
    exports bspq.bsviewer;
}