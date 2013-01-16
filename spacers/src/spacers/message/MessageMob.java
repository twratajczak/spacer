package spacers.message;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;


@Serializable
public class MessageMob extends AbstractMessage {
    public int[] id;
    public Vector3f[] position;
    public Vector3f[] speed;

    public MessageMob() {
        super(false);
    }

}
