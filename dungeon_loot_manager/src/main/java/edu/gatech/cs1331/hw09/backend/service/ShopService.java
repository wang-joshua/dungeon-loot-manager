package edu.gatech.cs1331.hw09.backend.service;

import edu.gatech.cs1331.hw09.backend.model.Item;
import edu.gatech.cs1331.hw09.backend.model.Player;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ShopService {

    private final ItemService itemService;
    private final PlayerService playerService;
    private final InventoryService inventoryService;
    private final Random random = new Random();
    private List<Item> cachedOffers = new ArrayList<>();
    private int lastBatch = -1;

    public ShopService(ItemService itemService,
                       PlayerService playerService,
                       InventoryService inventoryService) {
        this.itemService = itemService;
        this.playerService = playerService;
        this.inventoryService = inventoryService;
    }

    /**
     * Returns up to 4 random items for sale, chosen by rarity weights.
     *  rarity weights:
     *  - COMMON:       62%
     *  - RARE:         24%
     *  - EPIC:         8%
     *  - LEGENDARY:    6%
     *
     * The set of offers is cached and only refreshed when the
     * player's encounter "batch" changes (every 5 encounters).
     */
    public List<Item> getCurrentOffers() {
        Player player = playerService.getOrCreatePlayer();
        int currentBatch = player.getEncountersFoughtCount() / 5;
        List<Item> all = itemService.getAllItems();
        if (all.isEmpty()) {
            return List.of();
        }

        if (cachedOffers.isEmpty() || currentBatch != lastBatch) {
            lastBatch = currentBatch;

            Map<String, List<Item>> byRarity = new HashMap<>();
            for (Item item : all) {
                String rarity = item.getRarity();
                if (rarity == null) {
                    rarity = "COMMON";
                }
                rarity = rarity.toUpperCase(Locale.ROOT);
                byRarity.computeIfAbsent(rarity, k -> new ArrayList<>()).add(item);
            }

            int limit = Math.min(4, all.size());
            List<Item> offers = new ArrayList<>();
            for (int i = 0; i < limit; i++) {
                Item chosen = pickWeightedItem(byRarity);
                if (chosen == null) {
                    break;
                }
                offers.add(chosen);
            }
            cachedOffers = offers;
        }

        return cachedOffers;
    }

    private Item pickWeightedItem(Map<String, List<Item>> byRarity) {
        int wCommon = byRarity.getOrDefault("COMMON", List.of()).isEmpty() ? 0 : 62;
        int wRare = byRarity.getOrDefault("RARE", List.of()).isEmpty() ? 0 : 24;
        int wEpic = byRarity.getOrDefault("EPIC", List.of()).isEmpty() ? 0 : 8;
        int wLegendary = byRarity.getOrDefault("LEGENDARY", List.of()).isEmpty() ? 0 : 6;

        int totalWeight = wCommon + wRare + wEpic + wLegendary;
        if (totalWeight <= 0) {
            return null;
        }

        int roll = random.nextInt(totalWeight);
        if (roll < wCommon) {
            return randomFromList(byRarity.get("COMMON"));
        }
        roll -= wCommon;
        if (roll < wRare) {
            return randomFromList(byRarity.get("RARE"));
        }
        roll -= wRare;
        if (roll < wEpic) {
            return randomFromList(byRarity.get("EPIC"));
        }

        return randomFromList(byRarity.get("LEGENDARY"));
    }

    private Item randomFromList(List<Item> items) {
        if (items == null || items.isEmpty()) {
            return null;
        }
        return items.get(random.nextInt(items.size()));
    }

    public Player buyItem(Long itemId) {
        Player player = playerService.getOrCreatePlayer();
        Item item = itemService.getItemOrThrow(itemId);

        int cost = item.getGoldValue();
        if (player.getGold() < cost) {
            throw new IllegalStateException("Not enough gold to buy this item.");
        }

        player.setGold(player.getGold() - cost);
        playerService.save(player);
        inventoryService.addItem(item.getId());

        return playerService.getOrCreatePlayer();
    }
}