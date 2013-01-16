package mygame.message;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;

/**
 *
 * @author Krzysztof Hasiński <krzysztof.hasinski+spacer@gmail.com>
 */
public class MessagePlayerSpeed extends AbstractMessage  {
    Vector3f speed;
    
    public MessagePlayerSpeed(Vector3f speed) {
        this.speed = speed;
    }
    
}
