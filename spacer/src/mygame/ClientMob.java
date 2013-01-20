package mygame;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import spacers.Mob;
import spacers.Spacers;
import spacers.message.MessageMob;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.control.BillboardControl;
import com.jme3.scene.shape.Sphere;

public class ClientMob extends Mob {
	public final Node geometry;
	private final BitmapText labelText;

	public static interface MobInterface {

		ClientMob onCreate(Mob m);
	}

	public static long ts;
	public static MobInterface callback;
	public static final List<ClientMob> mobs = new ArrayList<ClientMob>();

	public ClientMob(final Mob that, final AssetManager assetManager) {
		super(that);

		final Sphere b = new Sphere(8, 24, 0.1f);
		final Geometry geometry = new Geometry("sphere", b);
		final Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		switch (type) {
		case CHECKPOINT:
			mat.setColor("Color", ColorRGBA.Blue);
			break;
		case PLAYER:
			mat.setColor("Color", ColorRGBA.Red);
			break;
		default:
			throw new RuntimeException();
		}
		geometry.setMaterial(mat);

		this.geometry = new Node(String.format("mob %d", id));
		this.geometry.attachChild(geometry);

		final Node label = new Node();
		final BitmapFont font = assetManager.loadFont("Interface/Fonts/Console.fnt");
		labelText = new BitmapText(font);
		labelText.setSize(0.1f);
		labelText.setColor(ColorRGBA.White);
		labelText.setLocalTranslation(0.1f, 2 * labelText.getLineHeight(), 0);
		label.attachChild(labelText);

		final BillboardControl control = new BillboardControl();
		control.setAlignment(BillboardControl.Alignment.Screen);
		label.addControl(control);

		this.geometry.attachChild(label);
	}

	public static void fromMessage(final MessageMob msg) {
		ts = new Date().getTime();
		for (final Mob m : msg.mobs) {
			ClientMob c;
			if (mobs.size() <= m.id) {
				c = callback.onCreate(m);
				mobs.add(c);
			} else
				c = mobs.get(m.id);
			c.position = m.position;
			c.speed = m.speed;
			c.health = m.health;
			c.healthMax = m.healthMax;
		}
	}

	public static void update() {
		final float t = (new Date().getTime() - ClientMob.ts) / (1000.f / Spacers.TICKS);

		for (final ClientMob c : ClientMob.mobs)
			c.update(t);
	}

	private void update(final float t) {
		geometry.setLocalTranslation(position.add(speed.mult(t)));
		if (health >= healthMax)
			labelText.setCullHint(CullHint.Always);
		else {
			labelText.setText(String.format("%5.2f%%", 100.f * health / healthMax));
			labelText.setCullHint(CullHint.Never);
		}
	}
}
