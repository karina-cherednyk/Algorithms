import java.util.*

fun main(args: Array<String>) {
    val mapTest = mapOf(
        Pair('A',50), Pair('B', 39), Pair('C', 18),
        Pair('D', 49), Pair('E',35), Pair('F', 24)
    )
    val res = SF(mapTest)
    println(res)
}



fun SF(map:Map<Char, Int>): Map<Char, String> {
    val reverse = map.entries.associate{(k,v)-> v to k}.toMap()
    val ints = map.values.toIntArray().sortedArrayDescending()
    return SFInt(ints).mapKeys { (intKey, _) -> reverse.getValue(intKey) }
}

fun IntArray.sSum(): String = joinToString(separator = "+")

fun SFInt(ints: IntArray):Map<Int, String>{
    val res = mutableMapOf<Int, String>()
    val stack =  Stack<Pair<IntArray, String>>()
    var i = -1


    stack.push(Pair(ints, ""))
    while(!stack.isEmpty()) {
        val (arr, pr) = stack.pop()


        if(arr.size == 1) { res[arr.single()] = pr; }
        else {
            val (arr1, arr2) = divide(arr)
            print(arr.sSum())
            print( " - ${arr1.sSum()}, ")
            println("${arr2.sSum()}")

            stack.push(Pair(arr1, "1$pr") )
            stack.push(Pair(arr2, "0$pr") )
        }
    }
    return res
}
fun divide(ints: IntArray):Pair<IntArray,IntArray>{
    var arr1 = mutableListOf<Int>()
    var arr2 = mutableListOf<Int>()

    ints.forEach { if(arr1.sum() > arr2.sum()) arr2.add(it) else arr1.add(it) }
    return Pair(arr1.toIntArray(), arr2.toIntArray())
}
