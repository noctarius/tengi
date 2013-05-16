package com.github.tengi;

import io.netty.channel.Channel;

import java.security.NoSuchAlgorithmException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import org.eclipse.jetty.npn.NextProtoNego;

import com.github.tengi.transport.protocol.NGNServerProvider;

public class ConnectionManager
{

    private final Map<UniqueId, Connection> connections = new HashMap<UniqueId, Connection>();

    private final SSLContext sslContext;

    private final SSLEngine sslEngine;

    public ConnectionManager()
        throws NoSuchAlgorithmException
    {
        sslContext = SSLContext.getDefault();
        sslEngine = sslContext.createSSLEngine();
        NextProtoNego.put( sslEngine, new NGNServerProvider() );
    }

    public Connection getConnectionByConnectionId( UniqueId connectionId )
    {
        return connections.get( connectionId );
    }

    public Connection registerConnection( UniqueId connectionId, Channel channel, TransportType transportType )
    {
        return null; // TODO
    }

    public void registerConnection( UniqueId connectionId, Connection connection )
    {
        connections.put( connectionId, connection );
    }

    public void deregisterConnection( UniqueId connectionId )
    {
        connections.remove( connectionId );
    }

    public void deregisterConnection( Connection connection )
    {
        Iterator<Entry<UniqueId, Connection>> iterator = connections.entrySet().iterator();
        while ( iterator.hasNext() )
        {
            Entry<UniqueId, Connection> entry = iterator.next();
            if ( entry.getValue() == connection )
            {
                iterator.remove();
            }
        }
    }

    public SSLEngine getSSLEngine()
    {
        return sslEngine;
    }

    public EnumSet<TransportType> getSupportedTransportTypes()
    {
        return null; // TODO
    }

}
