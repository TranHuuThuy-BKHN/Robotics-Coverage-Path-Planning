package com.robotics.decompose;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class Environment implements Cloneable {
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

    public ArrayList<Cell> getCells(){
        return this.cells;
    }

    public void setTreeContour() {
        TreeContour temp = this.treeContour.findAllContour();
        this.treeContour = temp;
    }

    public TreeContour getTreeContour(){
        return this.treeContour;
    }

    public Tree getTree(TreeContour treeContour) {
        return tree;
    }

    public Tree convertTree(TreeContour treeContour) {
        Tree tree = new Tree();
        if (treeContour != null) {
            treeContour.getKeyContour().printContour();
            tree.addRoot(treeContour.getKeyContour());
            ArrayList<TreeContour> childs = treeContour.getChildren();
            if (childs != null && childs.size() != 0) {
                while (childs != null && childs.size() > 0) {
                    System.out.println("Lap");
                    if (childs.size() == 1){
                        if (childs.get(0).getKeyContour().getDistance() != -1){
                            System.out.println("Chi co 1 con va khong phai split gop: ");
                            childs.get(0).getKeyContour().printContour(); //in thu
                            tree.addRoot(childs.get(0).getKeyContour());
                            childs = childs.get(0).getChildren();
                        } else {
                            System.out.println("Cell gop...");
                            childs.get(0).getKeyContour().setDistance(childs.get(0).getKeyContour().getCells().get(0).getDistance());
                            tree.addChild(convertTree(childs.get(0))); //Them node con la moi truong Contour connected moi
                            break;
                        }
                    }
                    else {
                        System.out.println("Cell phan...");
                        for (int k = 0; k < childs.size(); k++) {
                            tree.addChild(convertTree(childs.get(k)));
                        }
                        break;
                    }
                }
            } else {
                System.out.println("Khong co contour phia sau, tra ve cay...");
                return tree;
            }
        }
        System.out.println("Tra ve cay...");
        return tree;
    }


    public static void main(String[] args) {

        ArrayList<Cell> cells = new ArrayList<>();
        int row = 19, col = 10;
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                Cell c = new Cell(j - col, row - i, false);
                if (i >= 5 && i <= 9 && j >= 5 && j <= 7) c.setObtacle(true);
                if (i >= 10 && i <= 14 && j >= 8 && j <= 16) c.setObtacle(true);
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
        System.out.println("In cay contour");
        e.getTreeContour().printTreeContour();
        System.out.println("af: ");
        System.out.println("Convert Tree...");
        Tree tree = e.convertTree(e.getTreeContour());
        System.out.println("Print Tree...");
        if (tree!=null)
            tree.printTree();
        else System.out.println("Cay rong");
    }
}
