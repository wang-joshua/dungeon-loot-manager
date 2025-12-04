package edu.gatech.cs1331.hw09.backend.controller;

import edu.gatech.cs1331.hw09.backend.model.InventoryItem;
import edu.gatech.cs1331.hw09.backend.model.Player;
import edu.gatech.cs1331.hw09.backend.service.InventoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for inventory actions.
 * Exposes endpoints that let the frontend:
 * - View the player's inventory
 * - Sell items
 * - Use potions
 * - Equip or unequip armor and weapons
 *
 * All business logic is delegated to the InventoryService.
 */

/*
 * TODO:
 *      Add the correct annotations so that this class defines REST operations
 *      and has a base endpoint path of "/api/inventory".
 */

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * Creates an InventoryController with the given InventoryService.
     *
     * TODO:
     *      Complete this constructor using proper dependency injection.
     *
     * @param inventoryService service for inventory logic
     */
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * Returns all inventory rows for the player.
     * Each row includes an item id and quantity.
     * GET /api/inventory
     *
     * The frontend joins this with the item catalog to display names.
     *
     * TODO:
     *      Add the correct annotation to handle GET requests.
     *      Implement this method to retrieve and return the player's inventory.
     *      HINT: Look through the InventoryService.java methods.
     *
     * @return list of InventoryItem entries
     */
    @GetMapping
    public List<InventoryItem> getInventory() {
        return inventoryService.getInventory();
    }

    /**
     * Adds an item to the player's inventory after looting.
     * POST /api/inventory/loot/{itemId}
     *
     * The InventoryService:
     * - Increments the quantity if item already exists
     * - Creates a new inventory entry if it doesn't
     *
     * TODO:
     *      Add the correct annotation to handle POST requests to "/loot/{itemId}".
     *      Add the correct annotation to capture itemId from the URL path.
     *      Implement this method to add the item to inventory and return the result.
     *      HINT: Look through the InventoryService.java methods.
     *
     * @param itemId id of the Item to loot
     * @return the InventoryItem after looting
     */
    @PostMapping("/loot/{itemId}")
    public InventoryItem loot(@PathVariable Long itemId) {
        return inventoryService.addItem(itemId);
    }

    /**
     * Removes an item from the player's inventory.
     * DELETE /api/inventory/drop/{itemId}
     *
     * The InventoryService:
     * - Decrements the quantity or removes the inventory row
     *
     * TODO:
     *      Add the correct annotation to handle DELETE requests to "/drop/{itemId}".
     *      Add the correct annotation to capture itemId from the URL path.
     *      Implement this method to remove the item from inventory and return the result.
     *      HINT: Look through the InventoryService.java methods.
     *
     * @param itemId id of the Item to drop
     * @return the updated InventoryItem after dropping
     */
    @DeleteMapping("/drop/{itemId}")
    public InventoryItem drop(@PathVariable Long itemId) {
        inventoryService.removeOne(itemId);
        return inventoryService.getInventory().stream()
                .filter(inv -> inv.getItemId().equals(itemId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Sells exactly one copy of the given item id from the inventory.
     * POST /api/inventory/sell/{itemId}
     *
     * The InventoryService:
     * - Decrements or removes the inventory row
     * - Adds the item's gold value to the player
     *
     * TODO:
     *      Add the correct annotation to handle POST requests to "/sell/{itemId}".
     *      Add the correct annotation to capture itemId from the URL path.
     *      Implement this method to sell the item and return the updated Player.
     *      HINT: Look through the InventoryService.java methods.
     *
     * @param itemId id of the Item to sell
     * @return updated InventoryItem after selling (or null if item was removed)
     */
    @PostMapping("/sell/{itemId}")
    public InventoryItem sell(@PathVariable Long itemId) {
        inventoryService.sellOne(itemId);
        return inventoryService.getInventory().stream()
                .filter(inv -> inv.getItemId().equals(itemId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Uses a potion from the inventory.
     * POST /api/inventory/use/{itemId}
     *
     * Only items of type POTION are valid here. The service will:
     * - Reduce the quantity by one
     * - Heal the player by the potion's power (up to max health)
     *
     * TODO:
     *      Add the correct annotation to handle POST requests to "/use/{itemId}".
     *      Add the correct annotation to capture itemId from the URL path.
     *      Implement this method to use the potion and return the updated Player.
     *      HINT: Look through the InventoryService.java methods.
     *
     * @param itemId id of the potion item
     * @return updated InventoryItem after using the potion (or null if item was removed)
     */
    @PostMapping("/use/{itemId}")
    public InventoryItem use(@PathVariable Long itemId) {
        inventoryService.usePotion(itemId);
        return inventoryService.getInventory().stream()
                .filter(inv -> inv.getItemId().equals(itemId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Equips a weapon or armor from the inventory.
     * POST /api/inventory/equip/{itemId}
     *
     * The service will:
     * - Equip the weapon or armor
     * - Adjust health if armor is equipped
     * - Initialize durability if needed
     *
     * TODO:
     *      Add the correct annotation to handle POST requests to "/equip/{itemId}".
     *      Add the correct annotation to capture itemId from the URL path.
     *      Implement this method to equip the item and return the updated Player.
     *      HINT: Look through the InventoryService.java methods.
     *
     * @param itemId id of the item to equip
     * @return updated InventoryItem after equipping
     */
    @PostMapping("/equip/{itemId}")
    public InventoryItem equip(@PathVariable Long itemId) {
        inventoryService.equipItem(itemId);
        return inventoryService.getInventory().stream()
                .filter(inv -> inv.getItemId().equals(itemId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Unequips a weapon or armor that is currently equipped.
     * POST /api/inventory/unequip/{itemId}
     *
     * The service will:
     * - Remove the item from the equipped slot
     * - Adjust health back if armor was unequipped
     *
     * TODO:
     *      Add the correct annotation to handle POST requests to "/unequip/{itemId}".
     *      Add the correct annotation to capture itemId from the URL path.
     *      Implement this method to unequip the item and return the updated Player.
     *      HINT: Look through the InventoryService.java methods.
     *
     * @param itemId id of the item to unequip
     * @return updated InventoryItem after unequipping
     */
    @PostMapping("/unequip/{itemId}")
    public InventoryItem unequip(@PathVariable Long itemId) {
        inventoryService.unequipItem(itemId);
        return inventoryService.getInventory().stream()
                .filter(inv -> inv.getItemId().equals(itemId))
                .findFirst()
                .orElse(null);
    }
}
