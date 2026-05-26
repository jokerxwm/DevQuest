<template>
  <div class="user-manage">
    <el-card>
      <template #header>
        <span>用户管理</span>
      </template>

      <div class="filter-bar">
        <el-input v-model="keyword" placeholder="搜索用户名/昵称/邮箱" style="width: 300px" @input="fetchUsers" clearable>
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-select v-model="statusFilter" placeholder="状态筛选" clearable @change="fetchUsers" style="width: 120px">
          <el-option label="正常" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
      </div>

      <el-table :data="users" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'info'">{{ row.role }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reputation" label="声望" width="80" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-popconfirm :title="row.status === 1 ? '确定禁用该用户？' : '确定启用该用户？'" @confirm="toggleStatus(row)">
              <template #reference>
                <el-button :type="row.status === 1 ? 'danger' : 'success'" link>{{ row.status === 1 ? '禁用' : '启用' }}</el-button>
              </template>
            </el-popconfirm>
            <el-button type="primary" link @click="showRoleDialog(row)">改角色</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination style="margin-top: 16px; justify-content: flex-end" v-model:current-page="page" :page-size="20" :total="total" layout="total, prev, pager, next" @current-change="fetchUsers" />
    </el-card>

    <el-dialog v-model="roleDialogVisible" title="修改角色" width="400px">
      <el-select v-model="newRole" style="width: 100%">
        <el-option label="普通用户" value="USER" />
        <el-option label="管理员" value="ADMIN" />
      </el-select>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleChangeRole">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUsers, updateUserStatus, updateUserRole } from '@/api/admin'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

const users = ref([])
const page = ref(1)
const total = ref(0)
const keyword = ref('')
const statusFilter = ref(null)
const roleDialogVisible = ref(false)
const selectedUser = ref(null)
const newRole = ref('USER')

const fetchUsers = async () => {
  try {
    const res = await getUsers({ page: page.value, size: 20, keyword: keyword.value, status: statusFilter.value })
    users.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error(error)
  }
}

const toggleStatus = async (user) => {
  try {
    await updateUserStatus(user.id, { status: user.status === 1 ? 0 : 1 })
    ElMessage.success('操作成功')
    fetchUsers()
  } catch (error) {
    console.error(error)
  }
}

const showRoleDialog = (user) => {
  selectedUser.value = user
  newRole.value = user.role
  roleDialogVisible.value = true
}

const handleChangeRole = async () => {
  try {
    await updateUserRole(selectedUser.value.id, { role: newRole.value })
    ElMessage.success('角色修改成功')
    roleDialogVisible.value = false
    fetchUsers()
  } catch (error) {
    console.error(error)
  }
}

onMounted(fetchUsers)
</script>

<style scoped>
.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}
</style>
