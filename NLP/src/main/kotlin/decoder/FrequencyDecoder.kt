package decoder

import com.github.shiguruikai.combinatoricskt.Combinatorics
import com.github.shiguruikai.combinatoricskt.combinations
import com.github.shiguruikai.combinatoricskt.permutations
import inDictionary
import org.sat4j.core.VecInt
import org.sat4j.minisat.SolverFactory
import org.sat4j.specs.ISolver
import tokens
import words

class FrequencyDecoder(freqChar: Map<Char, Double>, private val encodedText: String) {
    private val globalFrequency = freqChar.entries.sortedBy { it.value }

    private val alphabet = globalFrequency.map { it.key }.distinct().sorted()
    private val localAlph = encodedText.toList().distinct()

    fun decode(): String {
        val localFreq = encodedText.groupingBy { it }.eachCount()
            .entries.sortedBy { it.value }

        val bijection = localFreq.zip(globalFrequency)
            .associate { (f1, f2) -> f1.key to f2.key }

        return encodeWith(encodedText, bijection)
    }

    fun decodeNLP(): String {
        var decodAlph: Map<Char, Char>? = null
        var iter = 0
        var exp = 10
        var rec = 0.0

        globalFrequency.map { it.key }.permutations().forEach { dict ->
            val bijection = localAlph.zip(dict).toMap()
            val words = encodeWith(encodedText, bijection).split(" ")

            val newRec = words.count { it.inDictionary() }.toDouble() / words.size
            if (newRec > rec) {
                rec = newRec
                decodAlph = bijection
            }

            iter++
            if (iter % exp == 0) {
                println(iter)
                exp *= exp
            }
        }
        return encodeWith(encodedText, decodAlph!!)
    }

    private fun encodeWith(txt: String, bijection: Map<Char, Char>) =
        txt.fold("") { acc, c ->
            acc + bijection[c].also { it != null }
        }

    private fun relax() = sequence<List<Char>> {

    }
}

fun Collection<Int>.toVec() = VecInt(this.toIntArray())

class Decoder(private val encodedText: String) {

    private val alphabet = ('а'..'я').toList()
    private val localAlph = encodedText.toList().distinct().minus(' ')
    private val variables = localAlph.flatMap { c1 ->
        alphabet.map { c2 -> Variable(c1, c2) }
    }

    // Ограничения
    val constraints = mutableListOf<Set<Int>>()

    data class Variable(val from: Char, val to: Char)

    private fun findVar(k: Char, v: Char) = variables.first { it.from == k && it.to == v }
    private fun varIdx(k: Char, v: Char) = variables.indexOfFirst { it.from == k && it.to == v } + 1
    private fun varIdx(pair: Pair<Char, Char>) = varIdx(pair.first, pair.second)
    private fun varIdx(variable: Variable) = varIdx(variable.from, variable.to)
    private fun idxVar(idx: Int) = variables[idx - 1]

    fun decode(): String {

        // Решение
        val solver = SolverFactory.newDefault()
        solver.setKeepSolverHot(true)
        solver.newVar(variables.size)
        println("Всего переменных: ${variables.size}")

        fun varsGroup(byKey: Boolean) =
            variables.groupBy { if (byKey) it.from else it.to }
                .mapValues { (_, pairs) -> pairs.map { varIdx(it) } }

        fun addInSolverNotRepeat(vars: List<Int>) =
            vars.combinations(2).forEach { twoVar ->
                solver.addClause(twoVar.map { -it }.toVec())
            }

        varsGroup(true).forEach { (_, vars) ->
            // Расшифрованы все
            solver.addClause(vars.toVec())
            // Однозначно
            addInSolverNotRepeat(vars)
        }
        // Без дубликатов
        varsGroup(false).forEach { (_, vars) ->
            addInSolverNotRepeat(vars)
        }

        // Расшифровывая разумные слова
        val words = encodedText.split(' ')
            .filter { it.isNotEmpty() }.toMutableSet()

        words.forEach { word ->
            val localChars = word.toList().distinct()

            alphabet.combinations(localChars.size)
                .flatMap { it.permutations() }
                .map { localChars.zip(it).toMap() }
                .filter { map -> // проверка на допустимость
                    val set = map.map { (k, v) -> varIdx(k, v) }.toSet()
                    constraints.all { cnt -> !set.containsAll(cnt) }
                }
                .forEach { localBijection ->
                    val encoded = encodeWith(word, localBijection)
                    if (!encoded.inDictionary()) {

                        val localVars = localBijection.entries
                            .map { varIdx(it.key, it.value) }
                        // Одновременно не должны быть, ибо дадут некорректное слово
                        constraints.add(localVars.toSet())
                        solver.addClause(localVars.map { -it }.toVec()) // с минусом!
                    }
                }

            val vars = solveAndGetPairs(solver)
            println(vars.joinToString("\n"))
            val bijection = vars.groupBy { it.from }
                .mapValues { (_, value) -> value.single().to }
            println("Ограничений: ${solver.nConstraints()}")
            println(encodeWith(encodedText, bijection))
            println()
        }
        // выводим все
        while (true) {
            val bijection = solveAndGetPairs(solver)
                .groupBy { it.from }
                .mapValues { (_, value) -> value.single().to }
            println(encodeWith(encodedText, bijection))

            val localVars = bijection.entries
                .map { -varIdx(it.key, it.value) }
            solver.addClause(localVars.toVec())
        }
        return ""
    }

