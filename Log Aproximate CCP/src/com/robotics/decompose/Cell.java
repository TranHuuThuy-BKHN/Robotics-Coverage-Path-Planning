package com.robotics.decompose;

import java.util.HashMap;

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

public class Cell {
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
        distance = Integer.MAX_VALUE;
        mapCells.put(new Key(x, y), this);
    }

    public boolean isObtacle() {
        return obtacle;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public static void main(String[] args) {
        Cell c = new Cell(0, 0, false);
        Cell c2 = new Cell(-1, 0, true);
        Cell c3 = new Cell(1, 0, false);

        int x = c.x - 1, y = c.y;
        System.out.println(mapCells.get(new Key(x, y)).isObtacle());

    }
}

