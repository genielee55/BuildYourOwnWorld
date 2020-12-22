package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;

import java.io.*;


public class Engine {
    TERenderer ter = new TERenderer();
    MapGenerator map = new MapGenerator();
    Position userPosition;

    private StringBuilder seedString = new StringBuilder();
    private StringBuilder avatarString = new StringBuilder();
    private long seed;
    private TETile[][] interactiveWorld;
    private boolean replay = false;
    private int numFlowers = 10;

    public static final int WIDTH = 80;
    public static final int HEIGHT = 50;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        createStartMenu();
        boolean takeInput = true;
        while (takeInput) {
            if (StdDraw.hasNextKeyTyped()) {
                char initialInput = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (initialInput == 'N') {
                    newGame();
                } else if (initialInput == 'L') {
                    loadGame(0);
                } else if (initialInput == 'Q') {
                    System.exit(0);
                } else if (initialInput == 'R') {
                    replayGame();
                }
            }
        }
    }

    // use seed to create new interactive world
    private void newGame() {
        seed = extractSeed();
        setAvatarName();
        worldInitializer(WIDTH, HEIGHT, seed);
        ter.renderFrame(interactiveWorld);
        interactAvatar();
    }

    private void setAvatarName() {
        boolean gameState = true;

        int width = WIDTH;
        int height = HEIGHT;
        StdDraw.clear(Color.BLACK);
        Font font = new Font("Sans Serif", Font.BOLD, 50);
        StdDraw.setFont(font);
        StdDraw.text(width / 2, height - 15, "Enter Avatar Name: ");
        StdDraw.text(width / 2, height - 35, "Press (+) when finished.");
        StdDraw.show();

        int maxNum = 0;
        while (gameState) {
            if (StdDraw.hasNextKeyTyped()) {
                char nextInput = StdDraw.nextKeyTyped();
                if (nextInput == '+' || maxNum > 19) {
                    gameState = false;
                } else {
                    avatarString.append(nextInput);
                    StdDraw.clear(Color.BLACK);
                    StdDraw.text(width / 2, height - 15, "Enter Avatar Name: ");
                    StdDraw.text(width / 2, height - 25, String.valueOf(avatarString));
                    StdDraw.text(width / 2, height - 35, "Press (+) when finished.");
                    StdDraw.show();
                    maxNum++;
                }
            }
        }
    }

    // extracts the seed from the inputted test (N1456...423S)
    private long extractSeed() {
        int width = WIDTH;
        int height = HEIGHT;
        long seedLocal = 0;
        int maxNum = 0;
        boolean gameState = true;

        StdDraw.clear(Color.BLACK);
        Font font = new Font("Sans Serif", Font.BOLD, 50);
        StdDraw.setFont(font);
        StdDraw.text(width / 2, height - 15, "Enter Seed: ");
        StdDraw.text(width / 2, height - 35, "Press (S) when finished.");
        StdDraw.show();

        while (gameState) {
            if (StdDraw.hasNextKeyTyped()) {
                char nextInput = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (nextInput == 'S' || maxNum > 19) {
                    gameState = false;
                } else {
                    seedLocal = (seedLocal * 10) + Character.getNumericValue(nextInput);
                    seedString.append(nextInput);
                    StdDraw.clear(Color.BLACK);
                    StdDraw.text(width / 2, height - 15, "Enter Seed: ");
                    StdDraw.text(width / 2, height - 25, String.valueOf(seedString));
                    StdDraw.text(width / 2, height - 35, "Press (S) when finished.");
                    StdDraw.show();
                    maxNum++;
                }
            }
        }
        seedString.insert(0, 'N');
        seedString.append('S');
        return seedLocal;
    }

    private void worldInitializer(int width, int height, long seedInitial) {
        ter.initialize(width, height);
        interactiveWorld = new TETile[width][height];
        userPosition = map.generateMap(interactiveWorld, seedInitial);
    }

    private void interactAvatar() {
        boolean takeInput = true;
        while (takeInput) {
            headsUpDisplay(avatarString);
            if (StdDraw.hasNextKeyTyped()) {
                char initialInput = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (initialInput == 'W') {
                    moveAvatar(0, 1, interactiveWorld);
                    seedString.append(initialInput);
                    ter.renderFrame(interactiveWorld);
                } else if (initialInput == 'A') {
                    moveAvatar(-1, 0, interactiveWorld);
                    seedString.append(initialInput);
                    ter.renderFrame(interactiveWorld);
                } else if (initialInput == 'D') {
                    moveAvatar(1, 0, interactiveWorld);
                    seedString.append(initialInput);
                    ter.renderFrame(interactiveWorld);
                } else if (initialInput == 'S') {
                    moveAvatar(0, -1, interactiveWorld);
                    seedString.append(initialInput);
                    ter.renderFrame(interactiveWorld);
                } else if (initialInput == ':') {
                    while (true) {
                        if (StdDraw.hasNextKeyTyped()) {
                            char quitCheck = Character.toUpperCase(StdDraw.nextKeyTyped());
                            if (quitCheck == 'Q') {
                                saveGame();
                                System.exit(0);
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private void headsUpDisplay(StringBuilder avatarName) {
        if (avatarName.length() != 0) {
            ter.renderFrame(interactiveWorld);
            int xPos = (int) StdDraw.mouseX();
            int yPos = (int) StdDraw.mouseY();
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.text(WIDTH / 2, HEIGHT - 2, "You have " + numFlowers + " more flowers left to eat!");
            if (yPos >= HEIGHT) {
                yPos = HEIGHT - 1;
            }
            if (interactiveWorld[xPos][yPos].equals(Tileset.AVATAR)) {
                StdDraw.text(3, HEIGHT - 2, String.valueOf(avatarName));
                StdDraw.text(WIDTH - 3, HEIGHT - 2, String.valueOf(avatarName));
                StdDraw.show();
            } else {
                StdDraw.text(3, HEIGHT - 2, interactiveWorld[xPos][yPos].description());
                StdDraw.text(WIDTH - 3, HEIGHT - 2, String.valueOf(avatarName));
                StdDraw.show();
            }
        } else {
            ter.renderFrame(interactiveWorld);
            int xPos = (int) StdDraw.mouseX();
            int yPos = (int) StdDraw.mouseY();
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.text(3, HEIGHT - 2, interactiveWorld[xPos][yPos].description());
            StdDraw.text(WIDTH / 2, HEIGHT - 2, "You have " + numFlowers + " more flowers left to eat!");
            StdDraw.show();
        }

    }

    private void moveAvatar(int changeX, int changeY, TETile[][] world) {
        int x = userPosition.x() + changeX;
        int y = userPosition.y() + changeY;
        if (world[x][y].equals(Tileset.FLOOR)) {
            world[userPosition.x()][userPosition.y()] = Tileset.FLOOR;
            userPosition = new Position(x, y);
            world[x][y] = Tileset.AVATAR;
        } else if (world[x][y].equals(Tileset.FLOWER)) {
            world[userPosition.x()][userPosition.y()] = Tileset.FLOOR;
            userPosition = new Position(x, y);
            world[x][y] = Tileset.AVATAR;
            numFlowers--;
        }
        if (numFlowers == 0 && world[x][y].equals(Tileset.LOCKED_DOOR)) {
            world[x][y] = Tileset.UNLOCKED_DOOR;
            displayWonScreen();
        }
        if (world[x][y].equals(Tileset.TREE)) {
            displayLostScreen();
        }
    }

    // save avatar position and seed of game while exiting game
    // @source: https://www.javatpoint.com/how-to-create-a-file-in-java
    private void saveGame() {
        String filePath = System.getProperty("user.dir") + File.separator + "saved_data.txt";
        File file = new File(filePath);
        boolean exists = file.exists();
        try {
            if (!exists) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            // stores the string with seed and movements made by user
            oos.writeObject(seedString);
            oos.writeObject(avatarString);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    // load existing game based on data from text file about a previously exited game
    private void loadGame(int inputMethod) {
        //creates the file used to store string
        String filePath = System.getProperty("user.dir") + File.separator + "saved_data.txt";
        File fileGame = new File(filePath);
        boolean pathExists = fileGame.exists();

        try {
            if (pathExists) {
                FileInputStream fis = new FileInputStream(fileGame);
                ObjectInputStream ois = new ObjectInputStream(fis);
                seedString = (StringBuilder) ois.readObject();
                avatarString = (StringBuilder) ois.readObject();
                if (inputMethod == 0) {
                    long loadSeed = loadSeedHelper(seedString);

                    worldInitializer(WIDTH, HEIGHT, loadSeed);
                    // goes through all recorded movements from string and moves avatar accordingly
                    updateState(seedString);
                    ter.renderFrame(interactiveWorld);
                    // allows the user to continue interacting from where user first left the game
                    if (!replay) {
                        interactAvatar();
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private long loadSeedHelper(StringBuilder loadedString) {
        long loadSeed = 0;
        for (int i = 1; i < loadedString.indexOf("S"); i++) {
            loadSeed = (loadSeed * 10) + Character.getNumericValue(loadedString.charAt(i));
        }
        return loadSeed;
    }

    private void updateState(StringBuilder loadedString) {
        for (int j = loadedString.indexOf("S") + 1; j < loadedString.length(); j++) {
            if (loadedString.charAt(j) == 'W') {
                moveAvatar(0, 1, interactiveWorld);
            } else if (loadedString.charAt(j) == 'A') {
                moveAvatar(-1, 0, interactiveWorld);
            } else if (loadedString.charAt(j) == 'D') {
                moveAvatar(1, 0, interactiveWorld);
            } else if (loadedString.charAt(j) == 'S') {
                moveAvatar(0, -1, interactiveWorld);
            } else if (loadedString.charAt(j) == ':') {
                if (loadedString.charAt(j + 1) == 'Q') {
                    loadedString.deleteCharAt(j);
                    loadedString.deleteCharAt(j);
                    saveGame();
                }
            }
            if (replay) {
                ter.renderFrame(interactiveWorld);
                StdDraw.pause(300);
            }
        }
    }

    private void replayGame() {
        replay = true;
        loadGame(0);
    }

    private void displayLostScreen() {
        StdDraw.clear(Color.BLACK);
        Font font = new Font("Sans Serif", Font.BOLD, 50);
        StdDraw.setFont(font);
        StdDraw.text(WIDTH / 2, HEIGHT - 15, "You Lost!");
        StdDraw.text(WIDTH / 2, HEIGHT - 25, "You ran into a tree!");
        StdDraw.text(WIDTH / 2, HEIGHT - 35, "Press (:Q) to exit and try again!");
        StdDraw.show();
        StdDraw.pause(10000);
        boolean takeInput = true;
        while (takeInput) {
            if (StdDraw.hasNextKeyTyped()) {
                char initialInput = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (initialInput == ':') {
                    while (true) {
                        if (StdDraw.hasNextKeyTyped()) {
                            char quitCheck = Character.toUpperCase(StdDraw.nextKeyTyped());
                            if (quitCheck == 'Q') {
                                saveGame();
                                System.exit(0);
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
        }
        System.exit(0);
    }

    private void displayWonScreen() {
        StdDraw.clear(Color.BLACK);
        Font font = new Font("Sans Serif", Font.BOLD, 50);
        StdDraw.setFont(font);
        StdDraw.text(WIDTH / 2, HEIGHT - 15, "You Won!");
        StdDraw.text(WIDTH / 2, HEIGHT - 25, "Congratulations!!");
        StdDraw.text(WIDTH / 2, HEIGHT - 35, "Press (:Q) to exit");
        StdDraw.show();
        StdDraw.pause(10000);
        boolean takeInput = true;
        while (takeInput) {
            if (StdDraw.hasNextKeyTyped()) {
                char initialInput = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (initialInput == ':') {
                    while (true) {
                        if (StdDraw.hasNextKeyTyped()) {
                            char quitCheck = Character.toUpperCase(StdDraw.nextKeyTyped());
                            if (quitCheck == 'Q') {
                                saveGame();
                                System.exit(0);
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
        }
        System.exit(0);
    }

    public static void createStartMenu() {
        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16);
        StdDraw.clear(Color.BLACK);
        Font fontGame = new Font("Sans Serif", Font.BOLD, 50);
        StdDraw.setFont(fontGame);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.enableDoubleBuffering();
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(WIDTH / 2, HEIGHT - 13, "CS61B: THE GAME");
        Font fontNewGame = new Font("Sans Serif", Font.BOLD, 35);
        StdDraw.setFont(fontNewGame);

        StdDraw.text(WIDTH / 2, HEIGHT - 25, "New Game (N)");
        Font fontLoadGame = new Font("Sans Serif", Font.BOLD, 35);
        StdDraw.setFont(fontLoadGame);

        StdDraw.text(WIDTH / 2, HEIGHT - 30, "Load Game (L)");
        Font fontQuit = new Font("Sans Serif", Font.BOLD, 35);
        StdDraw.setFont(fontQuit);

        StdDraw.text(WIDTH / 2, HEIGHT - 35, "Replay Game (R)");
        Font fontReplay = new Font("Sans Serif", Font.BOLD, 35);
        StdDraw.setFont(fontReplay);

        StdDraw.text(WIDTH / 2, HEIGHT - 40, "Quit (Q)");
        StdDraw.show();

    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, both of these calls:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        interactiveWorld = new TETile[WIDTH][HEIGHT];
        if (Character.toUpperCase(input.charAt(0)) == 'L') {
            loadGame(1);
            String newMovements = input.substring(1, input.length());
            seedString.append(newMovements);
        } else {
            seedString.append(input);
        }
        String makeUpperCase = seedString.toString().toUpperCase();
        seedString.setLength(0);
        seedString.append(makeUpperCase);
        String seedInString = seedString.substring(1, seedString.indexOf("S"));
        seed = Long.parseLong(seedInString);
        userPosition = map.generateMap(interactiveWorld, seed);
        updateState(seedString);
        return interactiveWorld;

    }

    public static void main(String[] args) {
//        TERenderer ter = new TERenderer();
//        ter.initialize(WIDTH, HEIGHT);
//        Engine tester = new Engine();
//        ter.renderFrame(tester.interactWithInputString("lddssaawwaassd:q"));

        Engine tester = new Engine();
        tester.interactWithKeyboard();
    }
}
