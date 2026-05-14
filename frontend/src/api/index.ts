import axios from 'axios'
import type { User, GameCharacter, CharacterProfession, Profession, Team } from '@/types'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

// ===== Users =====
export const getUsers = () => api.get<User[]>('/users')
export const getUser = (id: number) => api.get<User>(`/users/${id}`)
export const getUserByName = (username: string) => api.get<User>(`/users/by-name/${encodeURIComponent(username)}`)
export const createUser = (username: string) => api.post<User>('/users', { username })
export const updateUser = (id: number, username: string) => api.put<User>(`/users/${id}`, { username })
export const deleteUser = (id: number) => api.delete(`/users/${id}`)

// ===== Batch Import =====
export const batchImport = (items: any[]) => api.post<any[]>('/batch-import', items)
export const searchProfessions = (q?: string) => api.get<any[]>('/professions/search', { params: { q } })

// ===== Characters =====
export const getAllCharacters = () => api.get<GameCharacter[]>('/characters')
export const getUserCharacters = (userId: number) => api.get<GameCharacter[]>(`/users/${userId}/characters`)
export const getCharacter = (id: number) => api.get<GameCharacter>(`/characters/${id}`)
export const createCharacter = (userId: number, name: string, serverName: string, tag?: string) =>
  api.post<GameCharacter>(`/users/${userId}/characters`, { name, serverName, tag })
export const updateCharacter = (id: number, name: string, serverName: string, tag?: string | null) =>
  api.put<GameCharacter>(`/characters/${id}`, { name, serverName, tag })
export const deleteCharacter = (id: number) => api.delete(`/characters/${id}`)

// ===== Character Professions =====
export const getCharacterProfessions = (characterId: number) =>
  api.get<CharacterProfession[]>(`/characters/${characterId}/professions`)
export const addCharacterProfession = (characterId: number, professionId: number, level: number, magicResistance: number) =>
  api.post<CharacterProfession>(`/characters/${characterId}/professions`, { professionId, level, magicResistance })
export const updateCharacterProfession = (id: number, data: { level?: number; magicResistance?: number; isActive?: boolean }) =>
  api.put<CharacterProfession>(`/character-professions/${id}`, data)
export const deleteCharacterProfession = (id: number) => api.delete(`/character-professions/${id}`)

// ===== Professions =====
export const getProfessions = () => api.get<Profession[]>('/professions')
export const createProfession = (data: { name: string; category: string; color: string; icon: string; description: string }) =>
  api.post<Profession>('/professions', data)

// ===== Teams =====
export const getTeams = () => api.get<Team[]>('/teams')
export const getTeam = (id: number) => api.get<Team>(`/teams/${id}`)
export const createTeam = (name: string, dungeonName?: string, minMagicResistance?: number) =>
  api.post<Team>('/teams', { name, dungeonName, minMagicResistance })
export const deleteTeam = (id: number) => api.delete(`/teams/${id}`)
export const addTeamMember = (teamId: number, characterProfessionId: number) =>
  api.post<Team>(`/teams/${teamId}/members`, { characterProfessionId })
export const removeTeamMember = (teamId: number, memberId: number) =>
  api.delete(`/teams/${teamId}/members/${memberId}`)

// ===== Smart Match =====
export const smartMatch = (params: { teamSize?: number; minMagicResistance?: number; dungeonName?: string }) =>
  api.post<Team[]>('/teams/smart-match', params)
