package com.robotics.decompose;

//import com.sun.crypto.provider.JceKeyStore;
//import sun.util.resources.CalendarData;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author TranHuuThuy
 * Lớp chỉ định môi trường contour-connected
 */

public class CcEnvironment {
    /**
     * Một contour sẽ chứa các cell theo thứ tự từ trái qua phải
     */
    //sort cells follow x


    public static class Contour {
        private ArrayList<Cell> cells;

        private int distance;

        public Contour(ArrayList<Cell> cells, int dis) {
            Collections.sort(cells, new Comparator<Cell>() {
                @Override
                public int compare(Cell cell, Cell t1) { //sắp xếp theo thứ tự tăng dần của x
                    if (cell.x > t1.x) return 1;
                    else if (cell.x == t1.x) return 0;
                    else return -1;
                }
            });
            this.cells = cells;
            this.distance = dis;
        }
        public void printContour(){
            if (this.cells.size() == 0) {
                System.out.println("Khong co cells nao: ");
                return;
            }
            System.out.println("Print Contour distance " + this.cells.get(0).getDistance() + " :");
            for (int i = 0; i < this.cells.size(); i++) {
                System.out.print("(" + this.cells.get(i).x + ", " + this.cells.get(i).y+ ") " );
            }
            System.out.println();
        }

        public ArrayList<Cell> getCells() {
            return cells;
        }

//        public int getDistance(){return this.distance;}

        public void setDistance(int d){this.distance = d;}

        public boolean isContain(Cell c){
            if (this.cells.contains(c)) return true;
            else return false;
        }

        public boolean isContainContour(Contour cnt){
            ArrayList<Cell> currentCells = this.cells;
            ArrayList<Cell> subCells = cnt.getCells();
            if (currentCells.size() <= subCells.size()) return false;
            else {
                for (int i = 0; i < subCells.size(); i++) {
                    if (currentCells.get(i).x != subCells.get(i).x || currentCells.get(i).y != subCells.get(i).y)
                        return false;
                }
                return true;
            }
        }

        public boolean equals(Contour cnt) {
            if(!(cnt instanceof Contour)) return false;
            Contour other = (Contour) cnt;
            if (this.getCells().size() != other.getCells().size()) return false;
            for (int i = 0; i < other.getCells().size(); i++){
                if (this.getCells().get(i) != other.getCells().get(i)) return false;
            }
            return true;
        }

        public Contour isMerge(Contour c){
            ArrayList<Cell> mergerCell = new ArrayList<>();
            mergerCell.addAll(c.getCells());
            mergerCell.addAll(this.cells);
            Set<Cell> setCells = new HashSet<>(mergerCell);
            Contour mergerCnt = new Contour(new ArrayList<>(setCells), mergerCell.get(0).getDistance());
            System.out.println("Contour neu duoc merge: ");
            mergerCnt.printContour();
            for (int i = 0; i <mergerCnt.getCells().size() - 1; i++) {
//                if(mergerCnt.getCells().get(i).x == mergerCnt.getCells().get(i+1).x) mergerCnt.getCells().remove(i); //neu 2 o lien tiep co cung toa do x thi xoa di (2 o trung nhau)
                if(mergerCnt.getCells().get(i).x + 1 != mergerCnt.getCells().get(i+1).x) {
                    return null;
                }
            }
            System.out.println("Merger Contour distance = " + this.getCells().get(0).getDistance());
            mergerCnt.setDistance(-1); //Contour nao gop thanh mot thi co khoang cach la -1
            return mergerCnt;
        }

        public ArrayList<Contour> nextCnt() { //Ham next Contour tra ve Arraylist Contour
            ArrayList<Cell> cells = this.getCells();
            Set<Cell> nextCnt = new HashSet<Cell>();
            int dis = 0;
            for (int i = 0; i < cells.size(); i++) {
                int x = cells.get(i).x;
                int y = cells.get(i).y;
                int xs[] = {x - 1, x, x + 1, x};
                int ys[] = {y, y + 1, y, y - 1};
                for (int j = 0; j < 4; j++) {
                    Cell nextCell = Cell.mapCells.get(new Key(xs[j], ys[j]));
                    if (nextCell == null || nextCell.isObtacle() == true) { //trường hợp gặp chướng ngại vật mà không phải đầu cuối contour
                        continue;
                    }
                    if (nextCell.getDistance() == cells.get(i).getDistance() + 1) {
                        nextCnt.add(nextCell);
                    }
                }
            }

            ArrayList<Cell> res = new ArrayList<>(nextCnt);
            if (res.size() == 0) {
                System.out.println("Khong ton tai contour tiep theo...");
                return null;
            }

            System.out.println("Len nextCnt size: " + res.size());

            //Kiem tra xem co split cell phan doi hay khong
            Contour tempCnt = new Contour(res, res.get(0).getDistance());
            res = tempCnt.getCells();
            ArrayList<Cell> temp = new ArrayList<>();
            ArrayList<Contour> resCnts = new ArrayList<>();

            if (res.size() == 1) {
                resCnts.add(tempCnt);
                return resCnts;
            }

            temp.add(res.get(0));
            for (int i = 0; i < res.size() - 1; i++) {
                if (res.get(i).x + 1 != res.get(i + 1).x) { // splitcell
                    resCnts.add(new Contour(temp, temp.get(0).getDistance()));
                    temp = new ArrayList<>();
                    temp.add(res.get(i + 1));
                } else {
                    temp.add(res.get(i + 1));
                }
                if (i == res.size() - 2 && temp.size() != 0) {
                    resCnts.add(new Contour(temp, temp.get(0).getDistance()));
                }
            }
            System.out.println("resCnts size: " + resCnts.size());
            return resCnts;
        }
        public int getDistance(){
            return distance;
        }
    }

    private ArrayList<Contour> contours;

    public CcEnvironment(){
        this.contours = new ArrayList<>();
    }

    public CcEnvironment(ArrayList<Contour> contours) {
        this.contours = contours;
    }

    public ArrayList<Contour> getContours() {
        return this.contours;
    }

    public void setContours(ArrayList<Contour> contours){
        this.contours = contours;
    }

    public void addContour(Contour contour){
        this.contours.add(contour);
    }

    public void printCcEnvironment(){
        System.out.println("In moi truong contour connected con...");
        for (int i = 0; i < this.contours.size(); i++) {
            this.contours.get(i).printContour();
        }
    }

    public static void main(String[] args) {
        Cell c = new Cell(0, 0, false);
        Cell c2 = new Cell(-1, 0, true);
        Cell c3 = new Cell(1, 0, false);
        ArrayList<Cell> ass = new ArrayList<Cell>();
        ass.add(c);
        ass.add(c2);
        ass.add(c3);
    }
}
