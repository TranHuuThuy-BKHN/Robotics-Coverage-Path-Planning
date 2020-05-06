package com.robotics.decompose;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class Environment {
    private ArrayList<Cell> cells;

    private Tree tree;

    public Environment(ArrayList<Cell> cells) {
        this.cells = cells;

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

    public Tree getTree() {
        if (tree == null) {
            tree = convertTree();
        }
        return tree;
    }

    private Tree convertTree() {
        return null;
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
    }
}
