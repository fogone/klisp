package ru.nobirds.klisp.token

import ru.nobirds.klisp.token.Predefined
import java.io.Writer

public open class Tuple(val elements:List<Token>, val lazy:Boolean) : Token {

    public val head:Token
        get() = elements.firstOrNull() ?: Predefined.Nil

    public val tail:List<Token>
        get() = elements.subList(1, elements.size())

    public fun <T:Token> get(index:Int):T = elements[index] as T

    override fun writeToken(writer: Writer) {
        writer.write("(")

        elements.forEachIndexed { i, expression ->
            if (i > 0) writer.write(" ")
            expression.writeToken(writer)
        }

        writer.write(")")
    }

    fun copy(lazy:Boolean = this.lazy):Tuple = Tuple(elements, lazy)
}