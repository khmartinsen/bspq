/*
Kevin Martinsen
Todo:
parser for BTBBTBT
input from files (separate class with static functions)
use B/b offsets in the drawBS method (should just be Bcount + offset count stuff) (depends on if we include initial condition zone or just start on the right most 0
draw what move was used (BT, T) on the left side
 - also need an array of the moves from each line to each line since they will have different offsets (such as T/BT)

Open files in BSp_q/movepath or manually

If files do not exist, ask if we want to generate them.


Add a styles sheet for the text fields that get added

 */

package bspq.viewer;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class BSViewer extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    private static Scene menuScene;

    public static Scene getMenuScene() { return menuScene; }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("bsviewer.fxml"));
        menuScene = new Scene(fxmlLoader.load());

        primaryStage.setScene(menuScene);
        primaryStage.setTitle("BSPQ Viewer");
        primaryStage.show();
    }
}

