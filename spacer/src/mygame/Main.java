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
import java.io.IOException;
import spacers.message.MessageMob;
import spacers.message.MessagePlayerSpeed;
import spacers.message.MessageWelcome;
import spacers.Mob;
import spacers.Spacers;

public class Main extends SimpleApplication {
    public static ClientMob me;
   
    private static Client client;

    public static void main(String[] args) throws IOException {
        Spacers.initializeClasses();

        Main app = new Main();
        
        client = Network.connectToServer(Spacers.NAME, Spacers.VERSION,
                Spacers.HOST, Spacers.PORT, Spacers.UDP_PORT);
        client.addMessageListener(new ChatHandler(), MessageMob.class);
        client.addMessageListener(new MessageListener<Client>() {
            public void messageReceived(Client source, Message m) {
                me = ClientMob.mobs.get(((MessageWelcome) m).mob);
            }
        }, MessageWelcome.class);

        app.start();
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
        
        Vector3f speed = new Vector3f(0,0,0.2f);
        client.send(new MessagePlayerSpeed(speed));
        getCamera().setLocation(me.position.add(new Vector3f(0,0,-10f)));
        getCamera().lookAt(me.position, me.position);
        
        //TODO: add update code
        for (ClientMob c : ClientMob.mobs) {
            c.geometry.setLocalTranslation(c.position.add(c.speed.mult(tpf)));
        }
        
        
        
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    private static class ChatHandler implements MessageListener<Client> {

        public void messageReceived(Client source, Message m) {
            MessageMob chat = (MessageMob) m;
            ClientMob.fromMessage(chat);
        }
    }
}
