package mygame;

import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;
import java.util.List;
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

        Box b = new Box(Vector3f.ZERO, 1, 1, 1);
        geometry = new Geometry("Box", b);
    }

    public static void fromMessage(Mob.MobMessage m) {
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
