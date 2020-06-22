package com.robotics.decompose.Boustrophedon;

import com.robotics.decompose.Cell;
import com.robotics.decompose.Key;

import java.util.ArrayList;
import java.util.Random;

public class TreeRow {
    Row keyRow;
    ArrayList<TreeRow> children;
    TreeRow parent;
    Random rd = new Random(1);

    public TreeRow(Row root, ArrayList<TreeRow> kids, TreeRow pa){
        this.keyRow = root;
        this.children = kids;
        this.parent = pa;
    }

    public void setKeyRow(Row r){
        this.keyRow = r;
    }

    public Row getKeyRow(){
        return this.keyRow;
    }

    public void setChildren(ArrayList<TreeRow> rows){
        this.children = rows;
    }

    public ArrayList<TreeRow> getChildren(){
        return this.children;
    }

    public void addChildren(TreeRow r){
        this.children.add(r);
    }

    public void addChildren(int ind, TreeRow tre){
        this.children.add(ind, tre);
    }

    public void setParent(TreeRow pa){
        this.parent = pa;
    }

    public TreeRow getParent(){
        return this.parent;
    }

    public TreeRow(){}

    public TreeRow(Row keyR) {
        this.keyRow = keyR;
        this.children = new ArrayList<>();
        this.parent = new TreeRow();
    }

    public TreeRow(Row keyR, TreeRow pa) {
        this.keyRow = keyR;
        this.parent = pa;
        this.children = new ArrayList<>();
    }

    public int delChild(TreeRow tr){
        int index = -1;
        boolean flag = false;
        for (int i = 0; i <this.getChildren().size() ; i++) {
            if(this.getChildren().get(i).equals(tr) == true);
            flag = true;
            index = i;
            break;
        }
        if (flag == false) {
            System.out.println("Khong co cay con...");
            return -1;
        } else{
            this.children.remove(tr);
            return index;
        }
    }

    public boolean equals(TreeRow treeRow) {
//        System.out.println("used");
        if(treeRow == null) System.out.println("treeContour null...");
        if(!(treeRow instanceof TreeRow)) return false;
        TreeRow other = (TreeRow) treeRow;
        if (this.getKeyRow().equals(other.getKeyRow()) == true) {
            System.out.println("2 tree giong nhau");
            return true;
        }
        else return false;
    }

    public TreeRow findRows(){
        System.out.println("Bắt đầu tìm kiếm cây Row... ");
        //Khởi tạo contour đầu tiên là contour chứa điểm S
        TreeRow treeRow = new TreeRow(); //Node goc rong
        ArrayList<Row> first = new ArrayList<>();
        ArrayList<Cell> fr = new ArrayList<>();
        for (int i = -30; i < 40; i++) {
            Cell c = Cell.mapCells.get(new Key(i, 0));
            if (c != null && c.isObtacle() != true)
                fr.add(c);
        }
        first.add(new Row(fr));
        ArrayList<Row> temp = first; //temp la tap cac Contour o 1 do sau nao do
        ArrayList<TreeRow> currentTree = new ArrayList<TreeRow>();
        currentTree.add(new TreeRow(new Row(fr))); //currentTree chua TreeContour tai 1node nao do
        treeRow.setChildren(currentTree); //current Tree la tap cac node con o do sau nao do
        treeRow.setParent(treeRow);
        while(true){
            ArrayList<Row> nextRows = new ArrayList<>(); //list chứa các contour tiếp theo
            ArrayList<TreeRow> nextNode = new ArrayList<>(); //list chứa các node con tiếp theo
            for (int i = 0; i < temp.size(); i++) { //Duyet cac Contour và Node TreeContour trong 1 mức nhất định
                Row cnt = temp.get(i);
                TreeRow subtree = currentTree.get(i);
                ArrayList<Row> nextRow = cnt.nextRow(); //Lấy Contour tiếp theo được sinh ra
                if (nextRow != null){
                    nextRows.addAll(nextRow);
                    if (nextRow.size()>1){
                        System.out.println("cell phan");
                        for (int j = 0; j < nextRow.size(); j++) {
                            nextRow.get(j).setFlag(false);
//                            nextRow.get(j).printRow();
                        }
                    }
                    for (int j = 0; j < nextRow.size(); j++) {
                        subtree.addChildren(new TreeRow(nextRow.get(j), subtree)); //add phan tu vao cay con
                        //Tạo ra cây con gồm Contour gồm tất cả các nút con.
                    }
                    nextNode.addAll(subtree.getChildren());
                } else {
                    //Gan con cua current tree bang null
                    subtree.setChildren(null);
                }
            }
            if (nextRows == null || nextRows.size() == 0 ||nextRows.get(0).getCells().size() == 0){
                //Xoa phan tu con thu nhat
                return treeRow.getChildren().get(0);
            }

            System.out.println("Cell in nextRows: ");
            for (int i = 0; i < nextRows.size(); i++) {
                nextRows.get(i).printRow();
            }

            //Kiem tra xem co chua split cell gop khong
            if (nextRows.size() > 1){
                for (int i = 0; i < nextRows.size() - 1; i++) {
                    System.out.println("Kiem tra xem co cell gop hay k?");
                    if (nextRows.get(i).isMerge(nextRows.get(i+1)) != null){ //neu co the gop
                        Row newBigCnt = nextRows.get(i).isMerge(nextRows.get(i+1));
                        nextRows.remove(i);
                        nextRows.remove(i);
                        nextRows.add(i, newBigCnt);
                        TreeRow paNode_i = nextNode.get(i).getParent();
                        TreeRow paNode_i1 = nextNode.get(i+1).getParent();
                        int index = paNode_i.delChild(nextNode.get(i)); //Xoa node con khoi cay
                        int index1 = paNode_i1.delChild(nextNode.get(i+1)); //Xoa node con khoi cay
                        nextNode.remove(i); //Xoa khoi do sau cung muc
                        nextNode.remove(i);
                        if(paNode_i.getKeyRow().getCells().get(0).isObtacle() == false){ //Them ngau nhien vao 1 hoac vao 2, treeContour con lai them 1 treeContour khong co gi
                            TreeRow newNode = new TreeRow(newBigCnt, paNode_i);
                            paNode_i.addChildren(index, newNode); //Them vao 1
                            nextNode.add(i, newNode);
                        } else {
                            TreeRow newNode = new TreeRow(newBigCnt, paNode_i1);
                            paNode_i1.addChildren(index1, newNode); //Them vao 2
                            nextNode.add(i, newNode);
                        }
                        i -= 1;
                    }
                }
            }
            temp = nextRows; //Cap nhat lai temp
            System.out.println("Cap nhat lai temp...");
            for (int m = 0; m < temp.size(); m++) {
                nextRows.get(m).printRow();
            }
            currentTree = nextNode; //Cap nhat lai currentTree
        }
    }
}
