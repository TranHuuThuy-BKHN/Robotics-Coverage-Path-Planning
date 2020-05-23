package com.robotics.decompose;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class TreeContour {
    CcEnvironment.Contour keyContour;
    ArrayList<TreeContour> children;
    TreeContour parent;
    public TreeContour(CcEnvironment.Contour root, ArrayList<TreeContour> kids, TreeContour pa){
        this.keyContour = root;
        this.children = kids;
        this.parent = pa;
    }

    public TreeContour(){

    }

    public TreeContour(CcEnvironment.Contour contour){
        this.keyContour = contour;
        this.children = new ArrayList<>();
        this.parent = null;
    }

    public TreeContour(CcEnvironment.Contour contour, TreeContour parent){
        this.keyContour = contour;
        this.parent = parent;
        this.children = new ArrayList<TreeContour>();
    }


    public void setChildren(ArrayList<TreeContour> children) {
        if (children == null) this.children = null;
        this.children = children;
    }

    public void addChild(int ind, TreeContour treecnt){
        this.children.add(ind, treecnt);
    }

    public void delChild(int ind){
        this.children.remove(ind);
    }

    public int delChild(TreeContour tr){
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

    public boolean equals(TreeContour treeContour) {
        System.out.println("used");
        if(treeContour == null) System.out.println("treeContour null...");
        if(!(treeContour instanceof TreeContour)) return false;
        TreeContour other = (TreeContour) treeContour;
        if (this.getKeyContour().equals(other.getKeyContour()) == true) {
            System.out.println("2 tree giong nhau");
            return true;
        }
        else return false;
    }

    public void setKeyContour(CcEnvironment.Contour root) {
        this.keyContour = root;
    }

    public CcEnvironment.Contour getKeyContour(){
        return this.keyContour;
    }

    public ArrayList<TreeContour> getChildren(){
        return this.children;
    }

    public TreeContour getParent(){ return this.parent;}

    public void setParent(TreeContour parent){
        this.parent = parent;
    }

    public void addChild(TreeContour treeContour) {
        this.children.add(treeContour);
    }

    public void printTreeContour(){
        if (this!= null){
            this.getKeyContour().printContour();
            if (this.getChildren()!= null && this.getChildren().size() !=0){
                for (int i = 0; i < this.getChildren().size(); i++) {
                    this.getChildren().get(i).printTreeContour();
                }
            } else System.out.println("Ket thuc 1 nhanh");
        }
    }

    public TreeContour findAllContour(){
        System.out.println("Bắt đầu tìm kiếm cây Contour... ");
        //Khởi tạo contour đầu tiên là contour chứa điểm S
        TreeContour treeContour = new TreeContour(); //Node goc rong
        ArrayList<CcEnvironment.Contour> first = new ArrayList<>();
        ArrayList<Cell> fr = new ArrayList<>();
        fr.add(Cell.mapCells.get(new Key(0, 0)));
        first.add(new CcEnvironment.Contour(fr, 0));
        ArrayList<CcEnvironment.Contour> temp = first; //temp la tap cac Contour o 1 do sau nao do
        ArrayList<TreeContour> currentTree = new ArrayList<TreeContour>();
        currentTree.add(new TreeContour(new CcEnvironment.Contour(fr, 0))); //currentTree chua TreeContour tai 1node nao do
        treeContour.setChildren(currentTree); //current Tree la tap cac node con o do sau nao do
        treeContour.setParent(treeContour);
        while(true){
            ArrayList<CcEnvironment.Contour> nextContours = new ArrayList<CcEnvironment.Contour>(); //list chứa các contour tiếp theo
//            ArrayList<Cell> nextCells = new ArrayList<Cell>(); //list chứa các Cells tiếp theo
            ArrayList<TreeContour> nextNode = new ArrayList<TreeContour>(); //list chứa các node con tiếp theo
            for (int i = 0; i < temp.size(); i++) { //Duyet cac Contour và Node TreeContour trong 1 mức nhất định
                CcEnvironment.Contour cnt = temp.get(i);
                TreeContour subtree = currentTree.get(i);
                ArrayList<CcEnvironment.Contour> nextContour = cnt.nextCnt(); //Lấy Contour tiếp theo được sinh ra
                if (nextContour != null){
                    System.out.println("add to next contour...");
                    nextContours.addAll(nextContour);
                    for (int j = 0; j < nextContour.size(); j++) {
                        nextContour.get(j).printContour();
                        subtree.addChild(new TreeContour(nextContour.get(j), subtree)); //add phan tu vao cay con
                         //Tạo ra cây con gồm Contour gồm tất cả các nút con.
                    }
                    nextNode.addAll(subtree.getChildren());
                } else {
                    //Gan con cua current tree bang null
                    subtree.setChildren(null);
                }
            }
            System.out.println(nextContours.size());
            if (nextContours == null || nextContours.size() == 0 ||nextContours.get(0).getCells().size() == 0){
                System.out.println("Khong co Contour nao tiep theo...");
                treeContour.getChildren().get(0).getKeyContour().printContour();
                return treeContour.getChildren().get(0);
            }
            /*
            //Tại đây có 3 trường hợp xảy ra
            //Trường hợp 1, các contour con không có contour nào chứa split cell
            //Trường hợp 2, các contour chứa split cell phân đôi, khi đó
            //Trường hợp 3, các contour chứa split cell gộp,
            Xét 2 Contour liên tiếp ArrayList Contour, nếu 2 contour gộp thành 1 tức là
            split cell loại 3. Với loại 3 ta loại bỏ 2 Node con đó thay thế bằng Node to có
            contour là gộp của 2 contour kia.
            Ngoài ra ta còn phải xét từng Contour 1 xem nó có bị chia thành 2 không, nếu bị
            chia thành 2 thì xóa node cũ đi và thêm vào 2 node mới.
             */
            //Kiem tra xem co chua split cell gop khong
            if (nextContours.size() > 1){
                for (int i = 0; i < nextContours.size() - 1; i++) {
                    System.out.println("Kiem tra xem co cell gop hay k?");
                    if (nextContours.get(i).isMerge(nextContours.get(i+1)) != null){ //neu co the gop
                        CcEnvironment.Contour newBigCnt = nextContours.get(i).isMerge(nextContours.get(i+1));
                        nextContours.remove(i);
                        nextContours.remove(i);
                        nextContours.add(i, newBigCnt);
                        TreeContour paNode_i = nextNode.get(i).getParent();
                        TreeContour paNode_i1 = nextNode.get(i+1).getParent();
                        int index = paNode_i.delChild(nextNode.get(i)); //Xoa node con khoi cay
                        int index1 = paNode_i1.delChild(nextNode.get(i+1)); //Xoa node con khoi cay
                        nextNode.remove(i); //Xoa khoi do sau cung muc
                        nextNode.remove(i);
                        Random rd = new Random();
                        if (rd.nextInt(2) == 0){ //Them ngau nhien vao 1 hoac vao 2
                            TreeContour newNode = new TreeContour(newBigCnt, paNode_i);
                            paNode_i.addChild(index, newNode); //Them vao 1
                            nextNode.add(i, newNode);
                        } else {
                            TreeContour newNode = new TreeContour(newBigCnt, paNode_i1);
                            paNode_i1.addChild(index1, newNode); //Them vao 2
                            nextNode.add(i, newNode);
                        }
                    }
                }
            }
            temp = nextContours; //Cap nhat lai temp
            System.out.println("Cap nhat lai temp...");
            for (int m = 0; m < temp.size(); m++) {
                nextContours.get(m).printContour();
            }
            currentTree = nextNode; //Cap nhat lai currentTree
        }
    }
}
