package com.github.tengi.codegen.ast;

public class ASTMethod
    extends AbstractASTMember
{

    private ASTType returnType;

    public ASTMethod( ASTNode parent )
    {
        super( parent );
    }

    public ASTType getReturnType()
    {
        return returnType;
    }

    public void setReturnType( ASTType returnType )
    {
        this.returnType = returnType;
    }

}
