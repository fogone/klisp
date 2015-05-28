package ru.nobirds.klisp

import ru.nobirds.klisp.token.*
import java.math.BigDecimal
import kotlin.math.minus
import kotlin.math.plus

public class Parameters(public val scope:Scope, public val parameters:List<Token>) {

    public fun head():Token = parameters.get(0)
    public fun tail():List<Token> = parameters.subList(1, parameters.size())

}

public inline fun <reified E:Token> Parameters.get(index:Int):E {
    require(parameters.size() > index)
    val expression = parameters.get(index)
    requireNotNull(expression)
    // require(expression is E)
    return expression as E
}

public inline fun <reified E:Token> Parameters.resolve(index:Int):E {
    val expression = get<Token>(index).resolve(scope)
    // require(expression is E, "Can't cast ")
    return expression as E
}

trait Function {

    fun call(parameters:Parameters):Token

}

public class IfFunction() : Function {

    override fun call(parameters: Parameters): Token {
        val condition: Value<Boolean> = parameters.resolve(0)

        val result:Token = if (condition.value) parameters.get(1) else {
            if(parameters.parameters.size() < 3) Predefined.Nil else parameters.get<Token>(2)
        }

        return parameters.scope.resolve(result)
    }
}

public open class BinaryOperator<T, R:Token>(val body:(T, T)->R) : Function {
    override fun call(parameters:Parameters): Token {
        val v1:Value<T> = parameters.resolve(0)
        val v2:Value<T> = parameters.resolve(1)
        return body(v1.value, v2.value)
    }
}

public class BinaryBooleanFunction(val ibody:(Boolean, Boolean)->Boolean) :
        BinaryOperator<Boolean, Value<Boolean>>({ t1, t2 -> Value.of(ibody(t1, t2)) })

public object Operators {

    public val EqualsOperator:Function = BinaryOperator<Number, Value<Boolean>> { b1, b2 -> Value.of(b1.equals(b2)) }
    public val PlusOperator:Function = BinaryOperator<BigDecimal, Decimal> { b1, b2 -> Decimal(b1 + b2) }
    public val MinusOperator:Function = BinaryOperator<BigDecimal, Decimal> { b1, b2 -> Decimal(b1 - b2) }
    public val OrOperator:Function = BinaryBooleanFunction { b1, b2 ->  b1 || b2 }
    public val AndOperator:Function = BinaryBooleanFunction { b1, b2 ->  b1 && b2 }

}

public abstract class AbstractFunction(val parameters:List<String>, val body: Token) : Function {

    override fun call(parameters:Parameters): Token {
        val functionScope = createScope(HierarchyScope(parameters.scope))

        require(this.parameters.size() == parameters.parameters.size(),
                "Different parameters count for function. Definition: ${this.parameters.size()}, call: ${parameters.parameters.size()}.")

        val names = this.parameters.iterator()
        val expressions = parameters.parameters.iterator()

        while (names.hasNext()) {
            val name = names.next()
            val expression = functionScope.resolve(expressions.next())
            functionScope.registerVariable(name, expression)
        }

        return functionScope.resolve(body)
    }

    abstract fun createScope(scope: MutableScope): MutableScope

}

public class OrdinalFunction(val name:String, parameters:List<String>, body: Token) : AbstractFunction(parameters, body) {

    override fun createScope(scope: MutableScope): MutableScope = scope
}

public class RegisterFunctionFunction() : Function {

    override fun call(parameters:Parameters): Token {
        val name: Identifier = parameters.get(0)

        val arguments: Tuple = parameters.get(1)

        val body: Token = parameters.get(2)

        require(parameters.scope is MutableScope, "Can't define function for immutable scope")

        (parameters.scope as MutableScope).registerFunction(name.value,
                OrdinalFunction(name.value, arguments.elements.map { (it as Identifier).value }.toList(), body))

        return Predefined.Nil
    }
}

public class ListFunctionFunction() : Function {

    override fun call(parameters:Parameters): Token {
        return Tuple(parameters.tail().map { parameters.scope.resolve(it) }, false)
    }
}

public class LambdaFunction() : Function {

    override fun call(parameters: Parameters): Token {

    }
}
