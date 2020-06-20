package com.robotics.decompose;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;



public class Cell{
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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
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

    public void printCell() {
        System.out.println("(" + this.x + ", " + this.y + ")");
    }
//    private int isSplitCell(){
//        if(this != null && this.isObtacle() == false) return 0;
//        if(this.x == 0){
//
//        }
//    }

    public Boolean isNextCellInCnt(Cell current, Cell next) {
        if (next.getDistance() != current.getDistance() && (next.getDistance() != -1 || next != null)) return false;
        if (next.getDistance() == current.getDistance()) return true;
        //Neu o tiep theo la o thuoc duong bien hoac chuong ngai vat
        //Kiem tra xem co ton tai o ke voi no thuoc duong bien hay khong
        int x_next = next.x;
        int y_next = next.y;
        int xs[] = {x_next - 1, x_next - 1, x_next + 1, x_next + 1};
        int ys[] = {y_next - 1, y_next + 1, y_next - 1, y_next - 1};
        int count = 0;
        for (int i = 0; i < 4; i++) {
            Cell next_next = Cell.mapCells.get(new Key(xs[i], ys[i]));
            if (next_next == null || next_next.getDistance() == -1) continue;
            if (next_next.getDistance() == current.getDistance()) count++;
        }
        if (count < 2) return false;
        else
            return true;
    }

    //    public Cell isNextCellOfNextCnt(Cell cur, Cell next){
//        if (next.getDistance() != cur.getDistance() + 1) return null;
//
//    }
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

    public ArrayList<Cell> fromToCell(Cell c) {
        class Cell2 {
            private Cell c;
            private Cell2 parC;

            public Cell2(Cell c, Cell2 parC) {
                this.c = c;
                this.parC = parC;
            }
        }
        HashMap<Cell, Boolean> map = new HashMap<>();

        LinkedList<Cell2> queue = new LinkedList<>();
        Cell2 cell2 = null;
        queue.push(new Cell2(this, null));
        map.put(this, true);

        while (!queue.isEmpty()) {
            Cell2 c2 = queue.poll();
            if (c2.c == c) {
                cell2 = c2;
                break;
            }
            int x = c2.c.x, y = c2.c.y;
            int xs[] = {x - 1, x + 1, x, x}, ys[] = {y, y, y - 1, y + 1};

            for (int i = 0; i < xs.length; i++) {
                Cell cell = mapCells.get(new Key(xs[i], ys[i]));
                if (cell != null && cell.isObtacle() == false && (map.get(cell) == null || map.get(cell) == false)) {
                    queue.add(new Cell2(cell, c2));
                    map.put(cell, true);
                }
            }
        }
        ArrayList<Cell> path = new ArrayList<>();
        while (cell2 != null) {
            path.add(cell2.c);
            cell2 = cell2.parC;
        }
        return path;
    }

    public static Cell getChargingStation() {
        return mapCells.get(new Key(0, 0));
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}

