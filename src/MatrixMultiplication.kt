import java.lang.System.currentTimeMillis
import java.util.*


fun multiplyMatrices(a: Array<IntArray>, b: Array<IntArray>): Array<IntArray> {
        val res = Array(a.size) { IntArray(b[0].size) }

        for (i in a.indices) for (j in b[i].indices) for (k in a[i].indices)
                    res[i][j] += a[i][k] * b[k][j]

        return res
}
fun multiplyMatricesPartly(res: Array<IntArray>, a: Array<IntArray>, b: Array<IntArray>, colFrom:Int, colTo:Int){
    for (i in a.indices) for (j in colFrom until Math.min(colTo,a.size)  ) for (k in a[i].indices)
        res[i][j] += a[i][k] * b[k][j]
}

fun multiplyMatricesInThreads(a: Array<IntArray>, b: Array<IntArray>): Array<IntArray> {
    val n = a.size
    val res = Array(n) { IntArray(n) }
    val matCols: Int = if (n > 3) n / 4 else 1

    val threads = mutableListOf<Thread>()
    val lastI = Math.min(n,4)
    for (i in 0 until lastI-1 )
        threads.add(Thread{ multiplyMatricesPartly(res, a, b, matCols * i, matCols * (i + 1)) } )
    //case when there may be remainder
    threads.add(Thread{ multiplyMatricesPartly(res, a, b, matCols * (lastI-1), n) } )

    threads.forEach(Thread::start)
    threads.forEach(Thread::join)
    return res
}
val rand = Random(currentTimeMillis())

fun makeMatrix(n: Int): Array<IntArray> {
    val res = Array(n) { IntArray(n) }
    for (i in 0 until n) for (j in 0 until n)
            res[i][j] = rand.nextInt(100)

    return res
}
fun printMatrix(res: Array<IntArray>) {
    val r = res.size
    val c = res[0].size
    for (i in 0 until r) {
        for (j in 0 until c)
            print("${res[i][j]} ")
        println()
    }
}



fun threadTest(n: Int, print: Boolean = false) {
    val a =  makeMatrix(n)
    val b =  makeMatrix(n)

    var startTime = currentTimeMillis()
    var res: Array<IntArray> = multiplyMatrices(a, b)
    var endTime = currentTimeMillis()
    if(print) printMatrix(res)
    println("Time without threads for $n-size matrices: " + (endTime - startTime) + " milliseconds")

    startTime = currentTimeMillis()
    res =  multiplyMatricesInThreads(a, b)
    endTime = currentTimeMillis()
    if(print) printMatrix(res)
    println("Time with threads for $n-size matrices: " + (endTime - startTime) + " milliseconds")

}


fun main(args: Array<String>) {
    //Check for correctness of calculations
   for(n in 1..5) {
       threadTest(n, print = true)
       println("-------")
   }
    //Compare speed
    for(n in 100..1200 step 200) {
        threadTest(n)
        println("=========")
    }
}
