import java.util.*
import kotlin.Exception
import kotlin.math.*

val doubleRegex =  Regex("[0-9]+(\\.[0-9]+)?")
val idRegex = Regex("[a-zA-Z][_a-zA-Z0-9]{0,30}")
val unMinusRegex = Regex("(^|[^\\d\\w_])-")
val fnRegex = Regex("("+ idRegex.pattern + ")\\((.*?)\\)")


fun main(){
    while(true){
        println("Write expression: ")
        val exp = readLine()
        println(preprocess(exp!!))
        val res = process(exp!!)
        println(res.toString())
        if(res.none { (it is String && it != "Fn") || it == '=' })
            println(eval(res))
    }
}

fun Char.isOperator() = listOf('+','-','*','/','^','~','=').contains(this)
fun GEPriority( c1: Char , c2: Char): Boolean {

    fun prior(c: Char) = when(c) {
        '(' -> 0
        '[' -> 0
        '=' -> 1
        '+' -> 7
        '-' -> 7
        '~' -> 8
        '*' -> 8
        '^' -> 9
        else -> throw Exception("Not accepted operator")
    }
    return prior(c1) >= prior(c2)
}
fun Any.op(o: Any, op:Char): Double {
    assert(o is Double && this is Double)
    val n1 = o as Double
    val n2 = this as Double
    return when(op) {
        '*' -> n2*n1
        '/' -> n1/n2
        '+' -> n2+n1
        '-' -> n1-n2
        '^' -> n1.pow(n2)
        else -> throw Exception("Not accepted operator")
    }
}

fun eval(queue : Queue<Any>): Double {
    val stack = Stack<Any>()
    while( queue.isNotEmpty() ) {
        when (val head = queue.remove()) {
            '~' -> stack.push(-1 * stack.pop() as Double)
            "Fn" -> {
                var argsNum = stack.pop() as Int
                val args = LinkedList<Double>()
                while(--argsNum > 0)  args.add(stack.pop() as Double)
                stack.push( (stack.pop() as Function).call(args) )
            }
            is Char -> stack.push(stack.pop().op(stack.pop(), head))
            else -> stack.push(head)
        }
        println("Queue: $queue")
        println("Stack: $stack")
        println("---------------")
    }
    return stack.single() as Double
}
fun preprocess(s: String): String {
        var res = unMinusRegex.replace(s){ m -> m.groupValues[1] +"~" }
        return fnRegex.replace(res) { m -> m.groupValues[1] + "{" + m.groupValues[2] + "}" }
}

class Function(private val name: String){
    fun call(list: List<Double>) =
        when(name) {
            "sin" -> sin(list.single())
            "cos" -> cos(list.single())
            "min" -> min(list[0], list[1])
            "max" -> max(list[0], list[1])
            "const3" -> 3.0
            else -> throw Exception("unrecognized function")
        }
    override fun toString() = name
}

fun process (exp: String ): Queue<Any> {
    var temp = preprocess( exp)
    val stack = Stack<Any>()
    val res = LinkedList<Any>()

    while (temp.isNotEmpty()){
        when(val t = temp[0]){
            ' ' -> {}
            '(' -> stack.push(t)
            ')' -> {
                while (stack.peek() != '(') res.add(stack.pop());
                stack.pop() }
            '[' -> { stack.push(t); stack.push(2) }
            '{' -> { stack.push(t); stack.push(2) }
            ',' -> {
                while (stack.peek() !is Int) res.add(stack.pop())
                stack.push(stack.pop() as Int + 1)
            }
            ']'-> {
                while (stack.peek() != '[') res.add(stack.pop())
                stack.pop()
                res.add(t)
            }
            '}'-> {
                while (stack.peek() != '{') res.add(stack.pop())
                stack.pop()
                res.add("Fn")
            }
            else -> when {
                t.isDigit() -> doubleRegex.find(temp)?.let { res.add(it.value.toDouble()); temp = temp.substring(it.value.length-1)}
                t.isLetter() -> idRegex.find(temp)?.let {
                    val id = it.value;
                    temp = temp.substring(it.value.length-1);
                    if(temp[1] == '{') res.add(Function(id))
                    else res.add(id)
                }
                t.isOperator() -> {
                    while (stack.isNotEmpty() && stack.peek() !is Int && GEPriority(stack.peek() as Char, t)) res.add(stack.pop())
                    stack.add(t)
                }
                else -> throw  Exception("Expected operator, got $t")
            }
        }
        temp = temp.substring(1)
    }
    while (stack.isNotEmpty()) res.add(stack.pop())
    return res
}

