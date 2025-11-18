package datamodel;

public class CreateGameResponse {
    Integer gameID;
    public CreateGameResponse(Integer gameID) {
        this.gameID = gameID;
    }

    public Integer gameID() {
        return gameID;
    }
}
