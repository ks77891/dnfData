package com.dnf.team.controller;

import com.dnf.team.dto.ProfessionDTO;
import com.dnf.team.service.DnfTeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/professions")
public class ProfessionController {

    private final DnfTeamService service;

    public ProfessionController(DnfTeamService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ProfessionDTO>> getAllProfessions() {
        return ResponseEntity.ok(service.getAllProfessions());
    }

    @PostMapping
    public ResponseEntity<ProfessionDTO> createProfession(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(service.createProfession(
                body.get("name"),
                body.get("category"),
                body.get("color"),
                body.get("icon"),
                body.get("description")));
    }
}
