package ru.nobirds.klisp.token

import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer

public trait Token {

    public fun writeToken(writer: Writer)

    override fun toString(): String = StringWriter().use {
        writeToken(it)
        it.toString()
    }

}