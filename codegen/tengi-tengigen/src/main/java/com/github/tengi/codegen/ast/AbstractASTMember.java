package com.github.tengi.codegen.ast;

public class AbstractASTMember
    extends AbstractASTNode
{

    public AbstractASTMember( ASTNode parent )
    {
        super( parent );
    }

    private ASTMemberVisibility visibility = ASTMemberVisibility.PUBLIC;

    private String identifier;

    public ASTMemberVisibility getVisibility()
    {
        return visibility;
    }

    public void setVisibility( ASTMemberVisibility visibility )
    {
        this.visibility = visibility;
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public void setIdentifier( String identifier )
    {
        this.identifier = identifier;
    }

}
