package spacers;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import java.util.ArrayList;
import java.util.List;

@Serializable
public class Mob  {

    public static enum Type {

        CHECKPOINT,
        PLAYER,
    }
    public int id;
    public Type type;
    public Vector3f position = new Vector3f();
    public Vector3f speed = new Vector3f();

    public Mob() {
    }

    public Mob(final int id) {
        this.id = id;
    }

    public Mob(final Mob that) {
        id = that.id;
        type = that.type;
        position = that.position;
        speed = that.speed;
    }
}
