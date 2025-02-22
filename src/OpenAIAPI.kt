import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

fun getResponse(
  fullPrompt: String,
  endpoint: String,
  apiKey: String,
): String? {
  val jsonObject = JSONObject()
  jsonObject.put(
    "messages", arrayOf(
      JSONObject().put("role", "system").put("content", "Follow the instructions"),
      JSONObject().put("role", "user").put("content", fullPrompt),
    )
  )
  jsonObject.put("temperature", 0.6)
  jsonObject.put("seed", 1)
  jsonObject.put("stream", false)
  jsonObject.put("cache_prompt", true)
  jsonObject.put("model", "deepseek-ai/DeepSeek-R1")
  val requestBody = jsonObject.toString()

  val connection = URL(endpoint).openConnection() as HttpURLConnection
  connection.requestMethod = "POST"
  connection.setRequestProperty("Content-Type", "application/json")
  connection.setRequestProperty("Authorization", "Bearer $apiKey")
  connection.doOutput = true

  try {
    connection.outputStream.use { it.write(requestBody.toByteArray()) }
    val responseCode = connection.responseCode
    if (responseCode == HttpURLConnection.HTTP_OK) {
      val responseRaw = connection.inputStream.bufferedReader().readText()
      val responseWithThink = parseResponse(responseRaw)
      val response = trimThink(responseWithThink)
      return response
    } else {
      val errorResponse = connection.inputStream.bufferedReader().readText()
      println(errorResponse)
      println("API request failed with code $responseCode")
      return null
    }
  } catch (e: Exception) {
    println("Error sending request: ${e.message}")
    return null
  } finally {
    connection.disconnect()
  }
}

private fun parseResponse(response: String): String {
  val lines = response.lines()
  val generatedTextBuilder = StringBuilder()
  for (line in lines) {
    val jsonObject = JSONObject(line)
    jsonObject.getJSONArray("choices")?.forEach { c ->
      val content = (c as JSONObject).getJSONObject("message").getString("content")
      generatedTextBuilder.append(content)
    }
    if (jsonObject.has("done") && jsonObject.getBoolean("done")) {
      break // Stop when the "done" flag is true
    }
  }
  return generatedTextBuilder.toString()
}