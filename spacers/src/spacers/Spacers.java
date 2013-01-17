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

        for (int i = 0; i < 10; ++i) {
            ServerMob.create(Mob.Type.CHECKPOINT).position = new Vector3f((float) Math.random(), (float) Math.random(), (float) Math.random());
        }
    }
    private static Map<Integer, Mob> owners = new TreeMap<Integer, Mob>();

    private static class ConHandler implements ConnectionListener {

        @Override
        public void connectionAdded(Server server, HostedConnection conn) {
            ServerMob m = ServerMob.create(Mob.Type.PLAYER);
            owners.put(conn.getId(), m);
            conn.send(ServerMob.toMessage().setReliable(true));
            conn.send(new MessageWelcome(m.id).setReliable(true));
        }

        @Override
        public void connectionRemoved(Server server, HostedConnection conn) {
        }
    }

    private static class ChatHandler implements MessageListener<HostedConnection> {

        public ChatHandler() {
        }

        public void messageReceived(HostedConnection source, Message m) {
            if (m instanceof ChatMessage) {
                // Keep track of the name just in case we
                // want to know it for some other reason later and it's
                // a good example of session data
                source.setAttribute("name", ((ChatMessage) m).getName());

                System.out.println("Broadcasting:" + m + "  reliable:" + m.isReliable());

                // Just rebroadcast... the reliable flag will stay the
                // same so if it came in on UDP it will go out on that too
                source.getServer().broadcast(m);
            } else {
                System.err.println("Received odd message:" + m);
            }
        }
    }

    @Serializable
    public static class ChatMessage extends AbstractMessage {

        private String name;
        private String message;

        public ChatMessage() {
        }

        public ChatMessage(String name, String message) {
            setName(name);
            setMessage(message);
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setMessage(String s) {
            this.message = s;
        }

        public String getMessage() {
            return message;
        }

        public String toString() {
            return name + ":" + message;
        }
    }
}
