package com.dnf.team.controller;

import com.dnf.team.dto.CharacterDTO;
import com.dnf.team.dto.CharacterProfessionDTO;
import com.dnf.team.service.DnfTeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CharacterController {

    private final DnfTeamService service;

    public CharacterController(DnfTeamService service) {
        this.service = service;
    }

    // All characters (overview)
    @GetMapping("/characters")
    public ResponseEntity<List<CharacterDTO>> getAllCharacters() {
        return ResponseEntity.ok(service.getAllCharacters());
    }

    // Characters by user
    @GetMapping("/users/{userId}/characters")
    public ResponseEntity<List<CharacterDTO>> getCharactersByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getCharactersByUser(userId));
    }

    @GetMapping("/characters/{id}")
    public ResponseEntity<CharacterDTO> getCharacterById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getCharacterById(id));
    }

    @PostMapping("/users/{userId}/characters")
    public ResponseEntity<CharacterDTO> createCharacter(
            @PathVariable Long userId,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(service.createCharacter(
                userId,
                body.get("name"),
                body.get("serverName"),
                body.get("tag")));
    }

    @PutMapping("/characters/{id}")
    public ResponseEntity<CharacterDTO> updateCharacter(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(service.updateCharacterWithTagFlag(
                id,
                body.get("name"),
                body.get("serverName"),
                body.containsKey("tag"),
                body.get("tag")));
    }

    @DeleteMapping("/characters/{id}")
    public ResponseEntity<Void> deleteCharacter(@PathVariable Long id) {
        service.deleteCharacter(id);
        return ResponseEntity.noContent().build();
    }

    // Character Professions
    @GetMapping("/characters/{characterId}/professions")
    public ResponseEntity<List<CharacterProfessionDTO>> getCharacterProfessions(
            @PathVariable Long characterId) {
        return ResponseEntity.ok(service.getCharacterProfessions(characterId));
    }

    @PostMapping("/characters/{characterId}/professions")
    public ResponseEntity<CharacterProfessionDTO> addProfessionToCharacter(
            @PathVariable Long characterId,
            @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(service.addProfessionToCharacter(
                characterId,
                Long.valueOf(body.get("professionId").toString()),
                body.get("level") != null ? toInteger(body.get("level")) : null,
                body.get("magicResistance") != null ? toInteger(body.get("magicResistance")) : null));
    }

    @PutMapping("/character-professions/{id}")
    public ResponseEntity<CharacterProfessionDTO> updateCharacterProfession(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(service.updateCharacterProfession(
                id,
                body.get("level") != null ? toInteger(body.get("level")) : null,
                body.get("magicResistance") != null ? toInteger(body.get("magicResistance")) : null,
                body.get("isActive") != null ? Boolean.valueOf(body.get("isActive").toString()) : null));
    }

    private Integer toInteger(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            String str = value.toString().trim();
            if (str.contains(".")) {
                return (int) Math.round(Double.parseDouble(str));
            }
            return Integer.valueOf(str);
        } catch (Exception e) {
            return 0;
        }
    }

    @DeleteMapping("/character-professions/{id}")
    public ResponseEntity<Void> deleteCharacterProfession(@PathVariable Long id) {
        service.deleteCharacterProfession(id);
        return ResponseEntity.noContent().build();
    }
}
