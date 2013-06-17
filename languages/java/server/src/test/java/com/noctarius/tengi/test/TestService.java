package com.noctarius.tengi.test;

import com.noctarius.tengi.Connection;
import com.noctarius.tengi.Message;
import com.noctarius.tengi.Streamable;
import com.noctarius.tengi.buffer.MemoryBuffer;
import com.noctarius.tengi.service.Service;

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
