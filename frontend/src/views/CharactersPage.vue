<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useGameStore } from '@/stores/game'

const props = defineProps<{
  selectedUserId: number | null
}>()

const emit = defineEmits<{
  (e: 'back'): void
}>()

const store = useGameStore()

const TAG_OPTIONS = ['1c', '2c', '3c', '4c', '5c', '6c', '垃圾号'] as const
const TAG_COLORS: Record<string, string> = {
  '1c': '#FFD700',
  '2c': '#C0C0C0',
  '3c': '#CD7F32',
  '4c': '#69CCF0',
  '5c': '#ABD473',
  '6c': '#C41F3B',
  '垃圾号': '#888888',
}

// Local state
const selectedUserId = ref<number | null>(props.selectedUserId)
const showAddCharModal = ref(false)
const showAddProfModal = ref<{ characterId: number } | null>(null)
const showEditCpModal = ref<{ id: number; level: number; magicResistance: number } | null>(null)

const newCharName = ref('')
const newCharServer = ref('')
const newCharTag = ref('')

const newProfessionId = ref<number>(0)
const newProfLevel = ref(1)
const newProfMr = ref(0)

watch(() => props.selectedUserId, (val) => {
  selectedUserId.value = val
})

const selectedUser = computed(() => {
  if (!selectedUserId.value) return null
  return store.users.find(u => u.id === selectedUserId.value) || null
})

const characters = computed(() => {
  return selectedUser.value?.characters || []
})

function selectUser(userId: number) {
  selectedUserId.value = userId
}

function openAddCharModal() {
  newCharName.value = ''
  newCharServer.value = ''
  newCharTag.value = ''
  showAddCharModal.value = true
}

async function handleAddChar() {
  if (!selectedUserId.value || !newCharName.value.trim()) return
  const char = await store.addCharacter(selectedUserId.value, newCharName.value.trim(), newCharServer.value.trim(), newCharTag.value || undefined)
  if (char) showAddCharModal.value = false
}

async function handleDeleteChar(id: number, name: string) {
  if (confirm(`确定要删除角色 "${name}" 吗？`)) {
    await store.removeCharacter(id)
  }
}

const editingTagCharId = ref<number | null>(null)
const editingTagValue = ref('')

async function startEditTag(characterId: number, currentTag: string | null) {
  // 先保存上一个正在编辑的标签
  if (editingTagCharId.value !== null && editingTagCharId.value !== characterId) {
    await saveTag()
  }
  editingTagCharId.value = characterId
  editingTagValue.value = currentTag || ''
}

async function saveTag() {
  const id = editingTagCharId.value
  if (id === null) return
  const tag = editingTagValue.value || null
  await store.editCharacter(id, '', '', tag)
  editingTagCharId.value = null
}

function openAddProfModal(characterId: number) {
  newProfessionId.value = store.professions[0]?.id || 0
  newProfLevel.value = 1
  newProfMr.value = 0
  showAddProfModal.value = { characterId }
}

async function handleAddProf() {
  if (!showAddProfModal.value || !newProfessionId.value) return
  try {
    const { addCharacterProfession } = await import('@/api')
    await addCharacterProfession(
      showAddProfModal.value.characterId,
      newProfessionId.value,
      newProfLevel.value,
      newProfMr.value
    )
    await store.fetchUsers()
    showAddProfModal.value = null
    store.showToast('职业添加成功', 'success')
  } catch (e: any) {
    store.showToast('添加职业失败', 'error')
  }
}

function openEditCpModal(cp: { id: number; level: number; magicResistance: number }) {
  showEditCpModal.value = { ...cp }
}

async function handleEditCp() {
  if (!showEditCpModal.value) return
  try {
    const { updateCharacterProfession } = await import('@/api')
    await updateCharacterProfession(showEditCpModal.value.id, {
      level: showEditCpModal.value.level,
      magicResistance: showEditCpModal.value.magicResistance,
    })
    await store.fetchUsers()
    showEditCpModal.value = null
    store.showToast('抗魔值更新成功', 'success')
  } catch (e: any) {
    store.showToast('更新失败', 'error')
  }
}

