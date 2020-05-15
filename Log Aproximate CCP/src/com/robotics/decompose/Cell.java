package com.robotics.decompose;

import java.util.HashMap;
import java.util.LinkedList;

class Key {
    int x;
    int y;

    public Key(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Key == false) return false;
        return x == ((Key) obj).x && y == ((Key) obj).y;
    }

    @Override
    public int hashCode() {
        int result = 17; // any prime number
        result = 31 * result + Integer.valueOf(x).hashCode();
        result = 31 * result + Integer.valueOf(y).hashCode();
        return result;
    }
}

public class Cell implements Cloneable {
    // map chi số hàng, cột tới cell
    public static HashMap<Key, Cell> mapCells = new HashMap<>();

    /* Tọa đô so với trạm sạc, trạm sạc O(0, 0) */
    int x;
    int y;

    /*true : chướng ngại vật
     false : ô đi được */
    private boolean obtacle;

    private int distance; // khoảng cách từ cell hiện tại tới trạm sạc

    public Cell(int x, int y, boolean obtacle) {
        this.x = x;
        this.y = y;
        this.obtacle = obtacle;
        distance = -1;
        mapCells.put(new Key(x, y), this);
    }

    public boolean isObtacle() {
        return obtacle;
    }

    public void setObtacle(boolean obtacle) {
        this.obtacle = obtacle;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }


    /**
     * Tính khoảng cách tới cell khác trong môi trường
     * @param c
     * @return -1, nếu 1 trong 2 là chướng ngại vật
     */
    public int distanceToCell(Cell c) {
        if (obtacle || c.isObtacle()) return -1;

        HashMap<Cell, Integer> mapDistance = new HashMap<>();
        for (Cell cell : mapCells.values()) {
            mapDistance.put(cell, cell.getDistance());
            cell.setDistance(-1);
        }

        LinkedList<Cell> queue = new LinkedList<>();
        queue.push(this);
        this.setDistance(0);
        serch:
        while (!queue.isEmpty()) {
            Cell cell = queue.poll();
            int x = cell.x, y = cell.y;
            int xs[] = {x, x, x - 1, x + 1}, ys[] = {y - 1, y + 1, y, y};

            for (int i = 0; i < xs.length; i++) {
                Cell nbh = Cell.mapCells.get(new Key(xs[i], ys[i]));
                if (nbh != null && !nbh.isObtacle() && nbh.getDistance() == -1) {
                    nbh.setDistance(cell.getDistance() + 1);
                    queue.add(nbh);
                    if (nbh == c) break serch;
                } else if (nbh != null && nbh.isObtacle()) {
                    nbh.setDistance(-1);
                }
            }
        }
        int dis = c.getDistance();
        for (Cell cell : mapCells.values()) {
            cell.setDistance(mapDistance.get(cell));
        }
        return dis;
    }

    public static void main(String[] args) throws CloneNotSupportedException {
        Cell c = new Cell(0, 0, false);
        Cell c2 = new Cell(-1, 0, true);
        Cell c3 = new Cell(1, 0, false);


        int x = c.x - 1, y = c.y;
        System.out.println(mapCells.get(new Key(x, y)).isObtacle());
    }
}

