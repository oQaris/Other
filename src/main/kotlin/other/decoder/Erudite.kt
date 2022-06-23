package other.decoder

import analmess.printInfoFromWord
import com.github.demidko.aot.MorphologyTag.*
import com.github.demidko.aot.WordformMeaning.lookupForMeanings
import com.github.shiguruikai.combinatoricskt.permutations
import com.github.shiguruikai.combinatoricskt.powerset

fun main() {
    prettyPrint("ракита")
}

private fun prettyPrint(word: String) {
    Erudite(word).solve()
        .sortedWith(
            Comparator.comparingInt(String::length)
                .reversed()
                .then(Comparator.comparing { it })
        )
        .also { println("Всего слов: " + it.size) }
        .forEach { printInfoFromWord(it) }
}

class Erudite(private val srcWord: String) {
    init {
        require(" " !in srcWord)
        require(srcWord.length > 2)
    }

    private val requiredMorph = listOf(Noun, Singular)
    private val prohibitedMorph = listOf(Name, Surname, Toponym, ColloquialSpeech, Typo, Immutable)

    fun solve() =
        srcWord.toList().powerset()
            .flatMap { it.permutations() }
            .map { it.joinToString("") }
            .filter { word ->
                lookupForMeanings(word)
                    .takeIf { it.isNotEmpty() }
                    ?.any {
                        it.morphology.containsAll(requiredMorph)
                                && it.morphology.intersect(prohibitedMorph).isEmpty()
                                && it.lemma == it
                    } ?: false
            }.toSet()
}
