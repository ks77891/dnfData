package com.dnf.team.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CharacterDTO {
    private Long id;
    private String name;
    private String serverName;
    private String tag;
    private LocalDateTime createdAt;
    private Long userId;
    private String username;
    private List<CharacterProfessionDTO> characterProfessions;
    private int professionCount;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getServerName() { return serverName; }
    public void setServerName(String serverName) { this.serverName = serverName; }
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public List<CharacterProfessionDTO> getCharacterProfessions() { return characterProfessions; }
    public void setCharacterProfessions(List<CharacterProfessionDTO> characterProfessions) { this.characterProfessions = characterProfessions; }
    public int getProfessionCount() { return professionCount; }
    public void setProfessionCount(int professionCount) { this.professionCount = professionCount; }
}
