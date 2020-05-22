package com.robotics.decompose;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author TranHuuThuy
 * Lớp chỉ định môi trường contour-connected
 */

public class CcEnvironment {
    /**
     * Một contour sẽ chứa các cell theo thứ tự từ trái qua phải
     */
    static class Contour {
        private ArrayList<Cell> cells;

        public Contour(ArrayList<Cell> cells) {
            this.cells = cells;
        }

        public ArrayList<Cell> getCells() {
            return cells;
        }

        public int getDistance(){
            return cells.get(0).getDistance();
        }
    }

    private ArrayList<Contour> contours;

    public CcEnvironment(ArrayList<Contour> contours) {
        this.contours = contours;
    }

    public ArrayList<Contour> getContours() {
        return contours;
    }
}
