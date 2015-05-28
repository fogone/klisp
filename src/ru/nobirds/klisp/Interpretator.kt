package ru.nobirds.klisp

import ru.nobirds.klisp.token.Token

public trait Interpretator {

    public fun run(text:String): List<Token>

}

