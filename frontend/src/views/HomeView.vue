<template>
  <el-container class="layout">
    <!-- 左侧目录树 -->
    <el-aside width="260px" class="aside">
      <div class="aside-title">CloudMind 目录</div>
      <el-tree
        :data="folderTree"
        node-key="id"
        :props="treeProps"
        default-expand-all
        @node-click="onFolderClick"
      />
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-input
            v-model="keyword"
            placeholder="搜索文件名"
            style="width: 260px"
            clearable
            @keyup.enter="loadFiles"
            @clear="loadFiles"
          >
            <template #append>
              <el-button @click="loadFiles">搜索</el-button>
            </template>
          </el-input>
        </div>

        <div class="header-right">
          <el-button type="primary" @click="ragDialogVisible = true">AI 检索对话框</el-button>
          <el-button type="danger" @click="logout">退出登录</el-button>
        </div>
      </el-header>

      <el-main>
        <!-- 上传拖拽组件 -->
        <el-card class="mb16">
          <template #header>
            <div class="card-title">上传文件（拖拽组件）</div>
          </template>
          <el-upload
            drag
            :show-file-list="false"
            :http-request="customUpload"
            multiple
          >
            <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
            <div class="el-upload__text">拖拽文件到此处，或 <em>点击上传</em></div>
          </el-upload>
        </el-card>

        <!-- 摘要卡片（基于 AI 检索结果） -->
        <el-card class="mb16" v-if="ragResults.length">
          <template #header>
            <div class="card-title">摘要卡片（Top {{ ragResults.length }}）</div>
          </template>
          <el-row :gutter="12">
            <el-col :span="8" v-for="item in ragResults.slice(0, 3)" :key="item.fileId">
              <el-card shadow="hover" class="summary-card">
                <h4>{{ item.fileName }}</h4>
                <p class="summary-text">{{ item.summary }}</p>
                <div>
                  <el-tag v-for="tag in item.tags" :key="tag" size="small" class="tag">{{ tag }}</el-tag>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </el-card>

        <!-- 文件列表 -->
        <el-card>
          <template #header>
            <div class="card-title">文件列表</div>
          </template>

          <el-table :data="fileList" @row-click="openDetail">
            <el-table-column prop="id" label="ID" width="90" />
            <el-table-column prop="fileName" label="文件名" min-width="220" />
            <el-table-column prop="fileType" label="类型" width="160" />
            <el-table-column prop="fileSize" label="大小(Byte)" width="150" />
            <el-table-column label="标签展示" min-width="220">
              <template #default="scope">
                <el-tag
                  v-for="tag in (scope.row.tags || [])"
                  :key="tag"
                  size="small"
                  class="tag"
                  type="success"
                >
                  {{ tag }}
                </el-tag>
                <span v-if="!(scope.row.tags || []).length" class="muted">暂无标签</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="scope">
                <el-button link type="danger" @click.stop="onDelete(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <!-- 分页 -->
          <div class="pager">
            <el-pagination
              background
              layout="total, prev, pager, next"
              :total="total"
              v-model:current-page="pageNum"
              :page-size="pageSize"
              @current-change="loadFiles"
            />
          </div>
        </el-card>
      </el-main>
    </el-container>
  </el-container>

  <!-- AI 检索对话框 -->
  <el-dialog v-model="ragDialogVisible" title="AI 检索对话框" width="760px">
    <el-input
      v-model="ragQuestion"
      type="textarea"
      :rows="3"
      placeholder="请输入你的问题，例如：机器学习课程资料重点是什么？"
    />
    <template #footer>
      <el-button @click="ragDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="runRag">检索</el-button>
    </template>

    <el-empty v-if="!ragResults.length" description="暂无检索结果" />
    <el-card v-for="item in ragResults" :key="item.fileId" class="mb12" shadow="hover">
      <div class="rag-head">
        <strong>{{ item.fileName }}</strong>
        <el-tag type="warning">相似度 {{ (item.similarity || 0).toFixed(4) }}</el-tag>
      </div>
      <p>{{ item.summary }}</p>
      <div>
        <el-tag v-for="tag in item.tags" :key="tag" size="small" class="tag">{{ tag }}</el-tag>
      </div>
      <p class="muted">命中片段：{{ item.topChunk }}</p>
    </el-card>
  </el-dialog>

  <!-- 文件详情抽屉 -->
  <el-drawer v-model="detailVisible" title="文件详情" size="40%">
    <el-descriptions :column="1" border>
      <el-descriptions-item label="文件 ID">{{ currentFile.id }}</el-descriptions-item>
      <el-descriptions-item label="文件名">{{ currentFile.fileName }}</el-descriptions-item>
      <el-descriptions-item label="类型">{{ currentFile.fileType }}</el-descriptions-item>
      <el-descriptions-item label="大小">{{ currentFile.fileSize }}</el-descriptions-item>
      <el-descriptions-item label="摘要">{{ currentFile.summary || '暂无摘要' }}</el-descriptions-item>
      <el-descriptions-item label="标签">
        <el-tag v-for="tag in (currentFile.tags || [])" :key="tag" size="small" class="tag">{{ tag }}</el-tag>
      </el-descriptions-item>
    </el-descriptions>
  </el-drawer>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import { pageFiles, deleteFile } from '../api/files'
