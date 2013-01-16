package spacers;

import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.List;


public class ServerMob extends Mob {

    public ServerMob(int id) {
        super(id);
        position.x = id;
    }



    public static final List<ServerMob> mobs = new ArrayList<ServerMob>();

    public static ServerMob create() {
        ServerMob result = new ServerMob(mobs.size());
        mobs.add(result);
        return result;
    }

    public static void tick() {
        for (Mob m : mobs)
            m.position = m.position.add(m.speed);
    }


        public static MobMessage toMessage() {
        MobMessage result = new MobMessage();
        result.id = new int[mobs.size()];
        result.position = new Vector3f[mobs.size()];
        result.speed = new Vector3f[mobs.size()];
        for (int i = 0; i < mobs.size(); ++i) {
            result.id[i] = mobs.get(i).id;
            result.position[i] = mobs.get(i).position;
            result.speed[i] = mobs.get(i).speed;
        }
        return result;
    }
}
