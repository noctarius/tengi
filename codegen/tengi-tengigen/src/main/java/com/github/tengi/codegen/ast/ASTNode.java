package com.github.tengi.codegen.ast;

public interface ASTNode
{

    Iterable<ASTNode> getChildren();

    ASTNode getParent();

    void addChild( ASTNode node );

    void addChildAfter( ASTNode node, ASTNode after );

    void addChildBefore( ASTNode node, ASTNode before );

    void removeChild( ASTNode node );

    Class<?> getType();

    String getNodeName();

}
