<script setup lang="ts">
import { ref, computed } from 'vue'
import { useGameStore } from '@/stores/game'
import type { Team, CharacterProfession } from '@/types'

const store = useGameStore()

const activeTab = ref<'teams' | 'smart'>('teams')

// Smart match params
const teamSize = ref(4)
const minMr = ref(0)
const dungeonName = ref('')
const matchResults = ref<Team[]>([])
const showResults = ref(false)

// Create team modal
const showCreateModal = ref(false)
const newTeamName = ref('')
const newTeamDungeon = ref('')
const newTeamMinMr = ref(0)

// Add member modal
const showAddMemberModal = ref(false)
const selectedTeamId = ref<number>(0)

// Computed
const allActiveProfessions = computed(() => {
  const list: { cpId: number; charName: string; userName: string; profName: string; mr: number; color: string; icon: string; level: number }[] = []
  for (const user of store.users) {
    for (const char of user.characters) {
      for (const cp of char.characterProfessions) {
        if (!cp.isActive) continue
        list.push({
          cpId: cp.id,
          charName: char.name,
          userName: user.username,
          profName: cp.professionName,
          mr: cp.magicResistance,
          color: cp.professionColor,
          icon: cp.professionIcon,
          level: cp.level,
        })
      }
    }
  }
  return list.sort((a, b) => b.mr - a.mr)
})

async function handleSmartMatch() {
  showResults.value = true
  const results = await store.smartMatch({
    teamSize: teamSize.value,
    minMagicResistance: minMr.value > 0 ? minMr.value : undefined,
    dungeonName: dungeonName.value || undefined,
  })
  matchResults.value = results
}

async function handleSaveTeam(team: Team) {
  if (!team.dungeonName || !confirm(`保存编队 "${team.name}" 吗？`)) return
  const savedTeam = await store.addTeam(
    team.name,
    team.dungeonName,
    minMr.value > 0 ? minMr.value : undefined,
  )
  if (savedTeam) {
    for (const member of team.teamMembers) {
      await store.addTeamMember(savedTeam.id, member.characterProfessionId)
    }
    await store.fetchTeams()
    store.showToast('编队已保存', 'success')
  }
}

function openCreateModal() {
  newTeamName.value = ''
  newTeamDungeon.value = ''
  newTeamMinMr.value = 0
  showCreateModal.value = true
}

async function handleCreateTeam() {
  if (!newTeamName.value.trim()) return
  await store.addTeam(newTeamName.value.trim(), newTeamDungeon.value || undefined, newTeamMinMr.value > 0 ? newTeamMinMr.value : undefined)
  showCreateModal.value = false
  activeTab.value = 'teams'
}

function openAddMemberModal(teamId: number) {
  selectedTeamId.value = teamId
  showAddMemberModal.value = true
}

async function handleAddMember(cpId: number) {
  await store.addTeamMember(selectedTeamId.value, cpId)
  showAddMemberModal.value = false
}

function getMrLevel(mr: number): { label: string; color: string } {
  if (mr >= 30000) return { label: 'SSS', color: '#FFD700' }
  if (mr >= 25000) return { label: 'SS', color: '#FF6EB4' }
  if (mr >= 20000) return { label: 'S', color: '#69CCF0' }
  if (mr >= 15000) return { label: 'A', color: '#ABD473' }
  if (mr >= 10000) return { label: 'B', color: '#C79C6E' }
  return { label: 'C', color: '#888' }
}
</script>

