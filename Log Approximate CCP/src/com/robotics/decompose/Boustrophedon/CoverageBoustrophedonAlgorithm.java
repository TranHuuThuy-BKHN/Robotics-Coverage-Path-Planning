package com.robotics.decompose.Boustrophedon;

import com.robotics.decompose.Cell;
import com.robotics.decompose.Key;

import java.util.*;

public class CoverageBoustrophedonAlgorithm {
    public class Path {
        public ArrayList<Cell> cells;

        public Path() {
            cells = new ArrayList<>();
        }

        public Path(ArrayList<Cell> cells) {
            this.cells = cells;
        }

        public boolean add(Cell c) {
            if (c == null) return false;
            boolean res = cells.add(c);
            if (res) mapVisitedCell.put(c, true);
            return res;
        }

        public boolean remove(Cell c) {
            if (c == null) return false;
            boolean res = cells.remove(c);
            if (res) mapVisitedCell.put(c, null);
            return res;
        }

        public void printPath() {
            for (Cell c : cells) {
                System.out.print("(" + c.getX() + "," + c.getY() + ") -->");
            }
            System.out.println();
        }

        public int length() {
            Cell S = Cell.getChargingStation();
            int length = S.distanceToCell(cells.get(0)) + S.distanceToCell(cells.get(cells.size() - 1));
            for (int i = 0; i < cells.size() - 1; i++) {
                length += cells.get(i).distanceToCell(cells.get(i + 1));
            }
            return length;
        }

        public ArrayList<Cell> getFullPath() {
            ArrayList<Cell> path = new ArrayList<>();
            Cell S = Cell.getChargingStation();
            path.addAll(S.fromToCell(cells.get(0)));
            for (int i = 0; i < cells.size() - 1; i++)
                path.addAll(cells.get(i).fromToCell(cells.get(i + 1)));
            path.addAll(cells.get(cells.size() - 1).fromToCell(S));
            return path;
        }
    }

    class Move {
        Cell c;
        Tree t;

        public Move(Cell c, Tree t) {
            this.c = c;
            this.t = t;
        }
    }

    private int B;
    private ArrayList<Tree> A; // working zone
    private HashMap<Cell, Boolean> mapVisitedCell;

    public CoverageBoustrophedonAlgorithm(int B, ArrayList<Tree> A) {
        this.A = A;
        this.B = B;
        this.mapVisitedCell = new HashMap<>();
    }

    public ArrayList<ArrayList<Path>> coverage() {
        ArrayList<Path> P = new ArrayList<>();
        ArrayList<Path> P1 = new ArrayList<>();
        ArrayList<Path> P2 = new ArrayList<>();
        int i = A.size() - 1;

        while (i >= 0) {
            System.out.println("Coverage Working Zone " + (A.size() - i));
            int B1 = B;
            Tree t = A.get(i);
            Move m = getClosetCell(t);
            if(m==null){
                i--;
                continue;
            }
            System.out.printf("closest cell " + m.c + ", distance " + m.c.getDistance() + ", B = " + B);
            m.c.printCell();
            B1 -= m.c.getDistance();
            Path p = cover(m.c, t, m.t, B1);
            p.printPath();

            P.add(p);
            P1.add(p);

            if (getClosetCell(t) == null) {
                i--;
                continue;
            }

            Path r = remains(t);
            if (r != null) {
                System.out.println("Remain Coverage woking zone " + (A.size() - i));
                P.add(r);
                P2.add(r);
                r.printPath();
                i--;
            }

        }

        return new ArrayList<>(Arrays.asList(P, P1, P2));
    }

    private Path cover(Cell c, Tree root, Tree t, int B) {
        Path path = new Path();
        if (c == null) return path;

        while (c.getDistance() <= B) {
            path.add(c);
            int x = c.getX(), y = c.getY();
            int xs[] = {x - 1, x + 1, x, x}, ys[] = {y, y, y - 1, y + 1};
            boolean flag = false;
            for (int i = 0; i < xs.length; i++) {
                Cell cell = Cell.mapCells.get(new Key(xs[i], ys[i]));
                Boolean b = mapVisitedCell.get(cell);
                if (cell != null && !cell.isObtacle() && (b == null || !b) && isCellInRootTree(cell, t)) {
                    c = cell;
                    B -= 1;
                    flag = true;
                    if (i < 2) break;
                }
            }

            if (!flag) {
                ArrayList<Tree> children = t.getChildren();
                if (children == null || children.size() == 0) {
                    Move m = getClosetCell(root);
                    if (m == null) break;
                    int d = c.distanceToCell(m.c);
                    c = m.c;
                    B -= d;
                    Path p = cover(c, root, m.t, B);
                    for (Cell cell : p.cells)
                        path.add(cell);
                    c = path.cells.get(path.cells.size() - 1);
                } else {
                    final Cell c1 = c;
                    Collections.sort(children, new Comparator<Tree>() {
                        @Override
                        public int compare(Tree o1, Tree o2) {
                            ArrayList<Cell> f1 = o1.getRoot().getRows().get(0).getCells();
                            ArrayList<Cell> l1 = o1.getRoot().getRows().get(o1.getRoot().getRows().size() - 1).getCells();
                            ArrayList<Cell> f2 = o2.getRoot().getRows().get(0).getCells();
                            ArrayList<Cell> l2 = o2.getRoot().getRows().get(o2.getRoot().getRows().size() - 1).getCells();

                            int d1 = Math.min(Math.min(c1.distanceToCell(f1.get(0)), c1.distanceToCell(f1.get(f1.size() - 1))),
                                    Math.min(c1.distanceToCell(l1.get(0)), c1.distanceToCell(l1.get(l1.size() - 1))));

                            int d2 = Math.min(Math.min(c1.distanceToCell(f2.get(0)), c1.distanceToCell(f2.get(f2.size() - 1))),
                                    Math.min(c1.distanceToCell(l2.get(0)), c1.distanceToCell(l2.get(l2.size() - 1))));
                            return d1 - d2;
                        }
                    });

                    Tree child = children.get(0);
                    ArrayList<Cell> f = child.getRoot().getRows().get(0).getCells();
                    ArrayList<Cell> l = child.getRoot().getRows().get(child.getRoot().getRows().size() - 1).getCells();
                    int d1 = c1.distanceToCell(f.get(0)), d2 = c1.distanceToCell(f.get(f.size() - 1));
                    int d3 = c1.distanceToCell(l.get(0)), d4 = c1.distanceToCell(l.get(l.size() - 1));

                    int d = Math.min(Math.min(d1, d2), Math.min(d3, d4));
                    B -= d;
                    if (d == d1) c = f.get(0);
                    else if (d == d2) c = f.get(f.size() - 1);
                    else if (d == d3) c = l.get(0);
                    else c = l.get(l.size() - 1);
                    Path p = cover(c, root, child, B);
                    for (int j = 0; j < p.cells.size(); j++)
                        path.add(p.cells.get(j));
                    c = path.cells.get(path.cells.size() - 1);
                }

                if (c == path.cells.get(path.cells.size() - 1)) break;
            }

        }
        return path;
    }

