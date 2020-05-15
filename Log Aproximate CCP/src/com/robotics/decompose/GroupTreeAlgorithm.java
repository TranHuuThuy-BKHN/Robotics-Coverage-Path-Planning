package com.robotics.decompose;

import java.util.*;

public class GroupTreeAlgorithm {
    private Tree tree;
    private int D; // độ sâu node lớn nhất trong cây
    private int B; // năng lượng tối đa của robot

    // Depth --> trees
    private HashMap<Integer, ArrayList<Tree>> mapTreeID;

    public GroupTreeAlgorithm(Tree tree, int B) {
        this.tree = tree;
        this.B = B;
        D = Integer.MIN_VALUE;
        mapTreeID = new HashMap<>();

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

    public boolean isCoverageBySinglePathSubTree(Tree t, int B) {
        Cell s = Cell.mapCells.get(new Key(0, 0));
        Stack<Tree> stack = new Stack<>();
        stack.push(t);

        int power = 0;
        while (!stack.isEmpty()) {
            Tree t2 = stack.pop();
            CcEnvironment root = t2.getRoot();
            CcEnvironment.Contour firstContour = root.getContours().get(0);
            CcEnvironment.Contour lastContour = root.getContours().get(root.getContours().size() - 1);
            Cell c1 = firstContour.getCells().get(0);
            Cell c2 = firstContour.getCells().get(firstContour.getCells().size() - 1);

            int d1 = s.distanceToCell(c1);
            int d2 = s.distanceToCell(c2);
            int d = d1 < d2 ? d1 : d2;
            if ((d == d1 && root.getContours().size() % 2 == 0) ||
                    (d == d2 && root.getContours().size() % 2 == 1)) {
                s = lastContour.getCells().get(0);
            } else s = lastContour.getCells().get(lastContour.getCells().size() - 1);
            power += d + root.getContours().size() * 2 - 2;

            // sắp xếp các cây theo thứ tự gần điểm s
            ArrayList<Tree> children = t2.getChildren();
            final Cell s1 = s;
            Collections.sort(children, new Comparator<Tree>() {
                @Override
                public int compare(Tree o1, Tree o2) {
                    ArrayList<Cell> fistC1 = o1.getRoot().getContours().get(0).getCells();
                    ArrayList<Cell> fistC2 = o2.getRoot().getContours().get(0).getCells();
                    int d1 = Math.min(s1.distanceToCell(fistC1.get(0)), s1.distanceToCell(fistC1.get(fistC1.size() - 1)));
                    int d2 = Math.min(s1.distanceToCell(fistC2.get(0)), s1.distanceToCell(fistC2.get(fistC2.size() - 1)));
                    if (d1 < d2) return 1;
                    else if (d1 > d2) return -1;
                    else return 0;
                }
            });
            for (Tree child : children)
                stack.push(child);
        }

        return power <= B ? true : false;
    }


    public static void main(String[] args) {
        Tree N7 = new Tree(new CcEnvironment(null), null);
        Tree N8 = new Tree(new CcEnvironment(null), null);

        Tree N4 = new Tree(new CcEnvironment(null), new ArrayList<>(Arrays.asList(N8)));
        Tree N6 = new Tree(new CcEnvironment(null), new ArrayList<>(Arrays.asList(N7)));
        Tree N5 = new Tree(new CcEnvironment(null), null);

        Tree N2 = new Tree(new CcEnvironment(null), new ArrayList<>(Arrays.asList(N4, N5)));
        Tree N3 = new Tree(new CcEnvironment(null), new ArrayList<>(Arrays.asList(N6)));

        Tree N1 = new Tree(new CcEnvironment(null), new ArrayList<>(Arrays.asList(N2, N3)));
        GroupTreeAlgorithm g = new GroupTreeAlgorithm(N1, 32);
    }
}
