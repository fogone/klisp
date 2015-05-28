package ru.nobirds.klisp

import ru.nobirds.klisp.token.*

public class InterpretatorImpl() : Interpretator {

    override fun run(text: String): List<Token> {
        val tokens = TokenStream(text).parseTokens()

        return run(tokens, HierarchyScope(RootScope))
    }

    public fun run(tokens:List<Token>, scope:Scope):List<Token> {
        return tokens.map { scope.resolve(it) }
    }

}