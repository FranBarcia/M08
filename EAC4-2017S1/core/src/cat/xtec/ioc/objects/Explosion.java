package cat.xtec.ioc.objects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import cat.xtec.ioc.helpers.AssetManager;

public class Explosion extends Actor {
    private Vector2 position;
    private float width, height;
    private float explosionTime;

    public Explosion(float x, float y, float width, float height) {
        this.width = width;
        this.height = height;
        this.position = new Vector2(x, y);
        this.explosionTime = 0.0f;
    }
    @Override
    public float getX() {
        return position.x;
    }
    @Override
    public float getY() {
        return position.y;
    }
    @Override
    public float getWidth() {
        return width;
    }
    @Override
    public float getHeight() {
        return height;
    }
    @Override
    public void act(float delta) {
        super.act(delta);
        explosionTime += delta;
    }
    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(AssetManager.explosionAnim.getKeyFrame(explosionTime, false),(position.x + width / 2) - (width / 2),position.y + height / 2 - (height / 2), width, height);
    }
}