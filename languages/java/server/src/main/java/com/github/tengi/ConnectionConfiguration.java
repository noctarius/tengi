package com.github.tengi;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ConnectionConfiguration
{
    private final Protocol protocol;

    private int port = 80;

    private InetAddress[] addresses;

    private String httpContext = "/http";

    private String wssContext = "/wss";

    private boolean ssl = true;

    public ConnectionConfiguration( Protocol protocol )
    {
        this.protocol = protocol;

        try
        {
            addresses = getAllLocalIntefaces();
        }
        catch ( UnknownHostException e )
        {
            addresses = new InetAddress[0];
        }
    }

    public int getPort()
    {
        return port;
    }

    public void setPort( int port )
    {
        this.port = port;
    }

    public InetAddress[] getAddresses()
    {
        return addresses;
    }

    public void setAddresses( InetAddress[] addresses )
    {
        this.addresses = addresses;
    }

    public String getHttpContext()
    {
        return httpContext;
    }

    public void setHttpContext( String httpContext )
    {
        this.httpContext = httpContext;
    }

    public String getWssContext()
    {
        return wssContext;
    }

    public void setWssContext( String wssContext )
    {
        this.wssContext = wssContext;
    }

    public boolean isSsl()
    {
        return ssl;
    }

    public void setSsl( boolean ssl )
    {
        this.ssl = ssl;
    }

    public Protocol getProtocol()
    {
        return protocol;
    }

    public static InetAddress[] getAllLocalIntefaces()
        throws UnknownHostException
    {
        return new InetAddress[] { Inet4Address.getByName( "0.0.0.0" ), Inet6Address.getByName( "::/0" ) };
    }

}
