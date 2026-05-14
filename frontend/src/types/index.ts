// === 数据类型定义 ===

export interface User {
  id: number
  username: string
  createdAt: string
  characters: GameCharacter[]
  characterCount: number
}

export interface GameCharacter {
  id: number
  name: string
  serverName: string
  tag: string | null
  createdAt: string
  userId: number
  username: string
  characterProfessions: CharacterProfession[]
  professionCount: number
}

export interface CharacterProfession {
  id: number
  level: number
  magicResistance: number
  isActive: boolean
  characterId: number
  professionId: number
  professionName: string
  professionCategory: string
  professionColor: string
  professionIcon: string
}

export interface Profession {
  id: number
  name: string
  category: string
  color: string
  icon: string
  description: string
}

export interface Team {
  id: number
  name: string
  dungeonName: string
  minMagicResistance: number | null
  createdAt: string
  teamMembers: TeamMember[]
  memberCount: number
  totalMagicResistance: number
  avgMagicResistance: number
}

export interface TeamMember {
  id: number
  teamId: number
  characterProfessionId: number
  characterName: string
  username: string
  professionName: string
  professionColor: string
  professionIcon: string
  level: number
  magicResistance: number
}
