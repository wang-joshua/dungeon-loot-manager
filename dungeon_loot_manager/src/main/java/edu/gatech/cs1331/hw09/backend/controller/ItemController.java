package edu.gatech.cs1331.hw09.backend.controller;

import edu.gatech.cs1331.hw09.backend.model.Item;
import edu.gatech.cs1331.hw09.backend.service.ItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for browsing the item catalog.
 * The item catalog is the list of all possible items in the game,
 * not the player's inventory.
 */

/*
 * TODO:
 *      Add the correct annotations so that this class defines REST operations
 *      and has a base endpoint path of "/api/items".
 */

@RestController
@RequestMapping("/api/items")
@CrossOrigin
public class ItemController {

    private final ItemService itemService;

    /**
     * Creates an ItemController that delegates to the given ItemService.
     *
     * TODO:
     *      Complete this constructor using proper dependency injection.
     *
     * @param itemService service for item catalog access
     */
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * Returns the full list of items in the item catalog.
     * GET /api/items
     *
     * The frontend uses this both to show all items and to
     * look up names/stats when rendering the inventory.
     *
     * TODO:
     *      Add the correct annotation to handle GET requests.
     *      Implement this method to retrieve and return all items.
     *      HINT: Look through the ItemService.java methods.
     *
     * @return list of all Item entities
     */
    @GetMapping
    public List<Item> getAllItems() {
        return itemService.getAllItems();
    }

    /**
     * Returns a specific item by its ID.
     * GET /api/items/{id}
     *
     * This can be used to fetch detailed information about a single item.
     *
     * TODO:
     *      Add the correct annotation to handle GET requests to "/{id}".
     *      Add the correct annotation to capture id from the URL path.
     *      Implement this method to retrieve and return the item by ID.
     *      HINT: Look through the ItemService.java methods.
     *
     * @param id the ID of the item to retrieve
     * @return the Item entity with the given ID
     */
    @GetMapping("/{id}")
    public Item getItemById(@PathVariable Long id) {
        return itemService.getItemOrThrow(id);
    }

    /**
     * Returns all items with a given rarity label.
     * GET /api/items/rarity/{rarity}
     *
     * The rarity is "COMMON", "RARE", "EPIC" or "LEGENDARY".
     * This can be used to filter the catalog or analyze drop tables.
     *
     * TODO:
     *      Add the correct annotation to handle GET requests to "/rarity/{rarity}".
     *      Add the correct annotation to capture rarity from the URL path.
     *      Implement this method to retrieve and return items by rarity.
     *      HINT: Look through the ItemService.java methods.
     *
     * @param rarity rarity filter to apply
     * @return list of items that match the given rarity
     */
    @GetMapping("/rarity/{rarity}")
    public List<Item> getItemsByRarity(@PathVariable String rarity) {
        return itemService.getItemsByRarity(rarity);
    }

    /**
     * Creates a new item in the item catalog from the request body.
     * POST /api/items
     *
     * This is useful as an admin or testing endpoint for
     * adding new items without touching the DataLoader.
     * 
     *
     * @param item the Item data sent from the client
     * @return the newly created Item after being saved
     */
    @PostMapping
    public Item createItem(@RequestBody Item item) {
        return itemService.createItem(item);
    }
}