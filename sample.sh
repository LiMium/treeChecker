set -ex

SOURCE="./"
BASE_URL="http://myServerAddress:8080"
FILENAME_PATTERN=".*\\.kt"
PROMPT="We want to opensource the following file. Check for any references to people or any other personal references. Check for identifiers, file paths and URLs in literal strings that might reveal user names and other personal information. Also check for bad language or embarrassing content. Check within comments as well. Don't check the logic of the code itself. Are there any such red flags or is it all green? At the end of your answer, emit a single line with Red or Green."
KEYWORD="Red"
KEY_OPTIONAL=""

java -jar treeChecker.jar \
    "$SOURCE" \
    "$BASE_URL/v1/chat/completions" \
    "$KEY_OPTIONAL" \
    "$PROMPT" \
    "$KEYWORD" \
    "$FILENAME_PATTERN"
