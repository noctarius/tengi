package com.github.tengi.test;

import java.net.InetAddress;

import com.github.tengi.Connection;
import com.github.tengi.ConnectionManager;
import com.github.tengi.Message;
import com.github.tengi.Protocol;
import com.github.tengi.ConnectionListenerAdapter;
import com.github.tengi.service.ServiceManager;

public class TestServer
    extends ConnectionListenerAdapter
{

    public static void main( String[] args )
        throws Exception
    {
        new TestServer();
    }

    private final Protocol serializationFactory = new TestSerializationFactory();

    private final ServiceManager<Message> serviceManager = new ServiceManager<Message>( new TestService(),
                                                                                        serializationFactory );

    private TestServer()
        throws Exception
    {
        ConnectionManager connectionManager =
            new ConnectionManager( "application/bbbinary", this, serializationFactory );
        connectionManager.bind( 80, InetAddress.getLoopbackAddress() );
    }

    @Override
    public void onConnect( Connection connection )
    {
        connection.setMessageListener( serviceManager );
    }
}
