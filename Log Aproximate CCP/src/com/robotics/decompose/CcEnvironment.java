package com.robotics.decompose;

import java.util.*;

/**
 * @author TranHuuThuy
 * Lớp chỉ định môi trường contour-connected
 */

public class CcEnvironment {
    /**
     * Một contour sẽ chứa các cell theo thứ tự từ trái qua phải
     */

    //sort cells follow x


    class Contour {
        private ArrayList<Cell> cells;

        private int distance;

        public Contour(ArrayList<Cell> cells, int dis) {
            Collections.sort(cells, new Comparator<Cell>() {
                @Override
                public int compare(Cell cell, Cell t1) {
                    if (cell.x < t1.x) return 1;
                    else if (cell.x == t1.x) return 0;
                    else return -1;
                }
            });
            this.cells = cells;
            this.distance = dis;
        }
        public void printContour(){
            for (int i = 0; i < this.cells.size(); i++) {
                System.out.print("(" + this.cells.get(i).x + ", " + this.cells.get(i).y+ ") " );
            }
        }
        public ArrayList<Cell> getCells() {
            return cells;
        }

        public int getDistance(){return this.distance;}

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

        public Contour isMerge(Contour c){
            ArrayList<Cell> mergerCell = this.cells;
            mergerCell.addAll(c.getCells());
            Contour mergerCnt = new Contour(mergerCell, mergerCell.get(0).getDistance());
            for (int i = 0; i <mergerCnt.getCells().size() - 1; i++) {
                if(mergerCnt.getCells().get(i).x == mergerCnt.getCells().get(i+1).x) mergerCnt.getCells().remove(i); //neu 2 o lien tiep co cung toa do x thi xoa di (2 o trung nhau)
                if(mergerCnt.getCells().get(i).x != mergerCnt.getCells().get(i+1).x + 1) return null;
            }
            mergerCnt.setDistance(-1); //Contour nao gop thanh mot thi co khoang cach la -1
            return mergerCnt;
        }
    }

    private ArrayList<Contour> contours;

    class TreeContour {
        Contour keyContour;
        ArrayList<TreeContour> children;
        TreeContour parent;
        public TreeContour(Contour root, ArrayList<TreeContour> kids, TreeContour pa){
            this.keyContour = root;
            this.children = kids;
            this.parent = pa;
        }

        public TreeContour(){

        }

        public TreeContour(Contour contour){
            this.keyContour = contour;
            this.children = null;
            this.parent = null;
        }

        public TreeContour(Contour contour, TreeContour parent){
            this.keyContour = contour;
            this.parent = parent;
            this.children = null;
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
            int index = this.children.indexOf(tr);
            this.children.remove(tr);
            return index;
        }

        public void setKeyContour(Contour root) {
            this.keyContour = root;
        }

