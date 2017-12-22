package cat.xtec.ioc.utils;

public class Settings {

    // Mida del joc, s'escalarà segons la necessitat
    public static final int GAME_WIDTH = 300;
    public static final int GAME_HEIGHT = 170;

    // Propietats de la nau
    public static final float SPACECRAFT_VELOCITY = 100;
    public static final int SPACECRAFT_WIDTH = 35;
    public static final int SPACECRAFT_HEIGHT = 15;
    public static final float SPACECRAFT_STARTX = 20;
    public static final float SPACECRAFT_STARTY = GAME_HEIGHT/2 - SPACECRAFT_HEIGHT/2;

    // TODO Exercici 2 - configurem les propietats del botó pausa
    // Propietats del pause
    public static final int PAUSE_WIDTH = 25;
    public static final int PAUSE_HEIGHT = 25;
    public static final float PAUSE_STARTX = GAME_WIDTH-35;
    public static final float PAUSE_STARTY = 5;

    // TODO Exercici 3 - configurem les propietats del botó fire i del làser
    // Propietats del pause
    public static final int FIRE_WIDTH = 35;
    public static final int FIRE_HEIGHT = 35;
    public static final float FIRE_STARTX = GAME_WIDTH-45;
    public static final float FIRE_STARTY = GAME_HEIGHT-45;

    // Propietats del làser
    public static final int LASER_WIDTH = 25;
    public static final int LASER_HEIGHT = 5;
    public static final float LASER_STARTX = SPACECRAFT_WIDTH + SPACECRAFT_STARTX;
    public static final float LASER_SPEED = 150;

    // Rang de valors per canviar la mida de l'asteroide.
    public static final float MAX_ASTEROID = 2.5f;
    public static final float MIN_ASTEROID = 0.5f;

    // Configuració Scrollable
    public static final int ASTEROID_SPEED = -200;
    public static final int ASTEROID_GAP = 75;
    public static final int BG_SPEED = -150;
}
