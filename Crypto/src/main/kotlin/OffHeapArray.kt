import sun.misc.Unsafe
import java.lang.reflect.Field

class OffHeapArray(s: Long) {
    var size: Long = s
        private set

    private val BYTE = 1
    private var address = getUnsafe().allocateMemory(size * BYTE)

    private fun getUnsafe(): Unsafe {
        val f: Field = Unsafe::class.java.getDeclaredField("theUnsafe")
        f.isAccessible = true
        return f.get(null) as Unsafe
    }

    operator fun set(i: Long, value: Byte) {
        getUnsafe().putByte(address + i * BYTE, value)
    }

    operator fun get(idx: Long): Byte {
        return getUnsafe().getByte(address + idx * BYTE)
    }

    fun freeMemory() {
        getUnsafe().freeMemory(address)
    }
}
