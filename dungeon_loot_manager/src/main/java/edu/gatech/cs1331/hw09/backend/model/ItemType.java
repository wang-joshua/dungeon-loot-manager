package edu.gatech.cs1331.hw09.backend.model;

/**
 * Enumeration of the different item categories supported by the game.
 * Each type is handled differently by the services and UI.
 */
public enum ItemType {
    /**
     * Increases the player's attack power while equipped.
     */
    WEAPON,

    /**
     * Increases the player's max (and current) health while equipped.
     */
    ARMOR,

    /**
     * Restores health immediately when used from the inventory.
     */
    POTION,

    /**
     * Has no combat effect and can only be sold for gold.
     */
    VALUABLE
}
