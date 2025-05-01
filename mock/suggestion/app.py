from flask import Flask, request, jsonify
from flask_cors import CORS
import json
from config import KEYWORDS, REPLACEMENTS

app = Flask(__name__)
CORS(app)


def highlight_text(text):
    """Scan text for predefined keywords and highlight them with <sugX> tags."""
    highlighted_text = text
    suggestions = []
    counter = 1

    for category, words in KEYWORDS.items():
        for word in words:
            if word in highlighted_text:
                tag = f"<sug{counter}>{word}</sug{counter}>"
                highlighted_text = highlighted_text.replace(word, tag, 1)
                suggestions.append({
                    "message": f"{highlighted_text}",
                    "feedback":
                        f"This phrase falls under {category}. Consider rewording.",
                    "replacement":
                        REPLACEMENTS.get(word, f"Consider rewording '{word}'")
                })
                counter += 1

    return highlighted_text, suggestions


@app.route('/api/mock-text-suggestion', methods=['POST'])
def mock_text_suggestion():
    """Mock API to analyze the original text input."""
    data = request.json
    text = data.get("text", "")

    if not text:
        return jsonify({"error": "No text provided"}), 400

    highlighted_text, suggestions = highlight_text(text)

    response = {
        "text": text,
        "suggestions": suggestions
    }

    return app.response_class(
        response=json.dumps(response, ensure_ascii=False),
        status=200,
        mimetype="application/json"
    )


@app.route('/api/mock-edited-text-suggestion', methods=['POST'])
def mock_edited_text_suggestion():
    """Mock API to analyze user-edited text."""
    data = request.json
    text = data.get("text", "")
    edited_text = data.get("edited_text", "")
    suggestions = data.get("suggestions", [])

    if not text or not edited_text:
        return jsonify({"error": "No text or edited_text provided"}), 400

    # Mock the text-suggestion for edited highlight text
    new_suggestions = []
    for suggestion in suggestions:
        if "<sug1>" in suggestion["message"]:  # Only provide suggestion for `<sug1>`
            new_suggestions.append({
                "edited_message": suggestion["edited_message"].replace(
                    "來佔便宜",
                    "可能尋求更好的經濟機會"
                ),
                "feedback": "‘佔便宜’ 可能帶有偏見性表述，建議以更客觀的方式描述難民的處境。",
                "replacement": "部分難民可能尋求更好的經濟機會與生活條件"
            })

    response = {
        "edited_text": edited_text,
        "suggestions": new_suggestions
    }

    return app.response_class(
        response=json.dumps(response, ensure_ascii=False),
        status=200,
        mimetype="application/json"
    )


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=False)  # nosec B104
