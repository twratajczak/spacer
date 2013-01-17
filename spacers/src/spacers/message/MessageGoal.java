package spacers.message;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import spacers.ServerMob;


@Serializable
public class MessageGoal extends AbstractMessage {
    public int mob;

    public MessageGoal() {
    }

    public MessageGoal(ServerMob mob) {
        this.mob = mob.id;
    }
}
