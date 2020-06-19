package com.robotics.decompose.Boustrophedon;

import com.robotics.decompose.Cell;
import com.robotics.decompose.Key;

import java.util.ArrayList;

public class EnvironmentBoustrophedon {
    // các hàng được sắp xếp từ dưới lên trên, các hàng phải liền nhau
    private ArrayList<Row> rows;

    public EnvironmentBoustrophedon(ArrayList<Row> rows) {
        this.rows = rows;
    }

    public EnvironmentBoustrophedon(){
        this.rows = new ArrayList<>();
    }

    public ArrayList<Row> getRows(){
        return rows;
    }

    public void addRow(Row r){
        if (this.rows == null){
            this.rows = new ArrayList<>();
        }
        this.rows.add(r);
    }

    public boolean isEqual(EnvironmentBoustrophedon evnb){
        ArrayList<Row> evnbrows = evnb.getRows();
        if (evnbrows.get(0).isEqual(this.getRows().get(0)) == false) return false;
        if (evnbrows.get(evnbrows.size()-1).isEqual(this.getRows().get(this.getRows().size()-1)) == false) return false;
        return true;
    }

    //Kiem tra xem row dau va cuoi co chua Row r hay khong
    public boolean isContainRow(Row r){
        if (this.getRows().get(0).isEqual(r) == true) return true;
        if (this.getRows().get(this.getRows().size()-1).isEqual(r) == true) return true;
        return false;
    }

    public boolean isContainObstacle(){
        if (this.getRows().get(0).getCells().get(0).isObtacle() == true){
            System.out.println("Moi truong chua chuong ngai vat...");
            return true;
        }
        return false;
    }

    public boolean isHall(){
        Row firstRow = this.getRows().get(0);
        Cell firstCell = firstRow.getCells().get(0);
        Cell lastCell = firstRow.getCells().get(firstRow.getCells().size());
        Cell left = Cell.mapCells.get(new Key(firstCell.getX(), firstCell.getY()));
        Cell right = Cell.mapCells.get(new Key(lastCell.getX(), lastCell.getY()));
        if (left==null && right == null)
            return false;
        if (left==null && right.isObtacle() == true)
            return true;
        if (left.isObtacle() == true && right == null)
            return true;
        return false;
    }

    public boolean isNext(EnvironmentBoustrophedon evb){
        Row lastRow = this.getRows().get(this.getRows().size());
        Row firstRow = evb.getRows().get(0);
        ArrayList<Row> nextRows = lastRow.nextRow();
        if (nextRows == null)
            return false;
        int i = 0;
        boolean flag = false;
        for (; i < nextRows.size(); i++) {
            if (nextRows.get(i).getCells().get(0).isObtacle() == false){
                flag = true;
                break;
            }
        }
        if (flag==true){
            for (int j = 0; j < firstRow.getCells().size(); j++) {
                if (firstRow.getCells().get(j).getX() == nextRows.get(i).getCells().get(0).getX() && firstRow.getCells().get(j).getY() == nextRows.get(i).getCells().get(0).getY()){
                    return true;
                }
            }
            return false;
        } else
            return false;
    }

    public void printEnvironmentBoustrophedon(){
        System.out.println("In moi truong Boustrophedon...");
        if (this.rows.get(0).getCells().get(0).isObtacle() == true)
            System.out.println("Moi truong chuong ngai vat...");
        for (int i = 0; i < this.rows.size(); i++) {
            this.rows.get(i).printRow();
        }
    }

    // method
    // ...
}
