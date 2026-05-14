<script setup lang="ts">
import { ref, computed } from 'vue'
import { useGameStore } from '@/stores/game'
import * as api from '@/api'

const store = useGameStore()
const inputText = ref('')
const parsedItems = ref<ParsedItem[]>([])
const importResults = ref<ImportResult[]>([])
const importing = ref(false)
const currentStep = ref(0)
const showPreview = ref(false)

interface ParsedItem {
  line: string
  username: string
  characterName: string
  professionInput: string
  professionMatched: string | null
  professionId: number | null
  tag: string | null
  magicResistance: number | null
  valid: boolean
  error: string
}

interface ImportResult {
  line: string
  status: string
  message: string
}

const TAG_PATTERNS = ['1c', '2c', '3c', '4c', '5c', '6c', '垃圾号']
const TAG_COLORS: Record<string, string> = {
  '1c': '#FFD700',
  '2c': '#C0C0C0',
  '3c': '#CD7F32',
  '4c': '#69CCF0',
  '5c': '#ABD473',
  '6c': '#C41F3B',
  '垃圾号': '#888888',
}

const totalItems = computed(() => parsedItems.value.length)
const okCount = computed(() => importResults.value.filter(r => r.status === 'ok').length)
const errorCount = computed(() => importResults.value.filter(r => r.status === 'error').length)
const skipCount = computed(() => importResults.value.filter(r => r.status === 'skip').length)

function parseInput() {
  const lines = inputText.value.split('\n').filter(l => l.trim())
  const items: ParsedItem[] = []

  for (const line of lines) {
    const parts = line.trim().split('/').map(s => s.trim())
    let username = '', characterName = '', professionInput = '', tag: string | null = null
    let magicResistance: number | null = null
    let error = ''

    if (parts.length >= 5) {
      // 5+ fields: username / charName / profession1 / professionWithTag / mr
      username = parts[0]
      characterName = parts[1]
      const baseProf = parts[2]
      const profWithTag = parts[3]
      const mrStr = parts[4]

      // Parse tag from professionWithTag
      let cleanProf = profWithTag
      for (const tp of TAG_PATTERNS) {
        const idx = profWithTag.indexOf(tp)
        if (idx >= 0) {
          tag = tp
          cleanProf = profWithTag.slice(0, idx) + profWithTag.slice(idx + tp.length)
          break
        }
      }
      professionInput = cleanProf
      magicResistance = parseInt(mrStr) || null
      if (isNaN(parseInt(mrStr))) error = '抗魔值格式错误'
    } else if (parts.length === 4) {
      // 4 fields: username / charName / professionWithTag / mr
      username = parts[0]
      characterName = parts[1]
      const profWithTag = parts[2]
      const mrStr = parts[3]

      let cleanProf = profWithTag
      for (const tp of TAG_PATTERNS) {
        const idx = profWithTag.indexOf(tp)
        if (idx >= 0) {
          tag = tp
          cleanProf = profWithTag.slice(0, idx) + profWithTag.slice(idx + tp.length)
          break
        }
      }
      professionInput = cleanProf
      magicResistance = parseInt(mrStr) || null
      if (isNaN(parseInt(mrStr))) error = '抗魔值格式错误'
    } else if (parts.length === 3) {
      // 3 fields: username / charName / mr (no profession)
      username = parts[0]
      characterName = parts[1]
      const mrStr = parts[2]
      magicResistance = parseInt(mrStr) || null
      if (isNaN(parseInt(mrStr))) error = '抗魔值格式错误'
    } else {
      error = '格式错误, 需要至少3个字段(用户名/角色名/抗魔值)'
    }

    if (!username) error = '用户名为空'
    if (!characterName) error = '角色名为空'

    // Fuzzy match profession
    let professionMatched: string | null = null
    let professionId: number | null = null
    if (professionInput) {
      const match = findProfession(professionInput)
      if (match) {
        professionMatched = match.name
        professionId = match.id
      }
    }

    items.push({
      line,
      username,
      characterName,
      professionInput,
      professionMatched,
      professionId,
      tag,
      magicResistance,
      valid: !error,
      error,
    })
  }

  parsedItems.value = items
  showPreview.value = true
  importResults.value = []
  currentStep.value = 0
}

