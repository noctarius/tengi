package com.noctarius.tengi.client.lang
{
    public class IllegalArgumentError extends Error
    {

        function IllegalArgumentError( message:String = "", id:* = 0 )
        {
            super( message, id );
        }
    }
}