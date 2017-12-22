package cat.xtec.ioc.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AssetManager {

    // Sprite sheet
    private static TextureAtlas atlas;

    // Nau, fons, laser, pasua, fire i asteroid
    public static TextureRegion spacecraft, spacecraftDown, spacecraftUp, background, asteroid;

    // TODO Exercici 2 - Creem al assetManager el pause
    public static TextureRegion pause;
    // TODO Exercici 3 - Creem al assetManager el fire i el laser
    public static TextureRegion fire, laser;

    // Animació de la explosió
    public static Animation explosionAnim;

    // Sons i música
    public static Sound explosionSound, laserSound;
    public static Music music;

    // Font
    public static BitmapFont font;

    public static void load() {
        atlas = new TextureAtlas(Gdx.files.internal("sheet.atlas"));
        loadBackground();
        loadButtons();
        loadSpacecraft();
        loadLaser();
        loadAsteroid();
        loadExplosion();
        loadSounds();
        loadMusic();
        loadFonts();
    }

    private static void loadFonts() {
        font = new BitmapFont(Gdx.files.internal("fonts/space.fnt"), true);
        font.getData().setScale(0.4f);
    }

    private static void loadMusic() {
        music = Gdx.audio.newMusic(Gdx.files.internal("sounds/Afterburner.ogg"));
        music.setLooping(true);
    }

    private static void loadSounds() {
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.wav"));
        laserSound = Gdx.audio.newSound(Gdx.files.internal("sounds/laser.mp3"));
    }

    private static void loadBackground() {
        background = atlas.findRegion("space");
        background.flip(false, true);
    }

    private static void loadExplosion() {
        TextureRegion[][] tmp = atlas.findRegion("explosion").split(128, 128);

        TextureRegion[] explosion = new TextureRegion[100];
        int index = 0;

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                explosion[index++] = tmp[i][j];
            }
        }
        explosionAnim = new Animation(0.0064f, explosion);
        explosionAnim.setPlayMode(Animation.PlayMode.NORMAL);
    }

    private static void loadAsteroid() {
        asteroid = atlas.findRegion("asteroid_alone");
        asteroid.flip(false, true);
    }
    private static void loadLaser() {
        //TODO Exercici 3 - sprite del làser
        laser = atlas.findRegion("laser");
        laser.flip(false, true);
    }

    private static void loadButtons() {
        //TODO Exercici 2 - botó de pause
        pause = atlas.findRegion("pause");
        pause.flip(false, true);
        //TODO Exercici 3 - botó de fire
        fire = atlas.findRegion("fire");
        fire.flip(false, true);
    }

    private static void loadSpacecraft() {
        spacecraft = atlas.findRegion("spacecraft_straight");
        spacecraft.flip(false, true);
        spacecraftUp = atlas.findRegion("spacecraft_up");
        spacecraftUp.flip(false, true);
        spacecraftDown = atlas.findRegion("spacecraft_down");
        spacecraftDown.flip(false, true);
    }

    public static void dispose() {
        atlas.dispose();
        explosionSound.dispose();
        laserSound.dispose();
        music.dispose();
    }
}
