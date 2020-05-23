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

    public void printCell(){
        System.out.println("("+this.x+", "+this.y+")");
    }
//    private int isSplitCell(){
//        if(this != null && this.isObtacle() == false) return 0;
//        if(this.x == 0){
//
//        }
//    }

    public Boolean isNextCellInCnt(Cell current, Cell next){
        if (next.getDistance() != current.getDistance() && (next.getDistance()!= -1 || next != null)) return false;
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
            if (next_next== null || next_next.getDistance() == -1) continue;
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
    public static void main(String[] args) {

    }
}

