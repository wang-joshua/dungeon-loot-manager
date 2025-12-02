package edu.gatech.cs1331.hw09.backend.config;

import edu.gatech.cs1331.hw09.backend.model.*;
import edu.gatech.cs1331.hw09.backend.service.ItemService;
import edu.gatech.cs1331.hw09.backend.service.PlayerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final PlayerService playerService;
    private final ItemService itemService;

    public DataLoader(PlayerService playerService, ItemService itemService) {
        this.playerService = playerService;
        this.itemService = itemService;
    }

    @Override
    public void run(String... args) {
        playerService.getOrCreatePlayer();

        if (itemService.getAllItems().isEmpty()) {

            // WEAPONS (4 COMMON, 2 RARE, 2 EPIC, 1 LEGENDARY)
            itemService.createItem(new Item("Practice Shortsword", ItemType.WEAPON, "COMMON", 1, 2));
            itemService.createItem(new Item("Stone Club", ItemType.WEAPON, "COMMON", 1, 1));
            itemService.createItem(new Item("Rusty Sword", ItemType.WEAPON, "COMMON", 2, 3));
            itemService.createItem(new Item("Bandit's Dagger", ItemType.WEAPON, "COMMON", 2, 4));
            itemService.createItem(new Item("Iron Sword", ItemType.WEAPON, "RARE", 3, 5));
            itemService.createItem(new Item("Orcish Axe", ItemType.WEAPON, "RARE", 6, 20));
            itemService.createItem(new Item("Holy Knight's Blade", ItemType.WEAPON, "EPIC", 5, 15));
            itemService.createItem(new Item("Stormforged Spear", ItemType.WEAPON, "EPIC", 6, 18));
            itemService.createItem(new Item("Dragonfang Greatsword", ItemType.WEAPON, "LEGENDARY", 8, 40));

            // ARMORS (4 COMMON, 2 RARE, 2 EPIC, 1 LEGENDARY)
            itemService.createItem(new Item("Leather Jerkin", ItemType.ARMOR, "COMMON", 1, 2));
            itemService.createItem(new Item("Chainmail Vest", ItemType.ARMOR, "COMMON", 2, 5));
            itemService.createItem(new Item("Wooden Shield", ItemType.ARMOR, "COMMON", 2, 3));
            itemService.createItem(new Item("Iron Shield", ItemType.ARMOR, "COMMON", 3, 5));
            itemService.createItem(new Item("Guardian Plate", ItemType.ARMOR, "RARE", 5, 15));
            itemService.createItem(new Item("Reinforced Helm", ItemType.ARMOR, "RARE", 4, 12));
            itemService.createItem(new Item("Aegis Breastplate", ItemType.ARMOR, "EPIC", 6, 18));
            itemService.createItem(new Item("Cloak of Evasion", ItemType.ARMOR, "EPIC", 3, 10));
            itemService.createItem(new Item("Dragonscale Mail", ItemType.ARMOR, "LEGENDARY", 8, 40));

            // POTIONS (3 COMMON, 1 RARE, 1 EPIC, 1 LEGENDARY)
            itemService.createItem(new Item("Stamina Tonic", ItemType.POTION, "COMMON", 8, 3));
            itemService.createItem(new Item("Rejuvenation Brew", ItemType.POTION, "COMMON", 12, 5));
            itemService.createItem(new Item("Small Healing Potion", ItemType.POTION, "COMMON", 10, 4));
            itemService.createItem(new Item("Healing Draught", ItemType.POTION, "RARE", 15, 6));
            itemService.createItem(new Item("Greater Healing Potion", ItemType.POTION, "EPIC", 20, 10));
            itemService.createItem(new Item("Elixir of Life", ItemType.POTION, "LEGENDARY", 35, 30));

            // VALUABLES (3 COMMON, 1 RARE, 1 EPIC, 1 LEGENDARY)
            itemService.createItem(new Item("Silver Locket", ItemType.VALUABLE, "COMMON", 0, 12));
            itemService.createItem(new Item("Merchant's Ledger", ItemType.VALUABLE, "COMMON", 0, 15));
            itemService.createItem(new Item("Lucky Copper Coin", ItemType.VALUABLE, "COMMON", 0, 8));
            itemService.createItem(new Item("Jeweled Ring", ItemType.VALUABLE, "RARE", 0, 25));
            itemService.createItem(new Item("Ancient Relic", ItemType.VALUABLE, "EPIC", 0, 40));
            itemService.createItem(new Item("Dragon Idol", ItemType.VALUABLE, "LEGENDARY", 0, 60));
        }
    }
}