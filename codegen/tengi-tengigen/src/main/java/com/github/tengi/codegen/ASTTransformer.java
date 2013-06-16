package com.github.tengi.codegen;

import com.github.tengi.codegen.ast.ASTNode;

public interface ASTTransformer
{

    int getPriority();

    boolean accept( ASTNode node );

    TransformationResult transform( ASTNode node );

    public static enum TransformationResult
    {
        SKIP_SUBTREE, CONTINUE, STOP
    }

}
