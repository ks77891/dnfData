package com.dnf.team.dto;

public class CharacterProfessionDTO {
    private Long id;
    private Integer level;
    private Integer magicResistance;
    private Boolean isActive;
    private Long characterId;
    private Long professionId;
    private String professionName;
    private String professionCategory;
    private String professionColor;
    private String professionIcon;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }
    public Integer getMagicResistance() { return magicResistance; }
    public void setMagicResistance(Integer magicResistance) { this.magicResistance = magicResistance; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public Long getCharacterId() { return characterId; }
    public void setCharacterId(Long characterId) { this.characterId = characterId; }
    public Long getProfessionId() { return professionId; }
    public void setProfessionId(Long professionId) { this.professionId = professionId; }
    public String getProfessionName() { return professionName; }
    public void setProfessionName(String professionName) { this.professionName = professionName; }
    public String getProfessionCategory() { return professionCategory; }
    public void setProfessionCategory(String professionCategory) { this.professionCategory = professionCategory; }
    public String getProfessionColor() { return professionColor; }
    public void setProfessionColor(String professionColor) { this.professionColor = professionColor; }
    public String getProfessionIcon() { return professionIcon; }
    public void setProfessionIcon(String professionIcon) { this.professionIcon = professionIcon; }
}
