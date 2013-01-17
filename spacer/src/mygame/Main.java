package mygame;

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
import java.io.IOException;
import java.util.Date;
import spacers.Spacers;
import spacers.message.MessageMob;
import spacers.message.MessagePlayerSpeed;
import spacers.message.MessageWelcome;

public class Main extends SimpleApplication {

    public static ClientMob me;
    private static Client client;
    private Vector3f speed = Vector3f.ZERO;
    private ChaseCamera chaseCam;
    private static final float SLOWDOWN = 0.99f;
    
    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        Spacers.initializeClasses();

        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        
        try{
            client = Network.connectToServer(Spacers.NAME, Spacers.VERSION,
                    Spacers.HOST, Spacers.PORT, Spacers.UDP_PORT);
            client.addMessageListener(new ChatHandler(), MessageMob.class);
            client.addMessageListener(new MessageListener<Client>() {
                public void messageReceived(Client source, Message m) {
                    me = ClientMob.mobs.get(((MessageWelcome) m).mob);
                    flyCam.setEnabled(true);
                    chaseCam = new ChaseCamera(cam, me.geometry, inputManager);
                    chaseCam.setMinDistance(5f);
                    chaseCam.setMaxDistance(10f);
                }
            }, MessageWelcome.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ClientMob.callback = new ClientMob.MobInterface() {
            public void onCreate(ClientMob m) {
                Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                switch (m.type) {
                    case CHECKPOINT:
                        mat.setColor("Color", ColorRGBA.Blue);
                        break;
                    case PLAYER:
                        mat.setColor("Color", ColorRGBA.Red);
                        break;
                }
                m.geometry.setMaterial(mat);
                rootNode.attachChild(m.geometry);         

            }
        };
        
        client.start();
        
        inputManager.addMapping("Speed up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Slow down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Strife left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Strife right", new KeyTrigger(KeyInput.KEY_D));
        AnalogListener analogListener = new AnalogListener() {
            public void onAnalog(String name, float value, float tpf) {
               
                if(name.equals("Speed up")) {
                    speed.set(speed.add(getCamera().getDirection().mult(value)));
                }
                
                if(name.equals("Slow down")) {
                    speed.set(speed.subtract(getCamera().getDirection().mult(value)));
                }
              
            }
          };
        
        inputManager.addListener(analogListener, new String[]{"Speed up", "Slow down"});
        
        
    }

    @Override
    public void simpleUpdate(float tpf) {
        final float t = (new Date().getTime() - ClientMob.ts) / (1000.f / Spacers.TICKS);
     
        speed.set(speed.mult(SLOWDOWN));
        
        client.send(new MessagePlayerSpeed(speed));

        final Vector3f p = me.position.add(speed.mult(t));

        for (ClientMob c : ClientMob.mobs) {
            c.geometry.setLocalTranslation(c.position.add(c.speed.mult(t)));
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
    
    public final void initializeCamera() {
        chaseCam = new ChaseCamera(cam, me.geometry, inputManager);
    }

    @Override
    public void destroy() {
        client.close();

        super.destroy();
    }
}
