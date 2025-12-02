package edu.gatech.cs1331.hw09.backend.service;

import edu.gatech.cs1331.hw09.backend.model.Item;
import edu.gatech.cs1331.hw09.backend.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public List<Item> getItemsByRarity(String rarity) {
        return itemRepository.findByRarity(rarity);
    }

    public Item createItem(Item item) {
        return itemRepository.save(item);
    }

    public Item getItemOrThrow(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item " + id + " not found"));
    }
}