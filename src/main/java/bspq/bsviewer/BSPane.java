package bspq.bsviewer;

import bspq.bspqtools.Coset;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class BSPane extends BorderPane {
    final int p;
    final int q;

    double firstTickSpacing = 50; // since q > p, this is the minimal for tick spacing
    double tickSpacingScaling; // q/p when going up and p/q going down
    double firstYLocation;
    double tickSize = 5; // height above line in pixels
    double lineSpacing = 100; // space between lines (maybe should be negative for when going up
    boolean showVerticalLine;
    boolean visibleIndex = false; // display absolute count with relative count

    // array and p,q modulus values
    int relativeIndex = 0;
    boolean direction = false; // true up, false down (change to enum)
    int currentMod;
    int nextMod; // the mod of the next line (replace with an if statement in the drawBS method?

    ArrayList<Coset> cosets = new ArrayList<>();

    /*
    ArrayList<int[]> bsArrays = new ArrayList<>();
    ArrayList<Pair<Integer,Integer>> zeroIndex = new ArrayList<>();
    ArrayList<String> movesString = new ArrayList<>(); // will be bsArray.length - 1
    ArrayList<Integer> movesOffset = new ArrayList<>(); // will be bsArray.length - 1\
    */

    Pane centerPane = new Pane();

    public BSPane(final int p, final int q, boolean direction) {
        this.p = p;
        this.q = q;
        this.direction = direction;

        if (direction) {
            lineSpacing = -100;
            currentMod = q; // if the sheet is going up we draw lines down every p
            nextMod = p;
            tickSpacingScaling = (double) q / p;
        }
        else {
            currentMod = p;
            nextMod = q;
            tickSpacingScaling = (double) p / q;
        }

        HBox topMenu = new HBox();

        setTop(topMenu);
        setCenter(centerPane);

        Text bsInfo = new Text("p: " + p + "\nq: " + q);
        CheckBox indicesBox = new CheckBox("Show Indices");
        TextField indexField = new TextField("Jump to index x");

        topMenu.setSpacing(30);
        topMenu.setAlignment(Pos.CENTER);
        topMenu.getChildren().addAll(bsInfo, indicesBox, indexField);

        // handlers and listeners

        indicesBox.setOnAction(event -> {
            setVisibleIndex(indicesBox.isSelected());
            requestFocus();
        });

        indexField.setOnAction(e -> {
            try {
                setIndex(Integer.parseInt(indexField.getText()));
                requestFocus();
            }
            catch (NumberFormatException ex) {
                indexField.setText("");
                indexField.setPromptText("Invalid Number");
                requestFocus();
            }
        });

        // zoom in by changing the tick spacing with scroll wheel
        setOnScroll(event -> {
            if (event.getDeltaY() > 0) incSpacing();
            else if (event.getDeltaY() < 0) decSpacing();
        });

        setOnKeyPressed(event -> {
            //bsPane.requestFocus();
            switch (event.getCode()) {
                case RIGHT:
                    incIndex();
                    break;
                case LEFT:
                    decIndex();
                    break;
                case R:
                    resetSpacing();
                    break;
            }
        });

        widthProperty().addListener(e -> drawBS());
        //drawBS();
    }

    public void drawBS() {
        centerPane.getChildren().clear();

        setTranslateX(30); // change based on max label size like BBT

        if (direction) {
            // if up we start drawing the line towards the bottom
            firstYLocation = getHeight() - 100;
        }
        else {
            firstYLocation = 100;
        }


        double lineY = firstYLocation;
        double tickSpacing = firstTickSpacing;
        //double zeroXPosition = widthProperty().doubleValue() / 2;
        //int arrayIndex = relativeIndex + cosets.get(0).getFirstZero();
        int arrayIndex = relativeIndex + cosets.get(0).getFirstZero();;
        int indexOffset = 0;
        double tickOffsetX = 0.0; // a non-negative value


        showVerticalLine = false;

        for (Coset coset: cosets) { // change to int i and use zeroLocation and other arrays
            arrayIndex = relativeIndex + coset.getFirstZero() - indexOffset;
            drawLines(lineY, tickSpacing, tickOffsetX, arrayIndex, coset);

            lineY += lineSpacing;
            double oldTickSpacing = tickSpacing;
            tickSpacing *= tickSpacingScaling;
            showVerticalLine = true; // true after the first line is drawn

            indexOffset = + (int)Math.ceil((coset.getLastMoveOffset() * oldTickSpacing) / tickSpacing) ;
            System.out.println(indexOffset);
            tickOffsetX = ((coset.getLastMoveOffset() * oldTickSpacing) + (nextMod * tickSpacing)) % tickSpacing;
            //System.out.println(coset.getLastMoveOffset() + " " + tickOffsetX);
            if (tickOffsetX < 0.0) tickOffsetX += nextMod * tickSpacing;


            //double currentTickOffset = (arrayIndex % nextMod) * tickSpacing; // previous line spacing for a horobrick in pixels
            //arrayIndex = (arrayIndex / nextMod) * currentMod + (int) Math.ceil(((arrayIndex % nextMod) * currentMod) / (double) nextMod);
            //zeroXPosition = zeroXPosition + (arrayIndex % currentMod) * tickSpacing - currentTickOffset;
        }
    }

    private void drawLines(double lineY, double tickSpacing, double tickOffsetX, int arrayIndex, Coset coset) {
        Line line = new Line(0, lineY, widthProperty().doubleValue(), lineY);
        int[] coordinates = coset.getCoordinates();
        Text move = new Text(coset.getMoves().get(coset.getMoves().size() - 1));
        move.setY(lineY + (lineSpacing / 2));
        centerPane.getChildren().addAll(line, move);

        for (double i = tickOffsetX; i < widthProperty().doubleValue(); i += tickSpacing) {
            Line tick = new Line(i, lineY - tickSize, i, lineY + tickSize);
            Text number = new Text(String.valueOf(coordinates[arrayIndex]));
            number.setX(i - number.getLayoutBounds().getWidth() / 2);
            number.setY(lineY - 10);
            centerPane.getChildren().addAll(tick, number);

            // throw everything above in this if statement and offset the numbers to the left or right for visibility?
            if ((arrayIndex - coset.getFirstZero()) % nextMod == 0 && showVerticalLine) { // connecting vertical lines
                Line edge = new Line(i, lineY, i, lineY - lineSpacing); // line goes the opposite way the lines are being built
                edge.setOpacity(.6);
                centerPane.getChildren().add(edge);
            }

            if (visibleIndex) {
                Text absCount = new Text(String.valueOf(arrayIndex - coset.getFirstZero()));
                absCount.setX(i - absCount.getLayoutBounds().getWidth() / 2);
                absCount.setY(lineY + 20); // put index under the tick marks
                centerPane.getChildren().add(absCount);
            }
            arrayIndex++;
        }
    }

    // ** Visual change control methods **

    public void incSpacing() {
        firstTickSpacing += 5;
        drawBS();
    }

    public void decSpacing() {
        if (firstTickSpacing - 5 > 5) {
            firstTickSpacing -= 5;
            drawBS();
        }
    }

    public void resetSpacing() {
        firstTickSpacing = 50;
        drawBS();
    }

    public void setVisibleIndex(boolean visibleIndex) {
        this.visibleIndex = visibleIndex;
        drawBS();
    }

    public void incIndex() {
        relativeIndex += 1;
        drawBS();
    }

    public void decIndex() {
        //if (indexStart - 1 >= cosets.get(0).getFirstZero()) {
            relativeIndex -= 1;
            drawBS();
        //}
    }

    public void setIndex(int x) {
        int indexOffset = cosets.get(0).getFirstZero();
        if (indexOffset + x >= 0 && x < cosets.get(0).getCoordinates().length - indexOffset) {
            relativeIndex = x;
            drawBS();
        }
    }

    public void resetStart() {
        relativeIndex = 0;
        drawBS();
    }

    public void addCoset(int[] rawArray, String path) {
        // change to cosets calling the BSFileReader methods?
        cosets.add(new Coset(rawArray, path));
    }
}