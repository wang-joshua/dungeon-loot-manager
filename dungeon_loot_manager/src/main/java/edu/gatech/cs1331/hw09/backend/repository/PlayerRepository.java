package edu.gatech.cs1331.hw09.backend.repository;

import edu.gatech.cs1331.hw09.backend.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
}
