package edu.gatech.cs1331.hw09.backend.controller;

import edu.gatech.cs1331.hw09.backend.model.Item;
import edu.gatech.cs1331.hw09.backend.model.Player;
import edu.gatech.cs1331.hw09.backend.service.ShopService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for the in-game shop.
 * Exposes endpoints that let the frontend:
 * - Fetch the current shop offers
 * - Buy an item from the shop
 *
 * The selection of items is handled by the ShopService.
 */

/*
 * TODO:
 *      Add the correct annotations so that this class defines REST operations
 *      and has a base endpoint path of "/api/shop".
 */

@CrossOrigin
public class ShopController {

    private final ShopService shopService;

    /**
     * Creates a ShopController with the given ShopService.
     *
     * TODO:
     *      Complete this constructor using proper dependency injection.
     *
     * @param shopService service for shop operations
     */
    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    /**
     * Returns up to four items currently for sale in the shop.
     * GET /api/shop
     *
     * The ShopService usually:
     * - Picks items based on rarity weights
     * - Randomizes the selection
     *
     * TODO:
     *      Add the correct annotation to handle GET requests.
     *      Implement this method to retrieve and return the shop's current offers.
     *      HINT: Look through the ShopService.java methods.
     *
     * @return list of Item entities the player can buy
     */
    public List<Item> getOffers() {
        
    }

    /**
     * Buys one copy of the given item from the shop.
     * POST /api/shop/buy/{itemId}
     *
     * The ShopService:
     * - Checks that the player has enough gold
     * - Deducts the item's gold cost
     * - Adds one copy to the inventory
     *
     * TODO:
     *      Add the correct annotation to handle POST requests to "/buy/{itemId}".
     *      Add the correct annotation to capture itemId from the URL path.
     *      Implement this method to purchase the item and return the updated Player.
     *      HINT: Look through the ShopService.java methods.
     *
     * @param itemId id of the item to buy
     * @return updated Player after purchase
     */
    public Player buy(Long itemId) {
        
    }
}