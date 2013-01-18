package spacers;

import java.util.ArrayList;
import java.util.List;

import spacers.message.MessageGoal;
import spacers.message.MessageMob;

import com.jme3.network.HostedConnection;

public class ServerMob extends Mob {
	public HostedConnection conn;
	public ServerMob goal;

	public ServerMob(final int id, final Type type) {
		super(id);
		this.type = type;
	}

	public static final List<ServerMob> mobs = new ArrayList<>();

	public static ServerMob create(final Mob.Type type) {
		final ServerMob result = new ServerMob(mobs.size(), type);
		mobs.add(result);
		return result;
	}

	private void _tick() {
		position = position.add(speed);

		if (null != goal && goal.position.distance(position) < 0.1) {
			final ServerMob next = mobs.get(1 + goal.id);
			if (Mob.Type.CHECKPOINT == next.type) {
				goal = next;
				conn.send(new MessageGoal(goal));
			}
		}
	}

	public static void tick() {
		for (final ServerMob m : mobs)
			m._tick();
	}

	public static MessageMob toMessage() {
		final MessageMob result = new MessageMob();
		for (final ServerMob m : mobs)
			result.mobs.add(new Mob(m));
		return result;
	}
}
