package com.dnf.team.repository;

import com.dnf.team.entity.GameCharacter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameCharacterRepository extends JpaRepository<GameCharacter, Long> {
    List<GameCharacter> findByUserId(Long userId);
    Optional<GameCharacter> findByUserIdAndName(Long userId, String name);
}
