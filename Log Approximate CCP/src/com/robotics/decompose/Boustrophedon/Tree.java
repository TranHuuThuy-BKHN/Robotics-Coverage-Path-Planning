package com.robotics.decompose.Boustrophedon;

import java.util.ArrayList;

public class Tree {
    private EnvironmentBoustrophedon root;
    private ArrayList<Tree> children;

    public Tree(EnvironmentBoustrophedon root, ArrayList<Tree> children) {
        this.root = root;
        this.children = children;
    }

    public EnvironmentBoustrophedon getRoot() {
        return root;
    }

    public ArrayList<Tree> getChildren() {
        return children;
    }
}
