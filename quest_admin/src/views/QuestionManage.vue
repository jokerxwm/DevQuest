<template>
  <div class="question-manage">
    <el-card>
      <template #header>
        <span>问题管理</span>
      </template>

      <div class="filter-bar">
        <el-input v-model="keyword" placeholder="搜索问题标题" style="width: 300px" @input="fetchQuestions" clearable>
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-select v-model="statusFilter" placeholder="状态筛选" clearable @change="fetchQuestions" style="width: 120px">
          <el-option label="正常" :value="1" />
          <el-option label="已删除" :value="0" />
        </el-select>
      </div>

      <el-table :data="questions" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="viewCount" label="浏览" width="80" />
        <el-table-column prop="answerCount" label="回答" width="80" />
        <el-table-column prop="voteCount" label="投票" width="80" />
        <el-table-column prop="isTop" label="置顶" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isTop === 1 ? 'warning' : 'info'">{{ row.isTop === 1 ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '已删' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleToggleTop(row)">{{ row.isTop === 1 ? '取消置顶' : '置顶' }}</el-button>
            <el-popconfirm title="确定删除该问题？" @confirm="handleDelete(row.id)">
              <template #reference><el-button type="danger" link>删除</el-button></template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination style="margin-top: 16px; justify-content: flex-end" v-model:current-page="page" :page-size="20" :total="total" layout="total, prev, pager, next" @current-change="fetchQuestions" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getQuestions, toggleQuestionTop, deleteQuestion } from '@/api/admin'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

const questions = ref([])
const page = ref(1)
const total = ref(0)
const keyword = ref('')
const statusFilter = ref(null)

const fetchQuestions = async () => {
  try {
    const res = await getQuestions({ page: page.value, size: 20, keyword: keyword.value, status: statusFilter.value })
    questions.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error(error)
  }
}

const handleToggleTop = async (question) => {
  try {
    await toggleQuestionTop(question.id)
    ElMessage.success('操作成功')
    fetchQuestions()
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = async (id) => {
  try {
    await deleteQuestion(id)
    ElMessage.success('删除成功')
    fetchQuestions()
  } catch (error) {
    console.error(error)
  }
}

onMounted(fetchQuestions)
</script>

<style scoped>
.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}
</style>
