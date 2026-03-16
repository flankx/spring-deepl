<template>
  <div class="profile">
    <div class="header">
      <button class="back-btn" @click="goBack">
        <span>←</span>
      </button>
      <h1>个人中心</h1>
      <div class="empty"></div>
    </div>
    
    <div class="user-info">
      <div class="avatar">
        {{ userInitial }}
      </div>
      <div class="user-details">
        <div class="user-name">{{ userName }}</div>
        <div class="user-email">{{ userEmail }}</div>
      </div>
    </div>
    
    <div class="menu-section">
      <h2>数据管理</h2>
      <div class="menu-items">
        <div class="menu-item" @click="backupData">
          <div class="menu-icon">💾</div>
          <div class="menu-text">备份数据</div>
          <div class="menu-arrow">→</div>
        </div>
        <div class="menu-item" @click="restoreData">
          <div class="menu-icon">📤</div>
          <div class="menu-text">恢复数据</div>
          <div class="menu-arrow">→</div>
        </div>
        <div class="menu-item" @click="exportData">
          <div class="menu-icon">📄</div>
          <div class="menu-text">导出数据</div>
          <div class="menu-arrow">→</div>
        </div>
      </div>
    </div>
    
    <div class="menu-section">
      <h2>设置</h2>
      <div class="menu-items">
        <div class="menu-item" @click="showEditProfileDialog = true">
          <div class="menu-icon">👤</div>
          <div class="menu-text">编辑个人信息</div>
          <div class="menu-arrow">→</div>
        </div>
        <div class="menu-item" @click="showSettingsDialog = true">
          <div class="menu-icon">⚙️</div>
          <div class="menu-text">应用设置</div>
          <div class="menu-arrow">→</div>
        </div>
        <div class="menu-item" @click="aboutApp">
          <div class="menu-icon">ℹ️</div>
          <div class="menu-text">关于应用</div>
          <div class="menu-arrow">→</div>
        </div>
      </div>
    </div>
    
    <div class="logout-btn">
      <button class="danger-btn" @click="logout">
        退出登录
      </button>
    </div>
    
    <!-- 编辑个人信息对话框 -->
    <div v-if="showEditProfileDialog" class="dialog-overlay" @click="showEditProfileDialog = false">
      <div class="dialog-content" @click.stop>
        <div class="dialog-header">
          <h3>编辑个人信息</h3>
          <button class="close-btn" @click="showEditProfileDialog = false">✕</button>
        </div>
        <div class="dialog-body">
          <div class="form-item">
            <label>姓名</label>
            <input 
              type="text" 
              v-model="userForm.name" 
              placeholder="请输入姓名" 
              class="input-field"
            />
          </div>
          <div class="form-item">
            <label>邮箱</label>
            <input 
              type="email" 
              v-model="userForm.email" 
              placeholder="请输入邮箱" 
              class="input-field"
            />
          </div>
        </div>
        <div class="dialog-footer">
          <button class="cancel-btn" @click="showEditProfileDialog = false">
            取消
          </button>
          <button class="save-btn" @click="saveUserInfo">
            保存
          </button>
        </div>
      </div>
    </div>
    
    <!-- 应用设置对话框 -->
    <div v-if="showSettingsDialog" class="dialog-overlay" @click="showSettingsDialog = false">
      <div class="dialog-content" @click.stop>
        <div class="dialog-header">
          <h3>应用设置</h3>
          <button class="close-btn" @click="showSettingsDialog = false">✕</button>
        </div>
        <div class="dialog-body">
          <div class="form-item">
            <label>默认账户</label>
            <select v-model="settingsForm.defaultAccount" class="select-field">
              <option value="">请选择默认账户</option>
              <option 
                v-for="account in accounts" 
                :key="account.id"
                :value="account.id"
              >
                {{ account.name }}
              </option>
            </select>
          </div>
          <div class="form-item">
            <label>默认分类</label>
            <select v-model="settingsForm.defaultCategory" class="select-field">
              <option value="">请选择默认分类</option>
              <option 
                v-for="category in categories" 
                :key="category.id"
                :value="category.id"
              >
                {{ category.name }}
              </option>
            </select>
          </div>
          <div class="form-item">
            <label>自动备份</label>
            <div class="toggle">
              <input 
                type="checkbox" 
                id="autoBackup" 
                v-model="settingsForm.autoBackup"
              />
              <label for="autoBackup"></label>
            </div>
          </div>
        </div>
        <div class="dialog-footer">
          <button class="cancel-btn" @click="showSettingsDialog = false">
            取消
          </button>
          <button class="save-btn" @click="saveSettings">
            保存
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useFinanceStore } from '../stores/finance'
import { localforage } from '../utils/storage'

