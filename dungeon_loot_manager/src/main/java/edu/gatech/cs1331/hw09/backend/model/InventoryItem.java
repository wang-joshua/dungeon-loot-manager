package edu.gatech.cs1331.hw09.backend.model;

import jakarta.persistence.*;

/**
 * JPA entity representing a single row in the player's inventory.
 * Each row stores an item id (pointing at an Item record) and a quantity.
 *
 * This design lets the inventory store multiple copies of the same item.
 */

/*
 * TODO:
 *      insert the correct annotations so that an Inventory is defined
 *      in our database, and has a unique Id.
 */

public class InventoryItem {


    private Long id;
    private Long itemId;
    private int quantity;

    public InventoryItem() { }

    public InventoryItem(Long itemId, int quantity) {
        setItemId(itemId);
        setQuantity(quantity);
    }

    //== SETTERS ==\\
    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    //== GETTERS ==\\
    public Long getItemId() {
        return itemId;
    }
    public int  getQuantity() {
        return quantity;
    }
}