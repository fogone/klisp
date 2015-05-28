package ru.nobirds.klisp

import ru.nobirds.klisp.token.*
import java.util.ArrayList
import java.util.HashMap

public trait Scope {

    fun resolveFunction(name:String): Function

    fun isFunctionRegistered(name:String):Boolean

    fun resolveVariable(name:String): Token

    fun isVariableRegistered(name:String):Boolean

    fun resolve(token: Token): Token

}

public object RootScope : AbstractScope() {

    init {
        registerFunction("defun", RegisterFunctionFunction())
        registerFunction("list", ListFunctionFunction())
        registerFunction("if", IfFunction())
        registerFunction("=", Operators.EqualsOperator)
        registerFunction("+", Operators.PlusOperator)
        registerFunction("-", Operators.MinusOperator)
        registerFunction("or", Operators.OrOperator)
        registerFunction("and", Operators.AndOperator)

        registerVariable("T", Predefined.True)
        registerVariable("Nil", Predefined.Nil)
    }

    override fun resolveFunctionImpl(name: String): Function? = functions.get(name)

    override fun resolveVariableImpl(name: String): Token? = variables.get(name)

    override fun isFunctionRegistered(name: String): Boolean = functions.containsKey(name)

    override fun isVariableRegistered(name: String): Boolean = variables.containsKey(name)

}

public trait MutableScope : Scope {

    fun registerVariable(name:String, value: Token)

    fun registerFunction(name:String, function:Function)

}

public abstract class AbstractScope() : MutableScope {

    protected val variables:MutableMap<String, Token> = HashMap()
    protected val functions:MutableMap<String, Function> = HashMap()

    public override fun registerVariable(name:String, value: Token) {
        require(!variables.containsKey(name), "Variable with name $name already defined.")
        variables.put(name, value)
    }

    public override fun registerFunction(name:String, function:Function) {
        require(!functions.containsKey(name), "Function with name ${name} already defined.")
        functions.put(name, function)
    }

    public override fun resolveFunction(name:String): Function {
        val function = resolveFunctionImpl(name)
        requireNotNull(function, "Function with name $name not defined.")
        return function!!
    }

    protected abstract fun resolveFunctionImpl(name: String):Function?

    public override fun resolveVariable(name:String): Token {
        val variable = resolveVariableImpl(name)
        requireNotNull(variable, "Variable with name $name not defined.")
        return variable!!
    }

    protected abstract fun resolveVariableImpl(name: String): Token?

    override fun resolve(token: Token): Token {
        return when(token) {
            is Tuple -> call(token)
            is Identifier -> resolveVariable(token.value)
            is Value<*> -> token
            else -> Predefined.Nil
        }
    }

    private fun call(tuple: Tuple): Token {
        if(tuple.lazy)
            return tuple.copy(false)

        require(tuple.head is Identifier)

        val name = (tuple.head as Identifier).value

        val function = resolveFunction(name)

        return function.call(Parameters(this, tuple.tail.map { resolve(it) }))
    }

}

public class HierarchyScope(val parent:Scope) : AbstractScope() {

    override fun resolveFunctionImpl(name: String): Function? = functions.getOrElse(name) { parent.resolveFunction(name) }

    override fun resolveVariableImpl(name: String): Token? = variables.getOrElse(name) { parent.resolveVariable(name) }

    override fun isFunctionRegistered(name: String): Boolean = functions.containsKey(name) || parent.isFunctionRegistered(name)

    override fun isVariableRegistered(name: String): Boolean = variables.containsKey(name) || parent.isVariableRegistered(name)

}

/*
public class MultiScope(vararg scopes:Scope) : Scope {

    private val scopes:List<Scope> = scopes.toList()

    override fun resolveFunction(name: String): Function = scopes.first { it.isFunctionRegistered(name) }.resolveFunction(name)

    override fun resolveVariable(name: String): Token = scopes.first { it.isVariableRegistered(name) }.resolveVariable(name)

    override fun isFunctionRegistered(name: String): Boolean = scopes.any { it.isFunctionRegistered(name) }

    override fun isVariableRegistered(name: String): Boolean = scopes.any { it.isVariableRegistered(name) }

}
*/
