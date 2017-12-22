package cat.xtec.ioc.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.actions.RotateByAction;

import java.util.Random;

import cat.xtec.ioc.helpers.AssetManager;
import cat.xtec.ioc.utils.Methods;
import cat.xtec.ioc.utils.Settings;

/**
 * Created by fbarcia on 27/11/2017.
 */

// TODO Exercici 3 - Classe per implementar el fire
public class Fire extends Actor {

    private Vector2 position;
    private int width, height;

    public Fire (float x, float y, int width, int height) {

        // Inicialitzem els arguments segons la crida del constructor
        this.width = width;
        this.height = height;
        position = new Vector2(x, y);

        // Per a la gestio de hit
        setBounds(position.x, position.y, width, height);
        setTouchable(Touchable.enabled);
    }

    public void act(float delta) {
        super.act(delta);
    }

    // Getters dels atributs principals
    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    // Obtenim el TextureRegion de la posició del pause
    public TextureRegion getFireTexture() {
        return AssetManager.fire;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(getFireTexture(), position.x, position.y, width, height);
    }

    // TODO
    public void firePaused(Boolean paused) {
        if (paused) {
            this.addAction(Actions.hide());
        } else {
            this.addAction(Actions.alpha(1));
            this.addAction(Actions.show());
        }
    }
}
