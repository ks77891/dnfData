package com.dnf.team.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "dnf_character_profession")
public class CharacterProfession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer level = 1;

    @Column(name = "magic_resistance", nullable = false)
    private Integer magicResistance = 0;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id", nullable = false)
    private GameCharacter gameCharacter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profession_id", nullable = false)
    private Profession profession;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }
    public Integer getMagicResistance() { return magicResistance; }
    public void setMagicResistance(Integer magicResistance) { this.magicResistance = magicResistance; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public GameCharacter getGameCharacter() { return gameCharacter; }
    public void setGameCharacter(GameCharacter gameCharacter) { this.gameCharacter = gameCharacter; }
    public Profession getProfession() { return profession; }
    public void setProfession(Profession profession) { this.profession = profession; }
}