async function handleDeleteCp(id: number) {
  if (!confirm('确定要删除该职业记录吗？')) return
  try {
    const { deleteCharacterProfession } = await import('@/api')
    await deleteCharacterProfession(id)
    await store.fetchUsers()
    store.showToast('职业记录已删除', 'info')
  } catch (e: any) {
    store.showToast('删除失败', 'error')
  }
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
      <div class="flex items-center gap-4">
        <button class="btn-ghost text-lg" @click="emit('back')">←</button>
        <div>
          <h1 class="text-2xl font-bold text-gradient-arcane">角色管理</h1>
          <p class="text-muted-foreground text-sm mt-1">
            {{ selectedUser ? `当前: ${selectedUser.username}` : '选择一个用户' }}
          </p>
        </div>
      </div>
      <button
        v-if="selectedUserId"
        class="btn-primary flex items-center gap-2"
        @click="openAddCharModal"
      >
        <span>＋</span>
        <span>添加角色</span>
      </button>
    </div>

    <!-- User Selector -->
    <div v-if="store.users.length > 0" class="flex gap-2 flex-wrap">
      <button
        v-for="user in store.users"
        :key="user.id"
        class="px-4 py-2 rounded-lg text-sm transition-all"
        :class="selectedUserId === user.id
          ? 'bg-arcane/20 text-arcane border border-arcane/30'
          : 'bg-surface text-muted-foreground hover:text-foreground border border-border'"
        @click="selectUser(user.id)"
      >
        {{ user.username }}
        <span class="text-xs ml-1 opacity-60">({{ user.characters.length }})</span>
      </button>
    </div>

    <!-- Character List -->
    <div v-if="!selectedUserId" class="card-game p-12 text-center">
      <div class="text-5xl mb-4">⚔️</div>
      <p class="text-muted-foreground">请先选择一个用户</p>
    </div>

    <div v-else-if="characters.length === 0" class="card-game p-12 text-center">
      <div class="text-5xl mb-4">📭</div>
      <p class="text-muted-foreground">该用户暂无角色</p>
      <button class="btn-primary mt-4" @click="openAddCharModal">添加角色</button>
    </div>

    <div v-else class="space-y-4">
      <div
        v-for="char in characters"
        :key="char.id"
        class="card-game p-5"
      >
        <!-- Character Header -->
        <div class="flex items-center justify-between mb-4">
          <div class="flex items-center gap-3">
            <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-purple-600 to-blue-600 flex items-center justify-center text-white font-bold">
              {{ char.name.charAt(0) }}
            </div>
            <div>
              <h3 class="font-semibold">{{ char.name }}</h3>
              <p class="text-xs text-muted-foreground">{{ char.serverName || '未设置服务器' }}</p>
            </div>
            <div class="flex items-center gap-2">
              <!-- Inline edit tag -->
              <div v-if="editingTagCharId === char.id" class="flex items-center gap-1">
                <select v-model="editingTagValue" class="text-xs px-1 py-0.5 rounded bg-background border border-border" @keyup.enter="saveTag">
                  <option value="">无</option>
                  <option v-for="tag in TAG_OPTIONS" :key="tag" :value="tag">{{ tag }}</option>
                </select>
                <button class="text-xs text-green-400 hover:text-green-300 font-bold text-base leading-none px-1" @click.stop="saveTag">✓</button>
                <button class="text-xs text-muted-foreground hover:text-foreground" @click.stop="editingTagCharId = null">✕</button>
              </div>
              <button v-else-if="char.tag"
                class="text-xs font-bold px-2 py-0.5 rounded-full border"
                :style="{
                  color: TAG_COLORS[char.tag] || '#888',
                  borderColor: (TAG_COLORS[char.tag] || '#888') + '44',
                  backgroundColor: (TAG_COLORS[char.tag] || '#888') + '15',
                }"
                :title="'点击修改标签'"
                @click="startEditTag(char.id, char.tag)"
              >
                {{ char.tag }}
              </button>
              <button v-else
                class="text-xs text-muted-foreground hover:text-foreground px-2 py-0.5 rounded-full border border-dashed border-border"
                @click="startEditTag(char.id, null)"
              >
                ＋标签
              </button>
            </div>
          </div>
          <div class="flex items-center gap-2">
            <button
              class="btn-secondary text-xs px-3 py-1.5"
              @click="openAddProfModal(char.id)"
            >
              ＋ 添加职业
            </button>
            <button
              class="btn-ghost text-xs px-2 py-1 text-red-400 hover:text-red-300"
              @click="handleDeleteChar(char.id, char.name)"
            >
              🗑️
            </button>
          </div>
        </div>

        <!-- Professions -->
        <div v-if="char.characterProfessions.length === 0" class="text-sm text-muted-foreground text-center py-4 bg-background/50 rounded-lg">
          暂无职业记录，点击"添加职业"录入
        </div>
        <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
          <div
            v-for="cp in char.characterProfessions"
            :key="cp.id"
            class="bg-background/50 rounded-lg p-3 border border-border/50"
            :class="{ 'border-arcane/30': cp.isActive }"
          >
            <div class="flex items-start justify-between mb-2">
              <div class="flex items-center gap-2">
                <span class="text-lg">{{ cp.professionIcon }}</span>
                <div>
                  <span class="text-sm font-medium">{{ cp.professionName }}</span>
                  <span class="text-xs text-muted-foreground ml-1">Lv.{{ cp.level }}</span>
                </div>
              </div>
              <div class="flex gap-1">
                <button class="text-xs text-muted-foreground hover:text-foreground" @click="openEditCpModal(cp)" title="编辑">✏️</button>
                <button class="text-xs text-muted-foreground hover:text-red-400" @click="handleDeleteCp(cp.id)" title="删除">✕</button>
              </div>
            </div>
            <div class="flex items-center justify-between">
              <div>
                <span class="text-xs text-muted-foreground">抗魔值</span>
                <div class="text-lg font-bold" :style="{ color: getMrLevel(cp.magicResistance).color }">
                  {{ cp.magicResistance.toLocaleString() }}
                </div>
              </div>
              <span
                class="text-xs font-bold px-2 py-1 rounded"
                :style="{ color: getMrLevel(cp.magicResistance).color, backgroundColor: getMrLevel(cp.magicResistance).color + '22' }"
              >
                {{ getMrLevel(cp.magicResistance).label }}
              </span>
            </div>
            <div class="mt-2">
              <div class="h-1.5 bg-background rounded-full overflow-hidden">
                <div
                  class="h-full rounded-full transition-all duration-500"
                  :style="{
                    width: Math.min(100, (cp.magicResistance / 35000) * 100) + '%',
                    background: `linear-gradient(90deg, ${getMrLevel(cp.magicResistance).color}, ${getMrLevel(cp.magicResistance).color}dd)`,
                  }"
                />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Add Character Modal -->
    <Teleport to="body">
      <div v-if="showAddCharModal" class="modal-overlay" @click.self="showAddCharModal = false">
        <div class="modal-content">
          <h2 class="text-lg font-semibold mb-1">添加角色</h2>
          <p class="text-sm text-muted-foreground mb-4">为 {{ selectedUser?.username }} 添加新的 DNF 角色</p>
          <div class="space-y-3">
            <input v-model="newCharName" class="input-game" placeholder="角色名" @keyup.enter="handleAddChar" />
            <input v-model="newCharServer" class="input-game" placeholder="所在服务器 (可选)" @keyup.enter="handleAddChar" />
            <div>
              <label class="text-xs text-muted-foreground mb-1 block">角色标签</label>
              <select v-model="newCharTag" class="select-game">
                <option value="">不设置</option>
                <option v-for="tag in TAG_OPTIONS" :key="tag" :value="tag">{{ tag }}</option>
              </select>
            </div>
          </div>
          <div class="flex justify-end gap-3 mt-4">
            <button class="btn-secondary" @click="showAddCharModal = false">取消</button>
            <button class="btn-primary" @click="handleAddChar" :disabled="!newCharName.trim()">确认添加</button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- Add Profession Modal -->
    <Teleport to="body">
      <div v-if="showAddProfModal" class="modal-overlay" @click.self="showAddProfModal = null">
        <div class="modal-content">
          <h2 class="text-lg font-semibold mb-1">添加职业</h2>
          <p class="text-sm text-muted-foreground mb-4">为角色添加职业、等级和抗魔值</p>
          <div class="space-y-3">
            <div>
              <label class="text-xs text-muted-foreground mb-1 block">选择职业</label>
              <select v-model="newProfessionId" class="select-game">
                <option v-for="prof in store.professions" :key="prof.id" :value="prof.id">
                  {{ prof.icon }} {{ prof.name }} ({{ prof.category }})
                </option>
              </select>
            </div>
            <div>
              <label class="text-xs text-muted-foreground mb-1 block">等级</label>
              <input v-model.number="newProfLevel" type="number" min="1" max="100" step="1" class="input-game" onkeypress="return (event.charCode >= 48 && event.charCode <= 57)" />
            </div>
            <div>
              <label class="text-xs text-muted-foreground mb-1 block">抗魔值</label>
              <input v-model.number="newProfMr" type="number" min="0" max="99999" step="1" class="input-game" onkeypress="return (event.charCode >= 48 && event.charCode <= 57)" />
            </div>
          </div>
          <div class="flex justify-end gap-3 mt-4">
            <button class="btn-secondary" @click="showAddProfModal = null">取消</button>
            <button class="btn-primary" @click="handleAddProf">确认添加</button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- Edit CharacterProfession Modal -->
    <Teleport to="body">
      <div v-if="showEditCpModal" class="modal-overlay" @click.self="showEditCpModal = null">
        <div class="modal-content">
          <h2 class="text-lg font-semibold mb-1">编辑抗魔值</h2>
          <p class="text-sm text-muted-foreground mb-4">更新等级和抗魔值</p>
          <div class="space-y-3">
            <div>
              <label class="text-xs text-muted-foreground mb-1 block">等级</label>
              <input v-model.number="showEditCpModal.level" type="number" min="1" max="100" step="1" class="input-game" onkeypress="return (event.charCode >= 48 && event.charCode <= 57)" />
            </div>
            <div>
              <label class="text-xs text-muted-foreground mb-1 block">抗魔值</label>
              <input v-model.number="showEditCpModal.magicResistance" type="number" min="0" max="99999" step="1" class="input-game" onkeypress="return (event.charCode >= 48 && event.charCode <= 57)" />
            </div>
          </div>
          <div class="flex justify-end gap-3 mt-4">
            <button class="btn-secondary" @click="showEditCpModal = null">取消</button>
            <button class="btn-primary" @click="handleEditCp">保存</button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>
