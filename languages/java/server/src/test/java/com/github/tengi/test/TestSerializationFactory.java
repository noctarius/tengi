package com.github.tengi.test;

import com.github.tengi.Entity;
import com.github.tengi.Protocol;
import com.github.tengi.Streamable;
import com.github.tengi.buffer.MemoryBuffer;

public class TestSerializationFactory
    implements Protocol
{

    @Override
    public Streamable instantiate( short classId )
    {
        switch ( classId )
        {
            case 1:
                return new Test();
            case 2:
                return new Test2();
            case 3:
                return new Request();
        }
        throw new RuntimeException( "No type found for classId " + classId );
    }

    @Override
    public short getClassIdentifier( Streamable streamable )
    {
        if ( streamable instanceof Test )
        {
            return 1;
        }
        else if ( streamable instanceof Test2 )
        {
            return 2;
        }
        else if ( streamable instanceof Request )
        {
            return 3;
        }
        throw new RuntimeException( "No classId found for type " + streamable.getClass().getCanonicalName() );
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
