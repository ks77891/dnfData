package com.dnf.team.service;

import com.dnf.team.dto.*;
import com.dnf.team.entity.*;
import com.dnf.team.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DnfTeamService {

    private final UserRepository userRepository;
    private final GameCharacterRepository gameCharacterRepository;
    private final CharacterProfessionRepository characterProfessionRepository;
    private final ProfessionRepository professionRepository;
    private final TeamRepository teamRepository;

    public DnfTeamService(UserRepository userRepository,
                          GameCharacterRepository gameCharacterRepository,
                          CharacterProfessionRepository characterProfessionRepository,
                          ProfessionRepository professionRepository,
                          TeamRepository teamRepository) {
        this.userRepository = userRepository;
        this.gameCharacterRepository = gameCharacterRepository;
        this.characterProfessionRepository = characterProfessionRepository;
        this.professionRepository = professionRepository;
        this.teamRepository = teamRepository;
    }

    // ==================== Users ====================

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + id));
        return toUserDTO(user);
    }

    @Transactional
    public UserDTO createUser(String username) {
        User user = new User();
        user.setUsername(username);
        user = userRepository.save(user);
        return toUserDTO(user);
    }

    @Transactional
    public UserDTO updateUser(Long id, String username) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + id));
        user.setUsername(username);
        return toUserDTO(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // ==================== Characters ====================

    public List<CharacterDTO> getCharactersByUser(Long userId) {
        return gameCharacterRepository.findByUserId(userId).stream()
                .map(this::toCharacterDTO)
                .collect(Collectors.toList());
    }

    public CharacterDTO getCharacterById(Long id) {
        GameCharacter character = gameCharacterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("角色不存在: " + id));
        return toCharacterDTO(character);
    }

    @Transactional
    public CharacterDTO createCharacter(Long userId, String name, String serverName, String tag) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));
        GameCharacter character = new GameCharacter();
        character.setName(name);
        character.setServerName(serverName);
        character.setTag(tag);
        character.setUser(user);
        character = gameCharacterRepository.save(character);
        return toCharacterDTO(character);
    }

    @Transactional
    public CharacterDTO updateCharacterWithTagFlag(Long id, Object nameObj, Object serverNameObj, boolean tagSet, Object tagObj) {
        GameCharacter character = gameCharacterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("角色不存在: " + id));
        String name = nameObj != null ? nameObj.toString() : null;
        String serverName = serverNameObj != null ? serverNameObj.toString() : null;
        if (name != null && !name.isEmpty()) character.setName(name);
        if (serverName != null && !serverName.isEmpty()) character.setServerName(serverName);
        if (tagSet) {
            character.setTag(tagObj != null ? tagObj.toString() : null);
        }
        return toCharacterDTO(gameCharacterRepository.save(character));
    }

    @Transactional
    public CharacterDTO updateCharacter(Long id, String name, String serverName, String tag) {
        GameCharacter character = gameCharacterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("角色不存在: " + id));
        if (name != null && !name.isEmpty()) character.setName(name);
        if (serverName != null && !serverName.isEmpty()) character.setServerName(serverName);
        if (tag != null) character.setTag(tag);
        return toCharacterDTO(gameCharacterRepository.save(character));
    }

    @Transactional
    public void deleteCharacter(Long id) {
        gameCharacterRepository.deleteById(id);
    }

    // ==================== Character Professions ====================

    @Transactional
    public CharacterProfessionDTO addProfessionToCharacter(Long characterId, Long professionId, Integer level, Integer magicResistance) {
        GameCharacter character = gameCharacterRepository.findById(characterId)
                .orElseThrow(() -> new RuntimeException("角色不存在: " + characterId));
        Profession profession = professionRepository.findById(professionId)
                .orElseThrow(() -> new RuntimeException("职业不存在: " + professionId));

        CharacterProfession cp = new CharacterProfession();
        cp.setGameCharacter(character);
        cp.setProfession(profession);
        cp.setLevel(level != null ? level : 1);
        cp.setMagicResistance(magicResistance != null ? magicResistance : 0);
        cp.setIsActive(true);
        cp = characterProfessionRepository.save(cp);
        return toCharacterProfessionDTO(cp);
    }

    @Transactional
    public CharacterProfessionDTO updateCharacterProfession(Long id, Integer level, Integer magicResistance, Boolean isActive) {
        CharacterProfession cp = characterProfessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("角色职业记录不存在: " + id));
        if (level != null) cp.setLevel(level);
        if (magicResistance != null) cp.setMagicResistance(magicResistance);
        if (isActive != null) cp.setIsActive(isActive);
        cp = characterProfessionRepository.save(cp);
        return toCharacterProfessionDTO(cp);
    }

    @Transactional
    public void deleteCharacterProfession(Long id) {
        characterProfessionRepository.deleteById(id);
    }

    public List<CharacterProfessionDTO> getCharacterProfessions(Long characterId) {
        return characterProfessionRepository.findByGameCharacterId(characterId).stream()
                .map(this::toCharacterProfessionDTO)
                .collect(Collectors.toList());
    }

    // ==================== Professions ====================

    public List<ProfessionDTO> getAllProfessions() {
        return professionRepository.findAll().stream()
                .map(this::toProfessionDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProfessionDTO createProfession(String name, String category, String color, String icon, String description) {
        Profession profession = new Profession();
        profession.setName(name);
        profession.setCategory(category);
        profession.setColor(color);
        profession.setIcon(icon);
        profession.setDescription(description);
        profession = professionRepository.save(profession);
        return toProfessionDTO(profession);
    }

    // ==================== Teams ====================

    public List<TeamDTO> getAllTeams() {
        return teamRepository.findAll().stream()
                .map(this::toTeamDTO)
                .collect(Collectors.toList());
    }

    public TeamDTO getTeamById(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("编队不存在: " + id));
        return toTeamDTO(team);
    }

    @Transactional
    public TeamDTO createTeam(String name, String dungeonName, Integer minMagicResistance) {
        Team team = new Team();
        team.setName(name);
        team.setDungeonName(dungeonName);
        team.setMinMagicResistance(minMagicResistance);
        team = teamRepository.save(team);
        return toTeamDTO(team);
    }

    @Transactional
    public void deleteTeam(Long id) {
        teamRepository.deleteById(id);
    }

    @Transactional
    public TeamDTO addMemberToTeam(Long teamId, Long characterProfessionId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("编队不存在: " + teamId));
        CharacterProfession cp = characterProfessionRepository.findById(characterProfessionId)
                .orElseThrow(() -> new RuntimeException("角色职业记录不存在: " + characterProfessionId));

        TeamMember member = new TeamMember();
        member.setTeam(team);
        member.setCharacterProfession(cp);
        team.getTeamMembers().add(member);
        team = teamRepository.save(team);
        return toTeamDTO(team);
    }

    @Transactional
    public void removeMemberFromTeam(Long teamId, Long memberId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("编队不存在: " + teamId));
        team.getTeamMembers().removeIf(m -> m.getId().equals(memberId));
        teamRepository.save(team);
    }

    // ==================== Smart Matching ====================

    public List<TeamDTO> smartMatch(Integer teamSize, Integer minMagicResistance, String dungeonName) {
        List<CharacterProfession> candidates = characterProfessionRepository
                .findAllActiveOrderByMagicResistanceDesc();

        if (minMagicResistance != null) {
            candidates = candidates.stream()
                    .filter(cp -> cp.getMagicResistance() >= minMagicResistance)
                    .collect(Collectors.toList());
        }

        int size = teamSize != null ? teamSize : 4;
        List<TeamDTO> result = new ArrayList<>();

        // Greedy matching: group by magic resistance levels
        List<CharacterProfession> remaining = new ArrayList<>(candidates);

        while (!remaining.isEmpty()) {
            List<CharacterProfession> team = new ArrayList<>();
            CharacterProfession first = remaining.remove(0);
            team.add(first);

            // Find members with closest magic resistance
            int baseMr = first.getMagicResistance();
            Iterator<CharacterProfession> iter = remaining.iterator();
            while (iter.hasNext() && team.size() < size) {
                CharacterProfession cp = iter.next();
                if (Math.abs(cp.getMagicResistance() - baseMr) <= 3000) {
                    team.add(cp);
                    iter.remove();
                }
            }
            result.add(toTeamDTOFromMatch(team, result.size() + 1, dungeonName, minMagicResistance));
        }

        return result;
    }

    private TeamDTO toTeamDTOFromMatch(List<CharacterProfession> group, int index, String dungeonName, Integer minMagicResistance) {
        TeamDTO dto = new TeamDTO();
        dto.setId(-1L);
        dto.setName("智能编队 #" + index);
        dto.setDungeonName(dungeonName != null ? dungeonName : "未知副本");
        dto.setMinMagicResistance(minMagicResistance);

        List<TeamMemberDTO> memberDTOs = group.stream().map(cp -> {
            TeamMemberDTO md = new TeamMemberDTO();
            md.setCharacterProfessionId(cp.getId());
            md.setCharacterName(cp.getGameCharacter().getName());
            md.setUsername(cp.getGameCharacter().getUser().getUsername());
            md.setProfessionName(cp.getProfession().getName());
            md.setProfessionColor(cp.getProfession().getColor());
            md.setProfessionIcon(cp.getProfession().getIcon());
            md.setLevel(cp.getLevel());
            md.setMagicResistance(cp.getMagicResistance());
            return md;
        }).collect(Collectors.toList());

        dto.setTeamMembers(memberDTOs);
        dto.setMemberCount(memberDTOs.size());
        dto.setTotalMagicResistance(memberDTOs.stream().mapToInt(TeamMemberDTO::getMagicResistance).sum());
        if (memberDTOs.size() > 0) {
            dto.setAvgMagicResistance(Math.round(dto.getTotalMagicResistance() * 1.0 / memberDTOs.size() * 10.0) / 10.0);
        }
        return dto;
    }

    // ==================== All Characters (for overview) ====================

    public List<CharacterDTO> getAllCharacters() {
        return gameCharacterRepository.findAll().stream()
                .map(this::toCharacterDTO)
                .collect(Collectors.toList());
    }

    // ==================== DTO Mappers ====================

    private UserDTO toUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setCharacters(user.getCharacters().stream()
                .map(this::toCharacterDTO)
                .collect(Collectors.toList()));
        dto.setCharacterCount(user.getCharacters().size());
        return dto;
    }

    private CharacterDTO toCharacterDTO(GameCharacter character) {
        CharacterDTO dto = new CharacterDTO();
        dto.setId(character.getId());
        dto.setName(character.getName());
        dto.setServerName(character.getServerName());
        dto.setTag(character.getTag());
        dto.setCreatedAt(character.getCreatedAt());
        dto.setUserId(character.getUser().getId());
        dto.setUsername(character.getUser().getUsername());
        dto.setCharacterProfessions(character.getCharacterProfessions().stream()
                .map(this::toCharacterProfessionDTO)
                .collect(Collectors.toList()));
        dto.setProfessionCount(character.getCharacterProfessions().size());
        return dto;
    }

    private CharacterProfessionDTO toCharacterProfessionDTO(CharacterProfession cp) {
        CharacterProfessionDTO dto = new CharacterProfessionDTO();
        dto.setId(cp.getId());
        dto.setLevel(cp.getLevel());
        dto.setMagicResistance(cp.getMagicResistance());
        dto.setIsActive(cp.getIsActive());
        dto.setCharacterId(cp.getGameCharacter().getId());
        dto.setProfessionId(cp.getProfession().getId());
        dto.setProfessionName(cp.getProfession().getName());
        dto.setProfessionCategory(cp.getProfession().getCategory());
        dto.setProfessionColor(cp.getProfession().getColor());
        dto.setProfessionIcon(cp.getProfession().getIcon());
        return dto;
    }

    private ProfessionDTO toProfessionDTO(Profession profession) {
        ProfessionDTO dto = new ProfessionDTO();
        dto.setId(profession.getId());
        dto.setName(profession.getName());
        dto.setCategory(profession.getCategory());
        dto.setColor(profession.getColor());
        dto.setIcon(profession.getIcon());
        dto.setDescription(profession.getDescription());
        return dto;
    }

    private TeamDTO toTeamDTO(Team team) {
        TeamDTO dto = new TeamDTO();
        dto.setId(team.getId());
        dto.setName(team.getName());
        dto.setDungeonName(team.getDungeonName());
        dto.setMinMagicResistance(team.getMinMagicResistance());
        dto.setCreatedAt(team.getCreatedAt());
        dto.setTeamMembers(team.getTeamMembers().stream()
                .map(this::toTeamMemberDTO)
                .collect(Collectors.toList()));
        dto.setMemberCount(dto.getTeamMembers().size());
        dto.setTotalMagicResistance(dto.getTeamMembers().stream()
                .mapToInt(TeamMemberDTO::getMagicResistance).sum());
        if (dto.getMemberCount() > 0) {
            dto.setAvgMagicResistance(Math.round(dto.getTotalMagicResistance() * 1.0 / dto.getMemberCount() * 10.0) / 10.0);
        }
        return dto;
    }

    private TeamMemberDTO toTeamMemberDTO(TeamMember member) {
        TeamMemberDTO dto = new TeamMemberDTO();
        dto.setId(member.getId());
        dto.setTeamId(member.getTeam().getId());
        dto.setCharacterProfessionId(member.getCharacterProfession().getId());
        dto.setCharacterName(member.getCharacterProfession().getGameCharacter().getName());
        dto.setUsername(member.getCharacterProfession().getGameCharacter().getUser().getUsername());
        dto.setProfessionName(member.getCharacterProfession().getProfession().getName());
        dto.setProfessionColor(member.getCharacterProfession().getProfession().getColor());
        dto.setProfessionIcon(member.getCharacterProfession().getProfession().getIcon());
        dto.setLevel(member.getCharacterProfession().getLevel());
        dto.setMagicResistance(member.getCharacterProfession().getMagicResistance());
        return dto;
    }

    // ==================== Batch Import ====================

    public UserDTO findOrCreateUser(String username) {
        return userRepository.findByUsername(username)
                .map(this::toUserDTO)
                .orElseGet(() -> createUser(username));
    }

    public List<ProfessionDTO> searchProfessions(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProfessions();
        }
        return professionRepository.findByNameContaining(keyword).stream()
                .map(this::toProfessionDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Map<String, Object> batchImportItem(String username, String characterName, String professionInput, Integer magicResistance) {
        Map<String, Object> result = new HashMap<>();
        result.put("line", String.format("%s/%s/%s/%d", username, characterName, professionInput, magicResistance));
        try {
            // 1. Find or create user
            UserDTO user = findOrCreateUser(username);
            result.put("userId", user.getId());

            // 2. Find or create character
            GameCharacter character = gameCharacterRepository.findByUserIdAndName(user.getId(), characterName)
                    .orElseGet(() -> {
                        GameCharacter c = new GameCharacter();
                        c.setName(characterName);
                        c.setUser(userRepository.findById(user.getId()).orElseThrow());
                        return gameCharacterRepository.save(c);
                    });
            result.put("characterId", character.getId());

            // 3. Find profession
            String professionName = professionInput;
            String tag = null;
            // Check for tag patterns in professionInput
            String[] tagPatterns = {"1c", "2c", "3c", "4c", "5c", "6c", "垃圾号"};
            for (String tp : tagPatterns) {
                if (professionInput.contains(tp)) {
                    tag = tp;
                    professionName = professionInput.replace(tp, "");
                    break;
                }
            }

            // Set tag on character
            if (tag != null) {
                character.setTag(tag);
                gameCharacterRepository.save(character);
            }
            result.put("tag", tag);

            // Fuzzy match profession
            Profession profession = null;
            if (professionName != null && !professionName.isEmpty()) {
                // Try exact match
                profession = professionRepository.findByName(professionName).orElse(null);
                if (profession == null) {
                    // Try containing match
                    List<Profession> matches = professionRepository.findByNameContaining(professionName);
                    if (!matches.isEmpty()) {
                        profession = matches.get(0);
                    } else {
                        // Try reverse matching - escape special characters
                        String regex = professionName.chars()
                                .mapToObj(c -> java.util.regex.Matcher.quoteReplacement(String.valueOf((char) c)))
                                .collect(java.util.stream.Collectors.joining(".*"));
                        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
                        for (Profession p : professionRepository.findAll()) {
                            if (pattern.matcher(p.getName()).find()) {
                                profession = p;
                                break;
                            }
                        }
                    }
                }
            }

            if (profession == null) {
                result.put("status", "skip");
                result.put("message", "未找到匹配的职业: " + professionInput);
                return result;
            }
            result.put("professionId", profession.getId());
            result.put("professionName", profession.getName());

            // 4. Add character profession
            if (magicResistance != null) {
                CharacterProfession cp = new CharacterProfession();
                cp.setGameCharacter(character);
                cp.setProfession(profession);
                cp.setLevel(80);
                cp.setMagicResistance(magicResistance);
                cp.setIsActive(true);
                characterProfessionRepository.save(cp);
            }

            result.put("status", "ok");
            result.put("message", "导入成功");
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
        }
        return result;
    }
}