function findProfession(input: string): { id: number; name: string } | null {
  // 1. Exact match
  let match = store.professions.find(p => p.name === input)
  if (match) return match

  // 2. Contains match
  match = store.professions.find(p => p.name.includes(input) || input.includes(p.name))
  if (match) return match

  // 3. Character-by-character fuzzy match
  const chars = input.split('')
  for (const p of store.professions) {
    let ci = 0
    for (const c of p.name) {
      if (ci < chars.length && c === chars[ci]) ci++
    }
    if (ci === chars.length) return p
  }

  return null
}

async function startImport() {
  importing.value = true
  importResults.value = []
  currentStep.value = 0

  const items = parsedItems.value.filter(i => i.valid)
  const batchItems = items.map(item => ({
    username: item.username,
    characterName: item.characterName,
    professionInput: item.professionInput + (item.tag || ''),
    magicResistance: item.magicResistance,
  }))
  
  try {
    const res = await api.batchImport(batchItems)
    importResults.value = res.data.map((r: any) => ({
      line: r.line,
      status: r.status,
      message: r.status === 'ok' ? '导入成功' : r.message,
    }))
    currentStep.value = items.length
    importing.value = false
    // Refresh data
    await store.fetchUsers()
  } catch (e: any) {
    importResults.value = items.map(item => ({
      line: item.line,
      status: 'error',
      message: e.response?.data?.message || e.message,
    }))
    importing.value = false
  }
}

function clearAll() {
  inputText.value = ''
  parsedItems.value = []
  importResults.value = []
  showPreview.value = false
  currentStep.value = 0
}
</script>

