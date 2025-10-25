package datamodel;

public record UserData(String username, String password, String email) {
    public UserData(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
