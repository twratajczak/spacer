package spacers;

import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;

@Serializable
public class Mob {

	public static enum Type {

		CHECKPOINT, PLAYER,
	}

	public int id;
	public Type type;
	public Vector3f position = new Vector3f();
	public Vector3f speed = new Vector3f();
	public float health, healthMax;

	public Mob() {
	}

	public Mob(final int id, final Type type) {
		this.id = id;
		this.type = type;
		health = healthMax = 100.f;
	}

	public Mob(final Mob that) {
		id = that.id;
		type = that.type;
		position = that.position;
		speed = that.speed;
		health = that.health;
		healthMax = that.healthMax;
	}
}
