package edu.gatech.cs1331.hw09.backend.service;

import edu.gatech.cs1331.hw09.backend.model.*;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class BattleService {

    private final PlayerService playerService;
    private final InventoryService inventoryService;
    private final ItemService itemService;
    private final Random random = new Random();

    private Monster currentMonster;

    public BattleService(PlayerService playerService,
                         InventoryService inventoryService,
                         ItemService itemService) {
        this.playerService = playerService;
        this.inventoryService = inventoryService;
        this.itemService = itemService;
    }

    public BattleState getCurrentState() {
        Player player = playerService.getOrCreatePlayer();
        if (currentMonster == null) {
            return new BattleState(player, null,
                    "NO_ENCOUNTER", "No active enemy.");
        }
        return new BattleState(player, currentMonster,
                "ONGOING", "Battle in progress.");
    }

    public BattleState startEncounter() {
        Player player = playerService.getOrCreatePlayer();

        if (currentMonster != null && currentMonster.isAlive()) {
            return buildBattleState("You are already in a battle!", player, currentMonster);
        }

        player.setEncountersFoughtCount(player.getEncountersFoughtCount() + 1);
        playerService.save(player);

        Monster monster = createMonsterForEncounter(player.getEncountersFoughtCount());
        this.currentMonster = monster;

        return buildBattleState("A wild " + monster.getKind() + " appears!", player, monster);
    }

    public BattleState playerAttack() {
        Player player = playerService.getOrCreatePlayer();
        if (currentMonster == null) {
            return new BattleState(player, null,
                    "NO_ENCOUNTER", "There is no enemy to attack.");
        }

        // If game is over or debt is paid, no more combat
        if (player.isGameOver()) {
            return new BattleState(player, null,
                    "GAME_OVER", "Game over. Please restart.");
        }
        if (player.isDebtPaid()) {
            return new BattleState(player, currentMonster,
                    "COMPLETED", "You already paid your Hero Academy Debt!");
        }

        int weaponBonus = inventoryService.getWeaponBonus();
        int armorBonus = inventoryService.getArmorBonus();
        int effectiveAttack = player.getBaseAttack() + weaponBonus;
        int effectiveMaxHp = player.getMaxHealth();

        currentMonster.setCurrentHealth(
                currentMonster.getCurrentHealth() - effectiveAttack);

        player = inventoryService.applyWeaponDurabilityLoss(player);

        if (currentMonster.getCurrentHealth() <= 0) {
            rewardPlayer(player, currentMonster);
            Monster defeated = currentMonster;
            currentMonster = null;
            return new BattleState(playerService.getOrCreatePlayer(), null,
                    "MONSTER_DEFEATED",
                    "You defeated the " + defeated.getKind() + "!");
        }

        int newHp = player.getCurrentHealth() - currentMonster.getAttackPower();

        if (newHp <= 0) {
            if (player.getRevivesLeft() > 0) {
                player.setRevivesLeft(player.getRevivesLeft() - 1);
                player.setCurrentHealth(effectiveMaxHp);
                player.setGold(Math.max(0, player.getGold() - 5));
                player = inventoryService.applyArmorDurabilityLoss(player);
                player = playerService.save(player);
                Monster m = currentMonster;
                currentMonster = null;
                return new BattleState(player, null,
                        "PLAYER_DEFEATED",
                        "You were defeated by the " + m.getKind()
                                + ". You revive and lose some gold. Revives left: "
                                + player.getRevivesLeft());
            } else {
                player.setCurrentHealth(0);
                player.setGameOver(true);
                player = inventoryService.applyArmorDurabilityLoss(player);
                player = playerService.save(player);
                Monster m = currentMonster;
                currentMonster = null;
                return new BattleState(player, null,
                        "GAME_OVER",
                        "You were defeated by the " + m.getKind()
                                + ". No revives left. Game over!");
            }
        } else {
            player.setCurrentHealth(newHp);
            player = inventoryService.applyArmorDurabilityLoss(player);
            playerService.save(player);
            return new BattleState(player, currentMonster,
                    "ONGOING",
                    "You strike the " + currentMonster.getKind()
                            + ", but it hits you back!");
        }
    }

    private void rewardPlayer(Player player, Monster monster) {
        player.setGold(player.getGold() + monster.getGoldReward());
        player = playerService.grantExperience(player, monster.getExperienceReward());

        if (random.nextDouble() < 0.5) {
            var allItems = itemService.getAllItems();
            if (!allItems.isEmpty()) {
                Item dropped = allItems.get(random.nextInt(allItems.size()));
                inventoryService.addItem(dropped.getId());
            }
        }
    }

    /**
     * Creates a monster appropriate for the given encounter number.
     * Every 5th encounter should be a stronger dragon; all other
     * encounters are random basic monsters (skeleton, orc, or bandit).
     *
     * @param encounterCount the number of encounters the player has fought
     * @return a Monster instance for this encounter
     */
    private Monster createMonsterForEncounter(int encounterCount) {
        if (encounterCount > 0 && encounterCount % 5 == 0) {
            return createDragon();
        }
        return createRandomBasic();
    }

    /**
     * Builds a BattleState with the given message and current entities.
     * If there is a monster present, the status will indicate an ongoing battle.
     * If there is no monster, the status indicates that no encounter is active.
     *
     * @param message a message describing the situation for the player
     * @param player  the current Player
     * @param monster the current Monster, or null if none
     * @return a new BattleState representing the current battle situation
     */
    private BattleState buildBattleState(String message, Player player, Monster monster) {
        String status;
        if (monster == null) {
            status = "NO_ENCOUNTER";
        } else {
            status = "ONGOING";
        }
        return new BattleState(player, monster, status, message);
    }

    private Monster createDragon() {
        return new Monster(MonsterKind.DRAGON,
                60,   // health
                10,             // attack
                20,             // xp
                20);            // gold
    }

    private Monster createRandomBasic() {
        int pick = random.nextInt(3);
        switch (pick) {
            case 0:
                return new Monster(MonsterKind.SKELETON, 20, 3, 6, 5);
            case 1:
                return new Monster(MonsterKind.ORC, 25, 4, 7, 6);
            default:
                return new Monster(MonsterKind.BANDIT, 18, 5, 8, 8);
        }
    }
}