<template>
  <div class="space-y-6 animate-fade-in">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gradient-arcane">智能编队</h1>
        <p class="text-muted-foreground text-sm mt-1">根据抗魔值智能组队，高效攻略副本</p>
      </div>
      <button class="btn-primary flex items-center gap-2" @click="openCreateModal">
        <span>＋</span>
        <span>创建编队</span>
      </button>
    </div>

    <!-- Tabs -->
    <div class="flex gap-1 bg-surface rounded-lg p-1 w-fit">
      <button
        class="px-4 py-2 rounded-md text-sm font-medium transition-all"
        :class="activeTab === 'teams' ? 'bg-arcane/20 text-arcane' : 'text-muted-foreground hover:text-foreground'"
        @click="activeTab = 'teams'"
      >
        我的编队 ({{ store.teams.length }})
      </button>
      <button
        class="px-4 py-2 rounded-md text-sm font-medium transition-all"
        :class="activeTab === 'smart' ? 'bg-arcane/20 text-arcane' : 'text-muted-foreground hover:text-foreground'"
        @click="activeTab = 'smart'"
      >
        智能匹配
      </button>
    </div>

    <!-- ===== Teams Tab ===== -->
    <div v-if="activeTab === 'teams'">
      <div v-if="store.teams.length === 0" class="card-game p-12 text-center">
        <div class="text-5xl mb-4">🛡️</div>
        <p class="text-muted-foreground">暂无编队</p>
        <p class="text-sm text-muted-foreground mt-1">创建编队或使用智能匹配生成</p>
      </div>

      <div v-else class="space-y-4">
        <div v-for="team in store.teams" :key="team.id" class="card-game p-5">
          <div class="flex items-center justify-between mb-4">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-yellow-500 to-orange-500 flex items-center justify-center text-white font-bold">
                {{ team.name.charAt(0) }}
              </div>
              <div>
                <h3 class="font-semibold">{{ team.name }}</h3>
                <p class="text-xs text-muted-foreground">
                  {{ team.dungeonName || '未指定副本' }}
                  <span v-if="team.minMagicResistance"> · 最低抗魔 {{ team.minMagicResistance }}</span>
                </p>
              </div>
            </div>
            <div class="flex items-center gap-3">
              <div class="text-right">
                <div class="text-sm font-medium">平均 {{ Math.round(team.avgMagicResistance) }}</div>
                <div class="text-xs text-muted-foreground">{{ team.memberCount }} / {{ 4 }} 人</div>
              </div>
              <button
                class="btn-ghost text-xs px-2 py-1 text-red-400 hover:text-red-300"
                @click="store.removeTeam(team.id)"
              >
                🗑️
              </button>
            </div>
          </div>

          <!-- Team Members -->
          <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
            <div
              v-for="member in team.teamMembers"
              :key="member.id"
              class="bg-background/50 rounded-lg p-3 flex items-center gap-3"
            >
              <span class="text-xl">{{ member.professionIcon }}</span>
              <div class="flex-1 min-w-0">
                <div class="text-sm font-medium truncate">{{ member.characterName }}</div>
                <div class="flex items-center gap-1.5">
                  <span class="text-xs text-muted-foreground">{{ member.username }}</span>
                  <span
                    class="text-[10px] px-1 py-0.5 rounded"
                    :style="{ backgroundColor: member.professionColor + '22', color: member.professionColor }"
                  >
                    {{ member.professionName }}
                  </span>
                </div>
              </div>
              <div class="text-right">
                <div class="text-xs font-bold" :style="{ color: getMrLevel(member.magicResistance).color }">
                  {{ member.magicResistance.toLocaleString() }}
                </div>
                <button
                  class="text-[10px] text-muted-foreground hover:text-red-400"
                  @click="store.removeTeamMember(team.id, member.id)"
                >
                  移除
                </button>
              </div>
            </div>

            <!-- Add Member Slot -->
            <button
              class="bg-background/30 rounded-lg p-3 border border-dashed border-border hover:border-arcane/50 flex items-center justify-center gap-2 text-sm text-muted-foreground hover:text-arcane transition-all"
              @click="openAddMemberModal(team.id)"
            >
              <span>＋</span>
              <span>添加成员</span>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- ===== Smart Match Tab ===== -->
    <div v-if="activeTab === 'smart'" class="space-y-6">
      <!-- Params -->
      <div class="card-game p-6">
        <h2 class="text-lg font-semibold mb-4">匹配参数</h2>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <label class="text-xs text-muted-foreground mb-1 block">队伍人数</label>
            <select v-model.number="teamSize" class="select-game">
              <option :value="2">2 人</option>
              <option :value="3">3 人</option>
              <option :value="4">4 人</option>
            </select>
          </div>
          <div>
            <label class="text-xs text-muted-foreground mb-1 block">最低抗魔值</label>
            <input v-model.number="minMr" type="number" min="0" class="input-game" placeholder="0 = 不限制" />
          </div>
          <div>
            <label class="text-xs text-muted-foreground mb-1 block">目标副本 (可选)</label>
            <input v-model="dungeonName" class="input-game" placeholder="如：罗特斯团本" />
          </div>
        </div>
        <button class="btn-primary mt-4 w-full md:w-auto" @click="handleSmartMatch">
          🔍 开始智能匹配
        </button>
      </div>

      <!-- Available Characters -->
      <div class="card-game p-6">
        <h2 class="text-lg font-semibold mb-4">当前可用角色 ({{ allActiveProfessions.length }})</h2>
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-2 max-h-64 overflow-y-auto scrollbar-thin pr-2">
          <div
            v-for="cp in allActiveProfessions"
            :key="cp.cpId"
            class="flex items-center justify-between bg-background/50 rounded-lg px-3 py-2 text-xs"
          >
            <div class="flex items-center gap-2 min-w-0">
              <span>{{ cp.icon }}</span>
              <span class="truncate">{{ cp.charName }}</span>
              <span class="text-muted-foreground">({{ cp.userName }})</span>
            </div>
            <span class="shrink-0 font-medium" :style="{ color: getMrLevel(cp.mr).color }">
              {{ cp.mr.toLocaleString() }}
            </span>
          </div>
        </div>
      </div>

      <!-- Match Results -->
      <div v-if="showResults" class="space-y-4">
        <div class="flex items-center justify-between">
          <h2 class="text-lg font-semibold">匹配结果</h2>
          <span class="text-sm text-muted-foreground">共 {{ matchResults.length }} 个编队</span>
        </div>

        <div v-if="matchResults.length === 0" class="card-game p-8 text-center">
          <div class="text-4xl mb-3">😅</div>
          <p class="text-muted-foreground">没有足够的角色满足匹配条件</p>
          <p class="text-sm text-muted-foreground mt-1">请调整参数或添加更多角色</p>
        </div>

        <div v-for="(team, idx) in matchResults" :key="idx" class="card-game p-5">
          <div class="flex items-center justify-between mb-4">
            <div class="flex items-center gap-3">
              <div class="text-2xl">{{ idx === 0 ? '🥇' : idx === 1 ? '🥈' : idx === 2 ? '🥉' : '🏅' }}</div>
              <div>
                <h3 class="font-semibold">{{ team.name }}</h3>
                <p class="text-xs text-muted-foreground">
                  {{ team.dungeonName }} · 平均抗魔 {{ Math.round(team.avgMagicResistance) }}
                </p>
              </div>
            </div>
            <div class="flex items-center gap-2">
              <span class="badge badge-primary">{{ team.memberCount }} 人</span>
              <button class="btn-primary text-xs px-3 py-1.5" @click="handleSaveTeam(team)">
                保存编队
              </button>
            </div>
          </div>

          <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
            <div
              v-for="member in team.teamMembers"
              :key="member.characterProfessionId"
              class="bg-background/50 rounded-lg p-3 flex items-center gap-3"
            >
              <span class="text-xl">{{ member.professionIcon }}</span>
              <div class="flex-1 min-w-0">
                <div class="text-sm font-medium truncate">{{ member.characterName }}</div>
                <div class="flex items-center gap-1.5">
                  <span class="text-xs text-muted-foreground">{{ member.username }}</span>
                  <span
                    class="text-[10px] px-1 py-0.5 rounded"
                    :style="{ backgroundColor: member.professionColor + '22', color: member.professionColor }"
                  >
                    {{ member.professionName }}
                  </span>
                </div>
              </div>
              <div class="text-right">
                <div class="text-xs">Lv.{{ member.level }}</div>
                <div class="text-xs font-bold" :style="{ color: getMrLevel(member.magicResistance).color }">
                  {{ member.magicResistance.toLocaleString() }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Create Team Modal -->
    <Teleport to="body">
      <div v-if="showCreateModal" class="modal-overlay" @click.self="showCreateModal = false">
        <div class="modal-content">
          <h2 class="text-lg font-semibold mb-1">创建编队</h2>
          <p class="text-sm text-muted-foreground mb-4">手动创建一个新的编队</p>
          <div class="space-y-3">
            <input v-model="newTeamName" class="input-game" placeholder="编队名称" @keyup.enter="handleCreateTeam" />
            <input v-model="newTeamDungeon" class="input-game" placeholder="目标副本 (可选)" />
            <input v-model.number="newTeamMinMr" type="number" class="input-game" placeholder="最低抗魔值要求 (可选)" />
          </div>
          <div class="flex justify-end gap-3 mt-4">
            <button class="btn-secondary" @click="showCreateModal = false">取消</button>
            <button class="btn-primary" @click="handleCreateTeam" :disabled="!newTeamName.trim()">创建</button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- Add Member Modal -->
    <Teleport to="body">
      <div v-if="showAddMemberModal" class="modal-overlay" @click.self="showAddMemberModal = false">
        <div class="modal-content max-w-2xl">
          <h2 class="text-lg font-semibold mb-1">添加成员</h2>
          <p class="text-sm text-muted-foreground mb-4">选择一个角色职业加入编队</p>
          <div class="space-y-2 max-h-80 overflow-y-auto scrollbar-thin pr-2">
            <button
              v-for="cp in allActiveProfessions"
              :key="cp.cpId"
              class="w-full flex items-center justify-between bg-background/50 rounded-lg p-3 hover:bg-accent transition-all text-left"
              @click="handleAddMember(cp.cpId)"
            >
              <div class="flex items-center gap-3">
                <span class="text-lg">{{ cp.icon }}</span>
                <div>
                  <div class="text-sm font-medium">{{ cp.charName }}</div>
                  <div class="text-xs text-muted-foreground">
                    {{ cp.userName }} · {{ cp.professionName }} · Lv.{{ cp.level }}
                  </div>
                </div>
              </div>
              <span class="font-bold" :style="{ color: getMrLevel(cp.mr).color }">
                {{ cp.mr.toLocaleString() }}
              </span>
            </button>
            <div v-if="allActiveProfessions.length === 0" class="text-center text-muted-foreground py-8">
              没有可用的角色职业
            </div>
          </div>
          <div class="flex justify-end mt-4">
            <button class="btn-secondary" @click="showAddMemberModal = false">取消</button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>
