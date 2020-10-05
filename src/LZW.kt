fun main(args: Array<String>) {
    val source = "applepie"
    val (res, map) = encrypt(source)
    val decrypted = decrypt(res, map)
    assert(decrypted == source)
}
fun encrypt(source: String):Pair<String, MutableMap<String, Int>> {
    val dict =  mutableMapOf<String, Int>()
    source.asSequence().distinct().toList().forEachIndexed{ i, c -> dict[c.toString()] = i }
    val initialDict = dict.toMutableMap()

    var i = dict.size
    var res = ""


    val last = source.map { c -> c.toString() }.
                    reduce { acc, s ->
                        if(dict.containsKey(acc)) (acc+s)
                        else (acc.last()+s).apply { dict.put(acc,i++); res+= dict[acc.dropLast(1)] } }
    dict.put(last, i)
    res+=dict[last.dropLast(1)]
    res+=dict[last.last().toString()]

    println(res)
    println(dict)

    return Pair(res, initialDict)
}

fun decrypt(source:String, dict:MutableMap<String, Int>):String{
    val reverse = dict.entries.associate{(k,v)-> v to k}.toMutableMap()
    var i = dict.size
    var res = ""

    source.map { c -> c.toString() }.
        fold("") {
                acc, j ->
                reverse[j.toInt()]!!.apply {
                    res+=this;
                    if(!dict.contains(acc+ this)) {dict[acc+first()] = i; reverse[i++] = acc+first() }
                }
          }

    println(res)
    println(dict)
    return res
}
