package smartphone_selector

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import org.jsoup.Jsoup
import kotlin.random.Random

val url = "https://nanoreview.net/api/price-quality/calculate"
val userAgent =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36"
val referer = "https://nanoreview.net/ru/phone-compare/samsung-galaxy-s8-plus-vs-oppo-realme-9i"


data class Smartphone(
    val id: String,
    val price: Int,
    val fullName: String,
    val rank: Int,
    val antutuScore: Int,
    val memory: String
)

fun compareRequest() {
    // Replace with actual Kotlin code to form and send the POST request
// Ensure you have appropriate libraries like OkHttp or HttpClient set up

    val url = "https://nanoreview.net/api/price-quality/calculate"
    val client = OkHttpClient()

// Replace these variables with user-provided values
    val modelName1 = "Sgsdgrf st 43"
    val price1 = 1000
    val score1 = 50

    val modelName2 = "Google Pixel 7"
    val price2 = 1500
    val score2 = 50

    val requestBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("price[0]", price1.toString())
        .addFormDataPart("score[0]", score1.toString())
        .addFormDataPart("name[0]", modelName1)
        .addFormDataPart("ins[0]", "0")
        .addFormDataPart("price[1]", price2.toString())
        .addFormDataPart("score[1]", score2.toString())
        .addFormDataPart("name[1]", modelName2)
        .addFormDataPart("ins[1]", "0")
        .addFormDataPart("currency", "rub")
        .addFormDataPart("ids", "3046,3048")
        .addFormDataPart("lang", "ru")
        .addFormDataPart("name", "ru")
        .addFormDataPart("content_type", "phone")
        .build()

    val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .addHeader("Host", "nanoreview.net")
        .addHeader("Sec-Ch-Ua", "\"Not/A)Brand\";v=\"8\", \"Chromium\";v=\"126\"")
        .addHeader("Sec-Ch-Ua-Platform", "\"Windows\"")
        .addHeader("Accept-Language", "ru-RU")
        .addHeader("Sec-Ch-Ua-Mobile", "?0")
        .addHeader(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.6478.127 Safari/537.36"
        )
        .addHeader("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundaryRbCj77JdBktj86Qs")
        .addHeader("Accept", "*/*")
        .addHeader("Origin", "https://nanoreview.net")
        .addHeader("Sec-Fetch-Site", "same-origin")
        .addHeader("Sec-Fetch-Mode", "cors")
        .addHeader("Sec-Fetch-Dest", "empty")
        .addHeader("Referer", "https://nanoreview.net/ru/phone-compare/")
        .addHeader("Accept-Encoding", "deflate")
        .addHeader("Priority", "u=1, i")
        .build()

    val response = client.newCall(request).execute()
    val responseBody = response.body()?.string()

    // Parse JSON response
    responseBody?.let {
        val jsonObject = JSONObject(it)
        val success = jsonObject.getBoolean("success")
        val html = jsonObject.getString("html")

        if (success) {
            // Extract winning smartphone and percentage improvement
            val regex =
                "<b>(.*?)</b> имеет на <b style=\"color: green;\">(.*?)%</b> лучшее соотношение цены/качества".toRegex()
            val matchResult = regex.find(html)
            if (matchResult != null && matchResult.groupValues.size == 3) {
                val winningPhone = matchResult.groupValues[1]
                val improvementPercentage = matchResult.groupValues[2].toFloat()
                println("Победивший смартфон: $winningPhone")
                println("Процент насколько он лучше: $improvementPercentage%")
            } else {
                println("Не удалось извлечь информацию о победившем смартфоне.")
            }
        } else {
            println("Запрос не выполнен успешно.")
        }
    }
    disconnect(client)
}

enum class Weight(val value: Int) {
    low(1), medium(2), high(3), veryhigh(4)
}

fun main() {
    compareRequest()

    val allSmartphones = getAllSmartphones()
    val defeated = mutableListOf<Smartphone>()
    var contender = allSmartphones.maxBy { it.price }
//    allSmartphones.combinations(2).forEach { pair ->
//        val (s1, s2) = pair
//    }

    val rating = getRating(contender.id, Weight.high, Weight.high, Weight.high, Weight.medium, Weight.high)
    println("for ${contender.fullName} rating = $rating")
}

