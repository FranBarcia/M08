package cat.xtec.ioc.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import cat.xtec.ioc.SpaceRace;
import cat.xtec.ioc.helpers.AssetManager;
import cat.xtec.ioc.utils.Settings;


public class SplashScreen implements Screen {

    private Stage stage;
    private SpaceRace game;

    private Label.LabelStyle textStyle;
    private Label textLbl;

    // TODO Exercici 1b - Declaració de les variables per definir el nous subtitol
    private Label.LabelStyle subTitleStyle;
    private Label subTitleLbl;

    public SplashScreen(SpaceRace game) {

        this.game = game;

        // Creem la càmera de les dimensions del joc
        OrthographicCamera camera = new OrthographicCamera(Settings.GAME_WIDTH, Settings.GAME_HEIGHT);
        // Posant el paràmetre a true configurem la càmera per a
        // que faci servir el sistema de coordenades Y-Down
        camera.setToOrtho(true);

        // Creem el viewport amb les mateixes dimensions que la càmera
        StretchViewport viewport = new StretchViewport(Settings.GAME_WIDTH, Settings.GAME_HEIGHT, camera);

        // Creem l'stage i assginem el viewport
        stage = new Stage(viewport);

        // Afegim el fons
        stage.addActor(new Image(AssetManager.background));

        // Creem l'estil de l'etiqueta i l'etiqueta
        textStyle = new Label.LabelStyle(AssetManager.font, null);
        textLbl = new Label("SpaceRace 2.0", textStyle);

        // TODO Exercici 1b - Assignem valors a les variables creades pel subtitol
        subTitleStyle = new Label.LabelStyle(AssetManager.font, null);
        subTitleLbl = new Label("Tap Screen To Start", subTitleStyle);

        // Creem el contenidor necessari per aplicar-li les accions
        Container container = new Container(textLbl);
        container.setTransform(true);
        container.center();

        // TODO Exercici 1a - Desplaçament modificat a HEIGHT / 3
        container.setPosition(Settings.GAME_WIDTH / 2, Settings.GAME_HEIGHT / 3);

        // Afegim les accions de escalar: primer es fa gran i després torna a l'estat original ininterrompudament
        container.addAction(Actions.repeat(RepeatAction.FOREVER, Actions.sequence(Actions.scaleTo(1.5f, 1.5f, 1), Actions.scaleTo(1, 1, 1))));
        stage.addActor(container);

        // TODO Exercici 1b - Creem el subtitol amb els requeriments necessaris
        // Creem el contenidor necessari per aplicar-li les accions
        Container subContainer = new Container(subTitleLbl);
        subContainer.setTransform(true);
        subContainer.center();
        subContainer.setScale(0.5f);
        subContainer.setPosition(Settings.GAME_WIDTH / 2, (5 * Settings.GAME_HEIGHT) / 6);


        // TODO Exercici 1c - Afegim l'animació
        // Afegim les accions de escalar: primer es fa gran i després torna a l'estat original ininterrompudament
        subContainer.addAction(Actions.repeat(RepeatAction.FOREVER, Actions.sequence(Actions.fadeIn(0.625f), Actions.fadeOut(0.625f))));
        stage.addActor(subContainer);

        // Creem la imatge de la nau i li assignem el moviment en horitzontal
        Image spacecraft = new Image(AssetManager.spacecraft);
        float y = Settings.GAME_HEIGHT / 2 + textLbl.getHeight();
        spacecraft.addAction(Actions.repeat(RepeatAction.FOREVER, Actions.sequence(Actions.moveTo(0 - spacecraft.getWidth(), y), Actions.moveTo(Settings.GAME_WIDTH, y, 5))));

        stage.addActor(spacecraft);


    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        stage.draw();
        stage.act(delta);

        // Si es fa clic en la pantalla, canviem la pantalla
        if (Gdx.input.isTouched()) {
            game.setScreen(new GameScreen(stage.getBatch(), stage.getViewport()));
            dispose();
        }

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
}
