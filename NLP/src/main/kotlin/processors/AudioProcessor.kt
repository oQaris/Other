import org.vosk.Model
import org.vosk.Recognizer
import java.io.File
import javax.sound.sampled.AudioSystem

fun voiceToText() {
    val model = Model("C:/Users/oQaris/Downloads/vosk/small_model")
    val audioFile =
        File("C:/Users/oQaris/Downloads/audio_1@06-10-2022_21-09-05.wav")

    val audio = AudioSystem.getAudioInputStream(audioFile)
    val rec = Recognizer(model, audio.format.sampleRate)
    audio.use { ais ->
        var nbytes: Int
        val b = ByteArray(4096)
        while (ais.read(b).also { nbytes = it } >= 0) {
            if (rec.acceptWaveForm(b, nbytes)) {
                println(rec.result)
            } else {
                println(rec.partialResult)
            }
        }
    }
    println(rec.finalResult)
}
