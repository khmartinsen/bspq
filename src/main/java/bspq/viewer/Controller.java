package bspq.viewer;

import bspq.tools.BSFileReader;
import bspq.tools.Coset;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class Controller {
    @FXML TextField pEntry;
    @FXML TextField qEntry;
    @FXML VBox tfVbox;
    @FXML Text errorText;
    @FXML Text directoryText;
    @FXML CheckBox pqDirectoryCheckBox;

    private String directory = "../data";
    private String pqDirectory = "";

    @FXML
    private void addRow() {
        TextField textfield = new TextField();
        textfield.setPromptText("File " + (tfVbox.getChildren().size() + 1));
        tfVbox.getChildren().add(textfield);
    }

    @FXML
    private void removeRow(){
        int lastIndex = tfVbox.getChildren().size() - 1;
        if (lastIndex >= 1) {
            tfVbox.getChildren().remove(lastIndex);
        }
    }

    @FXML
    private void generateBSPane() {
        try {
            boolean direction = false; // down default
            int p = Integer.parseInt(pEntry.getText());
            int q = Integer.parseInt(qEntry.getText());

            ArrayList<Coset> cosets = new ArrayList<>();

            for (Node node: tfVbox.getChildren()) {
                TextField textField = (TextField) node;
                String move = textField.getText().trim();
                cosets.add(new Coset(BSFileReader.fileToArray(move + ".ri", directory + pqDirectory), move));
            }

            if (cosets.size() >= 2) {
                direction = cosets.get(1).getDirection();
            }

            BSPane bsPane = new BSPane(p,q,direction);
            bsPane.addCosets(cosets);

            Stage currentStage = (Stage) tfVbox.getScene().getWindow();
            currentStage.setScene(new Scene(bsPane, 800, 600));
            bsPane.drawBS();
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

    @FXML
    private void chooseDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directory = directoryChooser.showDialog(tfVbox.getScene().getWindow()).getAbsolutePath();
        updateDirectoryText();
    }

    @FXML
    private void updateDirectoryText() {
        if (pqDirectoryCheckBox.isSelected()) {
            try {
                int p = Integer.parseInt(pEntry.getText());
                int q = Integer.parseInt(qEntry.getText());

                pqDirectory = "/BS" + p + "_" + q;

                directoryText.setText("Data Dir.: " + directory + pqDirectory);
            }
            catch (NumberFormatException ex) {
                errorText.setText("p and q must be integers");
            }
        }
        else {
            pqDirectory = "";
            directoryText.setText("Data Dir.: " + directory + pqDirectory);
        }
    }
}
