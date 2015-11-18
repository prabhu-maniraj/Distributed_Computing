package poke.server.managers;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import poke.core.Mgmt.Management;
import poke.core.Mgmt.Network;
import poke.core.Mgmt.Network.NetworkAction;
import poke.server.conf.ServerConf;

public class ClusterManager extends Thread{
	
	protected static Logger logger = LoggerFactory.getLogger("cluster");
	protected static AtomicReference<ClusterManager> instance = new AtomicReference<ClusterManager>();
	HashMap<Integer,Integer> hm=new HashMap<Integer,Integer>();

	private static ServerConf conf;

	/** @brief the number of votes this server can cast */
	//private int votes = 1;

	public static ClusterManager initManager(ServerConf conf) {
		ClusterManager.conf = conf;
		instance.compareAndSet(null, new ClusterManager());
		return instance.get();
	}

	public static ClusterManager getInstance() {
		// TODO throw exception if not initialized!
		return instance.get();
	}

	/**
	 * initialize the manager for this server
	 * 
	 */
	protected ClusterManager() {

	}

	/**
	 * @param args
	 */
	public void processRequest(Management mgmt, Channel channel) {
		Network req = mgmt.getGraph();
		if (req == null || channel == null)
			return;

		logger.info("Network: node '" + req.getFromNodeId() + "' sent a " + req.getAction());

		/**
		 * Outgoing: when a node joins to another node, the connection is
		 * monitored to relay to the requester that the node (this) is active -
		 * send a heartbeatMgr
		 */
		if (req.getAction().getNumber() == NetworkAction.NODEJOIN_VALUE) {
			if (channel.isOpen()) {
				// can i cast socka?
				SocketAddress socka = channel.localAddress();
				if (socka != null) {
					// this node will send messages to the requesting client
					InetSocketAddress isa = (InetSocketAddress) socka;
					logger.info("NODEJOIN: " + isa.getHostName() + ", " + isa.getPort());
					HeartbeatManager.getInstance().addOutgoingChannel(req.getFromNodeId(), isa.getHostName(),
							isa.getPort(), channel, socka);
				}
			} else
				logger.warn(req.getFromNodeId() + " not writable");
		} else if (req.getAction().getNumber() == NetworkAction.NODEDEAD_VALUE) {
			// possible failure - node is considered dead
		} else if (req.getAction().getNumber() == NetworkAction.NODELEAVE_VALUE) {
			// node removing itself from the network (gracefully)
		} else if (req.getAction().getNumber() == NetworkAction.ANNOUNCE_VALUE) {
			// nodes sending their info in response to a create map
		} else if (req.getAction().getNumber() == NetworkAction.CREATEMAP_VALUE) {
			// request to create a network topology map
		}

		// may want to reply to exchange information
	}
	
	
	

}