    private Move getClosetCell(Tree t) {
        EnvironmentBoustrophedon e = t.getRoot();
        Row row = null;

        for (Row r : e.getRows()) {
            ArrayList<Cell> cells = r.getCells();
            Boolean b1 = mapVisitedCell.get(cells.get(0));
            Boolean b2 = mapVisitedCell.get(cells.get(cells.size() - 1));

            if (((b1 == null || !b1) && (b2 != null && b2)) || ((b1 != null && b1) && (b2 == null || !b2))) {
                row = r;
                break;
            }
        }

        if (row != null) {
            Move m;
            for (int i = 0; i < row.getCells().size() - 1; i++) {
                Boolean b1 = mapVisitedCell.get(row.getCells().get(i));
                Boolean b2 = mapVisitedCell.get(row.getCells().get(i + 1));
                if ((b1 == null || !b1) && (b2 != null && b2)) {
                    m = new Move(row.getCells().get(i), t);
                    return m;
                }
                if ((b1 != null && b1) && (b2 == null || !b2)) {
                    m = new Move(row.getCells().get(i + 1), t);
                    return m;
                }
            }
        } else {
            int min_dis = Integer.MAX_VALUE;
            Move m = null;
            for (Row r : e.getRows()) {
                ArrayList<Cell> cells = r.getCells();
                Boolean b1 = mapVisitedCell.get(cells.get(0));
                Boolean b2 = mapVisitedCell.get(cells.size() - 1);
                if (((b1 == null || !b1) && (b2 == null || !b2))) {
                    int d = Math.min(cells.get(0).getDistance(), cells.get(cells.size() - 1).getDistance());
                    if (d < min_dis) {
                        min_dis = d;
                        if (d == cells.get(0).getDistance()) m = new Move(cells.get(0), t);
                        else m = new Move(cells.get(cells.size() - 1), t);
                    }
                }
            }
            if (m != null) return m;
            else {
                if (t.getChildren() == null || t.getChildren().size() == 0) return null;
                System.out.println("Have children");
                ArrayList<Move> list = new ArrayList<>();
                for (Tree child : t.getChildren()) {
                    list.add(getClosetCell(child));
                }
                Iterator iterator = list.iterator();
                while (iterator.hasNext()) {
                    Move move = (Move) iterator.next();
                    if (move == null) iterator.remove();
                }
                Collections.sort(list, new Comparator<Move>() {
                    @Override
                    public int compare(Move o1, Move o2) {
                        return o1.c.getDistance() - o2.c.getDistance();
                    }
                });
                if (list.size() == 0) return null;
                return list.get(0);
            }
        }
        return null;
    }

    private Path remains(Tree t) {
        Move m = getClosetCell(t);
        if (m == null) return null;
        Path p = cover(m.c, t, m.t, B);
        if (getClosetCell(t) == null) {
            return p;
        }
        for (Cell c : p.cells)
            mapVisitedCell.put(c, false);
        return null;
    }

    private boolean isCellInRootTree(Cell c, Tree t) {
        EnvironmentBoustrophedon e = t.getRoot();
        for (Row r : e.getRows()) {
            if (r.getCells().contains(c)) return true;
        }
        return false;
    }

    public static void main(String[] args) {
        Environment2 e = new Environment2("src/com/robotics/data/Environment 3.txt");
        Tree t = e.getTreeBoustrophedon();
        GroupTreeBoustrophedonAlgorithm algorithm = new GroupTreeBoustrophedonAlgorithm(t, 50);
        ArrayList<Tree> A = algorithm.getWorkingZone();

        CoverageBoustrophedonAlgorithm CBA = new CoverageBoustrophedonAlgorithm(50, A);
        ArrayList<ArrayList<Path>> paths = CBA.coverage();
    }
}
