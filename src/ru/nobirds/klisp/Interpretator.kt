package ru.nobirds.klisp

import ru.nobirds.klisp.token.Token
import ru.nobirds.klisp.token.TokenStream

public class Interpretator {

    fun run(text: String): List<Token> {
        val tokens = TokenStream(text).parseTokens()

        return run(tokens, HierarchyScope(RootScope))
    }

    public fun run(tokens:List<Token>, scope:Scope):List<Token> {
        return tokens.map { scope.resolve(it) }
    }


}

