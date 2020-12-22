package byow.Core;

import java.util.Random;

public class Room {
    private int xl;
    private int xr;
    private int yl;
    private int yr;
    private Position center;
    private int roomWidth;
    private int roomHeight;

    Room(int x, int y, int width, int height) {
        xl = x;
        xr = x + width;
        yl = y;
        yr = y + height;
        roomHeight = height;
        roomWidth = width;
        int a = (xl + xr) / 2;
        int b = (yl + yr) / 2;
        center = new Position(a, b);
    }
    public int xl() {
        return xl;
    }
    public int yl() {
        return yl;
    }
    public int xr() {
        return xr;
    }
    public int yr() {
        return yr;
    }
    public int width() {
        return roomWidth;
    }
    public int height() {
        return roomHeight;
    }
    public Position center() {
        return center;
    }
    public Position magicalCenter(Random r) {
//        Random r = new Random(seed);
        int x = r.nextInt((xr - 1) - (xl + 1) - 1) + xl + 1;
        int y = r.nextInt((yr - 1) - (yl + 1) - 1) + yl + 1;
        return new Position(x, y);
    }
}
