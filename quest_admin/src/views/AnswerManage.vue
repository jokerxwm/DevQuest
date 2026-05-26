<template>
  <div class="answer-manage">
    <el-card>
      <template #header>
        <span>回答管理</span>
      </template>

      <el-table :data="answers" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="questionId" label="问题ID" width="100" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="content" label="内容" min-width="300" show-overflow-tooltip />
        <el-table-column prop="voteCount" label="投票" width="80" />
        <el-table-column prop="isAccepted" label="采纳" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isAccepted === 1 ? 'success' : 'info'">{{ row.isAccepted === 1 ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '已删' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-popconfirm title="确定删除该回答？" @confirm="handleDelete(row.id)">
              <template #reference><el-button type="danger" link>删除</el-button></template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination style="margin-top: 16px; justify-content: flex-end" v-model:current-page="page" :page-size="20" :total="total" layout="total, prev, pager, next" @current-change="fetchAnswers" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAnswers, deleteAnswer } from '@/api/admin'
import { ElMessage } from 'element-plus'

const answers = ref([])
const page = ref(1)
const total = ref(0)

const fetchAnswers = async () => {
  try {
    const res = await getAnswers({ page: page.value, size: 20 })
    answers.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = async (id) => {
  try {
    await deleteAnswer(id)
    ElMessage.success('删除成功')
    fetchAnswers()
  } catch (error) {
    console.error(error)
  }
}

onMounted(fetchAnswers)
</script>
