package com.robotics.decompose.Boustrophedon;

import com.robotics.decompose.Cell;
import com.robotics.decompose.Key;

import java.util.*;

public class Row {
    // các cell được sắp xếp từ trái qua phải, các cell cùng trên một hàng, liền nhau
    private ArrayList<Cell> rows;
    private Boolean flag = true;

    public Row(ArrayList<Cell> rows) {
        //Sap xep cac Cell trong row

        Collections.sort(rows, new Comparator<Cell>() {
            @Override
            public int compare(Cell cell, Cell t1) { //sắp xếp theo thứ tự tăng dần của x
                if (cell.getX() > t1.getX()) return 1;
                else if (cell.getX() == t1.getX()) return 0;
                else return -1;
            }
        });
        this.rows = rows;
    }

    public void setFlag(Boolean f){
        this.flag = f;
    }

    public Boolean getFlag(){
        return flag;
    }

    public Row() {
        this.rows = new ArrayList<>();
    }

    public void setRows(ArrayList<Cell> rows) {
        this.rows = rows;
    }

    public ArrayList<Cell> getCells(){
        return this.rows;
    }

//    public ArrayList<Row> nextRow(){
//        ArrayList<Row> newR = new ArrayList<>();
//        ArrayList<Cell> newCells = new ArrayList<>();
////        for (int i = 0; i < this.rows.size(); i++) {
////            if (Cell.mapCells.get(new Key(this.getCells().get(i).getX(), 1 + this.getCells().get(i).getY())) != null && !Cell.mapCells.get(new Key(this.getCells().get(i).getX(), 1 + this.getCells().get(i).getY())).isObtacle()){
////                newCells.add(new Cell(this.getCells().get(i).getX(), 1 + this.getCells().get(i).getY(), false));
////            }
////        }
//        Cell ntCell = null;
//        for (int i = 0; i < this.getCells().size(); i++) {
//            if (Cell.mapCells.get(new Key(this.getCells().get(i).getX(), 1 + this.getCells().get(i).getY())) != null && !Cell.mapCells.get(new Key(this.getCells().get(i).getX(), 1 + this.getCells().get(i).getY())).isObtacle()){
//                ntCell = Cell.mapCells.get(new Key(this.getCells().get(i).getX(), 1 + this.getCells().get(i).getY()));
//                break;
//            }
//        }
//
//        //find left cells
//        Cell left = Cell.mapCells.get(new Key(ntCell.getX(), ntCell.getY()));
////        System.out.println("(" + left.getX() + ", " + left.getY() + ")");
//
//        while (left != null && left.isObtacle() != true){
//            newCells.add(left);
//            left = Cell.mapCells.get(new Key(left.getX() - 1, left.getY()));
//        }
//
//        //find right cell
//        Cell right = Cell.mapCells.get(new Key(ntCell.getX() + 1, ntCell.getY()));
//        while (right != null && right.isObtacle() != true) {
//            newCells.add(right);
//            right = Cell.mapCells.get(new Key(right.getX() + 1, right.getY()));
//        }
//
//        //Kiem tra xem co bi chia cat hay khong
//        ArrayList<Cell> tempCell = new ArrayList<>();
//        System.out.println("next Cells:");
//        for (int i = 0; i < newCells.size(); i++) {
//            System.out.print(" (" + newCells.get(i).getX() + ", " + newCells.get(i).getY() + ") ");
//        }
//        if (newCells.size() == 0){
//            return null;
//        }
//        System.out.println();
//        tempCell.add(newCells.get(0));
//        for (int i = 0; i < newCells.size() - 1; i++) {
//            if (newCells.get(i).getX() + 1 != newCells.get(i + 1).getX()){
//                newR.add(new Row(tempCell));
//                tempCell = new ArrayList<>();
//            }
//            tempCell.add(newCells.get(i + 1));
//            if (i==newCells.size()-2 && tempCell.size() != 0){
//                newR.add(new Row(tempCell));
//            }
//        }
//        return newR;
//    }

    public ArrayList<Row> nextRow(){
        ArrayList <Row> nextRows = new ArrayList<>();
        int y_next = this.getCells().get(0).getY() + 1;
//        int min = 0-39;
//        int max = 0+40;
        ArrayList<Cell> nextCells = new ArrayList<>();
        for (int i = 0; i < this.getCells().size(); i++) {
            Cell nextCell = Cell.mapCells.get(new Key(this.getCells().get(i).getX(), y_next));
            if (nextCell != null){
                nextCells.add(nextCell);
            }
        }

        if (nextCells.size() == 0)
            return null;

//        System.out.println("Cell in next Row: ");
//        for (int i = 0; i < nextCells.size(); i++) {
//            nextCells.get(i).printCell();
//        }

        if (nextCells.size() == 1){
            nextRows.add(new Row(nextCells));
            return nextRows;
        }

        ArrayList<Cell> temp = new ArrayList<>();
        temp.add(nextCells.get(0));
        for (int i = 0; i < nextCells.size() - 1; i++) {
            if (nextCells.get(i).isObtacle() != nextCells.get(i+1).isObtacle()){
                nextRows.add(new Row(temp));
                temp = new ArrayList<>();
            }
            temp.add(nextCells.get(i + 1));
            if (i==nextCells.size()-2 && temp.size() != 0){
                nextRows.add(new Row(temp));
            }
        }
        if (nextRows.size()==1 && nextRows.get(0).getCells().get(0).isObtacle() != this.getCells().get(0).isObtacle())
            nextRows.get(0).setFlag(false);
        return nextRows;
    }

    public Row isMerge(Row r){
        ArrayList<Cell> mergerCell = new ArrayList<>();
        if (r.getCells().get(0).isObtacle() != this.getCells().get(0).isObtacle()){
            System.out.println("Not merge obstacle");
            return null;
        }
        mergerCell.addAll(this.rows);
        mergerCell.addAll(r.getCells());
        Set<Cell> setCells = new HashSet<>(mergerCell);
        Row mergerCnt = new Row(new ArrayList<>(setCells));
        System.out.println("Contour neu duoc merge: ");
        mergerCnt.printRow();
        for (int i = 0; i <mergerCnt.getCells().size() - 1; i++) {
            if(mergerCnt.getCells().get(i).getX() + 1 != mergerCnt.getCells().get(i+1).getX()) {
                System.out.println("Not merge");
                return null;
            }
        }
        System.out.println("Merger Row y = " + this.getCells().get(0).getY());
        mergerCnt.setFlag(false); //Contour nao gop thanh mot thi co khoang cach la -1
        return mergerCnt;
    }

    public boolean isEqual(Row r){
        if (this.getCells().size() != r.getCells().size()) return false;
        for (int i = 0; i < r.getCells().size(); i++) {
            if (this.getCells().get(i).getX() != r.getCells().get(i).getX() || this.getCells().get(i).getY() != r.getCells().get(i).getY() || this.getCells().get(i).isObtacle() != r.getCells().get(i).isObtacle())
                return false;
        }
        return true;
    }

    public void printRow(){
        for (int i = 0; i < this.rows.size(); i++) {
            System.out.print("(" + this.rows.get(i).getX() + ", " + this.rows.get(i).getY() + ")" + ", ");
        }
        System.out.println();
    }

    // methods
    // ....

    public static void main(String[] args) {
        Cell a = new Cell(1,2, false);
        Cell b = new Cell(-2, 2, false);
        ArrayList<Cell> array = new ArrayList<>();
        array.add(a);
        array.add(b);
        Row r = new Row(array);
        r.printRow();
    }
}