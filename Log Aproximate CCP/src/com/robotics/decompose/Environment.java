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
                if (nbh != null && !nbh.isObtacle() && nbh.getDistance() == Integer.MAX_VALUE) {
                    nbh.setDistance(c.getDistance() + 1);
                    queue.push(nbh);
                }else if(nbh != null && nbh.isObtacle()){
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
        Cell s = new Cell(0, 0, false);
        Cell c1 = new Cell(0, 1, false);

        Cell c2 = new Cell(1, 0, true);

        Cell c3 = new Cell(1, 1, false);
        Cell c4 = new Cell(2, 1, false);
        Cell c5 = new Cell(2, 2, false);

        ArrayList<Cell> cells = new ArrayList<>(Arrays.asList(s, c1, c2, c3, c4, c5));
        Environment e = new Environment(cells);

        System.out.println(c5.getDistance());
    }
}
