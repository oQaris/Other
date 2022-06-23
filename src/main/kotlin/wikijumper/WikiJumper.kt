package wikijumper

import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import mu.KotlinLogging
import org.jsoup.Jsoup
import java.io.IOException
import java.net.URI
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.exitProcess

@OptIn(ExperimentalSerializationApi::class)
fun main() = runBlocking {
    val start = decode("Barack_Obama")
    val finish = decode("Thanos")

    /*val start = decode("Земля")
    val finish = decode("Боулинг")*/
    // Sunbittern

    /*println("$start -> $finish")

    val jumper = WikiJumper("en", start, finish)
    jumper.start()*/

    //val link = decode("Sunbittern")
    //println(Jsoup.connect("").get().html())

    //println(Jsoup.connect("https://en.wikipedia.org/wiki/").get().location())
}

fun decode(link: String): String {
    return URLDecoder.decode(link, "UTF-8")
    // withContext(Dispatchers.IO)
}

fun encode(link: String): String {
    return URLEncoder.encode(link, "UTF-8")
}

class WikiJumper(
    private val locale: String = "en",
    private val start: String,
    private val finish: String,
    private val startTime: Long = System.nanoTime(),
) {
    private val mainPage = Jsoup.connect("https://$locale.wikipedia.org/wiki/").get().location().drop(24)
    private val logger = KotlinLogging.logger {}

    private val dirNodes: MutableSet<Node> = Collections.synchronizedSet(LinkedHashSet())
    private val backNodes: MutableSet<Node> = mutableSetOf()
    private val finalLinks: MutableSet<String> = mutableSetOf(finish)

    private val maxDepth = AtomicInteger(Int.MAX_VALUE)

    // private val mutex = Mutex()
    // private val mutex2 = Mutex()
    // private val mutex3 = Mutex()

    private val job = Job()

    private var shutdownAction = {
        job.cancel()
        //saveState(dirNodes)
    }

    init {
        require(locale.length == 2)
        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() = shutdownAction.invoke()
        })
    }

    suspend fun start() {
        // Если начинаем с начала
        if (dirNodes.isEmpty())
            dirNodes.add(Node(start, null))
        //if (backNodes.isEmpty())
        //    backNodes.add(Node(finish, null))

        /*repeat(30) {
            CoroutineScope(job).launch {
                try {
                    reverseSearch()
                } catch (e: NoSuchElementException) {
                    //todo сделать ожидание
                    println("Страницы не связаны!")
                }
            }
            delay(500)
        }*/
        repeat(50) {
            CoroutineScope(job).launch {
                try {
                    var endNode = task()
                    job.cancel()

                    if (endNode.link != finish) // Если нашли родителя через reverseSearch()
                        endNode = Node(finish, endNode)

                    println("\nПуть:")
                    println(endNode.toString())

                    shutdownAction = {}
                    println(System.nanoTime() - startTime)
                    exitProcess(0)

                } catch (e: NoSuchElementException) {
                    //todo сделать ожидание
                    println("Страницы не связаны!")
                }
            }
            //todo костыль для искусственного ожидания
            delay(500)
        }
        job.join()
    }

    private fun task(): Node {
        while (true) {
            //todo сделать ожидание
            //val curNode = mutex.withLock {
            val curNode =
                synchronized(dirNodes) {
                    dirNodes.first { !it.isVisited }
                        .apply { isVisited = true }
                    //}
                }

            val links = getChild(curNode.link) ?: continue

            //mutex3.withLock {
            //val final = links.find { it in finalLinks }
            if (finish in links && curNode.depth() <= maxDepth.get()) {
                println(Node(finish, curNode))
                println()
                maxDepth.set(curNode.depth())
            } else if (finish in links) return Node("777", null)
            //}

            synchronized(dirNodes) {
                //mutex.withLock {
                dirNodes.addAll(links
                    .map { Node(it, curNode) })
                //}
            }
        }
    }

    /*private suspend fun reverseSearch() {
        while (true) {
            //todo сделать ожидание
            val curLink = mutex2.withLock {
                backNodes.first { !it.isVisited }
                    .apply { isVisited = true }
                    .run { link }
            }

            val links = getChild(curLink) ?: continue
            if (links.contains(finish)) {
                mutex3.withLock { finalLinks.add(curLink) }
                logger.debug { "Найдена прямая ссылка на финальную страницу - $curLink" }
            }

            mutex2.withLock {
                backNodes.addAll(links
                    .map { Node(it, null) })
            }
        }
    }*/

    private fun getChild(shortLink: String): List<String>? = try {
        val timeout = 30            // Увеличить значение при плохом интернете
        val prefixWiki = "/wiki/"   // Внутренняя ссылка википедии

        val escapedLink = // Экранируем специальные символы в ссылке
            URI.create("https://$locale.wikipedia.org/wiki/${encode(shortLink)}").toString()

        val doc = Jsoup.connect(escapedLink).timeout(timeout * 1000).get()
        logger.trace { "Зашли на $shortLink" }

        doc.select("a").eachAttr("href")
            .asSequence().distinct()
            .minusElement(mainPage) // Удаляем главную страницу, чтоб не было читерно
            .filter { it.startsWith(prefixWiki) } // Отсеиваем ссылки не на википедию
            .filterNot { it.contains(':') } // Исключаем файлы и специальные страницы
            .map { it.substring(prefixWiki.length) } // убираем /wiki/, чтоб не занимало память
            .map { link -> link.takeWhile { it != '#' } } // Удаляем якоря
            .map { decode(it) }.toList()

    } catch (e: IOException) {
        //logger.warn { "Пропущена страница: $shortLink" }
        null
    }


    /*@OptIn(ExperimentalSerializationApi::class)
    fun loadState(): MutableSet<Node> {
        println("Загрузка состояния...")
        val file = File("proto/state_$start-$finish.proto")
        return (if (file.exists()) {
            val bytes = file.readBytes()
            ProtoBuf.decodeFromByteArray(bytes)
        } else mutableSetOf<Node>()
                ).apply { println("Загружено $size ссылок") }
    }*/

    /*@OptIn(ExperimentalSerializationApi::class)
    fun saveState(state: MutableSet<Node>) {
        println("Завершаем сопрограммы...")
        runBlocking { job.cancelAndJoin() }
        println("Сохранение состояния...")
        val bytes = ProtoBuf.encodeToByteArray(state)
        File("proto/state_$start-$finish.proto").writeBytes(bytes)
        println("Сохранено ${state.size} ссылок")
    }*/

    @Serializable
    data class Node(val link: String, val parent: Node?, var isVisited: Boolean = false) {
        // Надо, чтобы при сравнении узлов учитывалась только ссылка
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as Node
            if (link != other.link) return false
            return true
        }

        override fun hashCode(): Int {
            return link.hashCode()
        }

        fun depth() = buildPath().size

        override fun toString() = buildPath().reversed()
            .joinToString("\n") { it.link }

        private fun buildPath() = buildList {
            var endNode = copy()
            add(endNode)
            do {
                add(endNode.parent ?: break)
                endNode = endNode.parent!!
            } while (true)
        }
    }
}
