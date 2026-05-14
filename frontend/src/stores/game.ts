import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { User, Profession, Team } from '@/types'
import * as api from '@/api'

export const useGameStore = defineStore('game', () => {
  const users = ref<User[]>([])
  const professions = ref<Profession[]>([])
  const teams = ref<Team[]>([])
  const loading = ref(false)
  const toast = ref<{ message: string; type: 'success' | 'error' | 'info' } | null>(null)

  let toastTimer: ReturnType<typeof setTimeout> | null = null

  function showToast(message: string, type: 'success' | 'error' | 'info' = 'info') {
    toast.value = { message, type }
    if (toastTimer) clearTimeout(toastTimer)
    toastTimer = setTimeout(() => { toast.value = null }, 3000)
  }

  // === Users ===
  async function fetchUsers() {
    try {
      const res = await api.getUsers()
      users.value = res.data
    } catch (e: any) {
      showToast('获取用户列表失败: ' + (e.response?.data?.message || e.message), 'error')
    }
  }

  async function addUser(username: string) {
    try {
      const res = await api.createUser(username)
      users.value.push(res.data)
      showToast('用户添加成功', 'success')
      return res.data
    } catch (e: any) {
      showToast('添加用户失败: ' + (e.response?.data?.message || e.message), 'error')
      return null
    }
  }

  async function editUser(id: number, username: string) {
    try {
      const res = await api.updateUser(id, username)
      const idx = users.value.findIndex(u => u.id === id)
      if (idx >= 0) users.value[idx] = res.data
      showToast('用户更新成功', 'success')
    } catch (e: any) {
      showToast('更新用户失败: ' + (e.response?.data?.message || e.message), 'error')
    }
  }

  async function removeUser(id: number) {
    try {
      await api.deleteUser(id)
      users.value = users.value.filter(u => u.id !== id)
      showToast('用户已删除', 'info')
    } catch (e: any) {
      showToast('删除用户失败: ' + (e.response?.data?.message || e.message), 'error')
    }
  }

  // === Characters ===
  async function addCharacter(userId: number, name: string, serverName: string, tag?: string) {
    try {
      const res = await api.createCharacter(userId, name, serverName, tag)
      const user = users.value.find(u => u.id === userId)
      if (user) {
        user.characters.push(res.data)
        user.characterCount = user.characters.length
      }
      showToast('角色添加成功', 'success')
      return res.data
    } catch (e: any) {
      showToast('添加角色失败: ' + (e.response?.data?.message || e.message), 'error')
      return null
    }
  }

  async function editCharacter(id: number, name: string, serverName: string, tag?: string | null) {
    try {
      const res = await api.updateCharacter(id, name, serverName, tag)
      for (const user of users.value) {
        const idx = user.characters.findIndex(c => c.id === id)
        if (idx >= 0) {
          user.characters[idx] = res.data
          break
        }
      }
      showToast('角色更新成功', 'success')
    } catch (e: any) {
      showToast('更新角色失败: ' + (e.response?.data?.message || e.message), 'error')
    }
  }

  async function removeCharacter(id: number) {
    try {
      await api.deleteCharacter(id)
      for (const user of users.value) {
        const before = user.characters.length
        user.characters = user.characters.filter(c => c.id !== id)
        if (user.characters.length !== before) {
          user.characterCount = user.characters.length
          break
        }
      }
      showToast('角色已删除', 'info')
    } catch (e: any) {
      showToast('删除角色失败: ' + (e.response?.data?.message || e.message), 'error')
    }
  }

  // === Professions ===
  async function fetchProfessions() {
    try {
      const res = await api.getProfessions()
      professions.value = res.data
    } catch (e: any) {
      showToast('获取职业列表失败', 'error')
    }
  }

  // === Teams ===
  async function fetchTeams() {
    try {
      const res = await api.getTeams()
      teams.value = res.data
    } catch (e: any) {
      showToast('获取编队列表失败', 'error')
    }
  }

  async function addTeam(name: string, dungeonName?: string, minMagicResistance?: number) {
    try {
      const res = await api.createTeam(name, dungeonName, minMagicResistance)
      teams.value.push(res.data)
      showToast('编队创建成功', 'success')
      return res.data
    } catch (e: any) {
      showToast('创建编队失败: ' + (e.response?.data?.message || e.message), 'error')
      return null
    }
  }

  async function removeTeam(id: number) {
    try {
      await api.deleteTeam(id)
      teams.value = teams.value.filter(t => t.id !== id)
      showToast('编队已删除', 'info')
    } catch (e: any) {
      showToast('删除编队失败', 'error')
    }
  }

  async function addTeamMemberAction(teamId: number, characterProfessionId: number) {
    try {
      const res = await api.addTeamMember(teamId, characterProfessionId)
      const idx = teams.value.findIndex(t => t.id === teamId)
      if (idx >= 0) teams.value[idx] = res.data
      showToast('成员已加入编队', 'success')
    } catch (e: any) {
      showToast('添加成员失败', 'error')
    }
  }

  async function removeTeamMemberAction(teamId: number, memberId: number) {
    try {
      const res = await api.removeTeamMember(teamId, memberId)
      // Refetch teams to get updated data
      await fetchTeams()
      showToast('成员已移除', 'info')
    } catch (e: any) {
      showToast('移除成员失败', 'error')
    }
  }

  async function smartMatchAction(params: { teamSize?: number; minMagicResistance?: number; dungeonName?: string }) {
    try {
      const res = await api.smartMatch(params)
      return res.data
    } catch (e: any) {
      showToast('智能匹配失败', 'error')
      return []
    }
  }

  async function loadAll() {
    loading.value = true
    await Promise.all([fetchUsers(), fetchProfessions(), fetchTeams()])
    loading.value = false
  }

  return {
    users, professions, teams, loading, toast,
    showToast, loadAll, fetchUsers, fetchProfessions, fetchTeams,
    addUser, editUser, removeUser,
    addCharacter, editCharacter, removeCharacter,
    addTeam, removeTeam, addTeamMember: addTeamMemberAction, removeTeamMember: removeTeamMemberAction,
    smartMatch: smartMatchAction,
  }
})
