package ru.nobirds.klisp

import ru.nobirds.klisp.expression.NilExpression


fun main(args: Array<String>) {

    val text = """
(defun fibonacci(n)
    (if (or (= n 0) (= n 1))
     1
     (+ (fibonacci (- n 1)) (fibonacci (- n 2)))))
(fibonacci 10)
    """

    val text2 = """
    ((lambda (x) (list x (list 'quote x))) '(lambda (x) (list x (list 'quote x))))
    """

    val result = InterpretatorImpl().run(text2)

    for (expression in scopeAndMain.main) {
        val result = expression.resolve(scopeAndMain.scope)
        if(result != NilExpression)
            println("Result: $result")
    }
    
}