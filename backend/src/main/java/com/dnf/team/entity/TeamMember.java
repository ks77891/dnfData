package com.dnf.team.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "dnf_team_member")
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_profession_id", nullable = false)
    private CharacterProfession characterProfession;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }
    public CharacterProfession getCharacterProfession() { return characterProfession; }
    public void setCharacterProfession(CharacterProfession characterProfession) { this.characterProfession = characterProfession; }
}
