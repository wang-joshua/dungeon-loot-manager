package edu.gatech.cs1331.hw09.backend.repository;

import edu.gatech.cs1331.hw09.backend.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByRarity(String rarity);
}
