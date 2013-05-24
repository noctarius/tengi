package com.github.tengi.test;

import com.github.tengi.Protocol;
import com.github.tengi.Streamable;
import com.github.tengi.buffer.MemoryBuffer;

public class Test
    implements Streamable
{

    private int foo;

    private String bar;

    public int getFoo()
    {
        return foo;
    }

    public void setFoo( int foo )
    {
        this.foo = foo;
    }

    public String getBar()
    {
        return bar;
    }

    public void setBar( String bar )
    {
        this.bar = bar;
    }

    @Override
    public void readStream( MemoryBuffer memoryBuffer, Protocol serializationFactory )
    {
        foo = memoryBuffer.readInt();
        bar = memoryBuffer.readString();
    }

    @Override
    public void writeStream( MemoryBuffer memoryBuffer, Protocol serializationFactory )
    {
        memoryBuffer.writeInt( foo );
        memoryBuffer.writeString( bar );
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( bar == null ) ? 0 : bar.hashCode() );
        result = prime * result + foo;
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
        Test other = (Test) obj;
        if ( bar == null )
        {
            if ( other.bar != null )
                return false;
        }
        else if ( !bar.equals( other.bar ) )
            return false;
        if ( foo != other.foo )
            return false;
        return true;
    }

    public String toString()
    {
        return "Test [foo=" + foo + ", bar=" + bar + "]";
    }

}
