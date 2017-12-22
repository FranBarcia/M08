package cat.xtec.ioc.objects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import cat.xtec.ioc.helpers.AssetManager;
import cat.xtec.ioc.utils.Settings;

public class Laser extends Scrollable {

    private Rectangle collisionRect;

    public Laser(float x, float y, float width, float height, float velocity) {
        super(x, y, width, height, velocity);
        this.width = width;
        this.height = height;

        collisionRect = new Rectangle();

        setBounds(position.x, position.y, width, height);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        // Actualitzem el recltangle de col·lisions.
        collisionRect.set(position.x, position.y, width, height);
        setBounds(position.x, position.y, width, height);
    }

    // Obtenim el TextureRegion de la posició del pause
    public TextureRegion getLaserTexture() {
        return AssetManager.laser;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(getLaserTexture(), position.x, position.y, width, height);
    }

    public Rectangle getCollisionRect() {
        return collisionRect;
    }

}
