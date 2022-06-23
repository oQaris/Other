package other.decoder

/**
 * Returns all elements yielding the smallest value of the given function or empty list if there are no elements.
 */
inline fun <T, R : Comparable<R>> Iterable<T>.minsBy(selector: (T) -> R): List<T> {
    val iterator = this.iterator()
    if (!iterator.hasNext()) return emptyList()
    val minElems = mutableListOf(iterator.next())
    if (!iterator.hasNext()) return minElems
    var minValue = selector(minElems[0])
    do {
        val e = iterator.next()
        val v = selector(e)
        if (minValue > v) {
            minElems.clear()
            minElems.add(e)
            minValue = v
        } else if (minValue == v) {
            minElems.add(e)
        }
    } while (iterator.hasNext())
    return minElems
}

/**
 * Returns all elements yielding the largest value of the given function or empty list if there are no elements.
 */
inline fun <T, R : Comparable<R>> Iterable<T>.maxsBy(selector: (T) -> R): List<T> {
    val iterator = this.iterator()
    if (!iterator.hasNext()) return emptyList()
    val maxElems = mutableListOf(iterator.next())
    if (!iterator.hasNext()) return maxElems
    var maxValue = selector(maxElems[0])
    do {
        val e = iterator.next()
        val v = selector(e)
        if (maxValue < v) {
            maxElems.clear()
            maxElems.add(e)
            maxValue = v
        } else if (maxValue == v) {
            maxElems.add(e)
        }
    } while (iterator.hasNext())
    return maxElems
}
