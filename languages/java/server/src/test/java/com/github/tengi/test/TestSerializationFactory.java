package com.github.tengi.test;

import com.github.tengi.Entity;
import com.github.tengi.SerializationFactory;
import com.github.tengi.Streamable;
import com.github.tengi.buffer.MemoryBuffer;

public class TestSerializationFactory
    implements SerializationFactory
{

    @Override
    public Streamable instantiate( int classId )
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
    public boolean isEntity( int classId )
    {
        return false;
    }

    @Override
    public Entity readEntity( MemoryBuffer memoryBuffer, int classId )
    {
        return null;
    }

}
