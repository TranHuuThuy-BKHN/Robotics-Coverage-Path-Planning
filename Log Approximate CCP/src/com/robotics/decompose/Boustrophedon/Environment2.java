package com.robotics.decompose.Boustrophedon;

import com.robotics.decompose.Cell;
import com.robotics.decompose.Environment;

import java.util.ArrayList;

public class Environment2 extends Environment {
    public Environment2(ArrayList<Cell> cells) {
        super(cells);
    }

    public Environment2(String filename) {
        super(filename);
    }

    public Tree getTreeBoustrophedon() {
        TreeRow treeRow = new TreeRow();
        treeRow = treeRow.findRows();
        Tree t = convertTree(treeRow);
        t.modifyTree(t);
        t.modifyTree(t);
        t.modify2(t);
        return t;
    }

    private Tree convertTree(TreeRow treeRow) {
        Tree tree = new Tree();
        if (treeRow != null && treeRow.getKeyRow().getCells().size() != 0) {
            System.out.println("Xet Row: ");
            treeRow.getKeyRow().printRow();
            tree.addRoot(treeRow.getKeyRow());
            ArrayList<TreeRow> childs = treeRow.getChildren();
            if (childs != null && childs.size() != 0) {
                while (childs != null && childs.size() > 0) {
                    System.out.println("Lap");
                    if (childs.size() == 1) {
                        if (childs.get(0).getKeyRow().getFlag() != false) {
                            System.out.println("Chi co 1 con va khong phai split gop: ");
                            childs.get(0).getKeyRow().printRow(); //in thu
                            tree.addRoot(childs.get(0).getKeyRow());
                            childs = childs.get(0).getChildren();
                        } else {
                            System.out.println("Cell gop...");
                            childs.get(0).getKeyRow().setFlag(true);
                            Tree tmp = convertTree(childs.get(0));
                            tmp.setParent(tree);
                            tree.addChildren(tmp); //Them node con la moi truong Contour connected moi
                            break;
                        }
                    } else {
                        System.out.println("Cell phan...");
                        for (int k = 0; k < childs.size(); k++) {
                            Tree tmp = convertTree(childs.get(k));
                            tmp.setParent(tree);
                            tree.addChildren(tmp);
                            childs.get(k).getKeyRow().setFlag(true);
                        }
                        break;
                    }
                }
            } else {
                System.out.println("Khong co contour phia sau, tra ve cay...");
                return tree;
            }
        }
        System.out.println("Tra ve cay...");
        return tree;
    }

    public static void main(String[] args) {

        Environment2 e = new Environment2("src/com/robotics/data/Environment 7.txt");

        Tree tre = e.getTreeBoustrophedon();
        tre.printTree();
    }
}
