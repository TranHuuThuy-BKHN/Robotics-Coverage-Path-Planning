package com.robotics.decompose;

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

    public TreeContour findAllContour(ArrayList<Cell> cells){
        System.out.println("Bắt đầu tìm kiếm cây Contour... ");
        ArrayList<ArrayList<CcEnvironment.Contour>> allContour = new ArrayList<ArrayList<CcEnvironment.Contour>>();
        //Khởi tạo contour đầu tiên là contour chứa điểm S
        TreeContour treeContour = new TreeContour(); //Node goc rong
        ArrayList<CcEnvironment.Contour> first = new ArrayList<CcEnvironment.Contour>();
        Cell f = Cell.mapCells.get(new Key(0, 0));
        ArrayList<Cell> fr = new ArrayList<Cell>();
        fr.add(f);
        first.add(new CcEnvironment.Contour(fr, 0));
        ArrayList<CcEnvironment.Contour> temp = first; //temp la tap cac Contour o 1 do sau nao do
        ArrayList<TreeContour> currentTree = new ArrayList<TreeContour>();
        currentTree.add(new TreeContour(new CcEnvironment.Contour(fr, 0))); //Con cua root la node 0
        treeContour.setChildren(currentTree); //current Tree la tap cac node con o do sau nao do
        treeContour.setParent(treeContour);
        while(true){
            ArrayList<CcEnvironment.Contour> nextContours = new ArrayList<CcEnvironment.Contour>(); //list chứa các contour tiếp theo
//            ArrayList<Cell> nextCells = new ArrayList<Cell>(); //list chứa các Cells tiếp theo
            ArrayList<TreeContour> nextNode = new ArrayList<TreeContour>(); //list chứa các node con tiếp theo
            for (int i = 0; i < temp.size(); i++) { //Duyet cac Contour và Node TreeContour trong 1 mức nhất định
                CcEnvironment.Contour cnt = temp.get(i);
                TreeContour subtree = currentTree.get(i);
                CcEnvironment.Contour nextContour = cnt.nextCnt(); //Lấy Contour tiếp theo được sinh ra
                if (nextContour != null){
                    System.out.println("add to next contour...");
                    nextContours.add(nextContour);
                    subtree.addChild(new TreeContour(nextContour, subtree)); //add phan tu vao cay con
                    nextNode.add(new TreeContour(nextContour, subtree)); //Tạo ra cây con gồm Contour gồm tất cả các nút con.
                }
            }
            System.out.println(nextContours.size());
            if (nextContours == null || nextContours.size() == 0 ||nextContours.get(0).getCells().size() == 0){
                System.out.println("Khong co Contour nao tiep theo...");
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
            //Kiem tra xem co chua split cell phan doi hay khong
            System.out.println("So luong Contour trong next Contour: " + nextContours.size() + " distance: "+ nextContours.get(0).getCells().get(0).getDistance());
            for (int j = 0; j < nextContours.size(); j++) {
                ArrayList<CcEnvironment.Contour> newContour = new ArrayList<CcEnvironment.Contour>();
                ArrayList<Cell> ces = nextContours.get(j).getCells();
//                System.out.println("So luong cell trong Contour: " + ces.size());
                ArrayList<Cell> t = new ArrayList<Cell>();
                t.add(ces.get(0));
                for (int k = 0; k < ces.size() - 1; k++){
                    if (ces.get(k).x + 1 != ces.get(k + 1).x) { //Nếu gặp trường hợp chia thành 2 Contour con
                        System.out.println("Contour bi chia thanh 2 contour:");
                        CcEnvironment.Contour subCnt = new CcEnvironment.Contour(t, ces.get(0).getDistance());
                        newContour.add(subCnt);
                        t = new ArrayList<>();
                    }
                    t.add(ces.get(k+1));
                    if(k==ces.size()-2){
                        CcEnvironment.Contour subCnt = new CcEnvironment.Contour(t, ces.get(0).getDistance());
                        newContour.add(subCnt);
                    }
                }

                for (int z = 0; z < newContour.size(); z++) {
                    newContour.get(z).printContour();
                }

                if (newContour.size()>1){ //Nếu có split cell chia cắt
                    System.out.println("Xu ly Contour bi chia cat");
                    nextContours.remove(j);
                    TreeContour paNode = nextNode.get(0).getParent();
                    System.out.println("panode: ");
                    paNode.getChildren().get(0).getKeyContour().printContour();
                    nextNode.get(j).getKeyContour().printContour();
                    int idx = paNode.delChild(nextNode.get(j)); //Cap nhat parent
                    System.out.println(idx);
                    nextNode.remove(j); //Xoa node do khoi cay
                    System.out.println(newContour.size());
                    for (int it = newContour.size() - 1; it >=0; it--) {
                        System.out.println("add "+ j + " " + it);
                        newContour.get(it).printContour();
                        nextContours.add(j, newContour.get(it));
                        nextNode.add(j, new TreeContour(newContour.get(it), paNode));
                        paNode.addChild(idx, new TreeContour(newContour.get(it), paNode));
                    }
                }
            }

            //Kiem tra xem co chua split cell gop khong
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
                    paNode_i1.delChild(nextNode.get(i+1)); //Xoa node con khoi cay
                    nextNode.remove(i); //Xoa khoi do sau cung muc
                    nextNode.remove(i);
                    Random rd = new Random(22);
                    if (rd.nextInt(1) == 0){
                        paNode_i.addChild(new TreeContour(newBigCnt, paNode_i));
                        nextNode.add(i, new TreeContour(newBigCnt, paNode_i));
                    } else {
                        paNode_i1.addChild(new TreeContour(newBigCnt, paNode_i1)); //Them ngau nhien vao 1 trong 2 cay con
                        nextNode.add(i, new TreeContour(newBigCnt, paNode_i1));
                    }
                }
            }
            temp = nextContours; //Cap nhat lai temp
            currentTree = nextNode; //Cap nhat lai currentTree
        }
//        treeContour.getChildren().get(0).getKeyContour().printContour();
//        return treeContour.getChildren().get(0); //Lay bat dau tu Node (Node chua 0)
    }
}
