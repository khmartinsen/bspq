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

package bspq.bsviewer;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;

public class BSViewer extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("bsviewer.fxml"));
        Scene menuScene = new Scene(fxmlLoader.load());

        primaryStage.setScene(menuScene);
        primaryStage.show();
    }



    private static ArrayList<String> pathParse(String path) {
        // When we encounter a t/T, we split and add to the array list
        ArrayList<String> movesList = new ArrayList<String>(path.length()/2); // too generous?
        int start = 0;

        for (int i = 0; i < path.length(); i ++) {
            if (path.charAt(i) == 't' || path.charAt(i) == 'T') {
                movesList.add(path.substring(start,i + 1));
                start = i + 1;
            }
        }

        return movesList;
    }

    private static int moveOffset(String move) {
        // count number of b's or B's
        return 0;
    }
}

