/*
 * copyright 2012, gash
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
import java.util.Iterator;
import java.util.List;

import poke.comm.App.Request;
import poke.server.resources.Resource;
import poke.server.resources.ResourceUtil;

public class JobResource implements Resource {
	public static List<String> clients = new ArrayList<String>();
	@Override
	public Request process(Request request) {
		// TODO Auto-generated method stub
		 System.out.println(request.getBody().getClusterMessage().getClientMessage().getMsgImageBits());
		 System.out.println("Sending request received to the clients if attached to this cluster");
         clients.add("localhost");
         //clients.add("localhost");
         String nodeAddress;
			// broadcast the request to all my clients.
			Iterator<String> it = clients.iterator();
			while (it.hasNext()) {
				nodeAddress = (String) it.next();
				ChannelFuture lastWriteFuture;
				try {
					lastWriteFuture = ResourceUtil.getChannel(nodeAddress,
							request).writeAndFlush(request);
					lastWriteFuture.channel().closeFuture().sync();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		return null;
		
	}

}