const router = useRouter()
const financeStore = useFinanceStore()

// 状态
const showEditProfileDialog = ref(false)
const showSettingsDialog = ref(false)

// 用户信息
const userForm = ref({
  name: '用户',
  email: 'user@example.com'
})

// 应用设置
const settingsForm = ref({
  defaultAccount: '',
  defaultCategory: '',
  autoBackup: true
})

// 计算属性
const accounts = computed(() => financeStore.accounts)
const categories = computed(() => financeStore.categories)

const userName = computed(() => userForm.value.name)
const userEmail = computed(() => userForm.value.email)

const userInitial = computed(() => {
  return userName.value.charAt(0).toUpperCase()
})

// 方法
const goBack = () => {
  router.push('/')
}

const backupData = async () => {
  try {
    // 从 store 中获取所有数据
    const data = {
      categories: financeStore.categories,
      expenses: financeStore.expenses,
      incomes: financeStore.incomes,
      budgets: financeStore.budgets,
      accounts: financeStore.accounts,
      user: userForm.value,
      settings: settingsForm.value,
      timestamp: new Date().toISOString()
    }
    
    // 转换为 JSON 字符串
    const jsonData = JSON.stringify(data, null, 2)
    
    // 创建下载链接
    const blob = new Blob([jsonData], { type: 'application/json' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `finance-backup-${new Date().toISOString().split('T')[0]}.json`
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    URL.revokeObjectURL(url)
    
    alert('数据备份成功！')
  } catch (error) {
    console.error('Failed to backup data:', error)
    alert('数据备份失败，请重试')
  }
}

const restoreData = () => {
  // 创建文件输入元素
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = '.json'
  
  input.onchange = async (e) => {
    const file = e.target.files[0]
    if (!file) return
    
    try {
      const reader = new FileReader()
      reader.onload = async (event) => {
        try {
          const data = JSON.parse(event.target.result)
          
          // 验证数据格式
          if (!data.categories || !data.expenses || !data.incomes) {
            throw new Error('Invalid backup file format')
          }
          
          // 确认恢复
          if (confirm('确定要恢复数据吗？这将覆盖当前所有数据。')) {
            // 恢复数据到 store
            financeStore.categories = data.categories
            financeStore.expenses = data.expenses
            financeStore.incomes = data.incomes
            financeStore.budgets = data.budgets || []
            financeStore.accounts = data.accounts || []
            
            // 恢复用户信息和设置
            if (data.user) {
              userForm.value = data.user
            }
            if (data.settings) {
              settingsForm.value = data.settings
            }
            
            // 保存到本地存储
            await localforage.setItem('categories', data.categories)
            await localforage.setItem('expenses', data.expenses)
            await localforage.setItem('incomes', data.incomes)
            await localforage.setItem('budgets', data.budgets || [])
            await localforage.setItem('accounts', data.accounts || [])
            await localforage.setItem('user', data.user || userForm.value)
            await localforage.setItem('settings', data.settings || settingsForm.value)
            
            alert('数据恢复成功！')
          }
        } catch (error) {
          console.error('Failed to parse backup file:', error)
          alert('备份文件格式错误，请选择正确的备份文件')
        }
      }
      reader.readAsText(file)
    } catch (error) {
      console.error('Failed to restore data:', error)
      alert('数据恢复失败，请重试')
    }
  }
  
  input.click()
}

const exportData = async () => {
  try {
    // 从 store 中获取所有交易数据
    const transactions = [
      ...financeStore.expenses.map(expense => ({
        type: 'expense',
        ...expense
      })),
      ...financeStore.incomes.map(income => ({
        type: 'income',
        ...income
      }))
    ]
    
    // 转换为 CSV 格式
    const headers = ['Type', 'Amount', 'Date', 'Category', 'Description']
    const rows = transactions.map(transaction => [
      transaction.type === 'expense' ? '支出' : '收入',
      transaction.amount,
      transaction.date,
      transaction.category?.name || '未分类',
      transaction.description || ''
    ])
    
    const csvContent = [
      headers.join(','),
      ...rows.map(row => row.join(','))
    ].join('\n')
    
    // 创建下载链接
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `finance-export-${new Date().toISOString().split('T')[0]}.csv`
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    URL.revokeObjectURL(url)
    
    alert('数据导出成功！')
  } catch (error) {
    console.error('Failed to export data:', error)
    alert('数据导出失败，请重试')
  }
}

const saveUserInfo = async () => {
  try {
    // 保存用户信息到本地存储
    await localforage.setItem('user', userForm.value)
    showEditProfileDialog.value = false
    alert('个人信息保存成功！')
  } catch (error) {
    console.error('Failed to save user info:', error)
    alert('保存失败，请重试')
  }
}

const saveSettings = async () => {
  try {
    // 保存设置到本地存储
    await localforage.setItem('settings', settingsForm.value)
    showSettingsDialog.value = false
    alert('设置保存成功！')
  } catch (error) {
    console.error('Failed to save settings:', error)
    alert('保存失败，请重试')
  }
}

const aboutApp = () => {
  alert('记账小程序 v1.0.0\n\n一个简单易用的记账工具，帮助你管理个人财务。\n\n© 2026 记账小程序')
}

const logout = () => {
  if (confirm('确定要退出登录吗？')) {
    // 清除本地存储数据
    localforage.clear()
    // 重置 store 数据
    financeStore.categories = []
    financeStore.expenses = []
    financeStore.incomes = []
    financeStore.budgets = []
    financeStore.accounts = []
    // 重置用户信息
    userForm.value = {
      name: '用户',
      email: 'user@example.com'
    }
    // 重置设置
    settingsForm.value = {
      defaultAccount: '',
      defaultCategory: '',
      autoBackup: true
    }
    // 跳转到首页
    router.push('/')
  }
}

// 初始化
onMounted(async () => {
  await financeStore.initializeData()
  
  // 从本地存储加载用户信息和设置
  try {
    const savedUser = await localforage.getItem('user')
    if (savedUser) {
      userForm.value = savedUser
    }
    
    const savedSettings = await localforage.getItem('settings')
    if (savedSettings) {
      settingsForm.value = savedSettings
    }
  } catch (error) {
    console.error('Failed to load user info and settings:', error)
  }
})
</script>

<style scoped>
.profile {
  padding: 20px;
  max-width: 600px;
  margin: 0 auto;
  min-height: 100vh;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
}

.back-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #333;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header h1 {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.empty {
  width: 32px;
}

.user-info {
  display: flex;
  align-items: center;
  margin-bottom: 40px;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  color: white;
}

.avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: bold;
  margin-right: 16px;
}

