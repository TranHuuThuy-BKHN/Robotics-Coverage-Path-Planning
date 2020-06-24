package com.robotics.decompose;

import java.util.ArrayList;

/**
 * @author TranHuuThuy
 * lớp thể hiên việc phân chia môi trường thành cây
 */
public class Tree {
    private CcEnvironment root; // mỗi node của cây là một môi trường contour-connected

    private ArrayList<Tree> children;


    public Tree(){
        this.root = new CcEnvironment();
        this.children = new ArrayList<>();
    }
    int depth; // độ sâu của root

    public Tree(CcEnvironment root, ArrayList<Tree> children) {
        this.root = root;
        this.children = children;
    }

    public void printTree(){
        Tree tree = this;
        System.out.println("In cay theo thu tu truoc: ");
        if (tree!=null){
            tree.root.printCcEnvironment();
            if (tree.getChildren() != null && tree.getChildren().size() > 0){
                System.out.println("Cay co: "+ tree.getChildren().size() + " con");
                for (int i = 0; i < tree.getChildren().size(); i++) {
                    tree.getChildren().get(i).printTree();
                }
            }
        }
    }

    public void setRoot(CcEnvironment cc){this.root = cc;}

    public void addRoot(CcEnvironment.Contour contour){
        if (this.root == null) this.root = new CcEnvironment();
        this.root.addContour(contour);
    }

    public CcEnvironment getRoot() {
        return root;
    }

    public void setChildren(ArrayList<Tree> children){
        this.children = children;
    }

    public void addChild(Tree tree){
        if (this.children==null) this.children = new ArrayList<>();
        this.children.add(tree);
    }

    public ArrayList<Tree> getChildren() {
        return children;
    }
}
