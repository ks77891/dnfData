<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useGameStore } from '@/stores/game'
import OverviewPage from '@/views/OverviewPage.vue'
import UsersPage from '@/views/UsersPage.vue'
import CharactersPage from '@/views/CharactersPage.vue'
import SmartImportPage from '@/views/SmartImportPage.vue'
import TeamsPage from '@/views/TeamsPage.vue'

const store = useGameStore()
const currentView = ref<'overview' | 'users' | 'characters' | 'smartimport' | 'teams'>('overview')
const selectedUserId = ref<number | null>(null)

onMounted(() => {
  store.loadAll()
})

const navItems = [
  { key: 'overview' as const, label: '总览', icon: '📊' },
  { key: 'users' as const, label: '用户管理', icon: '👥' },
  { key: 'characters' as const, label: '角色管理', icon: '⚔️' },
  { key: 'smartimport' as const, label: '智能录入', icon: '📋' },
  { key: 'teams' as const, label: '智能编队', icon: '🛡️' },
]

function viewCharacters(userId: number) {
  selectedUserId.value = userId
  currentView.value = 'characters'
}
</script>

<template>
  <div class="min-h-screen bg-background flex">
    <!-- Sidebar -->
    <aside class="w-64 min-h-screen bg-surface border-r border-border flex flex-col shrink-0">
      <!-- Logo -->
      <div class="p-6 border-b border-border">
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-purple-500 to-blue-500 flex items-center justify-center text-lg font-bold shadow-lg">
            D
          </div>
          <div>
            <h1 class="text-sm font-bold text-gradient-arcane">DNF 智能编队</h1>
            <p class="text-xs text-muted-foreground mt-0.5">手游版 v1.0</p>
          </div>
        </div>
      </div>

      <!-- Navigation -->
      <nav class="flex-1 p-3 space-y-1">
        <button
          v-for="item in navItems"
          :key="item.key"
          @click="currentView = item.key"
          class="w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm transition-all duration-200"
          :class="currentView === item.key
            ? 'bg-accent text-accent-foreground shadow-sm'
            : 'text-muted-foreground hover:text-foreground hover:bg-accent/50'"
        >
          <span class="text-lg">{{ item.icon }}</span>
          <span>{{ item.label }}</span>
        </button>
      </nav>

      <!-- Footer -->
      <div class="p-4 border-t border-border">
        <div class="text-xs text-muted-foreground text-center">
          <p>Powered by Qoder</p>
        </div>
      </div>
    </aside>

    <!-- Main Content -->
    <main class="flex-1 overflow-auto">
      <div class="p-8 max-w-7xl mx-auto">
        <OverviewPage
          v-if="currentView === 'overview'"
          @view-characters="viewCharacters"
        />
        <UsersPage
          v-else-if="currentView === 'users'"
          @view-characters="viewCharacters"
        />
        <CharactersPage
          v-else-if="currentView === 'characters'"
          :selected-user-id="selectedUserId"
          @back="currentView = 'users'"
        />
        <SmartImportPage v-else-if="currentView === 'smartimport'" />
        <TeamsPage v-else-if="currentView === 'teams'" />
      </div>
    </main>

    <!-- Toast -->
    <Teleport to="body">
      <div class="fixed top-4 right-4 z-[100] space-y-2">
        <div
          v-if="store.toast"
          class="px-4 py-3 rounded-lg text-sm font-medium shadow-lg animate-slide-in"
          :class="{
            'bg-green-600/90 text-white': store.toast.type === 'success',
            'bg-red-600/90 text-white': store.toast.type === 'error',
            'bg-blue-600/90 text-white': store.toast.type === 'info',
          }"
        >
          {{ store.toast.message }}
        </div>
      </div>
    </Teleport>
  </div>
</template>
