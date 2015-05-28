package ru.nobirds.klisp

import ru.nobirds.klisp.token.Token

public class Parameters(public val scope: Scope, public val parameters:List<Token>) {

    public fun head(): Token = parameters.get(0)
    public fun tail():List<Token> = parameters.subList(1, parameters.size())

    public inline fun <reified E:Token> get(index:Int):E {
        require(parameters.size() > index)
        val expression = parameters.get(index)
        requireNotNull(expression)
        // require(expression is E)
        return expression as E
    }

    public inline fun <reified E:Token> resolve(index:Int):E {
        val expression = scope.resolve(get<Token>(index))
        // require(expression is E, "Can't cast ")
        return expression as E
    }

}

