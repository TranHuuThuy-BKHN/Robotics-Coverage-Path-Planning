package com.robotics.gui;

import com.robotics.decompose.Boustrophedon.Environment2;
import com.robotics.decompose.Boustrophedon.EnvironmentBoustrophedon;
import com.robotics.decompose.Boustrophedon.Row;
import com.robotics.decompose.Boustrophedon.Tree;
import com.robotics.decompose.Cell;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.ArrayList;
import java.util.HashMap;


public class GuiCCP2 extends Application {

    public static final int SIZE = 700;

    private Environment2 e;

    public void setE(Environment2 e) {
        this.e = e;
    }

    private Label labels[];
    private GridPane grid;

    private String[] colors = {"yellow", "blue", "pink", "orange", "green", "red", "brown"};

    private HashMap<Label, Cell> mapL2C;
    private HashMap<Cell, Label> mapC2L;

    @Override
    public void start(Stage state) {


        Environment2 e = new Environment2("src/com/robotics/data/Environment 7.txt");
        int length = (int) Math.sqrt(e.getCells().size());

        this.labels = new Label[length * length];
        mapL2C = new HashMap<>();
        mapC2L = new HashMap<>();

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                Cell c = e.getCells().get(i * length + j);
                labels[i * length + j] = new Label();
                labels[i * length + j].setPrefSize(SIZE / length, SIZE / length);
                labels[i * length + j].setStyle("-fx-border-color: white;");
                mapL2C.put(labels[i * length + j], c);
                mapC2L.put(c, labels[i * length + j]);
            }
        }

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
                    labels[i * length + j].setAlignment(Pos.CENTER);
                    labels[i * length + j].setStyle("-fx-background-color: red;");
                    labels[i * length + j].setText("S");
                } else {
                    labels[i * length + j].setAlignment(Pos.CENTER);
                    labels[i * length + j].setText(String.valueOf(c.getDistance()));
                }
                grid.add(labels[i * length + j], j, i);
            }
        }

        // draw working zone
        Tree t = e.getTreeBoustrophedon();
        t.modifyTree(t);
        t.modify2(t);
        t.printTree();
        drawTree(t, 0);

        Scene scene = new Scene(grid, SIZE, SIZE);
        state.setScene(scene);
        state.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });
        state.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void drawTree(Tree t, int color) {
        EnvironmentBoustrophedon root = t.getRoot();
        for (Row r : root.getRows()) {
            for (Cell c : r.getCells()) {
                Label l = mapC2L.get(c);
                l.setStyle("-fx-background-color: " + colors[color % colors.length] + ";");
            }
        }

        if (t.getChildren() == null || t.getChildren().size() == 0) return;
        System.out.println("Children size " + t.getChildren().size());

        for (Tree child : t.getChildren()) {
            color++;
            drawTree(child, color);
        }
    }
}
