package poke.client;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import poke.server.conf.ServerConf;

 

import poke.client.ClientInitializer;
import poke.comm.App.ClientMessage;
import poke.comm.App.Header;
import poke.comm.App.Payload;
import poke.comm.App.Ping;
import poke.comm.App.Request;
import poke.comm.App.ClusterMessage;

import com.google.protobuf.ByteString;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import javax.imageio.ImageIO;

public class Client {
	protected ServerConf conf;
    static final String HOST = System.getProperty("host", "localhost");
    static final int PORT = Integer.parseInt(System.getProperty("port", "5570"));
	
	private final String host;
	private final int port;
    
    public Client(String host, int port){
		
		this.host = host;
		this.port = port;
		
	}
	
    public static void main(String[] args) throws Exception {		
		
		//System.out.println("jhhhj");
		new Client(HOST, PORT).run();
		
	}
	
	public void run(){
		
        EventLoopGroup group = new NioEventLoopGroup();
        try{
        	Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class).handler(new ClientInitializer()); //2
			
			Channel ch; 
			// Start the connection attempt.
			ch = b.connect("localhost",PORT).sync().channel(); //3

			ChannelFuture lastWriteFuture;
			System.out.println("Sending image...");


			//File file = new File("/home/nishanth/Pictures/1.png");
			String dirName="/home/nishanth/Pictures/";
			ByteArrayOutputStream baos=new ByteArrayOutputStream(1000);
			BufferedImage img;
			byte[] bytearray=null;
			try {
				img = ImageIO.read(new File(dirName,"1.png"));
				ImageIO.write(img, "png", baos);
				System.out.println(img);
				baos.flush();

				 bytearray=  baos.toByteArray();
				baos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*Image.PayLoad payloadData = Image.PayLoad.newBuilder().setData(ByteString.readFrom(inputStream)).build();
			Image.Header head = Image.Header.newBuilder().setIsClient(true).setClusterId(8).setClientId(45).setCaption("pai").build();
			//Image.Request setPhotoData = Image.Request.newBuilder().setHeader(head).setData(payloadData).build();
			Image.Ping ping = Image.Ping.newBuilder().setIsPing(Boolean.FALSE).build();
			Image.Request setPhotoData = Image.Request.newBuilder().setHeader(head).setPayload(payloadData).setPing(ping).build();
            
			lastWriteFuture = ch.writeAndFlush(setPhotoData);
			lastWriteFuture.channel().closeFuture().sync();
			System.out.println("Running");*/
        	
        	
			/*
			 * 
			 * Server to Server Ping request
			 */
		
			//File f = new File("/home/ashish/shrek.jpg");
			//InputStream inputStream=new FileInputStream(f);
			//Image.PayLoad payloadData = Image.PayLoad.newBuilder().setData(ByteString.readFrom(inputStream)).build();
			//Image.Header head = Image.Header.newBuilder().setIsClient(true).setClusterId(8).setClientId(45).setCaption("pai").build();
			//Image.Request setPhotoData = Image.Request.newBuilder().setHeader(head).setData(payloadData).build();
			//Image.Ping ping = Image.Ping.newBuilder().setIsPing(Boolean.TRUE).build();
			//Image.Request setPhotoData = Image.Request.newBuilder().setHeader(head).setPayload(payloadData).setPing(ping).build();
           
			
			ClusterMessage.Builder cl = ClusterMessage.newBuilder();
			cl.setClusterId(12);
			
			ClientMessage.Builder f= ClientMessage.newBuilder();
			f.setMsgImageBits(ByteString.copyFrom(bytearray));
			f.setMsgImageName("myImage");
			System.out.println(ByteString.copyFrom(bytearray));
			//f.setClusterId(12);
			//f.setClientId(14);
			f.setReceiverUserName(3);
			//f.setNumber(5);
			//f.setTagBytes();
			// payload containing data
			
			Request.Builder r = Request.newBuilder();
			Payload.Builder p = Payload.newBuilder();
			
			cl.setClientMessage(f.build());
			p.setClusterMessage(cl.build());
			r.setBody(p.build());
			
			// header with routing info
			
			Header.Builder h = Header.newBuilder();
			h.setOriginator(1000);
			h.setTag("test finger");
			h.setTime(System.currentTimeMillis());
			h.setRoutingId(Header.Routing.PING);
		
			r.setHeader(h.build());
			
			Request req = r.build();


			
			lastWriteFuture = ch.writeAndFlush(req);
			lastWriteFuture.channel().closeFuture().sync();
		//	System.out.println("Running");
			
		}catch(Exception e){
        	e.printStackTrace();
        }
        finally{
        	group.shutdownGracefully();
        	
        }
        
        
		
	}
	
	
	
	
}