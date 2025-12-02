package edu.gatech.cs1331.hw09.backend.controller;

import edu.gatech.cs1331.hw09.backend.model.Player;
import edu.gatech.cs1331.hw09.backend.service.PlayerService;
import edu.gatech.cs1331.hw09.backend.service.InventoryService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * REST controller for player-related operations.
 * Exposes endpoints that let the frontend:
 * - Fetch the current player state
 * - Set or change the player's name
 * - Pay off the Hero Academy debt
 * - Restart the game
 */

/*
 * TODO:
 *      Add the correct annotations so that this class defines REST operations
 *      and has a base endpoint path of "/api/player".
 */

@CrossOrigin
public class PlayerController {

    private final PlayerService playerService;
    private final InventoryService inventoryService;

    /**
     * Creates a PlayerController with the given services.
     *
     * TODO:
     *      Complete this constructor using proper dependency injection.
     *
     * @param playerService service for player-related operations
     * @param inventoryService service for inventory operations
     */
    public PlayerController(PlayerService playerService,
                            InventoryService inventoryService) {
        this.playerService = playerService;
        this.inventoryService = inventoryService;
    }

    /**
     * Returns the current player record.
     * GET /api/player
     *
     * This is called frequently by the frontend to refresh the
     * Player card and show changes to stats, gold, and flags.
     *
     * TODO:
     *      Add the correct annotation to handle GET requests.
     *      Implement this method to retrieve or create the player and return it.
     *      HINT: Look through the PlayerService.java methods.
     *
     * @return current Player entity
     */
    public Player getPlayer() {
        
    }

    /**
     * Replaces the current player record with the one provided in the request body.
     * PUT /api/player
     *
     * This is a more general "save" endpoint that accepts a full Player object
     * and persists it using the PlayerService.
     *
     * TODO:
     *      Add the correct annotation to handle PUT requests.
     *      Add the correct annotation to indicate the player parameter comes from the request body.
     *      Implement this method to save the player and return it.
     *      HINT: Look through the PlayerService.java methods.
     *
     * @param player the Player object sent from the client
     * @return the saved Player after persistence
     */
    public Player updatePlayer(Player player) {
        
    }

    /**
     * Sets or updates the player's name.
     * POST /api/player/name
     *
     * The request body typically contains a JSON object with a "name" field.
     * This method extracts the name and updates the player record.
     *
     * TODO:
     *      Add the correct annotation to handle POST requests to "/name".
     *      Add the correct annotation to indicate the body parameter comes from the request body.
     *      Extract the "name" field from the body Map.
     *      If the name is null or blank, throw an IllegalArgumentException with a descriptive message.
     *      Update the player's name using the appropriate service method.
     *      Return the updated Player object.
     *      HINT: Look through the PlayerService.java methods.
     *
     * @param body map of JSON fields sent from the frontend
     * @return updated Player with the new name
     */
    public Player setName(Map<String, String> body) {
        
    }

    /**
     * Attempts to pay off the Hero Academy debt.
     * POST /api/player/pay-debt
     *
     * The PlayerService will:
     * - Check if the player has enough gold
     * - Deduct the required amount
     * - Mark debtPaid as true if successful
     *
     * TODO:
     *      Add the correct annotation to handle POST requests to "/pay-debt".
     *      Implement this method to pay the player's debt and return the updated player.
     *      HINT: Look through the PlayerService.java methods.
     *
     * @return updated Player after attempting payment
     */
    public Player payDebt() {
        
    }

    /**
     * Restarts the game for the player.
     * POST /api/player/restart
     *
     * Typical behavior:
     * - Reset player stats, health, gold, revives, and flags
     * - Clear the inventory
     * - Leave item catalog intact
     *
     * TODO:
     *      Add the correct annotation to handle POST requests to "/restart".
     *      Clear the inventory first, then restart the game.
     *      Return the fresh Player object.
     *      HINT: Look through both InventoryService.java and PlayerService.java methods.
     *
     * @return fresh Player after a full reset
     */
    public Player restartGame() {
        
    }
}