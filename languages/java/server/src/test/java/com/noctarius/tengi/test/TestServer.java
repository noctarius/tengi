package com.noctarius.tengi.test;

import com.noctarius.tengi.Connection;
import com.noctarius.tengi.ConnectionConfiguration;
import com.noctarius.tengi.ConnectionListenerAdapter;
import com.noctarius.tengi.ConnectionManager;
import com.noctarius.tengi.Message;
import com.noctarius.tengi.Protocol;
import com.noctarius.tengi.service.ServiceManager;

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
        ConnectionConfiguration configuration = ConnectionConfiguration.Builder().protocol( protocol ).build();
        ConnectionManager connectionManager = new ConnectionManager( configuration, this );
        connectionManager.bind();
    }

    @Override
    public void onConnect( Connection connection )
    {
        connection.setMessageListener( serviceManager );
    }
}
