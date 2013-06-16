package com.github.tengi.codegen.ast;

public class ASTMethodParameter
    extends AbstractASTMember
{

    private ASTType parameterType;

    public ASTMethodParameter( ASTNode parent )
    {
        super( parent );
    }

    public ASTType getParameterType()
    {
        return parameterType;
    }

    public void setParameterType( ASTType parameterType )
    {
        this.parameterType = parameterType;
    }

}