        public Contour getKeyContour(){
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
            ArrayList<ArrayList<Contour>> allContour = new ArrayList<ArrayList<Contour>>();
            //Khởi tạo contour đầu tiên là contour chứa điểm S
            TreeContour treeContour = new TreeContour(); //Node goc rong
            ArrayList<Contour> first = new ArrayList<Contour>();
            Cell f = new Cell(0, 0, false);
            ArrayList<Cell> fr = new ArrayList<Cell>();
            fr.add(f);
            first.add(new Contour(fr, 0));
            ArrayList<Contour> temp = first; //temp la tap cac Contour o 1 do sau nao do
            ArrayList<TreeContour> currentTree = new ArrayList<TreeContour>();
            currentTree.add(new TreeContour(new Contour(fr, 0))); //Con cua root la node 0
            treeContour.setChildren(currentTree); //current Tree la tap cac node con o do sau nao do
            treeContour.setParent(treeContour);
            while(true){
                ArrayList<Contour> nextContours = new ArrayList<Contour>(); //list chứa các contour tiếp theo
                ArrayList<Cell> nextCells = new ArrayList<Cell>(); //list chứa các Cells tiếp theo
                ArrayList<TreeContour> nextNode = new ArrayList<TreeContour>(); //list chứa các node con tiếp theo
                int nextDistance = -1;
                for (int i = 0; i < temp.size(); i++) { //Duyet cac Contour và Node TreeContour trong 1 mức nhất định
                    Contour cnt = temp.get(i);
                    TreeContour subtree = currentTree.get(i);
                    nextDistance = cnt.getCells().get(0).getDistance() + 1;
                    Contour nextContour = nextCnt(cnt); //Lấy Contour tiếp theo được sinh ra
                    nextContours.add(nextContour);
                    subtree.addChild(new TreeContour(nextContour, subtree)); //add phan tu vao cay con
                    nextNode.add(new TreeContour(nextContour, subtree)); //Tạo ra cây con gồm Contour gồm tất cả các nút con.
                }
                if (nextContours == null)
                    break;
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
                for (int j = 0; j < nextContours.size(); j++) {
                    ArrayList<Contour> newContour = new ArrayList<Contour>();
                    ArrayList<Cell> ces = nextContours.get(j).getCells();
                    ArrayList<Cell> t = new ArrayList<Cell>();
                    t.add(ces.get(0));
                    for (int k = 0; k < ces.size() - 1; k++){
                        if (ces.get(k).x + 1 == ces.get(k+1).x){
                            t.add(ces.get(k+1));
                            continue;
                        } else { //Nếu gặp trường hợp chia thành 2 Contour con
                            Contour subCnt = new Contour(t, ces.get(0).getDistance());
                            t.clear();
                        }
                    }
                    if (newContour.size()>0){ //Nếu có split cell chia cắt
                        nextContours.remove(j);
                        TreeContour paNode = nextNode.get(0).getParent();
                        nextNode.remove(j); //Xoa node do khoi cay
                        paNode.delChild(j); //Cap nhat parent
                        for (int it = newContour.size() - 1; it >=0; it++) {
                            nextContours.add(j, newContour.get(it));
                            nextNode.add(j, new TreeContour(newContour.get(it), paNode));
                            paNode.addChild(j, new TreeContour(newContour.get(it), paNode));
                        }
                    }
                }

                //Kiem tra xem co chua split cell gop khong
                for (int i = 0; i < nextContours.size() - 1; i++) {
                    if (nextContours.get(i).isMerge(nextContours.get(i+1)) != null){ //neu co the gop
                        Contour newBigCnt = nextContours.get(i).isMerge(nextContours.get(i+1));
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
            treeContour.getChildren().get(0).getKeyContour().printContour();
            return treeContour.getChildren().get(0); //Lay bat dau tu Node (Node chua 0)
        }
    }

    //Hàm này trả về contour trong cùng 1 subarea
    private Contour nextCnt(Contour cnt) {
        ArrayList<Cell> cells = cnt.getCells();
        Set<Cell> nextCnt = new HashSet<Cell>();
        int dis = 0;
        for (int i = 0; i < cells.size(); i++) {
            int x = cells.get(i).x;
            int y = cells.get(i).y;
            int xs[] = {x-1, x, x+1};
            int ys[] = {y, y+1, y};
            for (int j =0; j< 3; j++){
                Cell nextCell = Cell.mapCells.get(new Key(xs[j], ys[j]));
                if (nextCell == null || nextCell.isObtacle() == true){ //trường hợp gặp chướng ngại vật mà không phải đầu cuối contour
                    continue; //Trường hợp này contour tiếp theo bị chia thành 2.
                }
                //Trường hợp contour bị gộp thành 1
                if (nextCell!=null && nextCell.getDistance() == cells.get(i).getDistance()+1){
                    nextCnt.add(nextCell);
                    dis = nextCell.getDistance();
                }
            }
        }
        ArrayList<Cell> res = new ArrayList<>(nextCnt);
        Contour resCnt = new Contour(res, dis);
        return resCnt;
    }

    //Cài đặt hàm xác định môi trường nếu biết 1 contour đầu tiên trong tiến trình quét
    public CcEnvironment getCcEnv(Contour first){
        ArrayList<Contour> listCnt = new ArrayList<Contour>();
        Contour cur = first;
        while(nextCnt(cur)!= null){
            listCnt.add(nextCnt(cur));
        }
        return new CcEnvironment(listCnt);
    }

    //Hàm này tìm ra tất cả các contour theo thứ tự từ khoảng cách gần nhất đến khoảng cách xa nhất
//    public ArrayList<ArrayList<Contour>> findAllContour(ArrayList<Cell> cells){
//        ArrayList<ArrayList<Contour>> allContour = new ArrayList<ArrayList<Contour>>();
//        //Khởi tạo contour đầu tiên là contour chứa điểm S
//        ArrayList<Contour> first = new ArrayList<Contour>();
//        Cell f = new Cell(0, 0, false);
//        ArrayList<Cell> fr = new ArrayList<Cell>();
//        fr.add(f);
//        first.add(new Contour(fr, 0));
//        Queue<ArrayList<Contour>> q = new LinkedList<ArrayList<Contour>>();
//        q.add(first);
//        while(q.size() != 0){
//            ArrayList<Contour> current = q.poll(); //Lay phan tu cuoi cung cua Queue
//            ArrayList<Contour> nextContours = new ArrayList<Contour>();
//            ArrayList<Cell> nextCells = new ArrayList<Cell>();
//            int nextDistance = -1;
//            for (int i = 0; i < current.size(); i++) { //Duyet cac Contour
//                Contour cnt = current.get(i);
//                nextDistance = cnt.cells.get(i).getDistance() + 1;
//                for (int j=0; j< cnt.cells.size(); j++){ //Duyet qua cac Cell trong Contour
//                    int x = cnt.cells.get(j).x;
//                    int y = cnt.cells.get(j).y;
//                    int xs[] = {x-1, x, x+1};
//                    int ys[] = {y, y+1, y};
//                    for (int it = 0; it < 3; it++){
//                        Cell nextCell = Cell.mapCells.get(new Key(xs[it], ys[it]));
//                        if (nextCell == null || nextCell.isObtacle() == true) continue;
//                        else if(nextCell.getDistance() == cnt.cells.get(j).getDistance() + 1)
//                            nextCells.add(nextCell); //them cell do vao contour tiep theo
//                    }
//                }
//            }
//            Contour temp = new Contour(nextCells, nextDistance);
//            ArrayList<Cell> t = new ArrayList<Cell>();
//            t.add(temp.getCells().get(0));
//            for (int i = 0; i < temp.getCells().size() - 1; i++) {
//                if (temp.getCells().get(i).x + 1 == temp.getCells().get(i+1).x){
//                    t.add(temp.getCells().get(i+1));
//                } else {
//                    Contour sub = new Contour(t, nextDistance);
//                    nextContours.add(sub);
//                    t.clear();
//                }
//            }
//            nextContours.add(new Contour(t, nextDistance));
//            allContour.add(nextContours);
//            q.add(nextContours);
//        }
//        return allContour;
//    }

    public CcEnvironment(){}

    public CcEnvironment(ArrayList<Contour> contours) {
        this.contours = contours;
    }

    public ArrayList<Contour> getContours() {
        return contours;
    }

    public void setContours(ArrayList<Contour> contours){
        this.contours = contours;
    }

    public void addContour(Contour contour){
        this.contours.add(contour);
    }

//    public void addRoot(Contour contour){
//
//    }

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
