package spacers.message;

import java.util.ArrayList;

import spacers.Mob;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

@Serializable
public class MessageMob extends AbstractMessage {
	public ArrayList<Mob> mobs = new ArrayList<>();

	public MessageMob() {
		super(false);
	}

}
