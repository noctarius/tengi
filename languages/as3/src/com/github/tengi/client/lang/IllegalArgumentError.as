package com.github.tengi.client.lang
{
    public class IllegalArgumentError extends Error
    {

        function IllegalArgumentError( message:* = "", id:* = 0 )
        {
            super( message, id );
        }
    }
}