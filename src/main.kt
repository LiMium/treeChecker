import java.io.*
import java.util.regex.PatternSyntaxException

/**
 * Sends file content to the API and checks if it contains red flags.
 */
fun checkFileWithApi(endpoint: String, apiKey: String, prompt: String, content: String, keyword: String): Boolean {
  val fullPrompt = "$prompt:\n$content"

  val response = getResponse(fullPrompt, endpoint, apiKey)
  if (response != null) {
    val keywordFound = containsKeyword(response, keyword)
    if (keywordFound) {
      println("Response:\n$response")
    }
    return keywordFound
  } else {
    return true
  }
}

private fun containsKeyword(response: String, keyword: String) = response.split("\n").last().contains(keyword, ignoreCase = true)

fun trimThink(s: String): String {
  return s.replace("<think>(.*)</think>".toRegex(RegexOption.DOT_MATCHES_ALL), "$1")
}

/**
 * Recursively traverses a directory and applies an action to each file that matches the regex pattern.
 */
fun traverseDirectory(dir: File, regex: Regex, action: (File) -> Unit) {
  dir.listFiles()?.forEach { file ->
    if (file.isDirectory) {
      traverseDirectory(file, regex, action)
    } else if (regex.matches(file.name)) {
      println(file)
      action(file)
    }
  }
}

/**
 * Main entry point: processes command-line args and runs the program.
 */
fun main(args: Array<String>) {
  if (args.size != 6) {
    println("Usage: kotlin RedFlagCheckerKt <root_directory> <api_endpoint> <api_key> <prompt> <keyword> <regex_pattern>")
    return
  }

  val rootDir = File(args[0])
  if (!rootDir.isDirectory) {
    println("Error: ${args[0]} is not a directory")
    return
  }

  val endpoint = args[1]
  val apiKey = args[2]
  val prompt = args[3]
  val keyword = args[4]
  val pattern = args[5]

  // Validate regex pattern
  val regex = try {
    Regex(pattern)
  } catch (e: PatternSyntaxException) {
    println("Invalid regex pattern: ${e.message}")
    return
  }

  val filesWithRedFlags = mutableListOf<String>()

  traverseDirectory(rootDir, regex) { file ->
    try {
      val content = file.name + "\n" + file.readText(Charsets.UTF_8)
      if (checkFileWithApi(endpoint, apiKey, prompt, content, keyword)) {
        filesWithRedFlags.add(file.path)
      }
    } catch (e: Exception) {
      println("Error processing file ${file.path}: ${e.message}")
    }
  }

  if (filesWithRedFlags.isNotEmpty()) {
    println("Files with red flags:")
    filesWithRedFlags.forEach { println(it) }
  } else {
    println("No files with red flags found.")
  }
}