package edu.gatech.cs1331.hw09.backend.service;

import edu.gatech.cs1331.hw09.backend.model.*;
import edu.gatech.cs1331.hw09.backend.repository.InventoryItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    private final InventoryItemRepository inventoryRepo;
    private final ItemService itemService;
    private final PlayerService playerService;

    public InventoryService(InventoryItemRepository inventoryRepo,
                            ItemService itemService,
                            PlayerService playerService) {
        this.inventoryRepo = inventoryRepo;
        this.itemService = itemService;
        this.playerService = playerService;
    }

    private int durabilityForRarity(String rarity) {
        if (rarity == null) return 5;
        return switch (rarity.toUpperCase()) {
            case "COMMON" -> 3;
            case "RARE" -> 6;
            case "EPIC" -> 9;
            case "LEGENDARY" -> 12;
            default -> 3;
        };
    }

    public List<InventoryItem> getInventory() {
        return inventoryRepo.findAll();
    }

    public InventoryItem addItem(Long itemId) {
        InventoryItem inv = inventoryRepo.findByItemId(itemId)
                .orElseGet(() -> {
                    InventoryItem ii = new InventoryItem();
                    ii.setItemId(itemId);
                    ii.setQuantity(0);
                    return ii;
                });

        inv.setQuantity(inv.getQuantity() + 1);
        return inventoryRepo.save(inv);
    }

    public void removeOne(Long itemId) {
        inventoryRepo.findByItemId(itemId).ifPresent(existing -> {
            int newQty = existing.getQuantity() - 1;
            if (newQty <= 0) {
                inventoryRepo.delete(existing);
            } else {
                existing.setQuantity(newQty);
                inventoryRepo.save(existing);
            }
        });
    }

    public int getWeaponBonus() {
        Player player = playerService.getOrCreatePlayer();
        Long weaponId = player.getEquippedWeaponItemId();
        if (weaponId == null || player.getEquippedWeaponDurability() <= 0) {
            return 0;
        }
        Item item = itemService.getItemOrThrow(weaponId);
        return item.getType() == ItemType.WEAPON ? item.getPower() : 0;
    }

    public int getArmorBonus() {
        Player player = playerService.getOrCreatePlayer();
        Long armorId = player.getEquippedArmorItemId();
        if (armorId == null || player.getEquippedArmorDurability() <= 0) {
            return 0;
        }
        Item item = itemService.getItemOrThrow(armorId);
        return item.getType() == ItemType.ARMOR ? item.getPower() : 0;
    }

    public Player equipItem(Long itemId) {
        InventoryItem inv = inventoryRepo.findByItemId(itemId)
                .orElseThrow(() -> new IllegalArgumentException("You don't own this item"));

        Item item = itemService.getItemOrThrow(itemId);
        Player player = playerService.getOrCreatePlayer();

        int fullDurability = durabilityForRarity(item.getRarity());
        if (item.getType() == ItemType.WEAPON) {
            Long equippedId = player.getEquippedWeaponItemId();

            if (equippedId == null || equippedId.equals(itemId)) {
                if (player.getEquippedWeaponDurability() <= 0) {
                    player.setEquippedWeaponDurability(fullDurability);
                }
                player.setEquippedWeaponItemId(itemId);
            } else {
                player.setEquippedWeaponItemId(itemId);
                player.setEquippedWeaponDurability(fullDurability);
            }

        } else if (item.getType() == ItemType.ARMOR) {

            int newArmorPower = item.getPower();
            int oldArmorPower = 0;
            if (player.getEquippedArmorItemId() != null) {
                Item oldArmor = itemService.getItemOrThrow(player.getEquippedArmorItemId());
                oldArmorPower = oldArmor.getPower();
            }

            int delta = newArmorPower - oldArmorPower;
            if (delta != 0) {
                player.setMaxHealth(player.getMaxHealth() + delta);
                player.setCurrentHealth(player.getCurrentHealth() + delta);
                if (player.getCurrentHealth() > player.getMaxHealth()) {
                    player.setCurrentHealth(player.getMaxHealth());
                }
            }

            Long equippedArmorId = player.getEquippedArmorItemId();
            if (equippedArmorId == null || equippedArmorId.equals(itemId)) {
                if (player.getEquippedArmorDurability() <= 0) {
                    player.setEquippedArmorDurability(fullDurability);
                }
                player.setEquippedArmorItemId(itemId);
            } else {
                player.setEquippedArmorItemId(itemId);
                player.setEquippedArmorDurability(fullDurability);
            }
        } else {
            throw new IllegalArgumentException("Only WEAPON or ARMOR can be equipped.");
        }

        return playerService.save(player);
    }

    public Player unequipItem(Long itemId) {
        Player player = playerService.getOrCreatePlayer();

        if (player.getEquippedWeaponItemId() != null
                && player.getEquippedWeaponItemId().equals(itemId)) {
            player.setEquippedWeaponItemId(null);
        }

        if (player.getEquippedArmorItemId() != null
                && player.getEquippedArmorItemId().equals(itemId)) {

            Item armor = itemService.getItemOrThrow(itemId);
            int armorPower = armor.getPower();

            player.setMaxHealth(player.getMaxHealth() - armorPower);
            int newCurrent = player.getCurrentHealth() - armorPower;
            if (newCurrent < 1) {
                newCurrent = 1;
            }
            if (newCurrent > player.getMaxHealth()) {
                newCurrent = player.getMaxHealth();
            }
            player.setCurrentHealth(newCurrent);

            player.setEquippedArmorItemId(null);
        }

        return playerService.save(player);
    }

    public Player applyWeaponDurabilityLoss(Player player) {
        if (player.getEquippedWeaponItemId() == null) {
            return player;
        }
        int newDurability = player.getEquippedWeaponDurability() - 1;
        if (newDurability <= 0) {
            Long brokenId = player.getEquippedWeaponItemId();
            player.setEquippedWeaponItemId(null);
            player.setEquippedWeaponDurability(0);
            removeOne(brokenId);
        } else {
            player.setEquippedWeaponDurability(newDurability);
        }

        return playerService.save(player);
    }

    public Player applyArmorDurabilityLoss(Player player) {
        if (player.getEquippedArmorItemId() == null) {
            return player;
        }
        int newDurability = player.getEquippedArmorDurability() - 1;

        if (newDurability <= 0) {
            Long brokenId = player.getEquippedArmorItemId();
            Item armor = itemService.getItemOrThrow(brokenId);
            int armorPower = armor.getPower();
            player.setMaxHealth(player.getMaxHealth() - armorPower);
            int newCurrent = player.getCurrentHealth() - armorPower;
            if (newCurrent < 1) {
                newCurrent = 1;
            }
            if (newCurrent > player.getMaxHealth()) {
                newCurrent = player.getMaxHealth();
            }
            player.setCurrentHealth(newCurrent);
            player.setEquippedArmorItemId(null);
            player.setEquippedArmorDurability(0);
            removeOne(brokenId);
        } else {
            player.setEquippedArmorDurability(newDurability);
        }

        return playerService.save(player);
    }

    public Player sellOne(Long itemId) {
        InventoryItem inv = inventoryRepo.findByItemId(itemId)
                .orElseThrow(() -> new IllegalArgumentException("No such item in inventory"));

        Item item = itemService.getItemOrThrow(itemId);

        removeOne(itemId);

        Player player = playerService.getOrCreatePlayer();
        player.setGold(player.getGold() + item.getGoldValue());
        return playerService.save(player);
    }

    public Player usePotion(Long itemId) {
        InventoryItem inv = inventoryRepo.findByItemId(itemId)
                .orElseThrow(() -> new IllegalArgumentException("No such item in inventory"));

        Item item = itemService.getItemOrThrow(itemId);
        if (item.getType() != ItemType.POTION) {
            throw new IllegalArgumentException("Item is not a potion");
        }
        removeOne(itemId);
        Player player = playerService.getOrCreatePlayer();
        int effectiveMaxHp = player.getMaxHealth();
        int newHp = Math.min(effectiveMaxHp,
                player.getCurrentHealth() + item.getPower());
        player.setCurrentHealth(newHp);

        return playerService.save(player);
    }

    public void clearInventory() {
        inventoryRepo.deleteAll();
    }
}