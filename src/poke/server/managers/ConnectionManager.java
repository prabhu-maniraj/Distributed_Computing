/*
 * copyright 2014, gash
 * 
 * Gash licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package poke.server.managers;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import poke.client.ClientInitializer;
import poke.comm.App.Request;
import poke.core.Mgmt.Management;
import poke.server.NewServerInitializer;
import poke.server.Server;

/**
 * the connection map for server-to-server communication.
 * 
 * Note the connections/channels are initialized through the heartbeat manager
 * as it starts (and maintains) the connections through monitoring of processes.
 * 
 * 
 * TODO refactor to make this the consistent form of communication for the rest
 * of the code
 * 
 * @author gash
 * 
 */
public class ConnectionManager {
	protected static Logger logger = LoggerFactory.getLogger("management");


	/** node ID to channel */
	private static HashMap<Integer, Channel> connections = new HashMap<Integer, Channel>();
	private static HashMap<Integer, Channel> mgmtConnections = new HashMap<Integer, Channel>();
	//public static HashMap<String,Integer> nodeMap=new HashMap<String, Integer>();

	public static void addConnection(Integer nodeId, Channel channel, boolean isMgmt) {
		logger.info("ConnectionManager adding connection to " + nodeId);

		if (isMgmt)
			mgmtConnections.put(nodeId, channel);
		else
			connections.put(nodeId, channel);
	}

	public static Channel getConnection(Integer nodeId, boolean isMgmt) {

		if (isMgmt)
			return mgmtConnections.get(nodeId);
		else
			return connections.get(nodeId);
	}

	public synchronized static void removeConnection(Integer nodeId, boolean isMgmt) {
		if (isMgmt)
			mgmtConnections.remove(nodeId);
		else
			connections.remove(nodeId);
	}

	public synchronized static void removeConnection(Channel channel, boolean isMgmt) {

		if (isMgmt) {
			if (!mgmtConnections.containsValue(channel)) {
				return;
			}

			for (Integer nid : mgmtConnections.keySet()) {
				if (channel == mgmtConnections.get(nid)) {
					mgmtConnections.remove(nid);
					break;
				}
			}
		} else {
			if (!connections.containsValue(channel)) {
				return;
			}

			for (Integer nid : connections.keySet()) {
				if (channel == connections.get(nid)) {
					connections.remove(nid);
					break;
				}
			}
		}
	}

	public synchronized static void broadcast(Request req) {
		if (req == null)
			return;
		System.out.println("Inside broadcast");
		for (Channel ch : connections.values())
			ch.write(req);
	}
	public synchronized static void broadcastToAllNodes(poke.comm.App.Request req) throws InterruptedException
	{  
		System.out.println("The size of nodeMap is" +Server.nodeMap.size());
		System.out.println("Inside Broadcast");
		//Iterator<Integer> it= port.iterator();
		/*System.out.println("poke:"+req.getBody().getClusterMessage().getClientMessage().getMsgImageBits());
		if(req == null)
			return;

		for(Channel ch : mgmtConnections.values())
		{
			ch.write(req);
	//	logger.info("poke:"+req.getBody().getClusterMessage().getClientMessage().getMsgImageBits());
		}*/

		//System.out.println("Inside while in broadcast");


		Iterator it = Server.nodeMap.entrySet().iterator();
		//System.out.println("The values of node map are"+Server.nodeMap.values());
		// broadcast the request to all my clients.
		// ChannelFuture lastWriteFuture=null;
		while (it.hasNext()) {
			ChannelFuture lastWriteFuture=null;
			EventLoopGroup group = new NioEventLoopGroup();
			Map.Entry pair = (Map.Entry) it.next();
			   int leaderNode=ElectionManager.getInstance().whoIsTheLeader();
	//		   String host= cfg.getHost(int leaderNode);

			try{
				Bootstrap b = new Bootstrap();
				b.group(group).channel(NioSocketChannel.class).handler(new NewServerInitializer()); 

				Channel ch; 
				System.out.println("The address is"+pair.getValue());
				// Start the connection attempt.
				ch = b.connect((String)pair.getValue(),(Integer)pair.getKey()).sync().channel(); 
				System.out.println("After channel0...");

				lastWriteFuture = ch.writeAndFlush(req);
				System.out.println("After channel1...");

				System.out.println("Iterator");
				//it.remove();
				//lastWriteFuture.channel().closeFuture().syncUninterruptibly();
				//lastWriteFuture.channel()
				//lastWriteFuture.sync();
				//System.out.println(lastWriteFuture.isDone());
			/*	if(lastWriteFuture.isDone())
				{*/
					//System.out.println(lastWriteFuture.isSuccess());
					lastWriteFuture.channel().close();
/*
				}*/
				/*if(lastWriteFuture !=null)
				{
				//lastWriteFuture.channel().closeFuture().sync();
					lastWriteFuture.cancel(true);
				}*/
			}
			catch(Exception e){
				e.printStackTrace();
			}
			finally{


				group.shutdownGracefully();

			}
			//lastWriteFuture.channel().closeFuture().syncUninterruptibly();
		}

	}

	public synchronized static void broadcast(Management mgmt) {
		if (mgmt == null)
			return;

		for (Channel ch : mgmtConnections.values())
			ch.write(mgmt);
	}

	public static int getNumMgmtConnections() {
		return mgmtConnections.size();
	}
}