import { pageFolders } from '../api/folders'
import { uploadFile } from '../api/storage'
import { ragQuery } from '../api/rag'

const router = useRouter()

const folderTree = ref([])
const treeProps = { children: 'children', label: 'name' }
const currentFolderId = ref(0)

const keyword = ref('')
const fileList = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const detailVisible = ref(false)
const currentFile = reactive({})

const ragDialogVisible = ref(false)
const ragQuestion = ref('')
const ragResults = ref([])

// 加载目录树（MVP 简化：仅展示根目录分页结果）
const loadFolders = async () => {
  try {
    const { data } = await pageFolders({ pageNum: 1, pageSize: 100, parentId: 0 })
    const records = data?.data?.records || []
    folderTree.value = [{ id: 0, name: '全部文件', children: records.map((f) => ({ ...f, children: [] })) }]
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '目录加载失败')
  }
}

const onFolderClick = (node) => {
  currentFolderId.value = node.id
  pageNum.value = 1
  loadFiles()
}

// 加载文件列表
const loadFiles = async () => {
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      folderId: currentFolderId.value === 0 ? undefined : currentFolderId.value
    }
    const { data } = await pageFiles(params)
    const records = data?.data?.records || []

    // 前端关键词过滤（文件名 + 标签）
    const filtered = !keyword.value
      ? records
      : records.filter((it) => (it.fileName || '').toLowerCase().includes(keyword.value.toLowerCase()))

    fileList.value = filtered
    total.value = data?.data?.total || 0
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '文件列表加载失败')
  }
}

// 上传拖拽组件自定义上传
const customUpload = async ({ file }) => {
  try {
    await uploadFile(currentFolderId.value || 0, file)
    ElMessage.success('上传成功')
    await loadFiles()
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '上传失败')
  }
}

// 删除文件
const onDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除文件 ${row.fileName} 吗？`, '提示', { type: 'warning' })
    await deleteFile(row.id)
    ElMessage.success('删除成功')
    await loadFiles()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e?.response?.data?.message || '删除失败')
    }
  }
}

// 打开详情抽屉
const openDetail = (row) => {
  Object.assign(currentFile, row)
  detailVisible.value = true
}

// RAG 检索
const runRag = async () => {
  if (!ragQuestion.value.trim()) {
    ElMessage.warning('请输入问题')
    return
  }
  try {
    const { data } = await ragQuery({ question: ragQuestion.value, topK: 5 })
    ragResults.value = data?.data || []

    // 将标签摘要映射到文件列表展示
    const map = new Map(ragResults.value.map((it) => [it.fileId, it]))
    fileList.value = fileList.value.map((file) => {
      const hit = map.get(file.id)
      return hit
        ? { ...file, tags: hit.tags, summary: hit.summary, similarity: hit.similarity }
        : file
    })
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || 'AI 检索失败')
  }
}

const logout = async () => {
  localStorage.removeItem('token')
  ElMessage.success('已退出登录')
  await router.push('/login')
}

onMounted(async () => {
  await loadFolders()
  await loadFiles()
})
</script>

<style scoped>
.layout {
  min-height: 100vh;
  background: #f5f7fa;
}

.aside {
  background: #fff;
  border-right: 1px solid #eee;
  padding: 12px;
}

.aside-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 12px;
}

.header {
  background: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #eee;
}

.mb16 {
  margin-bottom: 16px;
}

.mb12 {
  margin-bottom: 12px;
}

.card-title {
  font-weight: 600;
}

.summary-card {
  min-height: 160px;
}

.summary-text {
  color: #606266;
  min-height: 48px;
}

.tag {
  margin-right: 6px;
  margin-bottom: 6px;
}

.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.rag-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.muted {
  color: #909399;
}
</style>
