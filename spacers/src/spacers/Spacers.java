package spacers;

import com.jme3.math.Vector3f;
import spacers.message.MessageMob;
import spacers.message.MessageWelcome;
import com.jme3.network.AbstractMessage;
import com.jme3.network.ConnectionListener;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.network.serializing.Serializable;
import com.jme3.network.serializing.Serializer;
import java.io.IOException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import spacers.message.MessageGoal;
import spacers.message.MessagePlayerSpeed;

public class Spacers {
    // Normally these and the initialized method would
    // be in shared constants or something.

    public static final String NAME = "Test Chat Server";
    public static final String HOST = "localhost";
    public static final int VERSION = 1;
    public static final int PORT = 8452;
    public static final int UDP_PORT = 8452;
    public static final int TICKS = 12;

    public static void initializeClasses() {
        // Doing it here means that the client code only needs to
        // call our initialize.
        Serializer.registerClass(MessageGoal.class);
        Serializer.registerClass(MessageWelcome.class);
        Serializer.registerClass(MessageMob.class);
        Serializer.registerClass(MessagePlayerSpeed.class);
        Serializer.registerClass(Mob.class);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        initializeClasses();

        // Use this to test the client/server name version check
        final Server server = Network.createServer(NAME, VERSION, PORT, UDP_PORT);
        server.start();

        server.addMessageListener(new MessageListener<HostedConnection>() {
            @Override
            public void messageReceived(HostedConnection source, Message m) {
                MessagePlayerSpeed p = (MessagePlayerSpeed) m;
                owners.get(source.getId()).speed = p.speed;
            }
        }, MessagePlayerSpeed.class);
        server.addConnectionListener(new ConHandler());

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ServerMob.tick();
            }
        }, 0, 1000 / TICKS);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                server.broadcast(ServerMob.toMessage());
            }
        }, 0, 100);

        ServerMob.create(Mob.Type.CHECKPOINT);
        for (int i = 0; i < 10; ++i) {
            ServerMob.create(Mob.Type.CHECKPOINT).position = new Vector3f(10.f * (float) Math.random(), 10.f * (float) Math.random(), 10.f * (float) Math.random());
        }
        ServerMob.create(Mob.Type.CHECKPOINT);
    }
    private static Map<Integer, Mob> owners = new TreeMap<Integer, Mob>();

    private static class ConHandler implements ConnectionListener {

        @Override
        public void connectionAdded(Server server, HostedConnection conn) {
            ServerMob m = ServerMob.create(Mob.Type.PLAYER);
            m.conn = conn;
            owners.put(conn.getId(), m);
            conn.send(ServerMob.toMessage().setReliable(true));
            conn.send(new MessageWelcome(m.id).setReliable(true));
            m.goal = ServerMob.mobs.get(0);
        }

        @Override
        public void connectionRemoved(Server server, HostedConnection conn) {
        }
    }
}
