package cat.xtec.ioc.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

import cat.xtec.ioc.objects.Fire;
import cat.xtec.ioc.objects.Laser;
import cat.xtec.ioc.objects.Pause;
import cat.xtec.ioc.objects.ScrollHandler;
import cat.xtec.ioc.objects.Spacecraft;
import cat.xtec.ioc.screens.GameScreen;
import cat.xtec.ioc.utils.Settings;

public class InputHandler implements InputProcessor {

    // Enter per a la gesitó del moviment d'arrastrar
    int previousY = 0;
    int previousPointer = 0;
    // Objectes necessaris
    private Spacecraft spacecraft;
    private GameScreen screen;
    private Vector2 stageCoord;
    private Stage stage;

    // TODO Ex2 - afegim el boto de pausa
    private Pause pause;

    // TODO Ex3 - afegim el boto de fire
    private Fire fire;

    // TODO Ex3 - creem l'objecte que representarà el laser disparat
    private Laser laser;

    public InputHandler(GameScreen screen) {

        // Obtenim tots els elements necessaris
        this.screen = screen;
        spacecraft = screen.getSpacecraft();
        pause = screen.getPause();
        fire = screen.getFire();
        stage = screen.getStage();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        switch (screen.getCurrentState()) {
            case READY:
                // Si fem clic comencem el joc
                screen.setCurrentState(GameScreen.GameState.RUNNING);
                break;
            case RUNNING:
                stageCoord = stage.screenToStageCoordinates(new Vector2(screenX, screenY));
                Actor actorHit = stage.hit(stageCoord.x, stageCoord.y, true);

                if (actorHit != null) {
                    // TODO Exercici 2 - quan es toca el botó de pause
                    if (actorHit.getName().equals("pause")) {
                        spacecraft.spacecraftPaused(true);
                        pause.pausePaused(true);
                        fire.firePaused(true);
                        screen.setCurrentState(GameScreen.GameState.PAUSE);
                    }
                    // TODO Exercici 3 - quan es toca el botó de disparar
                    if (actorHit.getName().equals("fire")) {
                        generaLaser(spacecraft);
                        AssetManager.laserSound.play();
                    }
                }

                break;
            // TODO Exercici 2 - afegir cas del pausa
            case PAUSE:
                spacecraft.spacecraftPaused(false);
                pause.pausePaused(false);
                fire.firePaused(false);
                screen.setCurrentState(GameScreen.GameState.RUNNING);

                break;
            // Si l'estat és GameOver tornem a iniciar el joc
            case GAMEOVER:
                screen.reset();
                break;
        }

        return true;
    }

    public void generaLaser(Spacecraft nau) {
        Laser laser = new Laser(Settings.LASER_STARTX, (nau.getY()+(Settings.SPACECRAFT_HEIGHT/2)), Settings.LASER_WIDTH, Settings.LASER_HEIGHT, Settings.LASER_SPEED);
        nau.generarLaser(laser);
        stage.addActor(laser);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // Quan deixem anar el dit acabem un moviment
        // i posem la nau en l'estat normal
        if (previousPointer == pointer) {
            spacecraft.goStraight();
        }

        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // TODO Exercici 2 - Afegirm condició per moure la nau només si l'estat es running
        if (screen.getCurrentState() == GameScreen.GameState.RUNNING) {

            if (previousPointer == pointer) {
                // Posem un umbral per evitar gestionar events quan el dit està quiet
                if (Math.abs(previousY - screenY) > 10) {

                    // Si la Y és major que la que tenim
                    // guardada és que va cap avall
                    if (previousY < screenY) {
                        spacecraft.goDown();
                    } else {
                        // En cas contrari cap a dalt
                        spacecraft.goUp();
                    }
                }
                // Guardem la posició de la Y
                previousY = screenY;
            }
        }
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
