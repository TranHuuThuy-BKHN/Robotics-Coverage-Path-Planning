package com.robotics.gui;

import com.robotics.decompose.Boustrophedon.*;
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

    private String[] colors = {"yellow", "blue", "pink", "orange", "green", "brown"};

    private HashMap<Label, Cell> mapL2C;
    private HashMap<Cell, Label> mapC2L;

    @Override
    public void start(Stage state) {

        Environment2 e = new Environment2("src/com/robotics/data/Environment 11.txt");
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
        int B = -1;
        for (Cell c : e.getCells())
            if (B < c.getDistance()) B = c.getDistance();
        B = 2 * B + 22;
        drawEnvironment(t);

        GroupTreeBoustrophedonAlgorithm GTBA = new GroupTreeBoustrophedonAlgorithm(t, B);
        ArrayList<Tree> A = GTBA.getWorkingZone();
        for (Tree tree : A) {
            drawElementWorkingZone(tree);
            this.color++;
        }

        CoverageBoustrophedonAlgorithm algorithm = new CoverageBoustrophedonAlgorithm(B, A);
        ArrayList<CoverageBoustrophedonAlgorithm.Path> P = algorithm.coverage().get(0);

        Scene scene = new Scene(grid, SIZE, SIZE);
        state.setScene(scene);
        state.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });
        state.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Cell S = Cell.getChargingStation();
                int timeDelay = 100;
                for (int i = 0; i < P.size(); i++) {
                    ArrayList<Cell> go = S.fromToCell(P.get(i).cells.get(0));
                    for (int j = go.size() - 1; j >= 0; j--) {
                        Label l = mapC2L.get(go.get(j));
                        String style = l.getStyle();
                        l.setStyle("-fx-background-color: purple;");
                        try {
                            Thread.sleep(timeDelay);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        l.setStyle(style);
                    }
                    if (P.get(i).cells.get(0) != S)
                        mapC2L.get(P.get(i).cells.get(0)).setStyle("-fx-background-color: white;");
                    for (int j = 1; j < P.get(i).cells.size(); j++) {
                        Cell c1 = P.get(i).cells.get(j - 1);
                        Cell c2 = P.get(i).cells.get(j);
                        System.out.println("Coverage Path " + (i + 1) + " " + c2.toString());
                        ArrayList<Cell> path = c1.fromToCell(c2);
                        for (int h = path.size() - 1; h >= 0; h--) {
                            Label l = mapC2L.get(path.get(h));
                            String style = l.getStyle();
                            l.setStyle("-fx-background-color: purple;");
                            try {
                                Thread.sleep(timeDelay);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                            l.setStyle(style);
                        }
                        Label label = mapC2L.get(c2);
                        if(c2!=S)
                        label.setStyle("-fx-background-color: white;");
                    }

                    System.out.println("Come back charging station");
                    ArrayList<Cell> back = P.get(i).cells.get(P.get(i).cells.size() - 1).fromToCell(S);
                    for (int j = back.size() - 1; j >= 0; j--) {
                        Label l = mapC2L.get(back.get(j));
                        String style = l.getStyle();
                        l.setStyle("-fx-background-color: purple;");
                        try {
                            Thread.sleep(timeDelay);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        l.setStyle(style);
                    }
                }

                int length = 0;
                for (int i = 0; i < P.size(); i++) {
                    length += P.get(i).length();
                }
                System.out.println("Complete coverage with length " + length + ", number of path " + P.size());

            }
        }).start();

    }

    public static void main(String[] args) {
        launch(args);
    }

    private int color = 0;

    private void drawEnvironment(Tree t) {
        EnvironmentBoustrophedon root = t.getRoot();
        for (Row r : root.getRows()) {
            for (Cell c : r.getCells()) {
                if (c == Cell.getChargingStation()) continue;
                Label l = mapC2L.get(c);
                l.setStyle("-fx-background-color: " + colors[this.color % colors.length] + ";");
            }
        }

        if (t.getChildren() == null || t.getChildren().size() == 0) return;
        System.out.println("Children size " + t.getChildren().size());

        for (Tree child : t.getChildren()) {
            this.color++;
            drawEnvironment(child);
        }
    }

    private void drawElementWorkingZone(Tree t) {
        EnvironmentBoustrophedon root = t.getRoot();
        for (Row r : root.getRows()) {
            for (Cell c : r.getCells()) {
                if (c == Cell.getChargingStation()) continue;
                Label l = mapC2L.get(c);
                l.setStyle("-fx-background-color: " + colors[this.color % colors.length] + ";");
            }
        }
        if (t.getChildren() == null || t.getChildren().size() == 0) return;
        for (Tree child : t.getChildren()) {
            drawElementWorkingZone(child);
        }
    }
}
