package spacers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import spacers.message.MessageGoal;
import spacers.message.MessageMob;

import com.jme3.math.Vector3f;
import com.jme3.network.HostedConnection;

public class ServerMob extends Mob {
	public HostedConnection conn;
	public ServerMob goal;
	final public long ts;

	public ServerMob(final int id, final Type type) {
		super(id, type);
		ts = new Date().getTime();
	}

	public static final List<ServerMob> mobs = new ArrayList<>();

	public static ServerMob create(final Mob.Type type) {
		final ServerMob result = new ServerMob(mobs.size(), type);
		mobs.add(result);
		return result;
	}

	private void _tick(final long ts) {
		if (Type.PLAYER == type) {
			if (ts - this.ts > 5000)
				health -= 0.1;
			if (health <= 0) {
				health = 0;
				speed = new Vector3f();
				return;
			}
		}

		position = position.add(speed);

		if (null != goal && goal.position.distance(position) < 0.3) {
			final ServerMob next = mobs.get(1 + goal.id);
			if (Mob.Type.CHECKPOINT == next.type) {
				goal = next;
				conn.send(new MessageGoal(goal));
			}
		}
	}

	public static void tick() {
		final long ts = new Date().getTime();
		for (final ServerMob m : mobs)
			m._tick(ts);
	}

	public static MessageMob toMessage() {
		final MessageMob result = new MessageMob();
		for (final ServerMob m : mobs)
			result.mobs.add(new Mob(m));
		return result;
	}
}
