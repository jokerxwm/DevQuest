<template>
  <div class="tag-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>标签管理</span>
          <el-button type="primary" @click="showDialog()">新增标签</el-button>
        </div>
      </template>

      <el-input v-model="keyword" placeholder="搜索标签" style="width: 300px; margin-bottom: 16px" @input="fetchTags" clearable>
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>

      <el-table :data="tags" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="标签名" width="150" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="color" label="颜色" width="100">
          <template #default="{ row }">
            <el-tag :color="row.color" style="color: #fff">{{ row.color }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="questionCount" label="问题数" width="100" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="showDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除该标签？" @confirm="handleDelete(row.id)">
              <template #reference><el-button type="danger" link>删除</el-button></template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination style="margin-top: 16px; justify-content: flex-end" v-model:current-page="page" :page-size="20" :total="total" layout="total, prev, pager, next" @current-change="fetchTags" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editingTag ? '编辑标签' : '新增标签'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标签名" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="颜色" prop="color">
          <el-color-picker v-model="form.color" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getTags, createTag, updateTag, deleteTag } from '@/api/admin'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

const tags = ref([])
const page = ref(1)
const total = ref(0)
const keyword = ref('')
const dialogVisible = ref(false)
const editingTag = ref(null)
const submitting = ref(false)
const formRef = ref()

const form = reactive({ name: '', description: '', color: '#409eff' })
const rules = {
  name: [{ required: true, message: '请输入标签名', trigger: 'blur' }]
}

const fetchTags = async () => {
  try {
    const res = await getTags({ page: page.value, size: 20, keyword: keyword.value })
    tags.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error(error)
  }
}

const showDialog = (tag = null) => {
  editingTag.value = tag
  if (tag) {
    form.name = tag.name
    form.description = tag.description
    form.color = tag.color
  } else {
    form.name = ''
    form.description = ''
    form.color = '#409eff'
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (editingTag.value) {
      await updateTag(editingTag.value.id, form)
      ElMessage.success('更新成功')
    } else {
      await createTag(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchTags()
  } catch (error) {
    console.error(error)
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (id) => {
  try {
    await deleteTag(id)
    ElMessage.success('删除成功')
    fetchTags()
  } catch (error) {
    console.error(error)
  }
}

onMounted(fetchTags)
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
