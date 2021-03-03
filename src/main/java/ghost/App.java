package ghost;

import processing.core.PApplet;
import processing.core.PFont;

public class App extends PApplet {

    public static final int WIDTH = 448;
    public static final int HEIGHT = 576;

    private AssetManager manager;
    private PFont font;

    /**
    * Constructs an app file with a manager attribute.
    */
    public App() {
        this.manager = new AssetManager(this);
    }

    /**
    * Sets up the entire game by calling manager to parse the configuration file.
    * Sets framerate to 60fps, and loads font for printing messages.
    */
    public void setup() {
        frameRate(60);
        this.manager.parseConfig();

        // Load Font
        this.font = this.createFont("src/main/resources/PressStart2P-Regular.ttf", 2, true);
        textFont(this.font, 15);
        fill(255);
        this.stroke(255); // DEBUG LINE COLOUR

    }

    /**
    * Sets the window sizes for the display.
    */
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
    * Draws the current state of the game. Drawing a black background and calling
    * the manager to draw all other game assets.
    */
    public void draw() {
        background(0, 0, 0);
        this.getManager().draw();
    }

    /**
    * Returns the game asset manager.
    * @return the game asset manager.
    */
    public AssetManager getManager() {
        return this.manager;
    }

    /**
    * Passes any key pressed to the asset manager.
    */
    public void keyPressed() {
        // only change direction when not closed
        this.manager.userInput(key, keyCode);

    }

    /**
    * Begins the game application.
    * @param args the command line, or shell, arguments
    */
    public static void main(String[] args) {
        PApplet.main("ghost.App");
    }
}
