package edu.gatech.cs1331.hw09.backend.model;

/**
 * Enumeration listing the kinds of monsters available in the game.
 * Different kinds have different stats and are created in the battle service.
 */
public enum MonsterKind {
    /**
     * Standard low-level skeleton enemy.
     */
    SKELETON,

    /**
     * Tougher melee enemy with more health than a skeleton.
     */
    ORC,

    /**
     * Faster, more dangerous enemy with higher attack.
     */
    BANDIT,

    /**
     * Boss monster that appears every five encounters.
     */
    DRAGON
}