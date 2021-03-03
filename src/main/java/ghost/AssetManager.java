package ghost;

import java.util.*;

import java.io.FileReader;
import java.io.Reader;
import java.io.IOException;

import org.json.simple.*;
import org.json.simple.parser.*;

public class AssetManager {

    private App app;

    private String arenaName;
    private int lives;
    private int wakaSpeed;
    private List<Integer> modeLengths;

    private Arena arena;
    private List<Ghost> ghosts; // change to Ghost list
    private int frightenedLength;
    private Waka player;

    private int deathTimer;
    private boolean isWin;
    private boolean debug;

    /** Constructs an asset manager which holds assets and information of the game, such as
     * the length of Ghost modes, whether the game as won or not, and controls debugging mode.
     * Player and the Ghosts are also stored here.
     * @param app App file for passing along to Player and Ghosts for drawing
    */
    public AssetManager(App app) {
        this.app = app;
        this.modeLengths = new ArrayList<Integer>();
        this.ghosts = new ArrayList<Ghost>();
        this.deathTimer = 0;
        this.isWin = false;
        this.debug = false;
    }

    /** Parses the configuration file, extracting game parameters from it.
     * Reads in the player's lives, speed, modeLengths for ghosts, frightened length
     * and creates all game entities, including player and ghosts.
    */
    public void parseConfig() {
        // Parse configuration file
        JSONParser parser = new JSONParser();
        try (Reader reader = new FileReader("config.json"))
        {
            JSONObject obj = (JSONObject) parser.parse(reader);

            this.arenaName = (String) obj.get("map");  // map is arena now
            this.lives = (int)(long) obj.get("lives");
            this.wakaSpeed = (int)(long) obj.get("speed");

            JSONArray arr = (JSONArray) obj.get("modeLengths");
            for (int i = 0; i < arr.size(); i++) {
                this.modeLengths.add((int)(long) arr.get(i));
            }

            this.frightenedLength = (int)(long) obj.get("frightenedLength");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Load images and Player objects
        this.arena = new Arena(this.app, this.arenaName);
        int[] wakaStart = this.arena.getWakaStart();
        this.player = new Waka(wakaStart[0], wakaStart[1], this.wakaSpeed, this.lives, this.app);

        // LOAD GHOSTS BASED ON TYPES
        this.ghosts.clear(); // clear when restarting game
        int i = 0;
        for (int[] coord : this.arena.getGhostStart()) {
            int x = coord[0];
            int y = coord[1];
            if (i % 4 == 0) {
                this.ghosts.add(new Ghost(x, y, this.wakaSpeed, this.app, this.modeLengths));
            } else if (i % 4 == 1) {
                this.ghosts.add(new Ambusher(x, y, this.wakaSpeed, this.app, this.modeLengths));
            } else if (i % 4 == 2) {
                this.ghosts.add(new Ignorant(x, y, this.wakaSpeed, this.app, this.modeLengths));
            } else {
                this.ghosts.add(new Whim(x, y, this.wakaSpeed, this.app, this.modeLengths));
            }
            i++;
        }

    }

    /** Returns the arena object of the game, where the grid of cells are stored.
     * @return arena of the game
    */
    public Arena getArena() {
        return this.arena;
    }

    /** Returns the list of ghosts in the game.
     * @return the list of ghosts in the game
    */
    public List<Ghost> getGhosts() {
        return this.ghosts;
    }

    /** Returns the player of the game.
     * @return the player of the game
    */
    public Waka getPlayer() {
        return this.player;
    }

    /** Returns the frightened length of ghosts, in seconds.
     * @return frightened length of ghosts
    */
    public int getFrightenedLength() {
        return this.frightenedLength;
    }

    /** Sets the death timer to -600 (counts upwards only) and resets the game by
     * calling this.app.setup()
    */
    public void death() {
        this.deathTimer = -600;
        this.app.setup();
    }

    /** Draws the assets of the game, including the player, all ghosts, and the arena
     * by calling each entity separately to draw themselves. If the player dies then
     * draws the game over screen for 10 seconds or until death timer runs out.
     * If the player wins, then display the win screen.
     * @return 1, -1, or 0 symbolising winscreen, deathscreen, gameplay.
    */
    public int draw() {
        if (this.deathTimer++ < 0) { //TODO move to assetmanager or arena
            app.text("GAME OVER", App.WIDTH/2 - 65, App.HEIGHT/2);
            return -1;
        } else if (this.isWin) {
            app.text("YOU WIN", App.WIDTH/2 - 55, App.HEIGHT/2);
            return 1;
        }
        // Main loop here
        this.getPlayer().tick();
        for (Ghost g : this.getGhosts()) {
            g.tick();
        }

        // Drawing player
        this.getArena().draw();
        this.getPlayer().draw();
        this.getPlayer().drawLives();
        for (Ghost g : this.getGhosts()) {
            g.draw();
            if (this.debug == true) { // move this away
                g.drawDebugLine();
            }
        }
        return 0;
    }

    /** Takes in user input and triggers Waka to respond. If space is pressed, debug
     * mode is activated. Returns false if currently the game is over.
     * @param key the char to check for CODED keys
     * @param keyCode the key that was pressed
     * @return true or false, whether input was registered
    */
    public boolean userInput(char key, int keyCode) {
        if (deathTimer < 0) {
            return false;
        }
        if (key == App.CODED) {
            this.player.changeDirection(keyCode);
        }
        if (keyCode == ' ') {
            this.debug = !this.debug;
        }
        return true;
    }

    /**
     * Set the game win status to true.
    */
    public void win() {
        this.isWin = true;
    }

    /**
     * Returns if the game is currently over, i.e. the player is dead.
     * @return true if player is dead and game is currently over.
    */
    public boolean isGameOver() {
        if (this.deathTimer < 0) {
            return true;
        }
        return false;
    }

    /**
     * Returns whether the debug mode is currently on.
     * @return true if debug mode is on
    */
    public boolean isDebug() {
        return this.debug;
    }
}
