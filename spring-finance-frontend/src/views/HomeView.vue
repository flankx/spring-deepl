<template>
  <div class="home">
    <div class="header">
      <h1>记账小程序</h1>
      <div class="date">{{ currentDate }}</div>
    </div>
    
    <div class="summary-card">
      <div class="summary-item">
        <div class="label">总收入</div>
        <div class="amount income">{{ formatCurrency(totalIncome) }}</div>
      </div>
      <div class="summary-item">
        <div class="label">总支出</div>
        <div class="amount expense">{{ formatCurrency(totalExpense) }}</div>
      </div>
      <div class="summary-item">
        <div class="label">结余</div>
        <div class="amount balance">{{ formatCurrency(balance) }}</div>
      </div>
    </div>
    
    <div class="quick-actions">
      <button class="action-btn primary" @click="navigateTo('/record')">
        <div class="icon">+</div>
        <div class="text">记一笔</div>
      </button>
      <button class="action-btn secondary" @click="navigateTo('/stats')">
        <div class="icon">📊</div>
        <div class="text">统计</div>
      </button>
      <button class="action-btn secondary" @click="navigateTo('/budget')">
        <div class="icon">💰</div>
        <div class="text">预算</div>
      </button>
    </div>
    
    <div class="recent-transactions">
      <h2>最近交易</h2>
      <div v-if="recentTransactions.length === 0" class="empty">
        暂无交易记录
      </div>
      <div v-else class="transaction-list">
        <div 
          v-for="transaction in recentTransactions" 
          :key="transaction.id"
          class="transaction-item"
          :class="{ 'expense': transaction.type === 'expense' }"
        >
          <div class="transaction-info">
            <div class="transaction-category">{{ transaction.category?.name || '未分类' }}</div>
            <div class="transaction-date">{{ formatDate(transaction.date) }}</div>
            <div class="transaction-desc">{{ transaction.description || '无备注' }}</div>
          </div>
          <div class="transaction-amount" :class="{ 'expense': transaction.type === 'expense' }">
            {{ transaction.type === 'expense' ? '-' : '+' }}{{ formatCurrency(transaction.amount) }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useFinanceStore } from '../stores/finance'
import dayjs from 'dayjs'

const router = useRouter()
const financeStore = useFinanceStore()

const currentDate = computed(() => {
  return dayjs().format('YYYY年MM月DD日')
})

const totalIncome = computed(() => financeStore.totalIncome)
const totalExpense = computed(() => financeStore.totalExpense)
const balance = computed(() => financeStore.balance)

const recentTransactions = computed(() => {
  // 合并支出和收入，按日期排序
  const transactions = [
    ...financeStore.expenses.map(expense => ({
      ...expense,
      type: 'expense'
    })),
    ...financeStore.incomes.map(income => ({
      ...income,
      type: 'income'
    }))
  ]
  
  return transactions
    .sort((a, b) => new Date(b.date) - new Date(a.date))
    .slice(0, 5)
})

const navigateTo = (path) => {
  router.push(path)
}

const formatCurrency = (amount) => {
  return '¥' + amount.toFixed(2)
}

const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD')
}

onMounted(async () => {
  await financeStore.initializeData()
})
</script>

<style scoped>
.home {
  padding: 20px;
  max-width: 600px;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h1 {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.date {
  font-size: 14px;
  color: #666;
}

.summary-card {
  display: flex;
  justify-content: space-between;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin-bottom: 20px;
}

.summary-item {
  flex: 1;
  text-align: center;
}

.summary-item .label {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.summary-item .amount {
  font-size: 20px;
  font-weight: bold;
}

.amount.income {
  color: #4caf50;
}

.amount.expense {
  color: #f44336;
}

.amount.balance {
  color: #2196f3;
}

.quick-actions {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.action-btn {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 16px;
  border-radius: 12px;
  border: none;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.action-btn.primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.action-btn.secondary {
  background: #f5f5f5;
  color: #333;
}

.action-btn .icon {
  font-size: 24px;
  margin-bottom: 8px;
}

.recent-transactions {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.recent-transactions h2 {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 16px;
}

.empty {
  text-align: center;
  color: #999;
  padding: 40px 0;
}

.transaction-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.transaction-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.transaction-item:last-child {
  border-bottom: none;
}

.transaction-info {
  flex: 1;
}

.transaction-category {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.transaction-date {
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.transaction-desc {
  font-size: 14px;
  color: #666;
}

.transaction-amount {
  font-size: 16px;
  font-weight: bold;
}

.transaction-amount.expense {
  color: #f44336;
}

.transaction-amount:not(.expense) {
  color: #4caf50;
}

@media (max-width: 480px) {
  .home {
    padding: 16px;
  }
  
  .summary-card {
    padding: 16px;
  }
  
  .summary-item .amount {
    font-size: 18px;
  }
  
  .action-btn {
    padding: 12px;
  }
  
  .recent-transactions {
    padding: 16px;
  }
}
</style>
