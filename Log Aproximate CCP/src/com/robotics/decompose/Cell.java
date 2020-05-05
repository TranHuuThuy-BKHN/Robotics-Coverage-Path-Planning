package com.robotics.decompose;

public class Cell {

    /* Tọa đô so với trạm sạc, trạm sạc O(0, 0) */
    private int x;
    private int y;

    /*true : chướng ngại vật
     false : ô đi được */
    private boolean obtacle;


    public Cell(int x, int y, boolean obtacle) {
        this.x = x;
        this.y = y;
        this.obtacle = obtacle;
    }

    /**
     * @return contour của ô, nếu là chướng ngại vật trả về -1
     */
    public int getContour() {
        if (obtacle) return -1;
        else return Math.abs(x) + Math.abs(y);
    }


}
