package com.robotics.decompose.Boustrophedon;

import java.util.ArrayList;

public class Tree {
    private EnvironmentBoustrophedon root;
    private ArrayList<Tree> children;
    private Tree parent;

    public Tree(){}

    public Tree(EnvironmentBoustrophedon root, ArrayList<Tree> children) {
        this.root = root;
        this.children = children;
        this.parent = new Tree();
    }

    public void addRoot(Row r){
        if (this.root == null) this.root = new EnvironmentBoustrophedon();
        this.root.addRow(r);
    }

    public EnvironmentBoustrophedon getRoot() {
        return root;
    }

    public ArrayList<Tree> getChildren() {
        return children;
    }

    public void addChildren(Tree child){
        if (children == null) children = new ArrayList<>();
        this.children.add(child);
    }

    public void addChildren(int i, Tree child){
        if (children == null) children = new ArrayList<>();
        this.children.add(i, child);
    }

    public Tree leftSibling(){
        Tree pa = this.parent;
        ArrayList<Tree> children = this.getChildren();
        int i = 0;
        for (; i < children.size(); i++) {
            if (this.getRoot().isEqual(children.get(i).getRoot()) == true)
                break;
        }
        return children.get(i-1);
    }

    public Tree rightSibling(){
        Tree pa = this.parent;
        ArrayList<Tree> children = this.getChildren();
        int i = 0;
        for (; i < children.size(); i++) {
            if (this.getRoot().isEqual(children.get(i).getRoot()) == true)
                break;
        }
        return children.get(i+1);
    }

    public Tree getParent() {
        return parent;
    }

    public void setParent(Tree t){
        this.parent = t;
    }

    public void setChildren(ArrayList<Tree> c){
        this.children = c;
    }

    public int chooseChild(){
        int i = 0;
        for (; i < this.getChildren().size(); i++) {
            if (this.getChildren().get(i).getRoot().isContainObstacle() == false)
                break;
        }
        return i;
    }

    public Tree findNearestDown(Tree t){
        EnvironmentBoustrophedon evn = this.getRoot();
        if (t.getRoot().isNext(evn)){
            System.out.println("Found nearst");
            return t;
        }
        else {
            if (t.getChildren()!=null){
                for (int i = 0; i < t.getChildren().size(); i++) {
                    Tree res =  this.findNearestDown(t.getChildren().get(i));
                    if (res != null)
                        return res;
                }
            }
        }
        System.out.println("Not Found nearest");
        return null;
    }

    public Tree findNearestUp(Tree t){
        EnvironmentBoustrophedon evn = this.getRoot();
        if (evn.isNext(t.getRoot())){
            System.out.println("Found nearst");
            return t;
        }
        else {
            if (t.getChildren()!=null){
                for (int i = 0; i < t.getChildren().size(); i++) {
                    Tree res =  this.findNearestUp(t.getChildren().get(i));
                    if (res != null)
                        return res;
                }
            }
        }
        System.out.println("Not Found nearest");
        return null;
    }

    public void delChild(Tree t){
        for (int i = 0; i < this.getChildren().size(); i++) {
            if (this.getChildren().get(i).getRoot().isEqual(t.getRoot())){
                {
                    this.getChildren().remove(i);
                    break;
                }
            }
        }
    }

    public void modifyTree(Tree rt){
        if (this.getRoot().isContainObstacle() == true && this.getChildren() == null){
            System.out.println("Loai bo moi truong chuong ngai vat");
            this.getParent().delChild(this);
        }
        if (this.getRoot().isContainObstacle() == true && this.getChildren() != null) {
            int flag = -1;
            for (int i = 0; i < this.getChildren().size(); i++) {
                if (this.getChildren().get(i).getRoot().isContainObstacle() == false){
                    flag = i;
                    break;
                }
            }
            System.out.println("Xoa moi truong chuong ngai vat");
            if (flag != -1 && this.getChildren().get(flag).getRoot().isHall() == true) {
                System.out.println("Xu ly phan ngoai le");
                Tree child = this.getChildren().get(flag);
                if (child.getChildren() != null){
                    for (int i = 0; i < child.getChildren().size(); i++) {
                        if (child.getChildren().get(i).getRoot().isContainObstacle() == false){
                            Tree kid = child.getChildren().get(i);
                            Tree nearest = kid.findNearestDown(rt);
                            Tree pa = child.getParent();
                            pa.delChild(child);
                            child.delChild(kid);
                            child.setParent(kid);
                            kid.addChildren(0, child);
                            kid.setParent(nearest);
                            nearest.addChildren(0, kid);
                        }
                    }
                } else {
                    child.getRoot().printEnvironmentBoustrophedon();
                    Tree nearest = child.findNearestUp(rt);
                    System.out.println("Environment nearest: ");
                    nearest.getRoot().printEnvironmentBoustrophedon();
                    child.setParent(nearest);
                    nearest.addChildren(child);
                }
            }
            this.getParent().delChild(this);
        }
        if (this.getChildren() != null){
            for (int i = 0; i < this.getChildren().size(); i++) {
                this.getChildren().get(i).modifyTree(rt);
            }
        }
    }

    public void modify2(Tree rt){
        if (this.getRoot().isContainObstacle() == true){
            this.getParent().delChild(this);
        }
        if (this.getChildren() != null){
            for (int i = 0; i < this.getChildren().size(); i++) {
                this.getChildren().get(i).modify2(rt);
            }
        }
    }

    public void printTree(){
        Tree tree = this;
        System.out.println("In cay theo thu tu truoc: ");
        if (tree!=null){
            tree.root.printEnvironmentBoustrophedon();
            if (tree.getChildren() != null && tree.getChildren().size() > 0){
                System.out.println("Cay co: "+ tree.getChildren().size() + " con");
                for (int i = 0; i < tree.getChildren().size(); i++) {
                    tree.getChildren().get(i).printTree();
                }
            }
            System.out.println("Het con");
        }
    }
}
