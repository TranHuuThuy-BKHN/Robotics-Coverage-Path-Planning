package com.robotics.gui;

import com.robotics.decompose.*;
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


public class GuiCCP extends Application {

    public static final int SIZE = 700;

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


        Environment e = new Environment("src/com/robotics/data/Environment 2.txt");
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
        Tree t = e.getTree();
        t.printTree();

        GroupTreeAlgorithm group = new GroupTreeAlgorithm(t, 80);
        ArrayList<Tree> A = group.getWorkingZone();
        for (int i = 0; i < A.size(); i++) {
            drawTree(A.get(i), i % colors.length);
        }


        CoverageAlgorithm algorithm = new CoverageAlgorithm(80, A);
        ArrayList<ArrayList<CoverageAlgorithm.Path>> paths = algorithm.coverage();

        ArrayList<CoverageAlgorithm.Path> P = paths.get(0);
//
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

                System.out.println("Complete coverage");
            }
        }).start();
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
