package com.dnf.team.dto;

import java.time.LocalDateTime;
import java.util.List;

public class TeamDTO {
    private Long id;
    private String name;
    private String dungeonName;
    private Integer minMagicResistance;
    private LocalDateTime createdAt;
    private List<TeamMemberDTO> teamMembers;
    private int memberCount;
    private int totalMagicResistance;
    private double avgMagicResistance;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDungeonName() { return dungeonName; }
    public void setDungeonName(String dungeonName) { this.dungeonName = dungeonName; }
    public Integer getMinMagicResistance() { return minMagicResistance; }
    public void setMinMagicResistance(Integer minMagicResistance) { this.minMagicResistance = minMagicResistance; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<TeamMemberDTO> getTeamMembers() { return teamMembers; }
    public void setTeamMembers(List<TeamMemberDTO> teamMembers) { this.teamMembers = teamMembers; }
    public int getMemberCount() { return memberCount; }
    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }
    public int getTotalMagicResistance() { return totalMagicResistance; }
    public void setTotalMagicResistance(int totalMagicResistance) { this.totalMagicResistance = totalMagicResistance; }
    public double getAvgMagicResistance() { return avgMagicResistance; }
    public void setAvgMagicResistance(double avgMagicResistance) { this.avgMagicResistance = avgMagicResistance; }
}
