package controller;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

public class PlayerControl {

	private static final float MAXSPEED = 5.0f;
	private static final float SLOWDOWN = 0.99f;

	private final Vector3f speed = Vector3f.ZERO;
	private final InputManager inputManager;

	private final Camera camera;

	public Vector3f getSpeed() {
		return speed;
	}

	public PlayerControl(final InputManager inputManager, final Camera camera) {
		this.inputManager = inputManager;
		this.camera = camera;
	}

	public void setupControls() {

		inputManager.addMapping("Speed up", new KeyTrigger(KeyInput.KEY_W));
		inputManager.addMapping("Slow down", new KeyTrigger(KeyInput.KEY_S));
		inputManager.addMapping("Strife left", new KeyTrigger(KeyInput.KEY_A));
		inputManager.addMapping("Strife right", new KeyTrigger(KeyInput.KEY_D));

		final AnalogListener analogListener = new AnalogListener() {
			@Override
			public void onAnalog(final String name, final float value, final float tpf) {

				if (name.equals("Speed up")) {
					final Vector3f direction = camera.getDirection();

					speed.setX(Math.min(MAXSPEED, speed.x + direction.x * value));
					speed.setY(Math.min(MAXSPEED, speed.y + direction.y * value));
					speed.setZ(Math.min(MAXSPEED, speed.z + direction.z * value));
				}

				if (name.equals("Slow down")) {
					final Vector3f direction = camera.getDirection();

					speed.setX(Math.max(-MAXSPEED, speed.x - direction.x * value));
					speed.setY(Math.max(-MAXSPEED, speed.y - direction.y * value));
					speed.setZ(Math.max(-MAXSPEED, speed.z - direction.z * value));
				}

			}
		};

		inputManager.addListener(analogListener, new String[] { "Speed up", "Slow down" });
	}

	public void update() {
		speed.set(speed.mult(SLOWDOWN));
	}
}
