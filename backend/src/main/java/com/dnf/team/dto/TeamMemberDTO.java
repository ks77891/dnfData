package com.dnf.team.dto;

public class TeamMemberDTO {
    private Long id;
    private Long teamId;
    private Long characterProfessionId;
    private String characterName;
    private String username;
    private String professionName;
    private String professionColor;
    private String professionIcon;
    private Integer level;
    private Integer magicResistance;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }
    public Long getCharacterProfessionId() { return characterProfessionId; }
    public void setCharacterProfessionId(Long characterProfessionId) { this.characterProfessionId = characterProfessionId; }
    public String getCharacterName() { return characterName; }
    public void setCharacterName(String characterName) { this.characterName = characterName; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getProfessionName() { return professionName; }
    public void setProfessionName(String professionName) { this.professionName = professionName; }
    public String getProfessionColor() { return professionColor; }
    public void setProfessionColor(String professionColor) { this.professionColor = professionColor; }
    public String getProfessionIcon() { return professionIcon; }
    public void setProfessionIcon(String professionIcon) { this.professionIcon = professionIcon; }
    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }
    public Integer getMagicResistance() { return magicResistance; }
    public void setMagicResistance(Integer magicResistance) { this.magicResistance = magicResistance; }
}
