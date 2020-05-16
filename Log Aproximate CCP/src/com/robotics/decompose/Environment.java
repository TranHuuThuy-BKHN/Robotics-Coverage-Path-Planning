package com.robotics.decompose;

import com.robotics.decompose.CcEnvironment.TreeContour;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;

public class Environment {
    private ArrayList<Cell> cells;

    private Tree tree;

    private TreeContour treeContour;

    public Environment(){
    }

    public Environment(ArrayList<Cell> cells) {
        this.cells = cells;
        this.treeContour = new TreeContour();
        // tính khoảng các các cell tới trạm sạc, sử dụng BFS
        Cell s = Cell.mapCells.get(new Key(0, 0)); // trạm sạc
        s.setDistance(0);
        LinkedList<Cell> queue = new LinkedList<>();
        queue.push(s);
        while (!queue.isEmpty()) {
            Cell c = queue.poll(); // xóa và trả về phân tử đầu tiên
            int x = c.x, y = c.y;
            int xs[] = {x, x, x - 1, x + 1}, ys[] = {y - 1, y + 1, y, y};
            for (int i = 0; i < xs.length; i++) {
                Cell nbh = Cell.mapCells.get(new Key(xs[i], ys[i]));
                if (nbh != null && !nbh.isObtacle() && nbh.getDistance() == -1) {
                    nbh.setDistance(c.getDistance() + 1);
                    queue.add(nbh);
                } else if (nbh != null && nbh.isObtacle()) {
                    nbh.setDistance(-1);
                }
            }
        }
    }

    public void setTreeContour() {
        this.treeContour = this.treeContour.findAllContour(this.cells);
    }

    public Tree getTree(TreeContour treeContour) {
        if (tree == null) {
            tree = convertTree(treeContour);
        }
        return tree;
    }

    private Tree convertTree(TreeContour treeContour)  {
        //Duyet TreeContour
        Tree tree = new Tree();
        CcEnvironment root = new CcEnvironment();
        ArrayList<CcEnvironment.Contour> firstContour = new ArrayList<CcEnvironment.Contour>();
        firstContour.add(treeContour.getKeyContour());
        root.setContours(firstContour);
        while (treeContour.getChildren() != null){
            if(treeContour.getChildren().size() == 1 && treeContour.getChildren().get(0).getKeyContour().getDistance()!= -1){ //Neu con co 1 nut thi them vao goc
                treeContour.getChildren().get(0).getKeyContour().printContour(); //in thu
                root.addContour(treeContour.getChildren().get(0).getKeyContour());
                treeContour = treeContour.getChildren().get(0);
                continue;
            }
            if(treeContour.getChildren().size() == 1 && treeContour.getChildren().get(0).getKeyContour().getDistance()!= -1){ //Truong hop split cell gop
                tree.addChild(convertTree(treeContour)); //Them node con la moi truong Contour connected moi
            }
            if(treeContour.getChildren().size() > 1){
                for (int k = 0; k<treeContour.getChildren().size(); k++){
                    tree.addChild(convertTree(treeContour.getChildren().get(k)));
                }
            }
        }
        return tree;
    }

    public static void main(String[] args) {

        ArrayList<Cell> cells = new ArrayList<>();
        int row = 19, col = 10;
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                Cell c = new Cell(j - col, row - i, false);
                if (i >= 5 && i <= 9 && j >= 5 && j <= 7) c.setObtacle(true);
                if (i >= 10 && i <= 14 && j >= 14 && j <= 16) c.setObtacle(true);
                cells.add(c);
            }
        }
        Environment e = new Environment(cells);
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                System.out.printf("%2d ", cells.get(20 * i + j).getDistance());
            }
            System.out.println();
        }
        e.setTreeContour();
    }
}
