package spacers.message;

import spacers.ServerMob;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

@Serializable
public class MessageGoal extends AbstractMessage {
	public int mob;

	public MessageGoal() {
	}

	public MessageGoal(final ServerMob mob) {
		this.mob = mob.id;
	}
}
