package ru.nobirds.klisp.token

public object CharacterSet {

    public val SpaceCharSet:Set<Char> = hashSetOf(' ', '\t', '\n')
    public val NumberCharSet:Set<Char> = ('0'..'9').toSet()
    public val FloatNumberCharSet:Set<Char> = (NumberCharSet + '.').toSet()
    public val OperatorCharSet:Set<Char> = hashSetOf('=', '+', '-', '*', '/', '%')
    public val IdentifierCharSet:Set<Char> = (('a'..'z') + ('A'..'Z') + NumberCharSet + OperatorCharSet).toSet()

}

public fun Char.isNotNewLine():Boolean = this != '\n'
public fun Char.isSpace():Boolean = this in CharacterSet.SpaceCharSet
public fun Char.isIdentifier():Boolean = this in CharacterSet.IdentifierCharSet
public fun Char.isOperator():Boolean = this in CharacterSet.OperatorCharSet
public fun Char.isNumber():Boolean = this in CharacterSet.NumberCharSet
public fun Char.isFloatNumber():Boolean = this in CharacterSet.FloatNumberCharSet
