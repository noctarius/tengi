package com.github.tengi.codegen.ast;

import java.util.LinkedList;
import java.util.List;

public class ASTType
    extends AbstractASTMember
{

    public ASTType( ASTNode parent )
    {
        super( parent );
    }

    public List<ASTProperty> selectProperties()
    {
        List<ASTProperty> properties = new LinkedList<>();
        for ( ASTNode node : getChildren() )
        {
            if ( node instanceof ASTProperty )
            {
                properties.add( (ASTProperty) node );
            }
        }
        return properties;
    }

    public List<ASTMethod> selectMethods()
    {
        List<ASTMethod> methods = new LinkedList<>();
        for ( ASTNode node : getChildren() )
        {
            if ( node instanceof ASTMethod )
            {
                methods.add( (ASTMethod) node );
            }
        }
        return methods;
    }

}
