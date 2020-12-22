package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.Tileset;
import byow.TileEngine.TETile;

import java.util.ArrayList;
import java.util.Random;

import java.util.LinkedList;
import java.util.concurrent.locks.Lock;

/**
 *
 * @source : https://craiky.github.io/tutorial/
 *
 */


public class MapGenerator {
    public static final int LOOP = 15;
    public static final int MAX_WIDTH = 5;
    public static final int MAX_HEIGHT = 5;
    public static final int WIDTH = 80;
    public static final int HEIGHT = 50;
    static Position userPos;
    static Position lockedDoorPos;
    static ArrayList<Position> obstaclePositions;
    static ArrayList<Position> flowerPositions;


    public Position generateMap(TETile[][] world, long seed) {
        Random r = new Random(seed);
        layNothingTiles(world);
        LinkedList<Position> centers = createRandomRooms(r, world);
        createRandomHallways(world, centers);
        addFlowersAndObstacles(r, world);
        world[userPos.x()][userPos.y()] = Tileset.AVATAR;   // add avatar to world
        return userPos;
    }

    private void layNothingTiles(TETile[][] world) {
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    private LinkedList<Position> createRandomRooms(Random r, TETile[][] world) {
//        Random r = new Random(seed);
        LinkedList<Room> rooms = new LinkedList<>();
        LinkedList<Position> centers = new LinkedList<>();
        obstaclePositions = new ArrayList<>();
        flowerPositions = new ArrayList<>();
        lockedDoorPos = new Position(0, 0);
        int i = 0;
        while (i < LOOP) {
            int w = r.nextInt(MAX_WIDTH) + 6;
            int h = r.nextInt(MAX_HEIGHT) + 6;
            int xBound = WIDTH - w - 1;
            int yBound = HEIGHT - h - 1;
            int x = r.nextInt(xBound) + 1;
            int y = r.nextInt(yBound) + 1;
            Room newRoom = new Room(x, y, w, h);
            if (checkOverlap(rooms, newRoom)) {
                rooms.add(newRoom);
                constructRoom(world, newRoom.height(), newRoom.width(), newRoom.xl(), newRoom.yl());
                centers.add(newRoom.magicalCenter(r));
            }
            i += 1;
        }
        // add avatar in one of magical centers
        int num = r.nextInt(centers.size());
        userPos = centers.get(num);

        // add obstacles to a few places in rooms
        while (obstaclePositions.size() < LOOP + 5) {
            int xPos = r.nextInt(WIDTH);
            int yPos = r.nextInt(HEIGHT);
            if (world[xPos][yPos].equals(Tileset.FLOOR)) {
                obstaclePositions.add(new Position(xPos, yPos));
                world[xPos][yPos] = Tileset.TREE;

            }
        }



        return centers;
    }

    private void addFlowersAndObstacles(Random r, TETile[][] world) {


        // add flowers
        while (flowerPositions.size() < LOOP - 5) {
            int xPos = r.nextInt(WIDTH);
            int yPos = r.nextInt(HEIGHT);
            if (world[xPos][yPos].equals(Tileset.FLOOR)) {
                flowerPositions.add(new Position(xPos, yPos));
                world[xPos][yPos] = Tileset.FLOWER;
            }
        }
        // add locked door
        while (!world[lockedDoorPos.x()][lockedDoorPos.y()].equals(Tileset.LOCKED_DOOR)) {
            int xPos = r.nextInt(WIDTH);
            int yPos = r.nextInt(HEIGHT);
            if (world[xPos][yPos].equals(Tileset.WALL)) {
                lockedDoorPos = new Position(xPos, yPos);
                world[xPos][yPos] = Tileset.LOCKED_DOOR;
            }
        }
    }



    private void constructRoom(TETile[][] world, int h, int w, int xl, int yr) {
        for (int x = xl; x < xl + w; x += 1) {
            for (int y = yr; y < yr + h; y += 1) {
                world[x][y] = Tileset.WALL;
                if (x >= (xl + 1) && x < xl + w - 1) {
                    if (y >= (yr + 1) && y < yr + h - 1) {
                        world[x][y] = Tileset.FLOOR;
                    }
                }
            }
        }
    }

    private void createRandomHallways(TETile[][] world, LinkedList<Position> centers) {
        for (int i = 1; i < centers.size(); i += 1) {
            createHorizontalHallways(world, centers.get(i), centers.get(i - 1));
            createVerticalHallways(world, centers.get(i), centers.get(i - 1));
        }
        addWalls(world);
    }

    private void createHorizontalHallways(TETile[][] world, Position cur, Position prev) {
        int x1 = prev.x();
        int x2 = cur.x();
        int y = prev.y();
        int x = 0;
        if (x1 < x2) {
            x = x1;
        } else {
            x = x2;
        }
        int max = 0;
        if (x1 > x2) {
            max = x1 + 1;
        } else {
            max = x2 + 1;
        }
        for (; x < max; x += 1) {
            world[x][y] = Tileset.FLOOR;
        }
    }


    private void createVerticalHallways(TETile[][] world, Position cur, Position prev) {
        int x = cur.x();
        int y1 = prev.y();
        int y2 = cur.y();
        int y = 0;
        if (y1 < y2) {
            y = y1;
        } else {
            y = y2;
        }
        int max = 0;
        if (y1 > y2) {
            max = y1 + 1;
        } else {
            max = y2 + 1;
        }
        for (; y < max; y += 1) {
            world[x][y] = Tileset.FLOOR;
        }
    }
    public void addWalls(TETile[][] world) {
        // adds wall tiles above and below hallway floor tiles
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                if (world[x][y] == Tileset.FLOOR && world[x + 1][y] == Tileset.NOTHING) {
                    world[x + 1][y] = Tileset.WALL;
                }
                if (world[x][y] == Tileset.FLOOR && world[x - 1][y] == Tileset.NOTHING) {
                    world[x - 1][y] = Tileset.WALL;
                }
                if (world[x][y] == Tileset.FLOOR && world[x][y + 1] == Tileset.NOTHING) {
                    world[x][y + 1] = Tileset.WALL;
                }
                if (world[x][y] == Tileset.FLOOR && world[x][y - 1] == Tileset.NOTHING) {
                    world[x][y - 1] = Tileset.WALL;
                }
                if (world[x][y] == Tileset.WALL && world[x - 1][y] == Tileset.FLOOR) {
                    if (world[x + 1][y] == Tileset.FLOOR) {
                        world[x][y] = Tileset.FLOOR;
                    }
                }
                if ((world[x][y] == Tileset.WALL && world[x][y - 1] == Tileset.FLOOR)) {
                    if (world[x][y + 1] == Tileset.FLOOR) {
                        world[x][y] = Tileset.FLOOR;
                    }
                }
                if (world[x][y] == Tileset.FLOOR) {
                    if (world[x][y + 1] == Tileset.WALL && world[x + 1][y] == Tileset.WALL) {
                        if (world[x + 1][y + 1] == Tileset.NOTHING) {
                            world[x + 1][y + 1] = Tileset.WALL;
                        }
                    } else if (world[x][y + 1] == Tileset.WALL && world[x - 1][y] == Tileset.WALL) {
                        if (world[x - 1][y + 1] == Tileset.NOTHING) {
                            world[x - 1][y + 1] = Tileset.WALL;
                        }
                    } else if (world[x][y - 1] == Tileset.WALL && world[x + 1][y] == Tileset.WALL) {
                        if (world[x + 1][y - 1] == Tileset.NOTHING) {
                            world[x + 1][y - 1] = Tileset.WALL;
                        }
                    } else if (world[x][y - 1] == Tileset.WALL && world[x - 1][y] == Tileset.WALL) {
                        if (world[x - 1][y - 1] == Tileset.NOTHING) {
                            world[x - 1][y - 1] = Tileset.WALL;
                        }
                    }
                }
            }
        }
    }
    public boolean overlapChecker(Room room1, Room room2) {
        if (room1.xl() <= room2.xr() && room1.xr() >= room2.xl()) {
            if (room1.yl() <= room2.yr() && room1.yr() >= room2.yl()) {
                return true;
            }
        }
        return false;
    }
    private boolean checkOverlap(LinkedList<Room> rooms, Room newRoom) {
        for (int i = 0; i < rooms.size(); i += 1) {
            if (overlapChecker(newRoom, rooms.get(i))) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {

        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] world = new TETile[WIDTH][HEIGHT];

        MapGenerator finalMap = new MapGenerator();
        finalMap.generateMap(world, 45355);
        ter.renderFrame(world);
    }
}
