package other.decoder

import analmess.inDictionary
import com.github.shiguruikai.combinatoricskt.combinations
import com.github.shiguruikai.combinatoricskt.permutations

class BrutDecoder(private val encodedText: String) {
    private val globalAlph = listOf(
        'а',
        'б',
        'в',
        'г',
        'д',
        'е',
        'ж',
        'з',
        'и',
        'й',
        'к',
        'л',
        'м',
        'н',
        'о',
        'п',
        'р',
        'с',
        'т',
        'у',
        'ф',
        'х',
        'ц',
        'ч',
        'ш',
        'щ',
        'ы',
        'ь',
        'э',
        'ю',
        'я',
    )
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
