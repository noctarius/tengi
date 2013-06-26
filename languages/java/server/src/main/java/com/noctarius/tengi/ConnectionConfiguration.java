package com.noctarius.tengi;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ConnectionConfiguration
{
    private final Protocol protocol;

    private final int port;

    private final Iterable<InetAddress> addresses;

    private final String httpContext;

    private final String wssContext;

    private final boolean ssl;

    private ConnectionConfiguration( ConnectionConfigurationBuilder builder )
    {
        this.protocol = builder.protocol;
        this.port = builder.port;
        this.addresses = Collections.unmodifiableList( builder.addresses );
        this.httpContext = builder.httpContext;
        this.wssContext = builder.wssContext;
        this.ssl = builder.ssl;
    }

    public int getPort()
    {
        return port;
    }

    public Iterable<InetAddress> getAddresses()
    {
        return addresses;
    }

    public String getHttpContext()
    {
        return httpContext;
    }

    public String getWssContext()
    {
        return wssContext;
    }

    public boolean isSsl()
    {
        return ssl;
    }

    public Protocol getProtocol()
    {
        return protocol;
    }

    public static ConnectionConfigurationBuilder Builder()
    {
        return new ConnectionConfigurationBuilder();
    }

    public static class ConnectionConfigurationBuilder
    {
        private Protocol protocol;

        private int port = 80;

        private List<InetAddress> addresses;

        private String httpContext = "/http";

        private String wssContext = "/wss";

        private boolean ssl = true;

        private ConnectionConfigurationBuilder()
        {
            try
            {
                addresses = getAllLocalIntefaces();
            }
            catch ( UnknownHostException e )
            {
                addresses = Collections.emptyList();
            }
        }

        public int port()
        {
            return port;
        }

        public ConnectionConfigurationBuilder port( int port )
        {
            this.port = port;
            return this;
        }

        public Iterable<InetAddress> addresses()
        {
            return addresses;
        }

        public ConnectionConfigurationBuilder addresses( InetAddress... addresses )
        {
            addresses( Arrays.asList( addresses ) );
            return this;
        }

        public ConnectionConfigurationBuilder addresses( Iterable<InetAddress> addresses )
        {
            this.addresses = new ArrayList<InetAddress>();
            Iterator<InetAddress> iterator = addresses.iterator();
            while ( iterator.hasNext() )
            {
                InetAddress address = iterator.next();
                if ( address != null )
                {
                    this.addresses.add( address );
                }
            }
            return this;
        }

        public ConnectionConfigurationBuilder localAddresses()
        {
            try
            {
                addresses( getAllLocalIntefaces() );
                return this;
            }
            catch ( UnknownHostException e )
            {
                throw new RuntimeException( "Local interfaces could not be queried", e );
            }
        }

        public String httpContext()
        {
            return httpContext;
        }

        public ConnectionConfigurationBuilder httpContext( String httpContext )
        {
            this.httpContext = httpContext;
            return this;
        }

        public String wssContext()
        {
            return wssContext;
        }

        public ConnectionConfigurationBuilder wssContext( String wssContext )
        {
            this.wssContext = wssContext;
            return this;
        }

        public boolean ssl()
        {
            return ssl;
        }

        public ConnectionConfigurationBuilder ssl( boolean ssl )
        {
            this.ssl = ssl;
            return this;
        }

        public Protocol protocol()
        {
            return protocol;
        }

        public ConnectionConfigurationBuilder protocol( Protocol protocol )
        {
            this.protocol = protocol;
            return this;
        }

        public ConnectionConfiguration build()
        {
            if ( protocol == null )
            {
                throw new IllegalStateException( "protocol must not be null" );
            }
            return new ConnectionConfiguration( this );
        }

        private static List<InetAddress> getAllLocalIntefaces()
            throws UnknownHostException
        {
            return Arrays.asList( new InetAddress[] { Inet4Address.getByName( "0.0.0.0" ),
                Inet6Address.getByName( "::/0" ) } );
        }

    }

}
