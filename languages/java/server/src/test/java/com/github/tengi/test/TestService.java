package com.github.tengi.test;

import com.github.tengi.Connection;
import com.github.tengi.Message;
import com.github.tengi.Streamable;
import com.github.tengi.buffer.MemoryBuffer;
import com.github.tengi.service.Service;

public class TestService
    implements Service<Message>
{

    @Override
    public void call( Message request, Connection connection )
    {
        if ( request.getBody() instanceof Test )
        {
            Test test = (Test) request.getBody();
            Test2 test2 = new Test2();
            test2.setServer( "Server-Request: " + test.getFoo() );
            test2.setVersion( "0.0.1 - " + test.getBar() );

            Message response = connection.prepareMessage( test2 );
            connection.sendMessage( response, null );
        }
        else
        {
            System.out.println( request );
        }
    }

    @Override
    public void call( MemoryBuffer request, Streamable metadata, Connection connection )
    {
        System.out.println( metadata );
    }

}
