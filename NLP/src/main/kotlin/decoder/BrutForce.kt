package decoder

import com.github.shiguruikai.combinatoricskt.combinations
import com.github.shiguruikai.combinatoricskt.permutations
import inDictionary

class BrutDecoder(private val encodedText: String) {
    private val globalAlph = ('а'..'я').toList()
    private val localAlph = encodedText.toList().distinct().minus(' ')

    /* private val allBj = localAlph.flatMap { c1 ->
         globalAlph.map { c2 -> c1 to c2 }
     }*/
    private val words = encodedText.split(' ').filter { it.isNotEmpty() }.toSet()

    fun decode(): String {
        //val grVars = allBj.groupBy { it.first }.values.toTypedArray()
        var encBj: Map<Char, Char>? = null
        var max = 0
        globalAlph.combinations(localAlph.size)
            .flatMap { it.permutations() }
            .map { localAlph.zip(it) }.forEach { encVars ->
                if (encVars.map { it.second }.distinct().size == encVars.size) {
                    // Расшифровывают слова
                    val bj = encVars.toMap()
                    val countEnc = words.count { word ->
                        val encWord = encodeWith(word, bj)
                        encWord.inDictionary()
                    }
                    // Выбираем с максимумов расшифрованных
                    if (countEnc > max) {
                        max = countEnc
                        encBj = bj.toMap()
                        println(encodeWith(encodedText, encBj!!))
                    }
                }
            }
        return encodeWith(encodedText, encBj!!)
    }
}
