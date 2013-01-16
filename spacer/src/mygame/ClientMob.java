package mygame;

import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import java.util.ArrayList;
import java.util.List;
import spacers.message.MessageMob;
import spacers.Mob;

public class ClientMob extends Mob {
    public final Geometry geometry;

    public static interface MobInterface {

        void onCreate(ClientMob m);
    }
    public static MobInterface callback;
    public static final List<ClientMob> mobs = new ArrayList<ClientMob>();

    public ClientMob(int id) {
        super(id);

        Sphere b = new Sphere(8, 24, 0.1f);
        geometry = new Geometry(String.format("mob %d", id), b);
    }

    public static void fromMessage(MessageMob m) {
        for (int i = 0; i < m.id.length; ++i) {
            ClientMob c;
            if (mobs.size() <= m.id[i]) {
                c = new ClientMob(mobs.size());
                mobs.add(c);
                callback.onCreate(c);
            } else {
                c = mobs.get(m.id[i]);
            }
            c.position = m.position[i];
            c.speed = m.speed[i];
        }
    }
}
