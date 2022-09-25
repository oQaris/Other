package other.decoder

import analmess.printInfoFromWord
import com.github.demidko.aot.MorphologyTag.*
import com.github.demidko.aot.WordformMeaning.lookupForMeanings
import com.github.shiguruikai.combinatoricskt.permutations
import com.github.shiguruikai.combinatoricskt.powerset
import java.io.File
import kotlin.random.Random

fun main() {
    File("C:\\Users\\oQaris\\Desktop\\вордс.txt").readLines()
        .filterNot { it.contains("у") || it.contains("п") || it.contains("с") }
        .filter { it.contains("к") && it.contains("т") }
        .sorted().forEach { println(it) }

    /*val all = (0..9).toList()
        .combinationsWithRepetition(3)
        .flatMap { digs ->
            val unDigs = digs.distinct()

            "авекмнорстух".toList()
                .combinations(unDigs.size)
                .map { chrs ->

                    val map = unDigs.zip(chrs).toMap()
                    map[digs[0]].toString() + digs.joinToString("") + map[digs[1]] + map[digs[2]]
                }
        }.toSet()

    println(all.size)
    all.forEach { println(it) }*/

    //prettyPrint("авекмнорстух")
}

fun getRnd(list: List<Int>): Int {
    val cumArr = buildList {
        var sum = 0
        add(0)
        list.forEach {
            sum += it
            add(sum)
        }
    }
    val rndElem = Random.nextInt(cumArr.last())

    val idx = cumArr.binarySearch(rndElem)
    val newIdx =
        if (idx >= 0) idx
        else -idx - 2

    return list[newIdx]
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
            //.filter { it.size == 3 }
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
