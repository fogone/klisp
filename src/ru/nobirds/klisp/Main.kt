package ru.nobirds.klisp

import ru.nobirds.klisp.token.Predefined


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

    val result = Interpretator().run(text)

    for (expression in result) {
        if(expression != Predefined.Nil)
            println("Result: $result")
    }
    
}