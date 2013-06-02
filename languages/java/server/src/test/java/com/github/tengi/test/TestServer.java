package com.github.tengi.test;

import com.github.tengi.Connection;
import com.github.tengi.ConnectionConfiguration;
import com.github.tengi.ConnectionListenerAdapter;
import com.github.tengi.ConnectionManager;
import com.github.tengi.Message;
import com.github.tengi.Protocol;
import com.github.tengi.service.ServiceManager;

public class TestServer
    extends ConnectionListenerAdapter
{

    public static void main( String[] args )
        throws Exception
    {
        new TestServer();
    }

    private final Protocol protocol = new TestSerializationFactory();

    private final ServiceManager<Message> serviceManager = new ServiceManager<Message>( new TestService(), protocol );

    private TestServer()
        throws Exception
    {
        ConnectionConfiguration configuration = new ConnectionConfiguration( protocol );
        ConnectionManager connectionManager = new ConnectionManager( configuration, this );
        connectionManager.bind();
    }

    @Override
    public void onConnect( Connection connection )
    {
        connection.setMessageListener( serviceManager );
    }
}
