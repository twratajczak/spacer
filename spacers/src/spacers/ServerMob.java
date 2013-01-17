package spacers;

import spacers.message.MessageMob;
import com.jme3.math.Vector3f;
import com.jme3.network.HostedConnection;
import java.util.ArrayList;
import java.util.List;
import spacers.message.MessageGoal;

public class ServerMob extends Mob {
    public HostedConnection conn;
    public ServerMob goal;

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

    private void _tick() {
        position = position.add(speed);

        if (null != goal && goal.position.distance(position) < 0.1) {
            ServerMob next = mobs.get(1 + goal.id);
            if (Mob.Type.CHECKPOINT == next.type) {
                goal = next;
                conn.send(new MessageGoal(goal));
            }
        }
    }

    public static void tick() {
        for (ServerMob m : mobs)
            m._tick();
    }

    public static MessageMob toMessage() {
        MessageMob result = new MessageMob();
        for (ServerMob m : mobs)
            result.mobs.add(new Mob(m));
        return result;
    }
}
