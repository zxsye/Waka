/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package ghost;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import processing.core.*;


class AppTest {
    private App app;

    @BeforeEach
    public void before() {
        app = new App();
        PApplet.runSketch(new String[] {""}, app);
        // app.delay(1000); // Wait for assets to be loaded in
        app.noLoop();
        app.setup();
    }

    @Test
    public void simpleTest() {
        App classUnderTest = new App();
        assertNotNull(classUnderTest);
    }
}
