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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;

import poke.comm.App.Payload;
import poke.comm.App.Ping;
import poke.comm.App.PokeStatus;
import poke.comm.App.Request;
import poke.server.Server;
import poke.server.resources.Resource;
import poke.server.resources.ResourceUtil;

public class PingResource implements Resource {
	protected static Logger logger = LoggerFactory.getLogger("server");
	 public static List<String> nodes = new ArrayList<String>();

	public PingResource() {
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see poke.server.resources.Resource#process(eye.Comm.Finger)
	 */
	public Request process(Request request) {
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
      
         nodes.add("10.189.98.187");
         String nodeAddress;
			// broadcast the request to all my clients.
			Iterator<String> it = nodes.iterator();
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
         
	/*	Request.Builder rb = Request.newBuilder();

		// metadata
		rb.setHeader(ResourceUtil.buildHeaderFrom(request.getHeader(), PokeStatus.SUCCESS, null));

		// payload
		Payload.Builder pb = Payload.newBuilder();
		Ping.Builder fb = Ping.newBuilder();
		fb.setTag(request.getBody().getPing().getTag());
		fb.setNumber(request.getBody().getPing().getNumber());
		pb.setPing(fb.build());
		rb.setBody(pb.build());

		Request reply = rb.build();
        logger.info("poke:"+reply.getBody().getPing().getTag());
		return reply;*/
	}
}
