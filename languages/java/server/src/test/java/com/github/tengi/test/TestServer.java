package com.github.tengi.test;

import com.github.tengi.Message;
import com.github.tengi.SerializationFactory;
import com.github.tengi.service.ServiceManager;

public class TestServer
{

    public static void main( String[] args )
    {
        new TestServer();
    }

    private final SerializationFactory serializationFactory = new TestSerializationFactory();

    private final ServiceManager<Message> serviceManager = new ServiceManager<Message>( new TestService(),
                                                                                        serializationFactory );

    private TestServer()
    {

    }

}
