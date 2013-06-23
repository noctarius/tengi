package com.noctarius.tengi;

import com.noctarius.tengi.buffer.MemoryBuffer;

public abstract class AbstractEntity
    implements Entity
{

    private int entityId = -1;

    private int parentEntityId = -1;

    private int entityType;

    private int version;

    @Override
    public int getParentEntityId()
    {
        return parentEntityId;
    }

    @Override
    public int getEntityId()
    {
        return entityId;
    }

    @Override
    public int getEntityType()
    {
        return entityType;
    }

    @Override
    public String toString()
    {
        return "AbstractEntity [entityId=" + entityId + ", " + "parentEntityId=" + parentEntityId + "]";
    }

    @Override
    public void writeStream( MemoryBuffer memoryBuffer, Protocol protocol )
    {
        memoryBuffer.writeInt( entityId );
        memoryBuffer.writeInt( parentEntityId );
        memoryBuffer.writeInt( entityType );
        memoryBuffer.writeInt( version );
    }

    @Override
    public void readStream( MemoryBuffer memoryBuffer, Protocol protocol )
    {
        this.entityId = memoryBuffer.readInt();
        this.parentEntityId = memoryBuffer.readInt();
        this.entityType = memoryBuffer.readInt();
        this.version = memoryBuffer.readInt();
    }

    @Override
    public void writeEntityHeader( MemoryBuffer memoryBuffer )
    {
        memoryBuffer.writeByte( (byte) 1 );
    }
}
