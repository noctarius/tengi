tengi
=====

Realtime communication platform for Java / ActionScript / C# / HTML5 / and many more using Sockets, Websockets, HTML Longpolling

Short example with ActionScript client and Java server
=====

*ActionScript:*

```as3
package com.github.tengi
{
    /** imports */

    public class ActionScriptExample extends SimpleConnectionListener implements MessageListener
    {
        public function ActionScriptExample()
        {
			// Configure ConnectionManager, internal MemoryBuffer pool and used protocol
            var connectionManager:ConnectionManager = new ConnectionManager( 20 );

            var protocol:Protocol = new ExampleProtocol();
            var configuration:ConnectionConfiguration = new ConnectionConfiguration(protocol);
            configuration.host = "localhost";
            configuration.port = 80;

			// Connect to localhost through best available transporttype
            connectionManager.createConnection( configuration, this );
        }

        override public function onConnect( connection:ClientConnection ):void
        {
            var example:Example = new Example();
            example.value = "From AS3";
            // Response is received through messageReceived(...)
            connection.sendObject( example );

            var linkedExample:Example = new Example();
            linkedExample.value = "This one has it's own callback";
            // Response is received in given callback
            connection.sendLinkedObject( linkedExample,
                function ( request:Message, response:Message, connection:ClientConnection ):void
                {
                    var sendExample:Example = request.body as Example;
                    var receivedExample:Example = response.body as Example;
                    trace("send=" + sendExample.value + ", received=" + receivedExample.value);
                }
            );
        }

        public function messageReceived( message:Message, connection:ClientConnection ):void
        {
            var body:Streamable = message.body;
            if ( body is Example )
            {
                trace( "Received Example::value = " + (body as Example).value );
            }
        }

        public function rawDataReceived( memoryBuffer:MemoryBuffer, metadata:Streamable,
                                         connection:ClientConnection ):void
        {
            throw new IOError( "rawdata are not supported by this protocol :-(" );
        }
    }
}

internal class ExampleProtocol implements Protocol
{

    public function instantiate( classId:int ):Streamable
    {
        switch ( classId )
        {
            case 1:
                return new Example();
        }
        throw new IOError( "Unknown classId" );
    }

    public function getClassIdentifier( streamable:Streamable ):int
    {
        if ( streamable is Example )
        {
            return 1;
        }
        throw new IOError( "Unknown streamable type" );
    }

    public function isEntity( classId:int ):Boolean
    {
        return false;
    }

    public function readEntity( memoryBuffer:MemoryBuffer, classId:int ):Entity
    {
        return null;
    }

    public function get mimeType():String
    {
        return "binary/tengi";
    }
}

internal class Example implements Streamable
{

    private var _value:String;

    public function get value():String
    {
        return _value;
    }

    public function set value( value:String ):void
    {
        _value = value;
    }

    public function readStream( memoryBuffer:MemoryBuffer ):void
    {
        memoryBuffer.writeString( _value );
    }

    public function writeStream( memoryBuffer:MemoryBuffer ):void
    {
        _value = memoryBuffer.readString();
    }
}
```
*Java:*

```java
    package com.github.tengi;
    
    /** imports */
    
    public class ExampleEchoServer
        extends SimpleConnectionListener
        implements MessageListener
    {
    
        public static void main( String[] args )
            throws Exception
        {
            new ExampleEchoServer();
        }
    
        public ExampleEchoServer()
            throws Exception
        {
            // Listen on port 80 for IPv4 / IPv6 connections with different TCP protocols and reliable UDP
            Protocol protocol = new MyProtocol();
            ConnectionConfiguration configuration = ConnectionConfiguration.Builder().
                protocol( protocol ).unifiedPort(80).localAddresses().build();

            ConnectionManager connectionManager = new ConnectionManager( configuration, this );
            connectionManager.bind();
        }
    
        @Override
        public void onConnect( Connection connection )
        {
            System.out.println( "New connection: " + connection );
           connection.setMessageListener( this );
        }
    
        @Override
        public void messageReceived( Message message, Connection connection )
        {
            Streamable body = message.getBody();
            if ( body instanceof Example )
            {
                connection.sendObject( body );
            }
        }
    
        @Override
        public void rawDataReceived( MemoryBuffer rawBuffer, Streamable metadata, Connection connection )
        {
        }
    
        private static class MyProtocol
            implements Protocol
        {
    
            @Override
            public Streamable instantiate( int classId )
            {
                switch ( classId )
                {
                    case 1:
                        return new Example();
                }
                throw new RuntimeException( "Unknown classId" );
            }
    
            @Override
            public short getClassIdentifier( Streamable streamable )
            {
                if ( streamable instanceof Example )
                {
                    return 1;
                }
                throw new RuntimeException( "Unknown streamable type" );
            }
    
            @Override
            public boolean isEntity( short classId )
            {
                return false;
            }
    
            @Override
            public Entity readEntity( MemoryBuffer memoryBuffer, short classId )
            {
                return null;
            }

            @Override
            public String getMimeType()
            {
                return "binary/tengi";
            }
        }
    
        private static class Example
            implements Streamable
        {
            private String value;
    
            public String getValue()
            {
                return value;
            }
    
            public void setValue( String value )
            {
                this.value = value;
            }
    
            @Override
            public void readStream( MemoryBuffer memoryBuffer )
            {
                value = memoryBuffer.readString();
            }
    
            @Override
            public void writeStream( MemoryBuffer memoryBuffer )
            {
                memoryBuffer.writeString( value );
            }
    
        }
    
    }
```
