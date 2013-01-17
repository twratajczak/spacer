package spacers.message;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import java.util.ArrayList;
import spacers.Mob;


@Serializable
public class MessageMob extends AbstractMessage {
    public ArrayList<Mob> mobs = new ArrayList<>();

    public MessageMob() {
        super(false);
    }

}
