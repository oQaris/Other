import java.io.File
import java.nio.charset.Charset

class Decoder(private val alphabet: List<Char>, private val origText: String) {
    //private val rnd = Random(43)
    private val ciphertext = prepareText(origText, alphabet)
    private var iteratedKeys = 0.0
    private val cash = createCash()
    private val quadgram = normalizedNGramArr(4, alphabet, IS_RUS)

    fun breakCipher(maxRounds: Int = 1000, consolidate: Int = 3) {
        var localMax = Double.NEGATIVE_INFINITY
        var maxHit = 0
        var bestKey = alphabet.toList()

        //println("fitness, time, iter")
        for (round in 0..maxRounds) {
            val key = alphabet.shuffled()
            //measureTimeMillis {
            val (localBestKey, fitness) = hillClimbing(key)
            println("$fitness ")
            if (fitness > localMax) {
                localMax = fitness
                bestKey = localBestKey

                //val newText = decodeWith(ciphertext, bestKey)
                //println(newText)
            } else if (fitness == localMax) {
                maxHit++
                if (maxHit == consolidate)
                    break
            }
            //}.also { print(it) }
            //println(" $iteratedKeys")
            iteratedKeys = 0.0
        }

        val newText = decodeWith(origText, bestKey)
        println(newText)
    }

    /**
     * Данные о позициях каждого символа из алфавита в зашифрованном тексте.
     */
    private fun createCash(): Array<List<Int>> {
        return alphabet.map { char ->
            ciphertext.foldIndexed(listOf<Int>()) { idx, acc, c ->
                if (char == c) acc + idx else acc
            }
        }.toTypedArray()
    }

    fun test() {
        quadgram.mapIndexed { index, d ->
            decodeNGram(alphabet, index, 4) to d
        }.sortedByDescending { it.second }
            .take(100)
            .forEach { (t, u) ->
                println("$t -> $u")
            }
    }

    private fun hillClimbing(startKey: List<Char>): Pair<List<Char>, Double> {
        val keyLen = startKey.size
        val key = startKey.toMutableList()
        var plaintext = ciphertext.toString()
        var maxFitness = Double.NEGATIVE_INFINITY
        var wasUpdateKey = true

        while (wasUpdateKey) {
            wasUpdateKey = false
            for (idx1 in 0 until keyLen) {
                for (idx2 in idx1 + 1 until keyLen) {
                    val ch1 = key[idx1]
                    val ch2 = key[idx2]

                    key[idx1] = ch2
                    key[idx2] = ch1
                    val tmpText = decodeWith(ciphertext, key)
                    key[idx1] = ch1
                    key[idx2] = ch2

//                    val tmpText = StringBuilder(plaintext)
//                    for (idx in cash[alphabet.indexOf(ch1)]) {
//                        tmpText[idx] = ch2
//                    }
//                    for (idx in cash[alphabet.indexOf(ch2)]) {
//                        tmpText[idx] = ch1
//                    }

                    /*var tmpFitness = 0.0
                    encodedNGramsSeq(plaintext, 4).forEach { quadIdx ->
                        tmpFitness += quadgram[quadIdx]
                    }*/
                    val tmpFitness = fitValue(prepareText(tmpText, RUS_ALPHABET))
                    iteratedKeys++

                    if (tmpFitness > maxFitness) {
                        maxFitness = tmpFitness
                        plaintext = tmpText.toString()
                        wasUpdateKey = true
                        key[idx1] = ch2
                        key[idx2] = ch1
                    } /*else if (tmpFitness < maxFitness) {
                        for (idx in cash[alphabet.indexOf(ch1)]) {
                            plaintext[idx] = ch1
                        }
                        for (idx in cash[alphabet.indexOf(ch2)]) {
                            plaintext[idx] = ch2
                        }
                    }*/
                }
            }
        }
        return key to maxFitness
    }

    fun fitValue(text: CharSequence) =
        //-pFitness(text, false)
        encodedNGramsSeq(text, 4).map { quadIdx ->
            quadgram[quadIdx]
        }.toList().mean()

    fun decodeWith(text: CharSequence, key: List<Char>): String {
        val bijection = alphabet.zip(key).toMap()
        return text.map { bijection[it] ?: it }
            .joinToString("")
    }

