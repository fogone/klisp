package ru.nobirds.klisp.token

import ru.nobirds.klisp.token.*
import java.math.BigDecimal
import java.util.ArrayList

public data class TokenRule(val applicable:Char.()->Boolean, val parse:TokenStream.()-> Token) {

    public fun isApplicable(char:Char):Boolean = char.applicable()

}

public class TokenStream(val text:String) {

    private val parseRules:List<TokenRule> = arrayListOf(
            TokenRule(Char::isFloatNumber, TokenStream::parseNumber),
            TokenRule(Char::isIdentifier, TokenStream::parseIdentifier),
            TokenRule({ equals('(') }, TokenStream::parseTuple),
            TokenRule({ equals('\'') }, TokenStream::parseLazyTuple)
    )

    private val iterator = text.iterator()
    private var currentChar:Char = iterator.next()
    private var endOfStream = false

    public fun parseTokens():List<Token> = sequence { parseToken() }.toList()

    public fun parseTuple(): Tuple {
        skipChar('(')

        val elements = parseTokens()

        skipChar(')')

        return Tuple(elements, false)
    }

    public fun parseLazyTuple(): Tuple {
        skipChar('\'')

        skipWhitespaces()

        skipChar('(')

        val elements = parseTokens()

        skipChar(')')

        return Tuple(elements, true)
    }

    public fun parseToken(): Token? {
        skipWhitespaces()

        val parser = parseRules.firstOrNull { it.isApplicable(currentChar) }?.parse

        val expression = if(parser == null) return null
            else parser()

        skipWhitespaces()

        return expression
    }

    private fun fetch(condition:Char.()->Boolean):String = StringBuilder {
        while(currentChar.condition() && !endOfStream) {
            append(currentChar)
            skipChar(currentChar)
        }
    }.toString()

    private fun parseIdentifier(): Identifier = Identifier(fetch(Char::isIdentifier))

    public fun parseNumber(): Decimal = Decimal(BigDecimal(fetch(Char::isFloatNumber)))

    public fun skipWhitespaces() {
        fetch(Char::isSpace)
        if(currentChar==';')
            fetch(Char::isNotNewLine)
        fetch(Char::isSpace)
    }

    public fun skipChar(ch:Char) {
        if(currentChar != ch || endOfStream)
            throw IllegalStateException()

        if(iterator.hasNext())
            currentChar = iterator.next()
        else
            endOfStream = true
    }
}