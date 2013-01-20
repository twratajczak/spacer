package mygame;

import java.io.IOException;

import mygame.camera.NoKeyPressChaseCamera;
import spacers.Mob;
import spacers.Spacers;
import spacers.message.MessageGoal;
import spacers.message.MessageMob;
import spacers.message.MessagePlayerSpeed;
import spacers.message.MessageWelcome;

import com.jme3.app.SimpleApplication;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;

public class Main extends SimpleApplication {

	public static ClientMob me;
	private static Client client;
	private final Vector3f speed = Vector3f.ZERO;
	private ChaseCamera chaseCam;
	private static final float SLOWDOWN = 0.99f;
	private static final float MAXSPEED = 3.0f;
	private Geometry goal;

	/**
	 * 
	 * @param args
	 */
	public static void main(final String[] args) {
		Spacers.initializeClasses();

		final Main app = new Main();
		app.start();
	}

	@Override
	public void simpleInitApp() {
		{
			goal = new Geometry("goal", new Sphere(8, 24, 0.2f));
			final Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
			mat.setColor("Color", ColorRGBA.Green);
			goal.setMaterial(mat);
			rootNode.attachChild(goal);
		}

		try {
			client = Network.connectToServer(Spacers.NAME, Spacers.VERSION, Spacers.HOST, Spacers.PORT, Spacers.UDP_PORT);
			client.addMessageListener(new ChatHandler(), MessageMob.class);
			client.addMessageListener(new MessageListener<Client>() {
				@Override
				public void messageReceived(final Client source, final Message m) {
					me = ClientMob.mobs.get(((MessageWelcome) m).mob);
					flyCam.setEnabled(true);
					chaseCam = new NoKeyPressChaseCamera(cam, me.geometry, inputManager);
					chaseCam.setMinDistance(5f);
					chaseCam.setMaxDistance(10f);
				}
			}, MessageWelcome.class);
			client.addMessageListener(new MessageListener<Client>() {
				@Override
				public void messageReceived(final Client source, final Message m) {
					final MessageGoal msg = (MessageGoal) m;
					final ClientMob mob = ClientMob.mobs.get(msg.mob);
					goal.setLocalTranslation(mob.position);
				}
			}, MessageGoal.class);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		ClientMob.callback = new ClientMob.MobInterface() {
			@Override
			public ClientMob onCreate(final Mob m) {
				final ClientMob c = new ClientMob(m, assetManager);
				rootNode.attachChild(c.geometry);
				return c;

			}
		};

		client.start();

		inputManager.addMapping("Speed up", new KeyTrigger(KeyInput.KEY_W));
		inputManager.addMapping("Slow down", new KeyTrigger(KeyInput.KEY_S));
		inputManager.addMapping("Strife left", new KeyTrigger(KeyInput.KEY_A));
		inputManager.addMapping("Strife right", new KeyTrigger(KeyInput.KEY_D));
		final AnalogListener analogListener = new AnalogListener() {
			@Override
			public void onAnalog(final String name, final float value, final float tpf) {

				if (name.equals("Speed up")) {
					final Vector3f direction = getCamera().getDirection();

					speed.setX(Math.min(MAXSPEED, speed.x + direction.x * value));
					speed.setY(Math.min(MAXSPEED, speed.y + direction.y * value));
					speed.setZ(Math.min(MAXSPEED, speed.z + direction.z * value));
				}

				if (name.equals("Slow down")) {
					final Vector3f direction = getCamera().getDirection();

					speed.setX(Math.max(-MAXSPEED, speed.x - direction.x * value));
					speed.setY(Math.max(-MAXSPEED, speed.y - direction.y * value));
					speed.setZ(Math.max(-MAXSPEED, speed.z - direction.z * value));
				}

			}
		};

		inputManager.addListener(analogListener, new String[] { "Speed up", "Slow down" });

		{
			final Texture west = assetManager.loadTexture("Textures/Sky/Space/space_west.png");
			final Texture east = assetManager.loadTexture("Textures/Sky/Space/space_east.png");
			final Texture north = assetManager.loadTexture("Textures/Sky/Space/space_north.png");
			final Texture south = assetManager.loadTexture("Textures/Sky/Space/space_south.png");
			final Texture up = assetManager.loadTexture("Textures/Sky/Space/space_up.png");
			final Texture down = assetManager.loadTexture("Textures/Sky/Space/space_down.png");
			rootNode.attachChild(SkyFactory.createSky(assetManager, west, east, north, south, up, down));
		}
	}

	@Override
	public void simpleUpdate(final float tpf) {
		speed.set(speed.mult(SLOWDOWN));

		client.send(new MessagePlayerSpeed(speed));

		ClientMob.update();
	}

	@Override
	public void simpleRender(final RenderManager rm) {
		// TODO: add render code
	}

	private static class ChatHandler implements MessageListener<Client> {

		@Override
		public void messageReceived(final Client source, final Message m) {
			final MessageMob chat = (MessageMob) m;
			ClientMob.fromMessage(chat);
		}
	}

	public final void initializeCamera() {
		chaseCam = new ChaseCamera(cam, me.geometry, inputManager);
	}

	@Override
	public void destroy() {
		client.close();

		super.destroy();
	}
}
