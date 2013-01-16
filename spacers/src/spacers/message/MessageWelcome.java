package spacers.message;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

@Serializable
public class MessageWelcome extends AbstractMessage {
    public int mob;

    public MessageWelcome() {
    }

    public MessageWelcome(int mob) {
        this.mob = mob;
    }
}
