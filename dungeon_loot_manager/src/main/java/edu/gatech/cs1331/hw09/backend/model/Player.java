package edu.gatech.cs1331.hw09.backend.model;

import jakarta.persistence.*;

/**
 * JPA entity representing the player in the game.
 * This class stores persistent state such as stats, health,
 * gold, equipped items, encounters fought, and game flags.
 */

/*
 * TODO:
 *      insert the correct annotations so that a Player is defined
 *      in our database, and has a unique Id.
 */


public class Player {

   
    private Long id;
    private String name;
    private int level;
    private int experience;
    private int baseAttack;
    private int maxHealth;
    private int currentHealth;
    private int gold;
    private int encountersFoughtCount;
    private Long equippedWeaponItemId;
    private int equippedWeaponDurability;
    private Long  equippedArmorItemId;
    private int equippedArmorDurability;
    private int revivesLeft;
    private boolean debtPaid;
    private boolean gameOver;

    public Player() {
    }

    public Player(String name, int level, int gold,
                  int baseAttack, int maxHealth) {
        setName(name);
        setLevel(level);
        setExperience(0);
        setBaseAttack(baseAttack);
        setMaxHealth(maxHealth);
        setCurrentHealth(maxHealth);
        setGold(gold);
        setEncountersFoughtCount(0);
    }

    //== SETTERS ==\\
    public void setId(Long id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public void setExperience(int experience) {
        this.experience = experience;
    }
    public void setBaseAttack(int baseAttack) {
        this.baseAttack = baseAttack;
    }
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }
    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }
    public void setGold(int gold) {
        this.gold = gold;
    }
    public void setEncountersFoughtCount(int encountersFoughtCount) {
        this.encountersFoughtCount = encountersFoughtCount;
    }
    public void setEquippedWeaponItemId(Long equippedWeaponItemId) {
        this.equippedWeaponItemId = equippedWeaponItemId;
    }
    public void setEquippedWeaponDurability(int equippedWeaponDurability) {
        this.equippedWeaponDurability = equippedWeaponDurability;
    }
    public void setEquippedArmorItemId(Long equippedArmorItemId) {
        this.equippedArmorItemId = equippedArmorItemId;
    }
    public void setEquippedArmorDurability(int equippedArmorDurability) {
        this.equippedArmorDurability = equippedArmorDurability;
    }
    public void setRevivesLeft(int revivesLeft) {
        this.revivesLeft = revivesLeft;
    }
    public void setDebtPaid(boolean debtPaid) {
        this.debtPaid = debtPaid;
    }
    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    //== GETTERS ==\\
    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public int getLevel() {
        return level;
    }
    public int getExperience() {
        return experience;
    }
    public int getBaseAttack() {
        return baseAttack;
    }
    public int getMaxHealth() {
        return maxHealth;
    }
    public int getCurrentHealth() {
        return currentHealth;
    }
    public int getGold() {
        return gold;
    }
    public int getEncountersFoughtCount() {
        return encountersFoughtCount;
    }
    public Long getEquippedWeaponItemId() {
        return equippedWeaponItemId;
    }
    public int getEquippedWeaponDurability() {
        return equippedWeaponDurability;
    }
    public Long getEquippedArmorItemId() {
        return equippedArmorItemId;
    }
    public int getEquippedArmorDurability() {
        return equippedArmorDurability;
    }
    public int getRevivesLeft() {
        return revivesLeft;
    }
    public boolean isDebtPaid() {
        return debtPaid;
    }
    public boolean isGameOver() {
        return gameOver;
    }
}
