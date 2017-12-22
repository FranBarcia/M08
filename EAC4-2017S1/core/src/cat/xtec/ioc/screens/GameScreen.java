package cat.xtec.ioc.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.Iterator;

import cat.xtec.ioc.helpers.AssetManager;
import cat.xtec.ioc.helpers.InputHandler;
import cat.xtec.ioc.objects.Asteroid;
import cat.xtec.ioc.objects.Explosion;
import cat.xtec.ioc.objects.Fire;
import cat.xtec.ioc.objects.Laser;
import cat.xtec.ioc.objects.Pause;
import cat.xtec.ioc.objects.ScrollHandler;
import cat.xtec.ioc.objects.Spacecraft;
import cat.xtec.ioc.utils.Settings;

public class GameScreen implements Screen {

    public static float score = 0;
    // Els estats del joc
    public enum GameState {
        READY, RUNNING, GAMEOVER, PAUSE,
    }

    private GameState currentState;

    // Objectes necessaris
    private Stage stage;
    private Spacecraft spacecraft;
    private ScrollHandler scrollHandler;
    // TODO Ex2 - declarem el objecte pause
    private Pause pause;

    // TODO Ex3 - declarem el objecte fire
    private Fire fire;

    // Encarregats de dibuixar elements per pantalla
    private ShapeRenderer shapeRenderer;
    private Batch batch;

    // Per controlar l'animació de l'explosió
    private float explosionTime = 0.0f;

    // Preparem el textLayout per escriure text
    private GlyphLayout textLayout;
    private GlyphLayout textScore;

    public GameScreen(Batch prevBatch, Viewport prevViewport) {

        // Iniciem la música
        AssetManager.music.play();

        // Creem el ShapeRenderer
        shapeRenderer = new ShapeRenderer();

        // Creem l'stage i assginem el viewport
        stage = new Stage(prevViewport, prevBatch);

        batch = stage.getBatch();

        // Creem la nau i la resta d'objectes
        spacecraft = new Spacecraft(Settings.SPACECRAFT_STARTX, Settings.SPACECRAFT_STARTY, Settings.SPACECRAFT_WIDTH, Settings.SPACECRAFT_HEIGHT);
        scrollHandler = new ScrollHandler();
        // TODO Ex2 - el creem amb les seves propietats
        pause = new Pause(Settings.PAUSE_STARTX, Settings.PAUSE_STARTY, Settings.PAUSE_WIDTH, Settings.PAUSE_HEIGHT);

        // TODO Ex3 - el creem amb les seves propietats
        fire = new Fire(Settings.FIRE_STARTX, Settings.FIRE_STARTY, Settings.FIRE_WIDTH, Settings.FIRE_HEIGHT);

        // Afegim els actors a l'stage
        stage.addActor(scrollHandler);
        stage.addActor(spacecraft);
        // Donem nom a l'Actor
        spacecraft.setName("spacecraft");

        // TODO Ex2 - i l'afegim al stage
        stage.addActor(pause);
        pause.setName("pause");

        // TODO Ex3 - i l'afegim al stage
        stage.addActor(fire);
        fire.setName("fire");

        // Iniciem el GlyphLayout
        textLayout = new GlyphLayout();
        textLayout.setText(AssetManager.font, "Are you\nready?");

        textScore = new GlyphLayout();

        currentState = GameState.READY;

        // Assignem com a gestor d'entrada la classe InputHandler
        Gdx.input.setInputProcessor(new InputHandler(this));

    }

    private void drawElements() {

        // Recollim les propietats del Batch de l'Stage
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());

        // Pintem el fons de negre per evitar el "flickering"
        //Gdx.gl20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        //Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Inicialitzem el shaperenderer
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        // Definim el color (verd)
        shapeRenderer.setColor(new Color(0, 1, 0, 1));

        // Pintem la nau
        shapeRenderer.rect(spacecraft.getX(), spacecraft.getY(), spacecraft.getWidth(), spacecraft.getHeight());

        // Recollim tots els Asteroid
        Array<Asteroid> asteroids = scrollHandler.getAsteroids();
        Asteroid asteroid;

