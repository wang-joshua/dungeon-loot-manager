package edu.gatech.cs1331.hw09.backend.repository;

import edu.gatech.cs1331.hw09.backend.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    Optional<InventoryItem> findByItemId(Long itemId);
}