.user-details {
  flex: 1;
}

.user-name {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 4px;
}

.user-email {
  font-size: 14px;
  opacity: 0.8;
}

.menu-section {
  margin-bottom: 24px;
}

.menu-section h2 {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 12px;
  padding-left: 16px;
}

.menu-items {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.3s ease;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-item:hover {
  background: #f5f5f5;
}

.menu-icon {
  font-size: 20px;
  margin-right: 16px;
  width: 24px;
  text-align: center;
}

.menu-text {
  flex: 1;
  font-size: 16px;
  color: #333;
}

.menu-arrow {
  font-size: 14px;
  color: #999;
}

.logout-btn {
  margin-top: 40px;
}

.danger-btn {
  width: 100%;
  padding: 16px;
  background: #f44336;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
}

.danger-btn:hover {
  background: #d32f2f;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(244, 67, 54, 0.4);
}

/* 对话框样式 */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog-content {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 400px;
  overflow: hidden;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.dialog-header h3 {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #666;
}

.dialog-body {
  padding: 16px;
}

.form-item {
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.form-item label {
  font-size: 14px;
  color: #333;
  flex: 1;
}

.select-field, .input-field {
  flex: 2;
  padding: 10px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 16px;
  outline: none;
}

/* 开关样式 */
.toggle {
  position: relative;
  display: inline-block;
  width: 50px;
  height: 24px;
}

.toggle input {
  opacity: 0;
  width: 0;
  height: 0;
}

.toggle label {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  transition: .4s;
  border-radius: 24px;
  margin: 0;
}

.toggle label:before {
  position: absolute;
  content: "";
  height: 18px;
  width: 18px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: .4s;
  border-radius: 50%;
}

.toggle input:checked + label {
  background-color: #667eea;
}

.toggle input:checked + label:before {
  transform: translateX(26px);
}

.dialog-footer {
  display: flex;
  gap: 8px;
  padding: 16px;
  border-top: 1px solid #f0f0f0;
  justify-content: flex-end;
}

.cancel-btn, .save-btn {
  padding: 10px 20px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.cancel-btn {
  background: white;
  color: #666;
}

.save-btn {
  background: #667eea;
  color: white;
  border-color: #667eea;
}

.save-btn:hover {
  background: #764ba2;
  border-color: #764ba2;
}

@media (max-width: 480px) {
  .profile {
    padding: 16px;
  }
  
  .user-info {
    padding: 16px;
  }
  
  .menu-item {
    padding: 12px 16px;
  }
}
</style>
