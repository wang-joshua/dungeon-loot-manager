package edu.gatech.cs1331.hw09.backend.model;

import jakarta.persistence.*;

/**
 * JPA entity representing an item that can exist in the dungeon.
 * Items can be weapons, armor, potions, or valuables and carry stats
 * such as power, rarity, and a gold value.
 *
 * These records are created once at startup and reused throughout the game.
 */

/*
 * TODO:
 *      insert the correct annotations so that a item is defined
 *      in our database, and has a unique Id.
 */

public class Item {

    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private ItemType type;
    private String rarity;
    private int power;
    private int goldValue;

    public Item() { }

    public Item(String name, ItemType type, String rarity,
                int power, int value) {
        setName(name);
        setType(type);
        setRarity(rarity);
        setPower(power);
        setGoldValue(value);
    }

    //== SETTERS ==\\
    public void setId(Long id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setType(ItemType type) {
        this.type = type;
    }
    public void setRarity(String rarity) {
        this.rarity = rarity;
    }
    public void setPower(int power) {
        this.power = power;
    }
    public void setGoldValue(int value) {
        this.goldValue = value;
    }

    //== GETTERS ==\\
    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public ItemType getType() {
        return type;
    }
    public String getRarity() {
        return rarity;
    }
    public int getPower() {
        return power;
    }
    public int getGoldValue() {
        return goldValue;
    }
}
