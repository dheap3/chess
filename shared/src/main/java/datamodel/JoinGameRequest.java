package datamodel;

import chess.ChessGame;

public class JoinGameRequest {
    public ChessGame.TeamColor playerColor;
    public Double gameID;
    public JoinGameRequest(ChessGame.TeamColor playerColor, Double gameID) {
        this.playerColor = playerColor;
        this.gameID = gameID;
    }
}
