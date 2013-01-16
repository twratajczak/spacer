package spacers.message;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

@Serializable
public class MessagePlayerSpeed extends AbstractMessage  {
    public Vector3f speed;

    public MessagePlayerSpeed() {
    }
    
    public MessagePlayerSpeed(final Vector3f speed) {
        this.speed = speed;
    }
    
}
