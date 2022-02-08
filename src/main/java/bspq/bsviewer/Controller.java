package bspq.bsviewer;

import bspq.bspqtools.BSFileReader;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller {
    @FXML TextField pEntry;
    @FXML TextField qEntry;
    @FXML VBox tfVbox;
    @FXML Text errorText;
    // add Scene bsPaneScene so we can swap back and forth


    @FXML
    private void addRow() {
        TextField textfield = new TextField();
        textfield.setPromptText("File " + (tfVbox.getChildren().size() + 1));
        tfVbox.getChildren().add(textfield);
    }

    @FXML
    private void removeRow(){
        int lastIndex = tfVbox.getChildren().size() - 1;
        if (lastIndex >= 0) {
            tfVbox.getChildren().remove(lastIndex);
        }
    }

    @FXML
    private void generateBSPane () {
        try {
            int p = Integer.parseInt(pEntry.getText());
            int q = Integer.parseInt(qEntry.getText());
            BSPane bsPane = new BSPane(p,q,false);

            for (Node node: tfVbox.getChildren()) {
                TextField textField = (TextField) node;
                bsPane.addCoset(BSFileReader.fileToArray(p, q, textField.getText().trim()), textField.getText().trim());
            }

            Stage currentStage = (Stage) tfVbox.getScene().getWindow();
            currentStage.setScene(new Scene(bsPane, 800, 600));
            bsPane.drawBS();

            //Scene mainScene = new Scene(bsPane, 700, 500);
            //primaryStage.setScene(mainScene);
        }
        catch (IOException ex) {
            errorText.setText(ex.getMessage());
        }
        catch (NumberFormatException ex) {
            errorText.setText("p and q must be integers");
        }
        catch (NullPointerException ex) { // custom file not found exception
            errorText.setText("file(s) not found");
        }
    }
}
