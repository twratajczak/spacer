package spacers;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Mob {
    public final int id;
    public Vector3f position;
    public Vector3f speed;

    public Mob(final int id) {
        this.id = id;
        position = new Vector3f();
        speed = new Vector3f();
    }




}
