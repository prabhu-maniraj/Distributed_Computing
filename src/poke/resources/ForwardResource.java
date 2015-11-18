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
package poke.resources;

import io.netty.channel.ChannelFuture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import poke.comm.App.PokeStatus;
import poke.comm.App.Request;
import poke.comm.App.RoutingPath;
import poke.server.conf.NodeDesc;
import poke.server.conf.ServerConf;
import poke.server.resources.Resource;
import poke.server.resources.ResourceUtil;

/**
 * The forward resource is used by the ResourceFactory to send requests to a
 * destination that is not this server.
 * 
 * Strategies used by the Forward can include TTL (max hops), durable tracking,
 * endpoint hiding.
 * 
 * @author gash
 * 
 */
public class ForwardResource implements Resource {
	protected static Logger logger = LoggerFactory.getLogger("server");
	public static HashMap<Integer,String> nodeMap = new HashMap<Integer,String>();
	public static List<String> nodes = new ArrayList<String>();

	private ServerConf cfg;

	public ServerConf getCfg() {
		return cfg;
	}

	/**
	 * Set the server configuration information used to initialized the server.
	 * 
	 * @param cfg
	 */
	public void setCfg(ServerConf cfg) {
		this.cfg = cfg;
	}

	@Override
	public Request process(Request request) {
		System.out.println("Inside ForwardResource");
		/* logger.info("poke:"+request.getBody().getClusterMessage().getMsgImageBits());

		//ForwardResource rs=new ForwardResource();
		//System.out.println("forwarded request is:"+rs.process(request));
		//System.out.println((request.getBody().getPing().getTag()).toStringUtf8());


        System.out.println("Sending request received to all the other nodes in my cluster");
        nodeMap.put(5571,"localhost");
        nodeMap.put(5572,"localhost");
         String nodeAddress;

			// broadcast the request to all my clients.
			Iterator it = nodeMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				ChannelFuture lastWriteFuture;
				try {
					 String value=(String)pair.getValue();
					 Integer key=(Integer)pair.getKey();
					lastWriteFuture = ResourceUtil.getChannel(value,key,
							request).writeAndFlush(request);
					lastWriteFuture.channel().closeFuture().sync();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		//Integer nextNode = determineForwardNode(request);
		/*Integer nextNode = 1;
		if (nextNode != null) {
			Request fwd = ResourceUtil.buildForwardMessage(request, cfg);
			return fwd;
		} else {
			Request reply = null;
			// cannot forward the message - no one to forward request to as
			// the request has traveled all known/available edges of this node
			String statusMsg = "Unable to forward message, no paths or have already traversed";
			Request rtn = ResourceUtil.buildError(request.getHeader(), PokeStatus.NOREACHABLE, statusMsg);
			return rtn;
		}*/
		/*String caption="1";
			// TODO add code to process the message/event received
			logger.info("poke:"+request.getBody().getPing().getTag());

			//ForwardResource rs=new ForwardResource();
			//System.out.println("forwarded request is:"+rs.process(request));
			//System.out.println((request.getBody().getPing().getTag()).toStringUtf8());
			if((request.getBody().getPing().getTag())!=null)
			{
				String dirName="/home/nishanth/Desktop/";

			      ByteString imageByteString = request.getBody().getClusterMessage().getMsgImageBits();


			      byte[] bytearray = imageByteString.toByteArray();
			      caption=caption+".png";

			      BufferedImage imag;
				try {
					imag = ImageIO.read(new ByteArrayInputStream(bytearray));
					ImageIO.write(imag, "png", new File(dirName,caption));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			else
				logger.error("Unknown management message");*/

		logger.info("poke:"+request.getBody().getClusterMessage().getClientMessage().getMsgImageBits());

		System.out.println("Sending the request to node in the other cluster ");

		/*nodeMap.put("10.189.97.188");
	         nodes.add("10.189.79.101");
	         nodes.add("10.189.213.250");*/

		nodeMap.put(5571,"10.189.97.188");
		nodeMap.put(5572,"10.189.79.101" );
		String nodeAddress;
		Iterator it = nodeMap.entrySet().iterator();
		// broadcast the request to all my clients.
		System.out.println(nodeMap.size());
		while (it.hasNext()) {


			Map.Entry pair = (Map.Entry) it.next();
			ChannelFuture lastWriteFuture;
			try {
				String value=(String)pair.getValue();
				Integer key=(Integer)pair.getKey();
				System.out.println(key+"hi prabhu");
				lastWriteFuture = ResourceUtil.getChannel(value,key,
						request).writeAndFlush(request);
				lastWriteFuture.channel().closeFuture().sync();
				System.out.println("second iteratio");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return null;

	}

	/**
	 * Find the nearest node that has not received the request.
	 * 
	 * TODO this should use the heartbeat to determine which node is active in
	 * its list.
	 * 
	 * @param request
	 * @return
	 */
	private Integer determineForwardNode(Request request) {
		List<RoutingPath> paths = request.getHeader().getPathList();
		System.out.println(paths);
		if (paths == null || paths.size() == 0) {
			// pick first nearest
			System.out.println("if paths==null");
			NodeDesc nd = cfg.getAdjacent().getAdjacentNodes().values().iterator().next();
			System.out.println("Node is:"+nd.getNodeId());
			return nd.getNodeId();
		} else {
			// if this server has already seen this message return null
			System.out.println("if paths!=null");
			for (RoutingPath rp : paths) {
				for (NodeDesc nd : cfg.getAdjacent().getAdjacentNodes().values()) {
					if (nd.getNodeId() != rp.getNodeId())
						return nd.getNodeId();
				}
			}
		}
		System.out.println("returning null in determineForwardNode method");
		return null;
	}
}
