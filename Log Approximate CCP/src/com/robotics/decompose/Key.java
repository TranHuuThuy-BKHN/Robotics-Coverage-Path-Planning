package com.robotics.decompose;

public class Key {
    int x;
    int y;

    public Key(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Key == false) return false;
        return x == ((Key) obj).x && y == ((Key) obj).y;
    }

    @Override
    public int hashCode() {
        int result = 17; // any prime number
        result = 31 * result + Integer.valueOf(x).hashCode();
        result = 31 * result + Integer.valueOf(y).hashCode();
        return result;
    }
}
