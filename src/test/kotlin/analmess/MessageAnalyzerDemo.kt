package analmess

import org.junit.jupiter.api.Test
import kotlin.math.sign

internal class MessageAnalyzerDemo {

    @Test
    fun mainTest() {
        powZ(57, 9, 77)
    }

    fun powZ(_a: Int, _e: Int, m: Int) {
        var a = _a
        var e = _e
        var r = 1
        /*if (e < 0) {
            print("e < 0 => e = m - e = $m + ($e)")
            e += m
            println(" = $e")
        }*/
        println("a=$a, e=$e, m=$m")
        var i = 1
        while (e != 0) {
            print("$i)\t")
            if (e % 2 != 0) {
                print("e-нечет. => r=(r*a)%m=($r*$a)%$m")
                r *= a
                r %= m
                print("=$r;\t")
            } else print("e - чет. => ничё не делаем;\t\t\t")
            print("e = e/2 = $e/2")
            e /= 2
            print(" = $e;\t")
            print("a = (a^2)%m = ($a^2)%$m")
            a = (a * a) % m
            println(" = $a;")
            i++
        }
        println("e == 0 - выход")
        println("Ответ: r = $r")
    }

    fun mod(n: Int, d: Int): Int {
        var result = n % d
        if (sign(result.toDouble()) * sign(d.toDouble()) < 0) result += d
        return result
    }
}
