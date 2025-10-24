package dataModel;

public record AuthData(String username, String authToken) {
    public AuthData(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }

}
