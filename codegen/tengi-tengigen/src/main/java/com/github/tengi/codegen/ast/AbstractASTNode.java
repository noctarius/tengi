package com.github.tengi.codegen.ast;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractASTNode
    implements ASTNode
{

    private final List<ASTNode> children = new LinkedList<>();

    private ASTNode parent;

    public AbstractASTNode( ASTNode parent )
    {
        this.parent = parent;
    }

    @Override
    public Iterable<ASTNode> getChildren()
    {
        return children;
    }

    @Override
    public ASTNode getParent()
    {
        return parent;
    }

    @Override
    public void addChild( ASTNode node )
    {
        children.add( node );
    }

    @Override
    public void addChildAfter( ASTNode node, ASTNode after )
    {
        int indexOf = children.indexOf( after );
        children.add( indexOf + 1, node );
    }

    @Override
    public void addChildBefore( ASTNode node, ASTNode before )
    {
        int indexOf = children.indexOf( before );
        children.add( indexOf, node );
    }

    @Override
    public void removeChild( ASTNode node )
    {
        children.remove( node );
    }

    @Override
    public Class<?> getType()
    {
        return getClass();
    }

    @Override
    public String getNodeName()
    {
        return getClass().getSimpleName().toUpperCase();
    }

}