    private fun applyConstraints(): List<Variable> {
        val groupingVars = variables.groupBy { it.from }.values.toTypedArray()
        return Combinatorics.cartesianProduct(*groupingVars).first { encodedVars ->
            val set = encodedVars.map { (k, v) -> varIdx(k, v) }.toSet()
            constraints.all { cnt -> !set.containsAll(cnt) }
        }
    }

    private fun solveAndGetPairs(solver: ISolver): List<Variable> {
        require(solver.isSatisfiable) { "Не разрешима!" }
        //solver.model()
        return solver.primeImplicant()
            .filter { it > 0 }
            .map { idxVar(it) }
    }
}

fun encodeWith(txt: String, bijection: Map<Char, Char>) =
    txt.fold("") { acc, c ->
        acc + (bijection[c] ?: c)
    }

fun main() {
    val freqChar = mapOf(
        'а' to 28,
        'б' to 10,
        'в' to 22,
        'г' to 14,
        'д' to 20,
        'е' to 30,
        'ж' to 3,
        'з' to 13,
        'и' to 31,
        'й' to 11,

        'к' to 19,
        'л' to 23,
        'м' to 17,
        'н' to 27,
        'о' to 29,
        'п' to 21,
        'р' to 25,
        'с' to 26,
        'т' to 24,
        'у' to 16,

        'ф' to 7,
        'х' to 8,
        'ц' to 9,
        'ч' to 2,
        'ш' to 6,
        'щ' to 5,
        'ы' to 18,
        'ь' to 12,
        'э' to 1,
        'ю' to 4,
        'я' to 15,
        ' ' to 32
    ).mapValues { it.value.toDouble() }

    val txt =
        "а щнящюяыфрьшяыэфйьшявщхйацфнньшяэтеыэацяртыаоаэфрщщяойт ыэфцжлтэяыабагяэфбжщмекянфощыфннксяежщнаощычсящяыа " +
                "тйдфиксяйтутоэящюпаэацжтнщляпжфюкйщя жляпанмфйньшящю тжщгяфцэайязэагяежщнаощыщяыэйтрлычяыейьэчяыа " +
                "тйдфнщтянфощыфннапаящыоажчюацфжяйт еаякоаэйтбжлтрьтяюнфещящпнайщйацфжянтеаэайьтяпжфыньтящяыапжфыньтяцртыэаящртнякоаэйтбщжяущхйь " +
                "щыоажчюацфжфычяэфгнаощычящяцяйкеаощыньшяофрлэнщефшя йтцнтпаятпщоэфяю тычявщхйацфжщычяйтжщпщаюньтяэтеыэьящярт щущныещтяйтутоэь " +
                "ыашйфнщжщычя аыэацтйньтяыцт тнщляаяыщыэтрфшявщхйацяойщртнлцвщшыляця йтцнтгяпйтущщящюцтыэнаямэаяцяыофйэтяыкитыэцацфжфяэфгнаощычяабйфюацфнньтяпйтещяэапаяцйтртнщяойщртнлжщявщхйацфжчньгяойщбайяыущэфжфяаняойт " +
                "ыэфцжлжяыабагя цфяущжщн йфяа щнфеацапая щфртэйфяефд флящюяотйтощыьцфсищшыляыэайанящртжфякяытбляа щнящюяущжщн йацявщхйацфнщтяаыкитыэцжлжаычяыжт " +
                "ксищряабйфюарянфяущжщн йянфрфэьцфжщякюексяоажаыекяотйпфртнэфяэтеыэяоа жтдфищгяюфвщхйацфнщсяцьощыьцфжщянфяжтнэкяц ажчяущжщн " +
                "йфяюфэтряжтнэкяырфэьцфжщящяаэойфцжлжщяеаййтыоан тнэкяаняабтйнкцяжтнэагяыцагяущжщн йямщэфжяыаабитнщтязэаябьжяотйцьгявщхйяаыкитыэцжлсищгяотйтыэфнацекябкецяцяэтеыэтяп " +
                "тябкецьявщхйктрапаяэтеыэфяотйтыэфцжлжщычящяаэыэалжщя йкпяаэя йкпфянфя жщнкяаейкднаыэщяущжщн йфяыашйфнтнщтяцяэфгнтя щфртэйфяущжщн " +
                "йацяабтыотмщцфжаяытейтэнаыэчяотйтощыещяцртыэаяыотущфжчньшяущжщн йацяойщртнлжщычядтюжьяйкеалэещяртмтгяещндфжацяеаощгящя йящюцтыэтняэфедтящяртэа " +
                "я твщхйацфнщля фннапаявщхйфяойщощыьцфтрьгяфйщыэаэтжс"


    // на дворе парад я параду рад выйду на парад возьму фотоаппарат

    // Даже шею, даже уши ты испачкал в чёрной туши.
    // Становись скорей под душ. Смой с ушей под душем тушь.
    // Смой и с шеи тушь под душем. После душа вытрись суше.
    // Шею суше, суше уши, и не пачкай больше уши.
    val example = "рг жесуз тгугж в тгугжц угж еюмжц рг тгугж ескяпц чсхсгттгугх"
    val exampple2 = "Даже шею, даже уши ты испачкал в чёрной туши.\n" +
            "Становись скорей под душ. Смой с ушей под душем тушь.\n" +
            "Смой и с шеи тушь под душем. После душа вытрись суше.\n" +
            "Шею суше, суше уши, и не пачкай больше уши."
    val norm = exampple2.tokens().words().joinToString(" ")
    println(norm)
    val dec = Decoder(norm).decode()
    println(dec)
}
