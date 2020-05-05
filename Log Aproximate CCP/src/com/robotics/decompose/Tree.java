package com.robotics.decompose;

import java.util.ArrayList;

/**
 * @author TranHuuThuy
 * lớp thể hiên việc phân chia môi trường thành cây
 */
public class Tree {
    private CcEnvironment root; // mỗi node của cây là một môi trường contour-connected

    private ArrayList<Tree> children;

    public Tree(CcEnvironment root, ArrayList<Tree> children) {
        this.root = root;
        this.children = children;
    }

    public CcEnvironment getRoot() {
        return root;
    }

    public ArrayList<Tree> getChildren() {
        return children;
    }
}
