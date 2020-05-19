package com.robotics.decompose;

import java.util.ArrayList;

/**
 * @author TranHuuThuy
 * lớp thể hiên việc phân chia môi trường thành cây
 */
public class Tree {
    private CcEnvironment root; // mỗi node của cây là một môi trường contour-connected

    private ArrayList<Tree> children;

    public Tree(){}

    public Tree(CcEnvironment root, ArrayList<Tree> children) {
        this.root = root;
        this.children = children;
    }

    public void printTree(){
        Tree tree = this;
        System.out.println("In cay theo thu tu truoc: ");
        while (tree != null || tree.getChildren().size() != 0){
            tree.root.printCcEnvironment();
            for (int i = 0; i < tree.getChildren().size(); i++) {
                tree.getChildren().get(i).printTree();
            }
        }
    }

    public void setRoot(CcEnvironment cc){this.root = cc;}

    public void addRoot(CcEnvironment.Contour contour){
        this.root.addContour(contour);
    }

    public CcEnvironment getRoot() {
        return root;
    }

    public void setChildren(ArrayList<Tree> children){
        this.children = children;
    }

    public void addChild(Tree tree){
        this.children.add(tree);
    }

    public ArrayList<Tree> getChildren() {
        return children;
    }
}
