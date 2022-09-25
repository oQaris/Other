package mastermind.tgbot

import dev.inmo.tgbotapi.bot.ktor.telegramBot
import dev.inmo.tgbotapi.extensions.api.bot.getMe
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.api.webhook.deleteWebhook
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.utils.usernameChatOrNull
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import mastermind.answerSequence
import mastermind.bc
import mastermind.evaluate
import mastermind.hardSelector
import java.io.File

@Serializable
data class Config(
    val token: String
)

private const val len = 4
private const val numAttempts = 7

suspend fun main(args: Array<String>) {
    // create json to decode config
    val json = Json { ignoreUnknownKeys = true }
    // decode config
    val config: Config = json.decodeFromString(Config.serializer(), File(args.first()).readText())
    // that is your bot
    val bot = telegramBot(config.token)

    bot.deleteWebhook()
    //bot.setWebhookInfo("https://d5diorr7o385na2g1cc2.apigw.yandexcloud.net")
    // https://api.telegram.org/bot{bot_token}/setWebHook?url={webhook_url}

    bot.buildBehaviourWithLongPolling {
        println(getMe())

        val pfpfp = 70

        onCommand("start") { content ->
            val curUser = content.chat.usernameChatOrNull()?.username?.username
            println("$curUser начал игру")
            suspend fun send(text: String) = send(content.chat, text)

            send(
                "Добро пожаловать в игру Mastermind (или по-русски \"Быки и Коровы\")!\n" +
                        "Вам предстоит угадать строку длины $len, состоящую из цифр, " +
                        "основываясь лишь на числе точных совпадений (быков) " +
                        "и количестве цифр не на своём месте (коров).\n" +
                        "Число попыток ограничено $numAttempts. Желаю удачи!\n"
            )
            val answers = answerSequence(len).toMutableList()

            repeat(numAttempts) { att ->
                send("${att + 1}) Введите свою догадку:")
                val guess = waitText().first().text.trim()

                if (guess.length != len || !guess.all { it.isDigit() }) {
                    send("Некорректный ввод!")
                    return@onCommand
                }
                answers.remove(guess)

                if (answers.isEmpty()) {
                    send("Победа!\n$guess - единственный верный ответ")
                    println("$curUser победил!")
                    return@onCommand
                }
                val answer = hardSelector.select(answers, guess)

                val (b, c) = evaluate(guess, answer)
                answers.removeIf { evaluate(it, guess) != b bc c }

                send("Быков: $b, Коров: $c")
            }
            send("Вы не угадали, попытайтесь ещё раз :(")
            send("Загадано было: " + answers.random())
        }
    }.join()
}