<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="mb-8">
      <h2 class="text-2xl font-bold">📋 智能录入</h2>
      <p class="text-sm text-muted-foreground mt-2">
        批量导入角色和职业，每行一条记录，用 <code class="text-purple-400">/</code> 分隔字段
      </p>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- Input Area -->
      <div class="lg:col-span-2">
        <div class="card-game p-6">
          <h3 class="text-sm font-semibold text-foreground mb-3">📝 输入数据</h3>
          <textarea
            v-model="inputText"
            class="input-game min-h-[240px] resize-y font-mono text-sm leading-relaxed"
            placeholder="格式: 用户名/角色名/职业(含标签)/抗魔值&#10;&#10;示例:&#10;柯爽/郑州暖男/枪2c师/172457&#10;柯爽/武汉爽哥/狂战士1c/165000&#10;宇龙/阿修罗/鬼泣/180000&#10;&#10;标签自动识别: 1c 2c 3c 4c 5c 6c 垃圾号"
          ></textarea>

          <div class="flex items-center gap-3 mt-4">
            <button
              class="btn-primary text-sm"
              :disabled="!inputText.trim()"
              @click="parseInput"
            >
              🔍 解析预览
            </button>
            <button
              class="btn-ghost text-sm"
              @click="clearAll"
            >
              清空
            </button>
            <span class="text-xs text-muted-foreground">
              {{ inputText.split('\n').filter(l => l.trim()).length || 0 }} 行
            </span>
          </div>
        </div>
      </div>

      <!-- Format Guide -->
      <div class="lg:col-span-1">
        <div class="card-game p-6 space-y-4">
          <h3 class="text-sm font-semibold text-foreground">📖 格式说明</h3>
          <div class="text-xs text-muted-foreground space-y-3">
            <div>
              <p class="text-foreground font-medium mb-1">标准格式 (4段):</p>
              <code class="block bg-accent/30 px-2 py-1.5 rounded text-purple-400">
                用户名/角色名/职业+标签/抗魔值
              </code>
            </div>
            <div>
              <p class="text-foreground font-medium mb-1">扩展格式 (5段):</p>
              <code class="block bg-accent/30 px-2 py-1.5 rounded text-purple-400">
                用户名/角色名/职业名称/职业+标签/抗魔值
              </code>
            </div>
            <div>
              <p class="text-foreground font-medium mb-1">简单格式 (3段):</p>
              <code class="block bg-accent/30 px-2 py-1.5 rounded text-purple-400">
                用户名/角色名/抗魔值
              </code>
            </div>
            <div class="pt-2 border-t border-border">
              <p class="text-foreground font-medium mb-1">支持的标签:</p>
              <div class="flex flex-wrap gap-1">
                <span v-for="t in TAG_PATTERNS" :key="t"
                  class="text-xs px-1.5 py-0.5 rounded-full border"
                  :style="{
                    color: TAG_COLORS[t] || '#888',
                    borderColor: (TAG_COLORS[t] || '#888') + '44',
                    backgroundColor: (TAG_COLORS[t] || '#888') + '15',
                  }"
                >{{ t }}</span>
              </div>
            </div>
            <div class="pt-2 border-t border-border">
              <p class="text-foreground font-medium mb-1">示例:</p>
              <code class="block bg-accent/30 px-2 py-1.5 rounded text-xs">
                柯爽/郑州暖男/枪2c师/172457
              </code>
              <p class="mt-1">→ 柯爽创建角色"郑州暖男", 职业枪炮师(tag=2c), 抗魔172457</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Preview Results -->
    <div v-if="showPreview" class="card-game p-6">
      <h3 class="text-sm font-semibold text-foreground mb-4">
        📊 解析结果
        <span class="text-xs text-muted-foreground ml-2 font-normal">
          {{ parsedItems.filter(i => i.valid).length }} 条有效
          <span v-if="parsedItems.filter(i => !i.valid).length">, {{ parsedItems.filter(i => !i.valid).length }} 条无效</span>
        </span>
      </h3>

      <div class="overflow-x-auto">
        <table class="w-full text-sm">
          <thead>
            <tr class="text-xs text-muted-foreground border-b border-border">
              <th class="text-left py-2 px-2">用户名</th>
              <th class="text-left py-2 px-2">角色名</th>
              <th class="text-left py-2 px-2">职业</th>
              <th class="text-center py-2 px-2">标签</th>
              <th class="text-right py-2 px-2">抗魔值</th>
              <th class="text-center py-2 px-2">状态</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, idx) in parsedItems" :key="idx"
              class="border-b border-border/50 hover:bg-accent/10 transition-colors"
            >
              <td class="py-2 px-2">{{ item.username }}</td>
              <td class="py-2 px-2">{{ item.characterName }}</td>
              <td class="py-2 px-2">
                <span v-if="item.professionMatched" class="text-purple-400">{{ item.professionMatched }}</span>
                <span v-else-if="item.professionInput" class="text-yellow-400">{{ item.professionInput }} (未匹配)</span>
                <span v-else class="text-muted-foreground">-</span>
              </td>
              <td class="py-2 px-2 text-center">
                <span v-if="item.tag"
                  class="text-xs font-bold px-2 py-0.5 rounded-full border"
                  :style="{
                    color: '#888',
                    borderColor: '#88844',
                    backgroundColor: '#88815',
                  }"
                >{{ item.tag }}</span>
                <span v-else class="text-muted-foreground text-xs">-</span>
              </td>
              <td class="py-2 px-2 text-right font-mono">{{ item.magicResistance ?? '-' }}</td>
              <td class="py-2 px-2 text-center">
                <span v-if="item.valid" class="text-xs text-green-400">✅ 有效</span>
                <span v-else class="text-xs text-red-400" :title="item.error">❌ {{ item.error }}</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Import Button -->
      <div class="flex items-center justify-between mt-6 pt-4 border-t border-border">
        <div class="flex items-center gap-2 text-xs text-muted-foreground">
          <span v-if="importing">正在导入 {{ currentStep }}/{{ totalItems }}...</span>
          <span v-else-if="importResults.length">
            完成: {{ okCount }} 成功,
            <span v-if="skipCount" class="text-yellow-400">{{ skipCount }} 跳过</span>
            <span v-if="errorCount" class="text-red-400">{{ errorCount }} 失败</span>
          </span>
        </div>
        <button
          class="btn-primary text-sm"
          :disabled="importing || !parsedItems.some(i => i.valid)"
          @click="startImport"
        >
          <span v-if="importing">导入中 {{ currentStep }}/{{ totalItems }}...</span>
          <span v-else>🚀 开始导入 ({{ parsedItems.filter(i => i.valid).length }}条)</span>
        </button>
      </div>
    </div>

    <!-- Import Results -->
    <div v-if="importResults.length" class="card-game p-6">
      <h3 class="text-sm font-semibold text-foreground mb-3">📋 导入结果</h3>
      <div class="space-y-1 max-h-48 overflow-y-auto scrollbar-thin">
        <div v-for="(r, idx) in importResults" :key="idx"
          class="text-xs px-3 py-1.5 rounded"
          :class="{
            'bg-green-500/10 text-green-400': r.status === 'ok',
            'bg-yellow-500/10 text-yellow-400': r.status === 'skip',
            'bg-red-500/10 text-red-400': r.status === 'error',
          }"
        >
          {{ r.message }}
        </div>
      </div>
    </div>
  </div>
</template>
