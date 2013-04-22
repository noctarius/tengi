package com.github.tengi.transport.websocket;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;

import com.github.tengi.CompletionFuture;
import com.github.tengi.Connection;
import com.github.tengi.Message;
import com.github.tengi.MessageListener;
import com.github.tengi.SerializationFactory;
import com.github.tengi.Streamable;
import com.github.tengi.TransportType;
import com.github.tengi.UniqueId;
import com.github.tengi.buffer.MemoryBuffer;

public class WebsocketConnection implements Connection
{

	private final UniqueId uniqueId = UniqueId.randomUniqueId();

	private final SerializationFactory serializationFactory;

	private final Channel channel;

	private ChannelInboundMessageHandlerAdapter messageListener = null;

	WebsocketConnection(Channel channel, SerializationFactory serializationFactory)
	{
		this.channel = channel;
		this.serializationFactory = serializationFactory;
	}

	@Override
	public TransportType getTransportType()
	{
		return TransportType.WebSocket;
	}

	@Override
	public <T extends Message> void sendMessage(T message, CompletionFuture<T> completionFuture)
	{
		ChannelFuture channelFuture = channel.write(message);
		channelFuture.addListener(new CompletionFutureAdapter<T>(completionFuture, message, this));
	}

	@Override
	public <T extends Streamable> void sendRawData(MemoryBuffer memoryBuffer, final T metadata, final CompletionFuture<T> completionFuture)
	{
		ChannelFuture channelFuture = channel.write(memoryBuffer);
		channelFuture.addListener(new CompletionFutureAdapter<T>(completionFuture, metadata, this));
	}

	@Override
	public void setMessageListener(MessageListener messageListener)
	{
		this.messageListener = new MessageListenerAdapter(messageListener, this);
	}

	@Override
	public void clearMessageListener()
	{
		messageListener = null;
	}

	@Override
	public Message prepareMessage(Streamable body)
	{
		return new Message(serializationFactory, this, body, UniqueId.randomUniqueId(), Message.MESSAGE_TYPE_DEFAULT);
	}

	@Override
	public void close()
	{
		channel.close();
	}

}
