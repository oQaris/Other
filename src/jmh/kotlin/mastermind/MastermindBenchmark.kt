package mastermind

import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.All)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Fork(value = 1)
@Warmup(iterations = 3, time = 5)
@Measurement(iterations = 5, time = 10)
open class MastermindBenchmark {

    @Benchmark
    fun evaluateInt(blackhole: Blackhole) {
        blackhole.consume(evaluateFast(1234, 5678))
        blackhole.consume(evaluateFast(5555, 5555))
        blackhole.consume(evaluateFast(12345, 54321))
        blackhole.consume(evaluateFast(876, 980))
    }

    @Benchmark
    fun evaluateStr(blackhole: Blackhole) {
        blackhole.consume(evaluate("1234", "5678"))
        blackhole.consume(evaluate("5555", "5555"))
        blackhole.consume(evaluate("12345", "54321"))
        blackhole.consume(evaluate("876", "980"))
    }
}
