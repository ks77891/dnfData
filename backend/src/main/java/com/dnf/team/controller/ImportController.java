package com.dnf.team.controller;

import com.dnf.team.dto.ProfessionDTO;
import com.dnf.team.dto.UserDTO;
import com.dnf.team.service.DnfTeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ImportController {

    private final DnfTeamService service;

    public ImportController(DnfTeamService service) {
        this.service = service;
    }

    @PostMapping("/batch-import")
    public ResponseEntity<List<Map<String, Object>>> batchImport(
            @RequestBody List<Map<String, Object>> items) {
        List<Map<String, Object>> results = new ArrayList<>();
        for (Map<String, Object> item : items) {
            String username = item.get("username") != null ? item.get("username").toString() : "";
            String characterName = item.get("characterName") != null ? item.get("characterName").toString() : "";
            String professionInput = item.get("professionInput") != null ? item.get("professionInput").toString() : "";
            Integer magicResistance = item.get("magicResistance") != null
                    ? Integer.valueOf(item.get("magicResistance").toString()) : null;
            results.add(service.batchImportItem(username, characterName, professionInput, magicResistance));
        }
        return ResponseEntity.ok(results);
    }

    @GetMapping("/professions/search")
    public ResponseEntity<List<ProfessionDTO>> searchProfessions(@RequestParam(required = false) String q) {
        return ResponseEntity.ok(service.searchProfessions(q));
    }
}
