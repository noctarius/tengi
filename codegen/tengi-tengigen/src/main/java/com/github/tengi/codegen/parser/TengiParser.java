package com.github.tengi.codegen.parser;

import org.parboiled.BaseParser;
import org.parboiled.MatcherContext;
import org.parboiled.Rule;
import org.parboiled.annotations.DontLabel;
import org.parboiled.annotations.MemoMismatches;
import org.parboiled.annotations.SuppressNode;
import org.parboiled.matchers.CustomMatcher;

public class TengiParser
    extends BaseParser<Object>
{

    final Rule DOT = Terminal( "." );

    final Rule LBRK = Terminal( "[" );

    final Rule RBRK = Terminal( "]" );

    final Rule COMMA = Terminal( "," );

    final Rule LPOINT = Terminal( "<" );

    final Rule RPOINT = Terminal( ">" );

    final Rule PRIMITIVE_BYTE = Keyword( "Byte" );

    final Rule PRIMITIVE_BOOL = Keyword( "Bool" );

    final Rule PRIMITIVE_SHORT = Keyword( "Short" );

    final Rule PRIMITIVE_INT = Keyword( "Int" );

    final Rule PRIMITIVE_LONG = Keyword( "Long" );

    final Rule PRIMITIVE_FLOAT = Keyword( "Float" );

    final Rule PRIMITIVE_DOUBLE = Keyword( "Double" );

    final Rule PRIMITIVE_ASCII = Keyword( "ASCII" );

    final Rule PRIMITIVE_UTF = Keyword( "UTF" );

    Rule Primitives()
    {
        return Sequence( FirstOf( PRIMITIVE_BYTE, PRIMITIVE_BOOL, PRIMITIVE_SHORT, PRIMITIVE_INT, PRIMITIVE_LONG,
                                  PRIMITIVE_FLOAT, PRIMITIVE_DOUBLE, PRIMITIVE_ASCII, PRIMITIVE_UTF ),
                         TestNot( LetterOrDigitOrUnderscore() ), Spacing() );
    }

    Rule ArrayDimension()
    {
        return Sequence( LBRK, RBRK );
    }

    Rule TypeArgument()
    {
        return FirstOf( Primitives(), RefType() );
    }

    Rule TypeArguments()
    {
        return Sequence( LPOINT, Spacing(), TypeArgument(), Spacing(),
                         ZeroOrMore( Sequence( COMMA, Spacing(), TypeArgument() ) ) );
    }

    Rule ObjectType()
    {
        return Sequence( QualifiedIdentifier(), Optional( TypeArguments() ) );
    }

    Rule RefType()
    {
        return FirstOf( Sequence( Primitives(), OneOrMore( ArrayDimension() ) ), ObjectType() );
    }

    Rule Type()
    {
        return FirstOf( Primitives(), RefType() );
    }

    Rule QualifiedIdentifier()
    {
        return Sequence( Identifier(), OneOrMore( Sequence( DOT, Identifier() ) ), Spacing().suppressNode() );
    }

    Rule Identifier()
    {
        return Sequence( TestNot( PredefinedKeywords() ), LetterOrUnderscore(), OneOrMore( LetterOrDigitOrUnderscore() ) );
    }

    @MemoMismatches
    Rule PredefinedKeywords()
    {
        return Sequence( FirstOf( "entity", "component", "class", "enum", "service", "package", "protocol",
                                  Primitives() ), TestNot( LetterOrDigitOrUnderscore() ) );
    }

    @DontLabel
    Rule Keyword( String keyword )
    {
        return Terminal( keyword, LetterOrDigitOrUnderscore() );
    }

    @SuppressNode
    Rule Spacing()
    {
        return ZeroOrMore(
        // whitespace
        OneOrMore( FirstOf( AnyOf( " \t\r\n\f" ).suppressNode(), MultiLineComment(), SingleLineComment(), DocComment() ) ) );
    }

    @SuppressNode
    Rule DocComment()
    {
        return Sequence( "/**", ZeroOrMore( TestNot( "*/" ), ANY ).label( "DocComment" ), "*/" ).suppressNode();
    }

    @SuppressNode
    Rule MultiLineComment()
    {
        return Sequence( "/*", ZeroOrMore( TestNot( "*/" ), ANY ).label( "MultiLineComment" ), "*/" ).suppressNode();
    }

    @SuppressNode
    Rule SingleLineComment()
    {
        return Sequence( "//", ZeroOrMore( TestNot( AnyOf( "\r\n" ) ), ANY ).label( "SingleLineComment" ),
                         FirstOf( "\r\n", '\r', '\n', EOI ).suppressNode() ).suppressNode();
    }

    @DontLabel
    @SuppressNode
    Rule Terminal( String value )
    {
        return Sequence( value, Spacing() ).label( "'" + value + "'" );
    }

    @DontLabel
    @SuppressNode
    Rule Terminal( String value, Rule mustNotFollow )
    {
        return Sequence( value, TestNot( mustNotFollow ), Spacing() ).label( "'" + value + "'" );
    }

    Rule UnicodeEscape()
    {
        return Sequence( 'u', HexDigit(), HexDigit(), HexDigit(), HexDigit() );
    }

    Rule HexDigit()
    {
        return FirstOf( CharRange( 'a', 'f' ), CharRange( 'A', 'F' ), CharRange( '0', '9' ) );
    }

    @MemoMismatches
    Rule LetterOrUnderscore()
    {
        return FirstOf( Sequence( '\\', UnicodeEscape() ), new ValidIdentifierMatcher( true ) );
    }

    @MemoMismatches
    Rule LetterOrDigitOrUnderscore()
    {
        return FirstOf( Sequence( '\\', UnicodeEscape() ), new ValidIdentifierMatcher( false ) );
    }

    @MemoMismatches
    Rule FullDigitOrUnderscore()
    {
        return FirstOf( CharRange( '0', '9' ), '_' );
    }

    @MemoMismatches
    Rule DecimalDigit()
    {
        return Sequence( FullDigitOrUnderscore(), '.', CharRange( '0', '9' ) );
    }

    private class ValidIdentifierMatcher
        extends CustomMatcher
    {

        private final boolean charOnly;

        private ValidIdentifierMatcher( boolean charOnly )
        {
            super( "Identifier" );
            this.charOnly = charOnly;
        }

        @Override
        public <V> boolean match( MatcherContext<V> context )
        {
            if ( !accept( context.getCurrentChar() ) )
            {
                return false;
            }
            context.advanceIndex( 1 );
            context.createNode();
            return true;
        }

        @Override
        public boolean isSingleCharMatcher()
        {
            return true;
        }

        @Override
        public boolean canMatchEmpty()
        {
            return false;
        }

        @Override
        public boolean isStarterChar( char c )
        {
            return accept( c );
        }

        @Override
        public char getStarterChar()
        {
            return 'a';
        }

        private boolean accept( char c )
        {
            return ( c >= 'A' && c <= 'Z' ) || ( c >= 'a' && c <= 'z' ) || ( c == '_' )
                || ( !charOnly && ( c >= '0' && c <= '9' ) );
        }
    }

}
