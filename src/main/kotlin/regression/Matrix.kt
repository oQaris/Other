package regression

import kotlin.math.abs

class Matrix(private val data: Array<DoubleArray>) {

    fun solve(vector: DoubleArray): DoubleArray {
        val n = vector.size
        for (i in 0 until n) {
            var maxRow = i
            for (j in i + 1 until n) {
                if (abs(data[j][i]) > abs(data[maxRow][i])) {
                    maxRow = j
                }
            }
            val temp = data[i]
            data[i] = data[maxRow]
            data[maxRow] = temp
            val temp1 = vector[i]
            vector[i] = vector[maxRow]
            vector[maxRow] = temp1
            for (j in i + 1 until n) {
                val factor = data[j][i] / data[i][i]
                vector[j] -= factor * vector[i]
                for (k in i until n) {
                    data[j][k] -= factor * data[i][k]
                }
            }
        }
        val solution = DoubleArray(n)
        for (i in n - 1 downTo 0) {
            var sum = 0.0
            for (j in i + 1 until n) {
                sum += data[i][j] * solution[j]
            }
            solution[i] = (vector[i] - sum) / data[i][i]
        }
        return solution
    }
}
