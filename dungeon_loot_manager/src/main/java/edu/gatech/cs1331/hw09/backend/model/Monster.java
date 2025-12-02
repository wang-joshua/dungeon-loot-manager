package edu.gatech.cs1331.hw09.backend.model;

/**
 * Simple model representing an enemy the player can fight.
 * Monsters are not stored as JPA entities; they are created
 * on demand for each encounter and kept in memory.
 */
public class Monster {

    private MonsterKind kind;
    private int maxHealth;
    private int currentHealth;
    private int attackPower;
    private int experienceReward;
    private int goldReward;

    public Monster(MonsterKind kind, int maxHealth,
                   int attackPower, int experienceReward, int goldReward) {
        setKind(kind);
        setMaxHealth(maxHealth);
        setCurrentHealth(maxHealth);
        setAttackPower(attackPower);
        setExperienceReward(experienceReward);
        setGoldReward(goldReward);
    }

    //== SETTERS ==\\
    public void setKind(MonsterKind kind) {
        this.kind = kind;
    }
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }
    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }
    public void setAttackPower(int attackPower) {
        this.attackPower = attackPower;
    }
    public void setExperienceReward(int experienceReward) {
        this.experienceReward = experienceReward;
    }
    public void setGoldReward(int goldReward) {
        this.goldReward = goldReward;
    }

    //== GETTERS ==\\
    public MonsterKind getKind() {
        return kind;
    }
    public boolean isAlive() {
        return currentHealth > 0;
    }
    public int getMaxHealth() {
        return maxHealth;
    }
    public int getCurrentHealth() {
        return currentHealth;
    }
    public int getAttackPower() {
        return attackPower;
    }
    public int getExperienceReward() {
        return experienceReward;
    }
    public int getGoldReward() {
        return goldReward;
    }
}