fun getRating(id: String, performance: Weight, battery: Weight, display: Weight, gaming: Weight, camera: Weight): Int {
    val pad = if (id == "apple-iphone-12") "apple-iphone-11" else "apple-iphone-12"
    val weights = "a.${performance.value}_b.${battery.value}_d.${display.value}_g.${gaming.value}_v.${camera.value}"
    val url = "https://nanoreview.net/ru/phone-compare/$id-vs-$pad?ws=$weights"

    val client = OkHttpClient()
    val request = Request.Builder().url(url)
        .addHeader("Sec-Ch-Ua", "\"Not/A)Brand\";v=\"8\", \"Chromium\";v=\"126\"")
        .addHeader("Sec-Ch-Ua-Mobile", "?0")
        .addHeader("Sec-Ch-Ua-Platform", "\"Windows\"")
        .addHeader("Accept-Language", "ru-RU")
        .addHeader("Upgrade-Insecure-Requests", "1")
        .addHeader(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.6478.127 Safari/537.36"
        )
        .addHeader(
            "Accept",
            "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7"
        )
        .addHeader("Sec-Fetch-Site", "same-origin")
        .addHeader("Sec-Fetch-Mode", "navigate")
        .addHeader("Sec-Fetch-User", "?1")
        .addHeader("Sec-Fetch-Dest", "document")
        .addHeader("Referer", "https://nanoreview.net/ru/phone-compare/")
        .addHeader("Accept-Encoding", "deflate")
        .addHeader("Priority", "u=0, i")
        .build()

    println("call")
    client.newCall(request).execute().use { response ->
        val responseBody = response.body()?.string()

        println("call2")
        responseBody?.let { body ->
            val document = Jsoup.parse(body)
            println("call3")

            disconnect(client)

            return document.select("div.main-container div.compare-head-item a[href*=$id]").first()!!
                .previousElementSibling()!!
                .select("span.compare-head-main-score-num")
                .text().toInt()
        }
    }
    throw IllegalStateException("Not found rating for $id")
}

fun disconnect(client: OkHttpClient){
    client.dispatcher().executorService().shutdown()
    client.connectionPool().evictAll()
    client.cache()?.close()
}

fun getAllSmartphones(): List<Smartphone> {
    val allSmartphones = mutableListOf<Smartphone>()
    var page = 1
    var hasNextPage = true

    while (hasNextPage) {
        val url = "https://nanoreview.net/ru/phone-list/antutu-rating?page=$page"
        val doc = Jsoup.connect(url)
            .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
            .referrer("http://www.google.com")
            .ignoreHttpErrors(true)
            .timeout(50 * 1000)
            .get()

        // Parsing logic
        val tableRows = doc.select("table.table-list tbody tr")
        for (row in tableRows) {
            val rank = row.select("td:nth-child(1)").text().toInt()
            val smartphoneElement = row.select("td:nth-child(2) a")
            val fullName = smartphoneElement.text()
            val id = smartphoneElement.attr("href").split("/").last()
            val antutuScore = row.select("td:nth-child(3)").text().toInt()
            val processor = row.select("td:nth-child(4)").text()
            val graphics = row.select("td:nth-child(5)").text()
            val memory = row.select("td:nth-child(6)").text()
            // Fetch price from OZON
            val price = fetchPrice(fullName)

            val smartphone = Smartphone(id, price, fullName, rank, antutuScore, memory)
//            println(smartphone)
            allSmartphones.add(smartphone)

//            println("Rank: $rank, Smartphone: $fullName, Antutu Score: $antutuScore")
//            println("Processor: $processor, Graphics: $graphics, Memory: $memory")
//            println("Price from OZON: $price")
//            println("Smartphone Link: https://nanoreview.net$id")
//            println("Inner name: ${id.split("/").last()}\n")
        }

        // Check if there's a next page
        val nextPageLink = doc.select("link[rel=next]").attr("href")
        if (nextPageLink.isBlank()) {
            hasNextPage = false
        } else {
            page++
        }
    }
    return allSmartphones
}

fun fetchPrice(productName: String): Int {
    return Random.nextInt(10000, 80000)
}