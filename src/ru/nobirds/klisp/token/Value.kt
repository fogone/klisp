package ru.nobirds.klisp.token

import java.io.Writer

public trait Value<T> : Token {

    val value:T

    override fun writeToken(writer: Writer) {
        writer.write(value.toString())
    }

    companion object {
        public fun <T> of(value:T):Value<T> = ValueImpl<T>(value)
    }

}

class ValueImpl<T>(override val value:T) : Value<T>