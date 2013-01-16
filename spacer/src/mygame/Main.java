package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import java.io.IOException;
import spacers.Mob;
import spacers.Spacers;
import spacers.Spacers.ChatMessage;

/**
 * test
 *
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    private static Client client;

    public static void main(String[] args) throws IOException {
        Spacers.initializeClasses();

        Main app = new Main();
        app.start();

        client = Network.connectToServer(Spacers.NAME, Spacers.VERSION,
                Spacers.HOST, Spacers.PORT, Spacers.UDP_PORT);
        client.addMessageListener(new ChatHandler(), Mob.MobMessage.class);
    }

    @Override
    public void simpleInitApp() {
        ClientMob.callback = new ClientMob.MobInterface() {
            public void onCreate(ClientMob m) {
                Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                mat.setColor("Color", ColorRGBA.Blue);
                m.geometry.setMaterial(mat);
                rootNode.attachChild(m.geometry);
            }
        };



        client.start();
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
        for (ClientMob c : ClientMob.mobs) {
            c.geometry.setLocalTranslation(c.position.x, c.position.y, c.position.z);
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    private static class ChatHandler implements MessageListener<Client> {

        public void messageReceived(Client source, Message m) {
            Mob.MobMessage chat = (Mob.MobMessage) m;
            ClientMob.fromMessage(chat);
        }
    }
}
