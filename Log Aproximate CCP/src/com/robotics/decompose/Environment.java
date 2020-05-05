package com.robotics.decompose;

import java.util.ArrayList;

public class Environment {
    private ArrayList<Cell> cells;

    private Tree tree;

    public Environment(ArrayList<Cell> cells) {
        this.cells = cells;
    }

    public Tree getTree() {
        if (tree == null) {
            tree = convertTree();
        }
        return tree;
    }

    private Tree convertTree() {
        return null;
    }
}
