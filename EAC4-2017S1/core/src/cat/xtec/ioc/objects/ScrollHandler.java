package cat.xtec.ioc.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.Random;

import cat.xtec.ioc.screens.GameScreen;
import cat.xtec.ioc.utils.Methods;
import cat.xtec.ioc.utils.Settings;

import static cat.xtec.ioc.screens.GameScreen.score;

public class ScrollHandler extends Group {

    // Fons de pantalla
    Background bg, bg_back;

    // Asteroides i lasers
    private Array<Asteroid> asteroids;
    private Array<Laser> lasers;

    // Objecte Random
    Random r = new Random();

    // Variable runTime per controlar el temps
    private float runTime = 0;

    // Definim una mida aleatòria entre el mínim i el màxim
    float newSize = Methods.randomFloat(Settings.MIN_ASTEROID, Settings.MAX_ASTEROID) * 34;

    Asteroid asteroid;
    private int MAX_ASTEROIDS = 4;

    public ScrollHandler() {

        // Creem els dos fons
        bg = new Background(0, 0, Settings.GAME_WIDTH * 2, Settings.GAME_HEIGHT, Settings.BG_SPEED);
        bg_back = new Background(bg.getTailX(), 0, Settings.GAME_WIDTH * 2, Settings.GAME_HEIGHT, Settings.BG_SPEED);

        // Afegim els fons al grup
        addActor(bg);
        addActor(bg_back);

        // Creem l'ArrayList
        asteroids = new Array<Asteroid>();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        generaAsteroides();

        // TODO Exercici 3 - Modifiquem el for per que si un asteroide se'n va de la pantalla per l'esquerra, reaparegui per la dreta
        for (int i = 0; i < asteroids.size; i++) {

            asteroid = asteroids.get(i);
            if (asteroid.isLeftOfScreen()) {
                asteroid.reset(Settings.GAME_WIDTH + Settings.ASTEROID_GAP);
            }
        }
        // Si algun element està fora de la pantalla, fem un reset de l'element.
        if (bg.isLeftOfScreen()) {
            bg.reset(bg_back.getTailX());

        } else if (bg_back.isLeftOfScreen()) {
            bg_back.reset(bg.getTailX());
        }
    }

    public boolean collides(Spacecraft nau) {

        // Comprovem les col·lisions entre cada asteroid i la nau
        for (Asteroid asteroid : asteroids) {
            if (asteroid.collides(nau)) {
                return true;
            }
        }
        return false;
    }

    // TODO Ex3 - mètode per comprovar si un asteroide colisiona amb un laser
    public boolean collidesAsteroidLaser(Spacecraft nau) {

        lasers = nau.getLasers();

        // Comprovem les col·lisions entre cada asteroid i els lasers
        for (Asteroid asteroid : asteroids) {
            for (Laser laser : lasers) {
                if (asteroid.collidesLaser(laser)) {
                    removeActor(asteroid);
                    laser.remove();
                    asteroids.removeValue(asteroid, true);
                    lasers.removeValue(laser, true);
                    addActor(new Explosion(asteroid.getX(), asteroid.getY(), asteroid.width, asteroid.height));

                    score += ((1/(asteroid.getWidth() * 0.5f))*100);

                    MAX_ASTEROIDS = ((int)score / 100 +1) + 4;

                    return true;
                }
            }
        }
        return false;
    }

    public void reset() {

        // Posem el primer asteroid fora de la pantalla per la dreta
        asteroids.get(0).reset(Settings.GAME_WIDTH);
        // Calculem les noves posicions de la resta d'asteroids.
        for (int i = 1; i < asteroids.size; i++) {

            asteroids.get(i).reset(asteroids.get(i - 1).getTailX() + Settings.ASTEROID_GAP);

        }
    }

    public Array<Asteroid> getAsteroids() {
        return asteroids;
    }

    // TODO Exercici 3 - Mètode que crea nous asteroides cada 1 segon sempre que no hi hagi més de 4 creats
    public void generaAsteroides() {
        runTime += Gdx.graphics.getDeltaTime();

        // Afegim l'asteroid.
        if (runTime >= 1) {
            if (asteroids.size < MAX_ASTEROIDS) {
                asteroid = new Asteroid(Settings.GAME_WIDTH, r.nextInt(Settings.GAME_HEIGHT - (int) newSize), newSize, newSize, Settings.ASTEROID_SPEED);
                asteroids.add(asteroid);
                addActor(asteroid);
                runTime = 0;
            }
        }
    }
}