        for (int i = 0; i < asteroids.size; i++) {

            asteroid = asteroids.get(i);
            switch (i) {
                case 0:
                    shapeRenderer.setColor(1, 0, 0, 1);
                    break;
                case 1:
                    shapeRenderer.setColor(0, 0, 1, 1);
                    break;
                case 2:
                    shapeRenderer.setColor(1, 1, 0, 1);
                    break;
                default:
                    shapeRenderer.setColor(1, 1, 1, 1);
                    break;
            }
            shapeRenderer.circle(asteroid.getX() + asteroid.getWidth() / 2, asteroid.getY() + asteroid.getWidth() / 2, asteroid.getWidth() / 2);
        }
        shapeRenderer.end();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        // Dibuixem tots els actors de l'stage
        stage.draw();

        // Depenent de l'estat del joc farem unes accions o unes altres
        switch (currentState) {
            case GAMEOVER:
                updateGameOver(delta);
                break;
            case RUNNING:
                updateRunning(delta);
                break;
            case READY:
                updateReady();
                break;
            // TODO Exercici 2 - afegir cas del pausa
            case PAUSE:
                updatePause(delta);
                break;
        }
    }

    private void updateReady() {
        // Dibuixem el text al centre de la pantalla

        batch.begin();
        AssetManager.font.draw(batch, textLayout, (Settings.GAME_WIDTH / 2) - textLayout.width / 2, (Settings.GAME_HEIGHT / 2) - textLayout.height / 2);
        //stage.addActor(textLbl);
        batch.end();
    }

    private void updateRunning(float delta) {
        stage.act(delta);
        AssetManager.music.setVolume(1f);
        textScore.setText(AssetManager.font, "Score: "+ String.format("%.2f", score));

        batch.begin();
        AssetManager.font.draw(batch, textScore, (Settings.GAME_WIDTH - textScore.width) / 5, (Settings.GAME_HEIGHT - textScore.height) / 6);
        batch.end();

        if (scrollHandler.collides(spacecraft)) {
            score -= 100;
            // Si hi ha hagut col·lisió: Reproduïm l'explosió i posem l'estat a GameOver
            AssetManager.explosionSound.play();
            stage.getRoot().findActor("spacecraft").remove();
            textLayout.setText(AssetManager.font, "Game Over :'(");
            currentState = GameState.GAMEOVER;
            score = 0;
        }

        if (scrollHandler.collidesAsteroidLaser(spacecraft)) {
            AssetManager.explosionSound.play();
        }

    }

    private void updatePause(float delta) {
        // TODO Exercici 2 - mètode que actua quan estem en pausa

        spacecraft.act(delta);
        fire.act(delta);
        pause.act(delta);

        textLayout.setText(AssetManager.font, "Pause");

        batch.begin();
        AssetManager.music.setVolume(0.25f);
        AssetManager.font.draw(batch, textScore, (Settings.GAME_WIDTH - textScore.width) / 5, (Settings.GAME_HEIGHT - textScore.height) / 6);
        AssetManager.font.draw(batch, textLayout, (Settings.GAME_WIDTH - textLayout.width) / 2, (Settings.GAME_HEIGHT - textLayout.height) / 2);
        batch.end();
    }

    private void updateGameOver(float delta) {
        stage.act(delta);

        batch.begin();
        AssetManager.font.draw(batch, textLayout, (Settings.GAME_WIDTH - textLayout.width) / 2, (Settings.GAME_HEIGHT - textLayout.height) / 2);
        // Si hi ha hagut col·lisió: Reproduïm l'explosió i posem l'estat a GameOver
        batch.draw(AssetManager.explosionAnim.getKeyFrame(explosionTime, false), (spacecraft.getX() + spacecraft.getWidth() / 2) - 32, spacecraft.getY() + spacecraft.getHeight() / 2 - 32, 64, 64);
        batch.end();

        explosionTime += delta;
    }

    public void reset() {

        // Posem el text d'inici
        textLayout.setText(AssetManager.font, "Are you\nready?");
        // Cridem als restart dels elements.
        spacecraft.reset();
        scrollHandler.reset();

        // Posem l'estat a 'Ready'
        currentState = GameState.READY;

        // Afegim la nau a l'stage
        stage.addActor(spacecraft);

        // Posem a 0 les variables per controlar el temps jugat i l'animació de l'explosió
        explosionTime = 0.0f;
    }


    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    public Spacecraft getSpacecraft() {
        return spacecraft;
    }

    // TODO Ex2 - métode que retorna el pause
    public Pause getPause() {
        return pause;
    }

    // TODO Ex3 - métode que retorna el fire
    public Fire getFire() {
        return fire;
    }

    public Stage getStage() {
        return stage;
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(GameState currentState) {
        this.currentState = currentState;
    }

}
