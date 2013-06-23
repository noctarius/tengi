package com.noctarius.tengi.test;

import com.noctarius.tengi.Protocol;
import com.noctarius.tengi.Streamable;
import com.noctarius.tengi.buffer.MemoryBuffer;

public class Test2
    implements Streamable
{

    private String server;

    private String version;

    public String getServer()
    {
        return server;
    }

    public void setServer( String server )
    {
        this.server = server;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion( String version )
    {
        this.version = version;
    }

    @Override
    public void readStream( MemoryBuffer memoryBuffer, Protocol serializationFactory )
    {
        server = memoryBuffer.readString();
        version = memoryBuffer.readString();
    }

    @Override
    public void writeStream( MemoryBuffer memoryBuffer, Protocol serializationFactory )
    {
        memoryBuffer.writeString( server );
        memoryBuffer.writeString( version );
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( server == null ) ? 0 : server.hashCode() );
        result = prime * result + ( ( version == null ) ? 0 : version.hashCode() );
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        Test2 other = (Test2) obj;
        if ( server == null )
        {
            if ( other.server != null )
                return false;
        }
        else if ( !server.equals( other.server ) )
            return false;
        if ( version == null )
        {
            if ( other.version != null )
                return false;
        }
        else if ( !version.equals( other.version ) )
            return false;
        return true;
    }

    public String toString()
    {
        return "Test2 [server=" + server + ", version=" + version + "]";
    }

}
