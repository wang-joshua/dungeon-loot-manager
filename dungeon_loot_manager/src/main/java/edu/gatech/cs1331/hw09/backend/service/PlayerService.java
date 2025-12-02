package edu.gatech.cs1331.hw09.backend.service;

import edu.gatech.cs1331.hw09.backend.model.Player;
import edu.gatech.cs1331.hw09.backend.repository.PlayerRepository;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private static final int DEBT_COST = 300;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player getOrCreatePlayer() {
        return playerRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> {
                    Player p = new Player();
                    p.setName("Hero");
                    p.setLevel(1);
                    p.setExperience(0);
                    p.setBaseAttack(5);
                    p.setMaxHealth(30);
                    p.setCurrentHealth(30);
                    p.setGold(0);
                    p.setEncountersFoughtCount(0);
                    p.setRevivesLeft(2);
                    p.setDebtPaid(false);
                    p.setGameOver(false);
                    p.setEquippedWeaponItemId(null);
                    p.setEquippedWeaponDurability(0);
                    p.setEquippedArmorItemId(null);
                    p.setEquippedArmorDurability(0);

                    return playerRepository.save(p);
                });
    }

    public Player save(Player player) {
        return playerRepository.save(player);
    }

    public Player incrementEncounters(Player player) {
        player.setEncountersFoughtCount(player.getEncountersFoughtCount() + 1);
        return playerRepository.save(player);
    }

    public Player grantExperience(Player player, int xp) {
        player.setExperience(player.getExperience() + xp);
        while (player.getExperience() >= xpToNextLevel(player.getLevel())) {
            player.setExperience(player.getExperience() - xpToNextLevel(player.getLevel()));
            player.setLevel(player.getLevel() + 1);
            player.setBaseAttack(player.getBaseAttack() + 1);
            player.setMaxHealth(player.getMaxHealth() + 5);
            player.setCurrentHealth(player.getMaxHealth());
        }
        return save(player);
    }

    public Player payDebt() {
        Player p = getOrCreatePlayer();
        if (p.isDebtPaid()) {
            return p;
        }
        if (p.getGold() < DEBT_COST) {
            throw new IllegalStateException("Not enough gold to pay the debt.");
        }
        p.setGold(p.getGold() - DEBT_COST);
        p.setDebtPaid(true);
        p.setGameOver(true);
        return save(p);
    }

    private int xpToNextLevel(int level) {
        // 10 -> 15 -> 20...
        return 10 + (level - 1) * 5;
    }

    public Player restartGame() {
        Player p = getOrCreatePlayer();
        p.setName("Hero");
        p.setLevel(1);
        p.setExperience(0);
        p.setBaseAttack(5);
        p.setMaxHealth(30);
        p.setCurrentHealth(30);
        p.setGold(0);
        p.setEncountersFoughtCount(0);
        p.setRevivesLeft(2);
        p.setDebtPaid(false);
        p.setGameOver(false);
        p.setEquippedWeaponItemId(null);
        p.setEquippedWeaponDurability(0);
        p.setEquippedArmorItemId(null);
        p.setEquippedArmorDurability(0);

        return save(p);
    }

}