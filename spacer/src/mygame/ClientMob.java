package mygame;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import spacers.Mob;
import spacers.message.MessageMob;

import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;

public class ClientMob extends Mob {
	public final Geometry geometry;

	public static interface MobInterface {

		void onCreate(ClientMob m);
	}

	public static long ts;
	public static MobInterface callback;
	public static final List<ClientMob> mobs = new ArrayList<ClientMob>();

	public ClientMob(final Mob that) {
		super(that);

		final Sphere b = new Sphere(8, 24, 0.1f);
		geometry = new Geometry(String.format("mob %d", id), b);
	}

	public static void fromMessage(final MessageMob msg) {
		ts = new Date().getTime();
		for (final Mob m : msg.mobs) {
			ClientMob c;
			if (mobs.size() <= m.id) {
				c = new ClientMob(m);
				mobs.add(c);
				callback.onCreate(c);
			} else
				c = mobs.get(m.id);
			c.position = m.position;
			c.speed = m.speed;
		}
	}
}
