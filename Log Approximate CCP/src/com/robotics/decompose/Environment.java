package com.robotics.decompose;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

public class Environment {
    private ArrayList<Cell> cells;

    private Tree tree;

    private TreeContour treeContour;

    public Environment() {
    }

    public Environment(String filename) {
        Scanner sc = null;
        File file = new File(filename);
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int nrows = sc.nextInt(), ncols = sc.nextInt();
        int row = 0, col = 0;
        int e[][] = new int[nrows][ncols];
        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < ncols; j++) {
                e[i][j] = sc.nextInt();
                if (e[i][j] == 2) {
                    row = i;
                    col = j;
                }
            }
        }
        sc.close();
        ArrayList<Cell> cells = new ArrayList<>();
        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < ncols; j++) {
                Cell c = new Cell(j - col, row - i, false);
                if (e[i][j] == 1) c.setObtacle(true);
                cells.add(c);
            }
        }
        System.out.println(Cell.mapCells.get(new Key(0, 0)));
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

    public ArrayList<Cell> getCells() {
        return this.cells;
    }

    private void setTreeContour() {
        TreeContour temp = this.treeContour.findAllContour();
        this.treeContour = temp;
    }

    public TreeContour getTreeContour() {
        return this.treeContour;
    }

    public Tree getTree() {
        if (tree == null) {
            setTreeContour();
            tree = convertTree(this.treeContour);
        }
        return tree;
    }

    private Tree convertTree(TreeContour treeContour) {
        Tree tree = new Tree();
        if (treeContour != null && treeContour.getKeyContour().getCells().size() != 0) {
            treeContour.getKeyContour().printContour();
            tree.addRoot(treeContour.getKeyContour());
            ArrayList<TreeContour> childs = treeContour.getChildren();
            if (childs != null && childs.size() != 0) {
                while (childs != null && childs.size() > 0) {
                    System.out.println("Lap");
                    if (childs.size() == 1) {
                        if (childs.get(0).getKeyContour().getDistance() != -1) {
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
                    } else {
                        System.out.println("Cell phan...");
                        for (int k = 0; k < childs.size(); k++) {
//                            if (childs.get(k).getKeyContour().getCells().size() != 0)
                            tree.addChild(convertTree(childs.get(k)));
                            childs.get(k).getKeyContour().setDistance(childs.get(k).getKeyContour().getCells().get(0).getDistance());
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
        Environment e = new Environment("src/com/robotics/data/Environment 1.txt");
        e.getTree();
    }
}