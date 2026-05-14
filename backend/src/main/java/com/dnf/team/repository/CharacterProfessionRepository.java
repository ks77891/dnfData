package com.dnf.team.repository;

import com.dnf.team.entity.CharacterProfession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CharacterProfessionRepository extends JpaRepository<CharacterProfession, Long> {
    List<CharacterProfession> findByGameCharacterId(Long gameCharacterId);

    @Query("SELECT cp FROM CharacterProfession cp WHERE cp.isActive = true ORDER BY cp.magicResistance DESC")
    List<CharacterProfession> findAllActiveOrderByMagicResistanceDesc();

    @Query("SELECT cp FROM CharacterProfession cp WHERE cp.isActive = true AND cp.magicResistance >= :minMagicResistance ORDER BY cp.magicResistance DESC")
    List<CharacterProfession> findActiveByMinMagicResistance(int minMagicResistance);
}
