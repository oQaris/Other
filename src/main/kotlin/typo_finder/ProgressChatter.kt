package typo_finder

class ProgressChatter(
    private val period: Int = 100,
    private val chatFunc: (Int) -> Unit = { println(it) }
) {
    private var counter: Int = 0

    fun incProgress(pattern: String? = null) {
        counter++
        if (counter % period == 0)
            chatProgress(pattern)
    }

    fun chatProgress(pattern: String? = null) {
        if (pattern != null)
            println(message(pattern))
        else chatFunc.invoke(counter)
    }

    private fun message(pattern: String) =
        pattern.replace("$", counter.toString())
}
