package wikijumper

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import mu.KotlinLogging
import java.util.*
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import kotlin.system.exitProcess

suspend fun main() {
    WikiJumper(
        "ru",
        decode("Изобретатель"),
        decode("Ажурное_вязание")
    ).start()

//    WikiCrawlerThreadPool(
//        "ru",
//        //decode("Путин"),
//        //decode("Танос")
//        decode("Изобретатель"),
//        decode("Ажурное_вязание")
//    ).findShortestPath()
//        .forEach { println(it) }
}

class WebPage(val url: String, val depth: Int, val parent: WebPage?)

class WikiCrawlerThreadPool(
    private val locale: String,
    private val startUrl: String,
    private val endUrl: String
) {
    private val logger = KotlinLogging.logger {}
    private val visited = ConcurrentHashMap.newKeySet<String>()
    private val queue = PriorityBlockingQueue(11, Comparator.comparingInt<WebPage> { it.depth })

    fun findShortestPath(): List<String> {
        // Начальная страница добавляется в очередь
        queue.add(WebPage(startUrl, 0, null))

        val executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
        val foundPath = AtomicReference<WebPage>()

        while (foundPath.get() == null && queue.isNotEmpty()) {
            val currentPage = queue.take()
            visited.add(currentPage.url)
            logger.debug { "[visited_size] ${visited.size}" }
            logger.debug { "[queue_size] ${queue.size}" }
            logger.debug { "[depth] ${currentPage.depth}" }

            val future = executor.submit {
                // Извлекаем все ссылки на текущей странице и добавляем их в очередь
                val links = getChild(locale, currentPage.url)
                for (link in links) {
                    // Проверяем, не является ли текущая страница конечной страницей
                    if (currentPage.url == endUrl) {
                        foundPath.set(currentPage)
                        break
                    }
                    // Проверяем, не посещали ли мы эту страницу ранее
                    if (!visited.contains(link)) {
                        queue.add(WebPage(link, currentPage.depth + 1, currentPage))
                    }
                }
            }
            if (queue.isEmpty())
                future.get() // выполняем сразу же, дабы пополнить очередь
        }
        executor.correctClose()
        return foundPath.get()?.let { buildPath(it) } ?: emptyList()
    }

    private fun ExecutorService.correctClose() {
        shutdown()
        try {
            if (!awaitTermination(10, TimeUnit.SECONDS))
                shutdownNow()
        } catch (e: InterruptedException) {
            shutdownNow()
        }
    }
}

class WikiJumper(
    private val locale: String,
    private val start: String,
    private val finish: String,
    private val startTime: Long = System.nanoTime(),
) {
    private val dirNodes: MutableSet<Node> = Collections.synchronizedSet(LinkedHashSet())
    private val maxDepth = AtomicInteger(Int.MAX_VALUE)

    private val job = Job()
    private var shutdownAction = {
        job.cancel()
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
        while (dirNodes.isNotEmpty()) {
            val future = CoroutineScope(job).launch {
                var endNode = task()
                if (endNode != null) {
                    job.cancel()
                    if (endNode.link != finish)
                        endNode = Node(finish, endNode)

                    println("\nПуть:")
                    println(endNode.toString())

                    shutdownAction = {}
                    println(System.nanoTime() - startTime)
                    exitProcess(0)
                }
            }
            if (dirNodes.isEmpty())
                future.join()
        }
        job.join()
    }

    private fun task(): Node? {
        val curNode =
            synchronized(dirNodes) {
                dirNodes.first { !it.isVisited }
                    .apply { isVisited = true }
            }

        val links = getChild(locale, curNode.link)

        if (finish in links && curNode.depth() <= maxDepth.get()) {
            maxDepth.set(curNode.depth())
            return Node(finish, curNode)
        }

        synchronized(dirNodes) {
            dirNodes.addAll(links
                .map { Node(it, curNode) })
        }
        return null
    }

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
