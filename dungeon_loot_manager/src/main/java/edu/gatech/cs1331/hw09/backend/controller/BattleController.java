package edu.gatech.cs1331.hw09.backend.controller;

import edu.gatech.cs1331.hw09.backend.model.BattleState;
import edu.gatech.cs1331.hw09.backend.service.BattleService;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for battle-related actions.
 * Exposes endpoints that let the frontend:
 * - Check the current battle state
 * - Start a new encounter
 * - Perform an attack in the current encounter
 *
 * This controller delegates all game logic to the BattleService.
 */

/*
 * TODO:
 *      Add the correct annotations so that this class defines REST operations
 *      and has a base endpoint path of "/api/battle".
 */

@CrossOrigin
public class BattleController {

    private final BattleService battleService;

    /**
     * Creates a BattleController that will use the given BattleService.
     *
     * TODO:
     *      Complete this constructor using proper dependency injection.
     *
     * @param battleService the service that handles all battle logic
     */
    public BattleController(BattleService battleService) {
        this.battleService = battleService;
    }

    /**
     * Returns the current battle state.
     * GET /api/battle
     *
     * The response includes the player, any active monster,
     * a status string, and a message for the UI.
     *
     * TODO:
     *      Add the correct annotation to handle GET requests to this endpoint.
     *      Implement this method to retrieve and return the current battle state.
     *      HINT: Look through the BattleService.java methods.
     *
     * @return current BattleState snapshot
     */
    public BattleState getCurrentBattle() {
        
    }

    /**
     * Starts a new encounter if none is in progress.
     * POST /api/battle/start
     *
     * If a battle is already ongoing, the service can decide
     * whether to re-use it or prevent starting a new one.
     *
     * TODO:
     *      Add the correct annotation to handle POST requests to "/start".
     *      Implement this method to start a new encounter and return the battle state.
     *      HINT: Look through the BattleService.java methods.
     *
     * @return updated BattleState after starting the encounter
     */
    public BattleState startEncounter() {
        
    }

    /**
     * Performs a player attack in the current encounter.
     * POST /api/battle/attack
     *
     * The BattleService will:
     * - Apply player damage to the monster (including item bonuses)
     * - Apply monster retaliation damage
     * - Handle leveling, loot, revives, and game over conditions
     *
     * TODO:
     *      Add the correct annotation to handle POST requests to "/attack".
     *      Implement this method to perform a player attack and return the battle state.
     *      HINT: Look through the BattleService.java methods.
     *
     * @return updated BattleState after resolving the attack
     */
    public BattleState attack() {
        
    }
}