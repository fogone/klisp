package ru.nobirds.klisp.token

import java.io.Writer

public class Identifier(val value:String) : Token {
    override fun equals(other: Any?): Boolean =
            other is Identifier && other.value.equalsIgnoreCase(value)

    override fun writeToken(writer: Writer) {
        writer.write(value)
    }
}