package datamodel;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ErrorResponse {
    public int status;
    public String message;

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public String fromJson() {
        JsonObject obj = JsonParser.parseString(message).getAsJsonObject();
        message = obj.get("message").getAsString();
        return status + " -> " + message;
    }

    public String fromText() {
        return status + " -> " + message;
    }

    public String toString() {
        if (message.charAt(0) == '{') { //simple parsing to look for JSON
            return fromJson();
        } else {
            return fromText();
        }
    }
}
