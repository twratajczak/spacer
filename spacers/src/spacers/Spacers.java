/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package spacers;

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

/**
 *
 * @author dhag
 */
public class Spacers {
    // Normally these and the initialized method would
    // be in shared constants or something.

    public static final String NAME = "Test Chat Server";
    public static final String HOST = "localhost";
    public static final int VERSION = 1;
    public static final int PORT = 8452;
    public static final int UDP_PORT = 8452;

    public static void initializeClasses() {
        // Doing it here means that the client code only needs to
        // call our initialize.
        Serializer.registerClass(ChatMessage.class);
        Serializer.registerClass(Mob.MobMessage.class);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        initializeClasses();

        // Use this to test the client/server name version check
        final Server server = Network.createServer(NAME, VERSION, PORT, UDP_PORT);
        server.start();

        server.addMessageListener(new ChatHandler(), ChatMessage.class);
        server.addConnectionListener(new ConHandler());

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ServerMob.tick();
            }
        }, 0, 1000 / 12);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                 server.broadcast(ServerMob.toMessage());
            }
        }, 0, 50);
    }
    private static Map<Integer, Mob> owners = new TreeMap<Integer, Mob>();

    private static class ConHandler implements ConnectionListener {

        @Override
        public void connectionAdded(Server server, HostedConnection conn) {
            owners.put(conn.getId(), ServerMob.create());
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