    /**
     * Формирует n-граммы и кодирует их в K-ричную систему, где K - размер алфавита,
     * затем преобразует их в 10-ичную систему, возвращая последовательность (для оптимизации).
     */
    private fun encodedNGramsSeq(text: CharSequence, n: Int) =
        text.windowedSequence(n) { gram ->
            encodeNGram(alphabet, gram)
        }
}

fun prepareText(text: String, alphabet: List<Char>) =
    StringBuilder().apply {
        text.lowercase()
            .filter { it in alphabet }
            .forEach { append(it) }
    }

val RUS_ALPHABET = rusData[0].keys.map { it.toCharArray()[0] }.sorted()
val ENG_ALPHABET = engData[0].keys.map { it.toCharArray()[0] }.sorted()

const val IS_RUS = true

fun main() {
    println("Heap max size: " + (Runtime.getRuntime().maxMemory() / 1024 / 1024) + "MB");
    val lang = if (IS_RUS) (RUS_ALPHABET + ' ') else ENG_ALPHABET
    println("alpabet: $lang")

    val text = File("08.txt").readText(Charset.forName("Windows-1251")).lowercase()
    val testTxt =
        "Юуб лойдб бесётпгбоб гтён, луп йифшбёу сфттлйк аиьл. Оп тптупйу поб оё йи рсбгйм, фрсбзоёойк й фшёвоьц уёлтупг. Ема юупдп тпиебоь есфдйё ибнёшбуёмэоьё фшёвойлй. Ф юупк лойдй тпгтён йоба ибебшб. Поб рпнпзёу гбн обфшйуэта оё упмэлп сбидпгбсйгбуэ, оп й сбиньщмауэ рп-сфттлй. Лойдб, лпупсфя гь еёсзйуё г сфлбц, тптубгмёоб йи бхпсйинпг й сбиньщмёойк гёмйлйц ньтмйуёмёк, рйтбуёмёк, рпюупг, хймптпхпг й пвъётугёооьц еёауёмёк сбимйшоьц юрпц. Йц ньтмй - п уёц гпрсптбц, лпупсьё оё рёсётубяу гпмопгбуэ шёмпгёшётугп. Гь нпзёуё тпдмбщбуэта ймй оё тпдмбщбуэта т уён, шуп рспшйубёуё г юупк лойдё. Гпинпзоп, гбн рплбзёута, шуп лблйё-уп ньтмй фзё фтубсёмй. Оп гь епмзоь пваибуёмэоп рпефнбуэ й пвптопгбуэ, рпшёнф гь убл тшйубёуё. Б ёъё гь фиобёуё й рпшфгтугфёуё, лбл рсёлсбтоп игфшбу тмпгб мявгй, тптусбебойа, нфесптуй й епвспуь об сфттлпн аиьлё."
    Decoder(lang, text).breakCipher()
    return

    val text2 = ("Rbo rpktigo vcrb bwucja wj kloj hcjd, km sktpqo, cq rbwr loklgo \n" +
            "vcgg cjqcqr kj skhcja wgkja wjd rpycja rk ltr rbcjaq cj cr.\n" +
            "-- Roppy Lpwrsborr").lowercase()

    val decoder = Decoder(lang, text2)
    println(decoder.decodeWith(prepareText(text2, lang), "ghidkzlmbnopfqerstcuvwajyx".toList()))

    decoder.fitValue("thetroublewithhavinganopenmindofcourseisthatpeoplewillinsistoncomingalongandtryingtoputthingsinitterrypratchett")
        .also { println(it) }
    decoder.fitValue("jumjfkcnxmpbjuuwsbyzwykvmyqbyrkhdkcfgmbgjuwjvmkvxmpbxxbygbgjkydkqbyzwxkyzwyrjflbyzjkvcjjubyzgbybjjmfflvfwjdumjj")
        .also { println(it) }
    decoder.fitValue("jlcjsivtmcegjllfrgbhfbipcbdgbniokivsacgajlfjpcipmcegmmgbagajibkidgbhfmibhfbnjsqgbhjipvjjlgbhagbgjjcssqpsfjklcjj")
        .also { println(it) }
    //decoder.test()
    decoder.breakCipher()
}
