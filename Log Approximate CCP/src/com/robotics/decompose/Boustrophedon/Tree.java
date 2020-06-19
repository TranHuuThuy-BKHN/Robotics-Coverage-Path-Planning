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

    public int chooseChild(){
        int i = 0;
        for (; i < this.getChildren().size(); i++) {
            if (this.getChildren().get(i).getRoot().isContainObstacle() == false)
                break;
        }
        return i;
    }

    public Tree findNearest(Tree t){
        EnvironmentBoustrophedon evn = t.getRoot();
        Tree temp = this;
        if (evn.isNext(temp.getRoot()))
            return temp;
        else {
            if (temp.getChildren()!=null){
                for (int i = 0; i < temp.getChildren().size(); i++) {
                    return temp.getChildren().get(i).findNearest(t);
                }
            }
        }
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
        } else
            if (this.getRoot().isContainObstacle() == true && this.getChildren().get(0).getRoot().isHall()){
                System.out.println("Xu ly phan ngoai le");
                this.getParent().delChild(this);
                Tree nearest = rt.findNearest(this);
                this.setParent(nearest);
                nearest.addChildren(this);
            }
        if (this.getChildren() != null){
            for (int i = 0; i < this.getChildren().size(); i++) {
                this.getChildren().get(i).modifyTree(rt);
            }
        }
//        return rt;
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
