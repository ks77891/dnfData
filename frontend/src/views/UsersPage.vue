<script setup lang="ts">
import { ref } from 'vue'
import { useGameStore } from '@/stores/game'

const emit = defineEmits<{
  (e: 'viewCharacters', userId: number): void
}>()

const store = useGameStore()

const showAddModal = ref(false)
const showEditModal = ref(false)
const editingUser = ref<{ id: number; username: string } | null>(null)
const newUserName = ref('')
const editUserName = ref('')

function openAddModal() {
  newUserName.value = ''
  showAddModal.value = true
}

function openEditModal(user: { id: number; username: string }) {
  editingUser.value = user
  editUserName.value = user.username
  showEditModal.value = true
}

async function handleAdd() {
  if (!newUserName.value.trim()) return
  await store.addUser(newUserName.value.trim())
  showAddModal.value = false
}

async function handleEdit() {
  if (!editingUser.value || !editUserName.value.trim()) return
  await store.editUser(editingUser.value.id, editUserName.value.trim())
  showEditModal.value = false
}

async function handleDelete(id: number, username: string) {
  if (confirm(`确定要删除用户 "${username}" 吗？该用户的所有角色数据将被删除。`)) {
    await store.removeUser(id)
  }
}
</script>

<template>
  <div class="space-y-6 animate-fade-in">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gradient-arcane">用户管理</h1>
        <p class="text-muted-foreground text-sm mt-1">管理所有 DNF 玩家用户</p>
      </div>
      <button class="btn-primary flex items-center gap-2" @click="openAddModal">
        <span>＋</span>
        <span>添加用户</span>
      </button>
    </div>

    <!-- User List -->
    <div v-if="store.users.length === 0" class="card-game p-12 text-center">
      <div class="text-5xl mb-4">👥</div>
      <p class="text-muted-foreground">暂无用户</p>
      <p class="text-sm text-muted-foreground mt-1">点击上方按钮添加第一个用户</p>
    </div>

    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      <div
        v-for="user in store.users"
        :key="user.id"
        class="card-game p-5 group"
      >
        <div class="flex items-start justify-between mb-4">
          <div class="flex items-center gap-3">
            <div class="w-12 h-12 rounded-full bg-gradient-to-br from-purple-500 to-blue-500 flex items-center justify-center text-white font-bold text-lg shadow-lg">
              {{ user.username.charAt(0) }}
            </div>
            <div>
              <h3 class="font-semibold text-base">{{ user.username }}</h3>
              <p class="text-xs text-muted-foreground">{{ user.characterCount }} 个角色</p>
            </div>
          </div>
          <div class="flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
            <button class="btn-ghost text-xs px-2 py-1" @click="openEditModal(user)" title="编辑">✏️</button>
            <button class="btn-ghost text-xs px-2 py-1 text-red-400 hover:text-red-300" @click="handleDelete(user.id, user.username)" title="删除">🗑️</button>
          </div>
        </div>

        <!-- Character Previews -->
        <div class="space-y-1.5">
          <div
            v-for="char in user.characters.slice(0, 4)"
            :key="char.id"
            class="flex items-center justify-between text-xs bg-background/50 rounded px-2.5 py-1.5"
          >
            <div class="flex items-center gap-2 min-w-0">
              <span class="truncate">{{ char.name }}</span>
              <span
                v-if="char.characterProfessions.length > 0"
                class="shrink-0 px-1.5 py-0.5 rounded text-[10px]"
                :style="{ backgroundColor: char.characterProfessions[0].professionColor + '22', color: char.characterProfessions[0].professionColor }"
              >
                {{ char.characterProfessions[0].professionName }}
              </span>
            </div>
            <span class="shrink-0 ml-2 text-muted-foreground">
              抗魔 {{ char.characterProfessions[0]?.magicResistance || 0 }}
            </span>
          </div>
          <div
            v-if="user.characters.length > 4"
            class="text-xs text-center text-muted-foreground py-1"
          >
            +{{ user.characters.length - 4 }} 更多角色...
          </div>
          <div v-if="user.characters.length === 0" class="text-xs text-muted-foreground text-center py-2">
            暂无角色
          </div>
        </div>

        <!-- View Characters Button -->
        <button
          class="w-full mt-3 text-xs text-center text-arcane py-2 rounded-lg hover:bg-accent transition-colors"
          @click="emit('viewCharacters', user.id)"
        >
          查看角色详情 →
        </button>
      </div>
    </div>

    <!-- Add Modal -->
    <Teleport to="body">
      <div v-if="showAddModal" class="modal-overlay" @click.self="showAddModal = false">
        <div class="modal-content">
          <h2 class="text-lg font-semibold mb-1">添加用户</h2>
          <p class="text-sm text-muted-foreground mb-4">输入 DNF 玩家用户名</p>
          <input
            v-model="newUserName"
            class="input-game mb-4"
            placeholder="输入用户名..."
            @keyup.enter="handleAdd"
            autofocus
          />
          <div class="flex justify-end gap-3">
            <button class="btn-secondary" @click="showAddModal = false">取消</button>
            <button class="btn-primary" @click="handleAdd" :disabled="!newUserName.trim()">确认添加</button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- Edit Modal -->
    <Teleport to="body">
      <div v-if="showEditModal" class="modal-overlay" @click.self="showEditModal = false">
        <div class="modal-content">
          <h2 class="text-lg font-semibold mb-1">编辑用户</h2>
          <p class="text-sm text-muted-foreground mb-4">修改用户名</p>
          <input
            v-model="editUserName"
            class="input-game mb-4"
            placeholder="输入用户名..."
            @keyup.enter="handleEdit"
            autofocus
          />
          <div class="flex justify-end gap-3">
            <button class="btn-secondary" @click="showEditModal = false">取消</button>
            <button class="btn-primary" @click="handleEdit" :disabled="!editUserName.trim()">保存</button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>
