package edu.gatech.cs1331.hw09.backend.model;

/**
 * Simple DTO representing the current state of a battle on the backend.
 * It bundles together the player, the current monster (if any),
 * a status string, and a message for the frontend.
 *
 * This is not a JPA entity; it is just used for responses from the battle API.
 */
public class BattleState {

    private Player player;
    private Monster monster;
    private String status;
    private String message;

    public BattleState(Player player, Monster monster,
                       String status, String message) {
        setPlayer(player);
        setMonster(monster);
        setStatus(status);
        setMessage(message);
    }

    //== SETTERS ==\\
    public void setPlayer(Player player) {
        this.player = player;
    }
    public void setMonster(Monster monster) {
        this.monster = monster;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    //== GETTERS ==\\
    public Player getPlayer() {
        return player;
    }
    public Monster getMonster() {
        return monster;
    }
    public String getStatus() {
        return status;
    }
    public String getMessage() {
        return message;
    }
}