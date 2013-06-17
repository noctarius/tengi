package com.noctarius.tengi.utils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.MessageList;
import io.netty.util.internal.TypeParameterMatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.noctarius.tengi.UnknownMessageListener;

public abstract class ChannelMessageHandler<MESSAGE>
    extends ChannelInboundHandlerAdapter
{

    private static final Logger LOGGER = LoggerFactory.getLogger( ChannelMessageHandler.class );

    private final TypeParameterMatcher messageTypeMatcher;

    private final UnknownMessageListener unknownMessageListener;

    public ChannelMessageHandler()
    {
        this( null );
    }

    public ChannelMessageHandler( UnknownMessageListener unknownMessageListener )
    {
        this.messageTypeMatcher = TypeParameterMatcher.find( this, ChannelMessageHandler.class, "MESSAGE" );
        this.unknownMessageListener = unknownMessageListener;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public void messageReceived( ChannelHandlerContext ctx, MessageList<Object> msgs )
        throws Exception
    {
        for ( int index = 0; index < msgs.size(); index++ )
        {
            Object message = msgs.get( index );
            if ( messageTypeMatcher.match( message ) )
            {
                messageReceived( ctx, (MESSAGE) message );
            }
            else
            {
                if ( unknownMessageListener == null )
                {
                    LOGGER.warn( "Unknown message type bubbled up: {}", message.getClass() );
                }
                else
                {
                    unknownMessageListener.unknownMessageReceived( ctx, message );
                }
            }
        }
        msgs.releaseAllAndRecycle();
    }

    public abstract void messageReceived( ChannelHandlerContext ctx, MESSAGE message )
        throws Exception;

}
