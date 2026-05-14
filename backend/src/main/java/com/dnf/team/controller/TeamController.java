package com.dnf.team.controller;

import com.dnf.team.dto.TeamDTO;
import com.dnf.team.service.DnfTeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final DnfTeamService service;

    public TeamController(DnfTeamService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TeamDTO>> getAllTeams() {
        return ResponseEntity.ok(service.getAllTeams());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamDTO> getTeamById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getTeamById(id));
    }

    @PostMapping
    public ResponseEntity<TeamDTO> createTeam(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(service.createTeam(
                (String) body.get("name"),
                (String) body.get("dungeonName"),
                body.get("minMagicResistance") != null
                        ? Integer.valueOf(body.get("minMagicResistance").toString())
                        : null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        service.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{teamId}/members")
    public ResponseEntity<TeamDTO> addMember(
            @PathVariable Long teamId,
            @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(service.addMemberToTeam(
                teamId,
                Long.valueOf(body.get("characterProfessionId").toString())));
    }

    @DeleteMapping("/{teamId}/members/{memberId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long teamId,
            @PathVariable Long memberId) {
        service.removeMemberFromTeam(teamId, memberId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/smart-match")
    public ResponseEntity<List<TeamDTO>> smartMatch(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(service.smartMatch(
                body.get("teamSize") != null
                        ? Integer.valueOf(body.get("teamSize").toString())
                        : null,
                body.get("minMagicResistance") != null
                        ? Integer.valueOf(body.get("minMagicResistance").toString())
                        : null,
                (String) body.get("dungeonName")));
    }
}
