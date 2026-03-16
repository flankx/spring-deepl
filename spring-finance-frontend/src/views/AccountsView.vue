<template>
  <div class="accounts">
    <div class="header">
      <button class="back-btn" @click="goBack">
        <span>←</span>
      </button>
      <h1>账户管理</h1>
      <div class="empty"></div>
    </div>
    
    <div class="add-account-btn">
      <button class="primary-btn" @click="showAddAccountDialog = true">
        + 添加账户
      </button>
    </div>
    
    <div class="account-list">
      <h2>账户列表</h2>
      <div v-if="accounts.length === 0" class="empty">
        暂无账户
      </div>
      <div v-else class="account-items">
        <div 
          v-for="account in accounts" 
          :key="account.id"
          class="account-item"
        >
          <div class="account-icon" :style="{ backgroundColor: account.color || '#667eea' }">
            {{ getAccountIcon(account.type) }}
          </div>
          <div class="account-info">
            <div class="account-name">{{ account.name }}</div>
            <div class="account-type">{{ getAccountTypeName(account.type) }}</div>
          </div>
          <div class="account-balance">
            {{ formatCurrency(account.balance) }}
          </div>
          <div class="account-actions">
            <button class="edit-btn" @click="editAccount(account)">
              编辑
            </button>
            <button class="delete-btn" @click="deleteAccount(account.id)">
              删除
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 添加/编辑账户对话框 -->
    <div v-if="showAddAccountDialog" class="dialog-overlay" @click="showAddAccountDialog = false">
      <div class="dialog-content" @click.stop>
        <div class="dialog-header">
          <h3>{{ editingAccount ? '编辑账户' : '添加账户' }}</h3>
          <button class="close-btn" @click="showAddAccountDialog = false">✕</button>
        </div>
        <div class="dialog-body">
          <div class="form-item">
            <label>账户名称</label>
            <input 
              type="text" 
              v-model="accountForm.name" 
              placeholder="请输入账户名称" 
              class="input-field"
            />
          </div>
          <div class="form-item">
            <label>账户类型</label>
            <select v-model="accountForm.type" class="select-field">
              <option value="">请选择账户类型</option>
              <option value="cash">现金</option>
              <option value="bank">银行卡</option>
              <option value="alipay">支付宝</option>
              <option value="wechat">微信</option>
              <option value="credit">信用卡</option>
              <option value="other">其他</option>
            </select>
          </div>
          <div class="form-item">
            <label>初始余额</label>
            <input 
              type="number" 
              v-model.number="accountForm.balance" 
              placeholder="0.00" 
              class="input-field"
              step="0.01"
              min="0"
            />
          </div>
          <div class="form-item">
            <label>账户颜色</label>
            <div class="color-picker">
              <div 
                v-for="color in colors" 
                :key="color"
                class="color-option"
                :class="{ active: accountForm.color === color }"
                :style="{ backgroundColor: color }"
                @click="accountForm.color = color"
              ></div>
            </div>
          </div>
        </div>
        <div class="dialog-footer">
          <button class="cancel-btn" @click="showAddAccountDialog = false">
            取消
          </button>
          <button class="save-btn" @click="saveAccount">
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

const router = useRouter()
const financeStore = useFinanceStore()

// 状态
const showAddAccountDialog = ref(false)
const editingAccount = ref(null)

// 账户表单
const accountForm = ref({
  name: '',
  type: '',
  balance: 0,
  color: '#667eea'
})

// 颜色选项
const colors = [
  '#667eea', '#764ba2', '#f093fb', '#f5576c',
  '#4facfe', '#00f2fe', '#43e97b', '#38f9d7',
  '#fa709a', '#fee140', '#fa709a', '#fee140',
  '#00dbde', '#fc00ff', '#30cfd0', '#330867'
]

// 计算属性
const accounts = computed(() => financeStore.accounts)

// 方法
const goBack = () => {
  router.push('/')
}

const formatCurrency = (amount) => {
  return '¥' + amount.toFixed(2)
}

const getAccountIcon = (type) => {
  const icons = {
    cash: '💰',
    bank: '🏦',
    alipay: '👛',
    wechat: '💬',
    credit: '💳',
    other: '📱'
  }
  return icons[type] || '📱'
}

const getAccountTypeName = (type) => {
  const types = {
    cash: '现金',
    bank: '银行卡',
    alipay: '支付宝',
    wechat: '微信',
    credit: '信用卡',
    other: '其他'
  }
  return types[type] || '其他'
}

const addAccount = () => {
  editingAccount.value = null
  accountForm.value = {
    name: '',
    type: '',
    balance: 0,
    color: '#667eea'
  }
  showAddAccountDialog.value = true
}

const editAccount = (account) => {
  editingAccount.value = account
  accountForm.value = {
    name: account.name,
    type: account.type,
    balance: account.balance,
    color: account.color || '#667eea'
  }
  showAddAccountDialog.value = true
}

const saveAccount = () => {
  if (!accountForm.value.name) {
    alert('请输入账户名称')
    return
  }
  
  if (!accountForm.value.type) {
    alert('请选择账户类型')
    return
  }
  
  if (accountForm.value.balance < 0) {
    alert('初始余额不能为负数')
    return
  }
  
  const account = {
    id: editingAccount.value?.id || Date.now(),
    name: accountForm.value.name,
    type: accountForm.value.type,
    balance: accountForm.value.balance,
    color: accountForm.value.color
  }
  
  if (editingAccount.value) {
    financeStore.updateAccount(account)
  } else {
    financeStore.addAccount(account)
  }
  
  showAddAccountDialog.value = false
}

const deleteAccount = (accountId) => {
  if (confirm('确定要删除这个账户吗？')) {
    financeStore.deleteAccount(accountId)
  }
}

// 初始化
onMounted(async () => {
  await financeStore.initializeData()
})
</script>

<style scoped>
.accounts {
  padding: 20px;
  max-width: 600px;
  margin: 0 auto;
  min-height: 100vh;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
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

.add-account-btn {
  margin-bottom: 20px;
}

.primary-btn {
  width: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.primary-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.account-list {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.account-list h2 {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 16px;
}

.account-list .empty {
  text-align: center;
  color: #999;
  padding: 40px 0;
  width: 100%;
}

.account-items {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.account-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.account-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.account-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-right: 16px;
}

.account-info {
  flex: 1;
}

.account-name {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.account-type {
  font-size: 14px;
  color: #666;
}

.account-balance {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-right: 16px;
}

.account-actions {
  display: flex;
  gap: 8px;
}

.edit-btn, .delete-btn {
  padding: 6px 12px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  background: white;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.edit-btn {
  color: #667eea;
  border-color: #667eea;
}

.edit-btn:hover {
  background: #667eea;
  color: white;
}

.delete-btn {
  color: #f44336;
  border-color: #f44336;
}

.delete-btn:hover {
  background: #f44336;
  color: white;
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
}

.form-item label {
  display: block;
  font-size: 14px;
  color: #333;
  margin-bottom: 8px;
}

.select-field, .input-field {
  width: 100%;
  padding: 10px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 16px;
  outline: none;
}

.color-picker {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 8px;
}

.color-option {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.color-option:hover {
  transform: scale(1.1);
}

.color-option.active {
  border-color: #333;
  transform: scale(1.1);
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
  .accounts {
    padding: 16px;
  }
  
  .account-list {
    padding: 16px;
  }
  
  .color-picker {
    grid-template-columns: repeat(6, 1fr);
  }
}
</style>
