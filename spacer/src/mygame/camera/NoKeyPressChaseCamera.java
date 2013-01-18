/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.camera;

import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;

/**
 * 
 * @author Krzysztof Hasiński<krzysztof.hasinski+spacer@gmail.com>
 */
public class NoKeyPressChaseCamera extends ChaseCamera {

	public NoKeyPressChaseCamera(final Camera cam, final Spatial target, final InputManager inputManager) {
		super(cam, target, inputManager);
	}

	@Override
	public void onAction(final String name, final boolean keyPressed, final float tpf) {
		if (dragToRotate)
			if (name.equals(ChaseCamToggleRotate) && enabled) {
				canRotate = true;
				if (hideCursorOnRotate)
					inputManager.setCursorVisible(false);
			}

	}

}
