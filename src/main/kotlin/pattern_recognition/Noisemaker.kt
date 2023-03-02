package pattern_recognition

import kotlin.random.Random

class Noisemaker(private val dataset: Set<Item>) {

    fun symmetric(p: Float): Set<Item> {
        return dataset.map { item ->
            item.data.map {
                if (Random.nextFloat() < p)
                    (it + 1) % 2
                else it
            } itm item.clazz
        }.toSet()
    }
}
