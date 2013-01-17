package spacers;

import spacers.message.MessageMob;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.List;

public class ServerMob extends Mob {

    public ServerMob(int id, Type type) {
        super(id);
        this.type = type;
    }
    public static final List<ServerMob> mobs = new ArrayList<>();

    public static ServerMob create(Mob.Type type) {
        ServerMob result = new ServerMob(mobs.size(), type);
        mobs.add(result);
        return result;
    }

    public static void tick() {
        for (Mob m : mobs) {
            m.position = m.position.add(m.speed);
        }
    }

    public static MessageMob toMessage() {
        MessageMob result = new MessageMob();
        for (ServerMob m : mobs)
            result.mobs.add(new Mob(m));
        return result;
    }
}
