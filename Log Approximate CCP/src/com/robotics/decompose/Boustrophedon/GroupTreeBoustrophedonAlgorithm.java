package com.robotics.decompose.Boustrophedon;

import com.robotics.decompose.Cell;

import java.util.*;

public class GroupTreeBoustrophedonAlgorithm {
    private Tree tree;
    private int D;
    private int B;

    // Depth --> trees
    private HashMap<Integer, ArrayList<Tree>> mapTreeID;

    public GroupTreeBoustrophedonAlgorithm(Tree tree, int b) {
        this.tree = tree;
        B = b;
        D = Integer.MIN_VALUE;
        mapTreeID = new HashMap<>();

        tree.depth = 0;
        tree.depth = 0;
        mapTreeID.put(0, new ArrayList<>(Arrays.asList(tree)));

        LinkedList<Tree> queue = new LinkedList<>();
        queue.push(mapTreeID.get(0).get(0));

        while (!queue.isEmpty()) {
            Tree t = queue.poll();
            D = D > t.depth ? D : t.depth;

            if (t.getChildren() == null) continue;
            int depth = t.depth + 1;
            if (mapTreeID.get(depth) == null) mapTreeID.put(depth, new ArrayList<>());

            for (Tree child : t.getChildren()) {
                mapTreeID.get(depth).add(child);
                queue.add(child);
                child.depth = depth;
            }
        }
    }

    private ArrayList<Tree> getTreesByDepth(int depth) {
        return mapTreeID.get(depth);
    }

    private boolean isCoverageBySinglePathBoustrophedonSubTree(Tree t, int B) {
        Cell s = Cell.getChargingStation();
        Stack<Tree> stack = new Stack<>();
        stack.push(t);
        int power = 0;

        while (!stack.isEmpty()) {
            Tree t2 = stack.pop();
            EnvironmentBoustrophedon e = t2.getRoot();
            ArrayList<Cell> fistRow = e.getRows().get(0).getCells();
            ArrayList<Cell> lastRow = e.getRows().get(e.getRows().size() - 1).getCells();
            int d1 = s.distanceToCell(fistRow.get(0)),
                    d2 = s.distanceToCell(fistRow.get(fistRow.size() - 1)),
                    d3 = s.distanceToCell(lastRow.get(0)),
                    d4 = s.distanceToCell(lastRow.get(lastRow.size() - 1));

            int d = Math.min(Math.min(d1, d2), Math.min(d3, d4));
            power += d - 1;
            for (Row r : e.getRows())
                power += r.getCells().size();

            if ((d == d1 && e.getRows().size() % 2 == 0) || (d == d2 && e.getRows().size() % 2 == 1)) {
                s = lastRow.get(0);
            } else if ((d == d1 && e.getRows().size() % 2 == 1) || (d == d2 && e.getRows().size() % 2 == 0)) {
                s = lastRow.get(lastRow.size() - 1);
            } else if ((d == d3 && e.getRows().size() % 2 == 1) || (d == d4 && e.getRows().size() % 2 == 0)) {
                s = fistRow.get(fistRow.size() - 1);
            } else {
                s = fistRow.get(0);
            }
            if (t2.getChildren() == null || t2.getChildren().size() == 0) continue;
            ArrayList<Tree> children = t2.getChildren();
            final Cell S = s;
            Collections.sort(children, new Comparator<Tree>() {
                @Override
                public int compare(Tree o1, Tree o2) {
                    ArrayList<Cell> f1 = o1.getRoot().getRows().get(0).getCells();
                    ArrayList<Cell> l1 = o1.getRoot().getRows().get(o1.getRoot().getRows().size() - 1).getCells();
                    ArrayList<Cell> f2 = o2.getRoot().getRows().get(0).getCells();
                    ArrayList<Cell> l2 = o2.getRoot().getRows().get(o2.getRoot().getRows().size() - 1).getCells();

                    int d1 = Math.min(Math.min(S.distanceToCell(f1.get(0)), S.distanceToCell(f1.get(f1.size() - 1))),
                            Math.min(S.distanceToCell(l1.get(0)), S.distanceToCell(l1.get(l1.size() - 1))));
                    int d2 = Math.min(Math.min(S.distanceToCell(f2.get(0)), S.distanceToCell(f2.get(f2.size() - 1))),
                            Math.min(S.distanceToCell(l2.get(0)), S.distanceToCell(l2.get(l2.size() - 1))));
                    return d1 - d2;
                }
            });

            for (Tree child : children)
                stack.push(child);
        }
        System.out.println("Power for subtree " + power);
        return power <= B ? true : false;
    }


    public ArrayList<Tree> getWorkingZone() {
        ArrayList<Tree> A = new ArrayList<>();
        for (int k = D; k >= 0; k--) {
            System.out.println("--Depth of Tree--" + k);
            // Các node ở độ sâu k
            ArrayList<Tree> treesDepthK = getTreesByDepth(k);
            Iterator iterator = treesDepthK.iterator();

            while (iterator.hasNext()) {
                Tree tree = (Tree) iterator.next();
                if (isCoverageBySinglePathBoustrophedonSubTree(tree, B) == false) {
                    A.add(tree);
                    iterator.remove();
                    tree.printTree();
                    // xóa khỏi cây ban đầu
                    dropSubTree(this.tree, tree);
                }
            }

            // với các node còn lại ở độ sâu k
            int i = 0, p = treesDepthK.size();
            while (i < p) {
                int j = 1;
                Tree N = treesDepthK.get(i);
                Tree Ni;
                if (N.getChildren() == null || N.getChildren().size() == 0)
                    Ni = new Tree(N.getRoot(), null);
                else Ni = new Tree(N.getRoot(), (ArrayList<Tree>) N.getChildren().clone()); // copy N
                while (j++ < p - i && isCoverageBySinglePathBoustrophedonSubTree(Ni, B) == false) {
                    Tree Nj = treesDepthK.get(i + j);
                    Ni.getChildren().add(Nj);
                }
                if (isCoverageBySinglePathBoustrophedonSubTree(Ni, B) == false) {
                    A.add(Ni);
                    // xóa các node khỏi cây
                    for (int h = i; h <= j; h++) {
                        dropSubTree(this.tree, treesDepthK.get(h));
                    }
                }
                i += j;
            }
        }

        if (this.tree != null) A.add(tree);
        return A;
    }

    private void dropSubTree(Tree tree, Tree t) {
        if (tree == t) {
            this.tree = null;
            return;
        }

        if (tree.getChildren() == null) return;
        Iterator iterator = tree.getChildren().iterator();
        drop:
        while (iterator.hasNext()) {
            Tree temp = (Tree) iterator.next();
            if (temp == t) {
                iterator.remove();
                break drop;
            } else dropSubTree(temp, t);
        }
    }

    public static void main(String[] args) {
        Environment2 e = new Environment2("src/com/robotics/data/Environment 3.txt");
        Tree t = e.getTreeBoustrophedon();
        GroupTreeBoustrophedonAlgorithm algorithm = new GroupTreeBoustrophedonAlgorithm(t, 50);
        ArrayList<Tree> A = algorithm.getWorkingZone();
        System.out.println(A.size());
    }
}
