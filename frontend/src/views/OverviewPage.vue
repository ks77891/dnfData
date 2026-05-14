<script setup lang="ts">
import { computed } from 'vue'
import { useGameStore } from '@/stores/game'

const emit = defineEmits<{
  (e: 'viewCharacters', userId: number): void
}>()

const store = useGameStore()

const stats = computed(() => {
  const totalUsers = store.users.length
  const totalCharacters = store.users.reduce((sum, u) => sum + u.characters.length, 0)
  const totalProfessions = store.users.reduce((sum, u) => {
    return sum + u.characters.reduce((s, c) => s + c.characterProfessions.length, 0)
  }, 0)
  const totalTeams = store.teams.length
  const allCps = store.users.flatMap(u => u.characters.flatMap(c => c.characterProfessions))
  const avgMr = allCps.length > 0
    ? Math.round(allCps.reduce((s, cp) => s + cp.magicResistance, 0) / allCps.length)
    : 0
  const highestMr = allCps.length > 0 ? Math.max(...allCps.map(cp => cp.magicResistance)) : 0

  return { totalUsers, totalCharacters, totalProfessions, totalTeams, avgMr, highestMr }
})

const categoryColors: Record<string, string> = {
  '鬼剑士': '#C41F3B',
  '格斗家': '#C79C6E',
  '魔法师': '#69CCF0',
  '圣职者': '#F58CBA',
  '枪手': '#FFF569',
}

function getCategoryProfessions() {
  const map: Record<string, { name: string; color: string; icon: string; count: number }[]> = {}
  for (const user of store.users) {
    for (const char of user.characters) {
      for (const cp of char.characterProfessions) {
        if (!cp.isActive) continue
        const cat = cp.professionCategory
        if (!map[cat]) map[cat] = []
        const existing = map[cat].find(p => p.name === cp.professionName)
        if (existing) {
          existing.count++
        } else {
          map[cat].push({ name: cp.professionName, color: cp.professionColor, icon: cp.professionIcon, count: 1 })
        }
      }
    }
  }
  return Object.entries(map)
    .sort(([a], [b]) => a.localeCompare(b))
    .map(([category, professions]) => ({ category, professions: professions.sort((a, b) => b.count - a.count) }))
}
</script>

<template>
  <div class="space-y-8 animate-fade-in">
    <!-- Header -->
    <div>
      <h1 class="text-2xl font-bold text-gradient-arcane">DNF 手游编队总览</h1>
      <p class="text-muted-foreground text-sm mt-1">查看所有用户、角色和编队信息</p>
    </div>

    <!-- Stats Grid -->
    <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
      <div class="card-game p-4 text-center">
        <div class="text-2xl mb-1">👥</div>
        <div class="text-2xl font-bold text-gradient-arcane">{{ stats.totalUsers }}</div>
        <div class="text-xs text-muted-foreground mt-1">用户总数</div>
      </div>
      <div class="card-game p-4 text-center">
        <div class="text-2xl mb-1">⚔️</div>
        <div class="text-2xl font-bold text-blue-400">{{ stats.totalCharacters }}</div>
        <div class="text-xs text-muted-foreground mt-1">角色总数</div>
      </div>
      <div class="card-game p-4 text-center">
        <div class="text-2xl mb-1">🔮</div>
        <div class="text-2xl font-bold text-purple-400">{{ stats.totalProfessions }}</div>
        <div class="text-xs text-muted-foreground mt-1">职业记录</div>
      </div>
      <div class="card-game p-4 text-center">
        <div class="text-2xl mb-1">🛡️</div>
        <div class="text-2xl font-bold text-gold-DEFAULT">{{ stats.totalTeams }}</div>
        <div class="text-xs text-muted-foreground mt-1">编队总数</div>
      </div>
      <div class="card-game p-4 text-center">
        <div class="text-2xl mb-1">📈</div>
        <div class="text-2xl font-bold text-green-400">{{ stats.avgMr }}</div>
        <div class="text-xs text-muted-foreground mt-1">平均抗魔值</div>
      </div>
      <div class="card-game p-4 text-center">
        <div class="text-2xl mb-1">🏆</div>
        <div class="text-2xl font-bold text-yellow-400">{{ stats.highestMr }}</div>
        <div class="text-xs text-muted-foreground mt-1">最高抗魔值</div>
      </div>
    </div>

    <!-- Users & Characters List -->
    <div class="card-game p-6">
      <h2 class="text-lg font-semibold mb-4">用户角色概览</h2>
      <div v-if="store.users.length === 0" class="text-center py-12 text-muted-foreground">
        <div class="text-4xl mb-3">📭</div>
        <p>暂无数据</p>
        <p class="text-sm mt-1">请先在「用户管理」中添加用户</p>
      </div>
      <div v-else class="space-y-4">
        <div
          v-for="user in store.users"
          :key="user.id"
          class="bg-surface rounded-lg p-4 glow-arcane-hover cursor-pointer transition-all"
          @click="emit('viewCharacters', user.id)"
        >
          <div class="flex items-center justify-between mb-3">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-full bg-gradient-to-br from-purple-500 to-blue-500 flex items-center justify-center text-white font-bold">
                {{ user.username.charAt(0) }}
              </div>
              <div>
                <h3 class="font-semibold">{{ user.username }}</h3>
                <p class="text-xs text-muted-foreground">{{ user.characterCount }} 个角色</p>
              </div>
            </div>
            <span class="badge badge-primary">{{ user.characters.length }} 角色</span>
          </div>

          <!-- Characters Grid -->
          <div v-if="user.characters.length > 0" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-2">
            <div
              v-for="char in user.characters"
              :key="char.id"
              class="flex items-center gap-2 bg-background/50 rounded-lg p-2.5"
            >
              <div class="flex-1 min-w-0">
                <div class="text-sm font-medium truncate">{{ char.name }}</div>
                <div class="text-xs text-muted-foreground">{{ char.serverName || '未设置服务器' }}</div>
              </div>
              <div class="flex items-center gap-1">
                <span v-if="char.characterProfessions.length > 0"
                  class="text-xs px-2 py-0.5 rounded-full"
                  :style="{ backgroundColor: char.characterProfessions[0].professionColor + '22', color: char.characterProfessions[0].professionColor }"
                >
                  {{ char.characterProfessions[0].professionName }}
                </span>
              </div>
            </div>
          </div>
          <p v-else class="text-xs text-muted-foreground text-center py-2">暂无角色，点击添加</p>
        </div>
      </div>
    </div>

    <!-- Profession Distribution -->
    <div class="card-game p-6" v-if="store.users.length > 0">
      <h2 class="text-lg font-semibold mb-4">职业分布</h2>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <div v-for="{ category, professions } in getCategoryProfessions()" :key="category" class="space-y-2">
          <h3 class="text-sm font-medium" :style="{ color: categoryColors[category] || '#ffffff' }">
            {{ category }}
          </h3>
          <div class="space-y-1">
            <div
              v-for="prof in professions"
              :key="prof.name"
              class="flex items-center justify-between text-xs bg-background/50 rounded px-2 py-1.5"
            >
              <span class="flex items-center gap-1">
                <span>{{ prof.icon }}</span>
                <span>{{ prof.name }}</span>
              </span>
              <span class="text-muted-foreground">{{ prof.count }} 人</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
