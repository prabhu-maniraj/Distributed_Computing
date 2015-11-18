package poke.server;


import poke.server.ServerHandler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

public class NewServerInitializer extends ChannelInitializer<SocketChannel> {
	boolean compress = false;

	public NewServerInitializer(boolean enableCompression) {
		compress = enableCompression;
	}

	public NewServerInitializer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		System.out.println("Server Initializer");
		// Enable stream compression (you can remove these two if unnecessary)
		if (compress) {
			System.out.println("Compress");
			pipeline.addLast("deflater", ZlibCodecFactory.newZlibEncoder(ZlibWrapper.GZIP));
			pipeline.addLast("inflater", ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP));
		}

		/**
		 * length (4 bytes).
		 * 
		 * Note: max message size is 64 Mb = 67108864 bytes this defines a
		 * framer with a max of 64 Mb message, 4 bytes are the length, and strip
		 * 4 bytes
		 */
		
		pipeline.addLast("protobufDecoder", new ProtobufDecoder(poke.comm.App.Request.getDefaultInstance()));
		pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
		pipeline.addLast("protobufEncoder", new ProtobufEncoder());

		// our server processor (new instance for each connection)
		//pipeline.addLast("handler", new ServerHandler());
		
		/*
		pipeline.addLast(new ProtobufVarint32FrameDecoder());

		pipeline.addLast(new ProtobufDecoder(Image.Request.getDefaultInstance()));

		pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
		pipeline.addLast(new ProtobufEncoder());
		*/

        // and then handler for business logic.
		//pipeline.addLast(new ServerHandler());
	}
}

