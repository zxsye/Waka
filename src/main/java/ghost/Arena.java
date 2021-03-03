package ghost;

import processing.core.PImage;
import java.io.*;
import java.util.*;

/*
 * Arena class contains the image files for the arena. It is responsible for:
 * - rendering the arena
 * - handling player object and arena interactions
*/

public class Arena {
    public static final int PIXEL = 18;

    private App app;
    private Map<Character, PImage> arenaArt;
    private Cell[][] grid;
    private List<int[]> ghostStart;
    private int[] wakaStart;
    private int totalFruits;

    /**
     * Constructor for Arena of the game.
     * @param applet App for drawing
     * @param filename Name of map file (usually .txt)
    */
    public Arena(App applet, String filename) {

        this.arenaArt = new HashMap<Character, PImage>();
        arenaArt.put('1', applet.loadImage("src/main/resources/horizontal.png"));
        arenaArt.put('2', applet.loadImage("src/main/resources/vertical.png"));
        arenaArt.put('3', applet.loadImage("src/main/resources/upLeft.png"));
        arenaArt.put('4', applet.loadImage("src/main/resources/upRight.png"));
        arenaArt.put('5', applet.loadImage("src/main/resources/downLeft.png"));
        arenaArt.put('6', applet.loadImage("src/main/resources/downRight.png"));
        arenaArt.put('7', applet.loadImage("src/main/resources/fruit.png"));
        arenaArt.put('8', applet.loadImage("src/main/resources/superfruit.png"));
        arenaArt.put('9', applet.loadImage("src/main/resources/soda.png"));

        this.app = applet;
        this.wakaStart = null;
        this.ghostStart = null;

        this.grid = new Cell[36][28];
        this.ghostStart = new ArrayList<int[]>();
        this.totalFruits = 0;
        this.parseGrid(filename);
    }

    /**
     * Parse method for Arena instance, called from constructor of Arena.
     * Creates a 2D array of Cell objects for the map.
     * @param filename name of map file
    */
    public void parseGrid(String filename) {
        File path = new File(filename);
        ArrayList<char[]> matrix = new ArrayList<char[]>();

        // Parse in alphanumerics on the resource file
        try {
            Scanner parser = new Scanner(path);
            // Scan each row and store in array
            char[] row;
            while (parser.hasNextLine()) {
                row = parser.nextLine().toCharArray();
                matrix.add(row);
            }
            parser.close();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // Create image grid
        for (int i = 0; i < 36; i++) {

            char[] row = matrix.get(i);
            for (int j = 0; j < 28; j++) {

                char e = row[j];
                Cell cellToAdd;
                if (e == 'p') {
                    wakaStart = new int[] {j, i};
                    cellToAdd = new Path( j, i, this.arenaArt.get(e), false );
                } else if (e == 'g') {
                    ghostStart.add(new int[] {j, i});
                    cellToAdd = new Path( j, i, this.arenaArt.get(e), false ); // ghost
                } else if (e == '7') {
                    totalFruits++;
                    cellToAdd = new Path( j, i, this.arenaArt.get(e), true); // fruit
                } else if (e == '8') {
                    totalFruits++;
                    cellToAdd = new SuperFruit( j, i, this.arenaArt.get(e) );
                } else if (e == '9') {
                    cellToAdd = new Soda( j, i, this.arenaArt.get(e) );
                } else {
                    cellToAdd = new Wall( j, i, this.arenaArt.get(e));
                }
                grid[i][j] = cellToAdd; // add cell to the grid
            }

        }
    }

    /**
     * Returns the starting coordinates of the Waka
     * @return Waka object
    */
    public int[] getWakaStart() {

        return wakaStart;
    }

    /**
     * Returns a list of the starting coordinates of all Ghosts
     * @return list of Ghost coordinates as int[x, y]
    */
    public List<int[]> getGhostStart() {
        return ghostStart;
    }

    /**
     * Returns a Map of the arenaArt PImages
     * @return Map object with key as Character corresponding to cell type,
     * and the value as PImage of the cell
    */
    public Map<Character, PImage> getArenaArt() {
        return this.arenaArt;
    }

    /**
     * Returns the grid of the Arena
     * @return 2D array of Cell objects
    */
    public Cell[][] getGrid() { return this.grid; }

    /**
     * Returns the total number of fruits on the map
     * @return number of fruit
    */
    public int getTotalFruits() { return this.totalFruits; }

    /**
     * Draws each cell of the map, given that the image of the Cell is not null
    */
    public void draw() {
        for (int i = 0; i < 36; i++) {
            for (int j = 0; j < 28; j++) {
                // if (grid[i][j] != null) {
                    // Tricky plotting the grid
                PImage cellArt = grid[i][j].getArt();

                if (cellArt != null) {
                    app.image(cellArt, j*16, i*16);
                    // }
                }
            }
        }
    }

}
