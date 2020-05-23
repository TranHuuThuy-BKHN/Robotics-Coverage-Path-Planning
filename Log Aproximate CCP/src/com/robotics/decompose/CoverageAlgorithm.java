package com.robotics.decompose;

import java.sql.Array;
import java.util.*;

public class CoverageAlgorithm {

    class Path {
        ArrayList<Cell> cells;

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
    }

    private int B;
    private ArrayList<Tree> A; // working zone
    private HashMap<Cell, Boolean> mapVisitedCell;

    public CoverageAlgorithm(int B, ArrayList<Tree> A) {
        this.B = B;
        this.A = A;
        mapVisitedCell = new HashMap<>();
    }

    private ArrayList<ArrayList<Path>> coverage() {
        ArrayList<Path> P = new ArrayList<>();
        ArrayList<Path> P1 = new ArrayList<>();
        ArrayList<Path> P2 = new ArrayList<>();
        int i = A.size() - 1;
        while (i >= 0) {
            int B1 = B;
            Tree t = A.get(i);
            Cell c = getClosestCell(t);
            B1 -= c.getDistance();
            Path p = cover(c, t, B1);

            P.add(p);
            P1.add(p);

            Path p2 = remains(t);
            if (p2 != null) {
                P.add(p2);
                P2.add(p2);
                i--;
            }
        }

        return new ArrayList<>(Arrays.asList(P, P1, P2));
    }

    private Path cover(Cell c, Tree t, int B) {
        Path path = new Path();
        while (c.getDistance() <= B) {

            path.add(c);

            int x = c.x, y = c.y;

            int xs[] = {x - 1, x - 1, x + 1, x + 1,
                    x, x, x - 1, x + 1};
            int ys[] = {y - 1, y + 1, y - 1, y + 1,
                    y - 1, y + 1, y, y};
            // flag kiểm tra xem từ vị trí hiện tại có thể di chuyển đế vị trí hàng xóm nào nữa không
            boolean flag = false;
            for (int i = 0; i < xs.length; i++) {
                Cell cell = Cell.mapCells.get(new Key(xs[i], ys[i]));
                if (cell != null && cell.isObtacle() == false && (mapVisitedCell.get(cell) == null || mapVisitedCell.get(cell) == false) && isCellInTree(c, t)) {
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
                            if (nc != null && nc.getDistance() == cell.getDistance() && (mapVisitedCell.get(nc) == null || mapVisitedCell.get(nc) == false)) {
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

            // nếu đã thăm toàn bộ đên node lá của tree, ta cần di chuyển đến node khác
            if (!flag) {
                ArrayList<Tree> children = t.getChildren();
                Cell cell = null;
                Tree tree = null;
                int minDis = Integer.MIN_VALUE;

                for (Tree child : children) {
                    ArrayList<Cell> cells = child.getRoot().getContours().get(0).getCells();
                    Cell cell1;
                    int d;
                    int d1 = c.distanceToCell(cells.get(0)), d2 = c.distanceToCell(cells.get(cells.size() - 1));
                    if (d1 < d2) {
                        cell1 = cells.get(0);
                        d = d1;
                    } else {
                        cell1 = cells.get(cells.size() - 1);
                        d = d2;
                    }
                    if (d > minDis) {
                        minDis = d;
                        cell = cell1;
                        tree = child;
                    }
                }
                Path p = cover(cell, tree, B - minDis);
                if (p.cells.size() != 0) {
                    path.cells.addAll(p.cells);
                }
            }

        }
        return path;
    }

    private Path remains(Tree t) {
        Cell c = getClosestCell(t);
        if (c == null) return null;
        int B1 = B - c.getDistance();
        Path path = cover(c, t, B1);
        Cell lastCell = path.cells.get(path.cells.size() - 1);
        int x = lastCell.x, y = lastCell.y;
        int xs[] = {x - 1, x - 1, x - 1, x, x, x + 1, x + 1, x + 1},
                ys[] = {y + 1, y, y - 1, y + 1, y - 1, y - 1, y, y + 1};
        boolean flag = false;
        for (int i = 0; i < xs.length; i++) {
            Cell cell = Cell.mapCells.get(new Key(xs[i], ys[i]));
            if ((mapVisitedCell.get(cell) == null || mapVisitedCell.get(cell) == false) && isCellInTree(cell, t)) {
                flag = true;
                break;
            }
        }

        if (flag) {
            for (Cell cell : path.cells)
                mapVisitedCell.put(cell, false);
            return null;
        }
        return path;
    }

    private Cell getClosestCell(Tree t) {
        ArrayList<CcEnvironment.Contour> contours = t.getRoot().getContours();
        ArrayList<Cell> first = contours.get(0).getCells();
        if ((mapVisitedCell.get(first.get(0)) == null || mapVisitedCell.get(first.get(0)) == false)
                && (mapVisitedCell.get(first.get(first.size() - 1)) == null || mapVisitedCell.get(first.get(first.size() - 1)) == false))
            return first.get(0);

        for (CcEnvironment.Contour c : contours) {
            ArrayList<Cell> cells = c.getCells();
            Cell fis = cells.get(0), las = cells.get(cells.size() - 1);

            if ((mapVisitedCell.get(fis) == null || !mapVisitedCell.get(fis))
                    && (mapVisitedCell.get(las) == null || !mapVisitedCell.get(las)))
                return fis;

            for (int i = 0; i < cells.size() - 1; i++) {
                if ((mapVisitedCell.get(cells.get(i)) == null || !mapVisitedCell.get(cells.get(i)))
                        && (mapVisitedCell.get(cells.get(i + 1)) != null && mapVisitedCell.get(cells.get(i + 1)))) {
                    return cells.get(i);
                }

                if ((mapVisitedCell.get(cells.get(i)) != null && mapVisitedCell.get(cells.get(i)))
                        && (mapVisitedCell.get(cells.get(i + 1)) == null || !mapVisitedCell.get(cells.get(i + 1)))) {
                    return cells.get(i + 1);
                }
            }
        }

        // nếu nút gốc đã được thăm toàn bộ, thì ta cần xem các node con
        ArrayList<Cell> cloestCellOfChildren = new ArrayList<>();
        if (t.getChildren() != null) {
            for (Tree child : t.getChildren()) {
                Cell c = getClosestCell(child);
                if (c != null)
                    cloestCellOfChildren.add(getClosestCell(child));
            }
        }

        if (cloestCellOfChildren.size() == 0) return null;

        Collections.sort(cloestCellOfChildren, new Comparator<Cell>() {
            @Override
            public int compare(Cell o1, Cell o2) {
                return o1.getDistance() - o2.getDistance();
            }
        });

        return cloestCellOfChildren.get(0);
    }

    private boolean isCellInTree(Cell c, Tree t) {
        ArrayList<CcEnvironment.Contour> contours = t.getRoot().getContours();
        for (CcEnvironment.Contour contour : contours) {
            if (contour.getCells().contains(c))
                return true;
        }

        if (t.getChildren() != null) {
            for (Tree child : t.getChildren()) {
                if (isCellInTree(c, child)) return true;
            }
        }

        return false;
    }
}
