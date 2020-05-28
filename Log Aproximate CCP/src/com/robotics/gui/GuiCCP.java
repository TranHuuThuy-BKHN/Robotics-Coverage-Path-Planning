package com.robotics.gui;

import com.robotics.decompose.*;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;


public class GuiCCP extends Application {

    public static final int SIZE = 500;

    private Environment e;

    public void setE(Environment e) {
        this.e = e;
    }

    private Label labels[];
    private GridPane grid;

    private String[] colors = {"yellow", "blue", "pink", "orange", "green", "red"};

    private HashMap<Label, Cell> mapL2C;
    private HashMap<Cell, Label> mapC2L;

    @Override
    public void start(Stage state) {
        int row = 19, col = 10;
        int length = 20;
        ArrayList<Cell> cells = new ArrayList<>();
        this.labels = new Label[length * length];
        mapL2C = new HashMap<>();
        mapC2L = new HashMap<>();

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                Cell c = new Cell(j - col, row - i, false);
                if (i >= 5 && i <= 9 && j >= 5 && j <= 7) c.setObtacle(true);
                if (i >= 10 && i <= 14 && j >= 14 && j <= 16) c.setObtacle(true);
                cells.add(c);

                labels[i * length + j] = new Label();
                labels[i * length + j].setPrefSize(SIZE / length, SIZE / length);
                labels[i * length + j].setStyle("-fx-border-color: black;");
                mapL2C.put(labels[i * length + j], c);
                mapC2L.put(c, labels[i * length + j]);
            }
        }

        e = new Environment(cells);

        // draw environment
        grid = new GridPane();
        grid.setMinSize(SIZE, SIZE);
        grid.setMaxSize(SIZE, SIZE);

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                Cell c = mapL2C.get(labels[i * length + j]);
                if (c.isObtacle())
                    labels[i * length + j].setStyle("-fx-background-color: black;");
                else if (c.getDistance() == 0) {
                    labels[i * length + j].setStyle("-fx-background-color: red;");
                } else {
                    labels[i * length + j].setAlignment(Pos.CENTER);
                    labels[i * length + j].setText(String.valueOf(c.getDistance()));
                }
                grid.add(labels[i * length + j], j, i);
            }
        }

        // draw working zone
        Tree t = e.getTree();
        GroupTreeAlgorithm group = new GroupTreeAlgorithm(t, 80);
        ArrayList<Tree> A = group.getWorkingZone();
        for (int i = 0; i < A.size(); i++) {
            drawTree(A.get(i), i % colors.length);
        }


        Scene scene = new Scene(grid, SIZE, SIZE);
        state.setScene(scene);
        state.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void drawTree(Tree t, int color) {
        CcEnvironment e = t.getRoot();
        for (CcEnvironment.Contour contour : e.getContours()) {
            ArrayList<Cell> cells = contour.getCells();
            for (Cell c : cells)
                mapC2L.get(c).setStyle("-fx-background-color: " + colors[color] + ";");
        }
        if (t.getChildren() != null && t.getChildren().size() != 0) {
            for (Tree child : t.getChildren())
                drawTree(child, color);
        }
    }
}
