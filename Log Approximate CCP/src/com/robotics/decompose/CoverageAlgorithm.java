package com.robotics.decompose;

import java.sql.Array;
import java.util.*;

public class CoverageAlgorithm {

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
                System.out.print("(" + c.x + "," + c.y + ") -->");
            }
            System.out.println();
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

    public CoverageAlgorithm(int B, ArrayList<Tree> A) {
        this.B = B;
        this.A = A;
        mapVisitedCell = new HashMap<>();
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
            Move m = getClosestCell(t);
            System.out.printf("closest cell");
            m.c.printCell();
            B1 -= m.c.getDistance();
            Path p = cover(m.c, t, m.t, B1);
            p.printPath();

            P.add(p);
            P1.add(p);

            if(getClosestCell(t) == null){
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

    /**
     * @param c vị trí gần trạm sạc nhất chưa thăm
     * @param t cây trong tập working zone chưa c
     * @param B năng lượng robot tại c
     * @return đường dẫn di chuyển của robot trong t
     */
    private Path cover(Cell c, Tree root, Tree t, int B) {
        Path path = new Path();
        if (c == null) return path;

        while (c.getDistance() <= B) {

            path.add(c);

            int x = c.x, y = c.y;

            int xs[] = {x - 1, x - 1, x + 1, x + 1,
                    x, x, x - 1, x + 1};
            int ys[] = {y - 1, y + 1, y - 1, y + 1,
                    y - 1, y + 1, y, y};

            boolean flag = false;

            // di chuyển trong root của t hiện tại
            for (int i = 0; i < xs.length; i++) {
                Cell cell = Cell.mapCells.get(new Key(xs[i], ys[i]));
                if (cell != null && cell.isObtacle() == false && (mapVisitedCell.get(cell) == null || mapVisitedCell.get(cell) == false) && isCellInRootTree(c, t)) {
                    if (cell.getDistance() == c.getDistance() && i < 4) {
                        c = cell;
                        B -= 2;
                        flag = true;
                        break;
                    } else if (cell.getDistance() == c.getDistance() + 1 && i >= 4) {
                        // check end contour
                        int xc = cell.x, yc = cell.y;
                        int xcs[] = {xc - 1, xc - 1, xc + 1, xc + 1};
                        int ycs[] = {yc - 1, yc + 1, yc - 1, yc + 1};
                        int d = 0;
                        for (int j = 0; j < xcs.length; j++) {
                            Cell nc = Cell.mapCells.get(new Key(xcs[j], ycs[j]));
                            if (nc != null && nc.getDistance() == cell.getDistance() && isCellInRootTree(nc, t) && (mapVisitedCell.get(nc) == null || mapVisitedCell.get(nc) == false)) {
                                d++;
                            }
                        }
                        if (d == 1) {
                            c = cell;
                            B -= 1;
                            flag = true;
                            break;
                        }
                    }
                }
            }

            // nếu đã thăm root của t
            if (!flag) {
                ArrayList<Tree> children = t.getChildren();

                if (children == null || children.size() == 0) {
                    // di chuyen den node gan nhat chua tham het
                    Move m = getClosestCell(root);
                    if (m == null) break;
                    int d = c.distanceToCell(m.c);
                    c = m.c;
                    B -= d;
                    Path p = cover(c, root, m.t, B);
                    for (Cell cell : p.cells)
                        path.add(cell);
                    c = path.cells.get(path.cells.size() - 1);
                } else {
                    // sắp xếp các cây con theo khoảng cách từ c
                    final Cell c1 = c;
                    Collections.sort(children, new Comparator<Tree>() {
                        @Override
                        public int compare(Tree o1, Tree o2) {
                            ArrayList<Cell> first = o1.getRoot().getContours().get(0).getCells();
                            ArrayList<Cell> second = o2.getRoot().getContours().get(0).getCells();
                            int d1 = Math.min(c1.distanceToCell(first.get(0)), c1.distanceToCell(first.get(first.size() - 1)));
                            int d2 = Math.min(c1.distanceToCell(second.get(0)), c1.distanceToCell(second.get(second.size() - 1)));
                            return d1 - d2;
                        }
                    });
                    //-------------------------------
                    ArrayList<Cell> first = children.get(0).getRoot().getContours().get(0).getCells();
                    int d1 = c.distanceToCell(first.get(0)), d2 = c.distanceToCell(first.get(first.size() - 1));
                    int d = d1 < d2 ? d1 : d2;
                    c = d == d1 ? first.get(0) : first.get(first.size() - 1);
                    B -= d;
                    Path p = cover(c, root, children.get(0), B);
                    for (Cell cell : p.cells)
                        path.add(cell);
                    c = path.cells.get(path.cells.size() - 1);

                }

            }
            if (c == path.cells.get(path.cells.size() - 1)) break;
        }

        return path;
    }


    private Path remains(Tree t) {
        Move m = getClosestCell(t);
        if (m == null) return null;
        Path p = cover(m.c, t, m.t, B);
        if (getClosestCell(t) == null) {
            return p;
        }
        for (Cell c : p.cells)
            mapVisitedCell.put(c, false);
        return null;
    }

    private Move getClosestCell(Tree t) {
        ArrayList<CcEnvironment.Contour> contours = t.getRoot().getContours();
        ArrayList<Cell> first = contours.get(0).getCells();
        if ((mapVisitedCell.get(first.get(0)) == null || mapVisitedCell.get(first.get(0)) == false)
                && (mapVisitedCell.get(first.get(first.size() - 1)) == null || mapVisitedCell.get(first.get(first.size() - 1)) == false))
            return new Move(first.get(0), t);

        for (CcEnvironment.Contour c : contours) {
            ArrayList<Cell> cells = c.getCells();
            Cell fis = cells.get(0), las = cells.get(cells.size() - 1);

            if ((mapVisitedCell.get(fis) == null || !mapVisitedCell.get(fis))
                    && (mapVisitedCell.get(las) == null || !mapVisitedCell.get(las)))
                return new Move(fis, t);

            for (int i = 0; i < cells.size() - 1; i++) {
                if ((mapVisitedCell.get(cells.get(i)) == null || !mapVisitedCell.get(cells.get(i)))
                        && (mapVisitedCell.get(cells.get(i + 1)) != null && mapVisitedCell.get(cells.get(i + 1)))) {
                    return new Move(cells.get(i), t);
                }

                if ((mapVisitedCell.get(cells.get(i)) != null && mapVisitedCell.get(cells.get(i)))
                        && (mapVisitedCell.get(cells.get(i + 1)) == null || !mapVisitedCell.get(cells.get(i + 1)))) {
                    return new Move(cells.get(i + 1), t);
                }
            }
        }

        // nếu nút gốc đã được thăm toàn bộ, thì ta cần xem các node con
        ArrayList<Move> cloestCellOfChildren = new ArrayList<>();
        if (t.getChildren() != null) {
            for (Tree child : t.getChildren()) {
                Move m = getClosestCell(child);
                if (m != null)
                    cloestCellOfChildren.add(getClosestCell(child));
            }
        }

        if (cloestCellOfChildren.size() == 0) return null;

        Collections.sort(cloestCellOfChildren, new Comparator<Move>() {
            @Override
            public int compare(Move o1, Move o2) {
                return o1.c.getDistance() - o2.c.getDistance();
            }
        });

        return cloestCellOfChildren.get(0);
    }

    private boolean isCellInRootTree(Cell c, Tree t) {
        ArrayList<CcEnvironment.Contour> contours = t.getRoot().getContours();
        for (CcEnvironment.Contour contour : contours) {
            if (contour.getCells().contains(c))
                return true;
        }
        return false;
    }
}
