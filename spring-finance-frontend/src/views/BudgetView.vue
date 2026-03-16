<template>
  <div class="budget">
    <div class="header">
      <button class="back-btn" @click="goBack">
        <span>←</span>
      </button>
      <h1>预算管理</h1>
      <div class="empty"></div>
    </div>
    
    <div class="add-budget-btn">
      <button class="primary-btn" @click="showAddBudgetDialog = true">
        + 添加预算
      </button>
    </div>
    
    <div class="budget-list">
      <h2>预算列表</h2>
      <div v-if="budgets.length === 0" class="empty">
        暂无预算设置
      </div>
      <div v-else class="budget-items">
        <div 
          v-for="budget in budgets" 
          :key="budget.id"
          class="budget-item"
          :class="{ 'over-budget': budget.used > budget.amount }"
        >
          <div class="budget-info">
            <div class="budget-category">{{ budget.categoryName }}</div>
            <div class="budget-period">{{ budget.period }}预算</div>
          </div>
          <div class="budget-progress">
            <div class="progress-bar">
              <div 
                class="progress-fill"
                :style="{ width: `${Math.min((budget.used / budget.amount) * 100, 100)}%` }"
                :class="{ 'over-budget': budget.used > budget.amount }"
              ></div>
            </div>
            <div class="budget-amounts">
              <span class="used">{{ formatCurrency(budget.used) }}</span>
              <span class="total">/{{ formatCurrency(budget.amount) }}</span>
            </div>
          </div>
          <div class="budget-actions">
            <button class="edit-btn" @click="editBudget(budget)">
              编辑
            </button>
            <button class="delete-btn" @click="deleteBudget(budget.id)">
              删除
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 添加/编辑预算对话框 -->
    <div v-if="showAddBudgetDialog" class="dialog-overlay" @click="showAddBudgetDialog = false">
      <div class="dialog-content" @click.stop>
        <div class="dialog-header">
          <h3>{{ editingBudget ? '编辑预算' : '添加预算' }}</h3>
          <button class="close-btn" @click="showAddBudgetDialog = false">✕</button>
        </div>
        <div class="dialog-body">
          <div class="form-item">
            <label>分类</label>
            <select v-model="budgetForm.categoryId" class="select-field">
              <option value="">请选择分类</option>
              <option 
                v-for="category in expenseCategories" 
                :key="category.id"
                :value="category.id"
              >
                {{ category.name }}
              </option>
            </select>
          </div>
          <div class="form-item">
            <label>预算金额</label>
            <input 
              type="number" 
              v-model.number="budgetForm.amount" 
              placeholder="0.00" 
              class="input-field"
              step="0.01"
              min="0"
            />
          </div>
          <div class="form-item">
            <label>预算周期</label>
            <select v-model="budgetForm.period" class="select-field">
              <option value="日">日</option>
              <option value="周">周</option>
              <option value="月">月</option>
              <option value="年">年</option>
            </select>
          </div>
        </div>
        <div class="dialog-footer">
          <button class="cancel-btn" @click="showAddBudgetDialog = false">
            取消
          </button>
          <button class="save-btn" @click="saveBudget">
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
import dayjs from 'dayjs'

const router = useRouter()
const financeStore = useFinanceStore()

// 状态
const showAddBudgetDialog = ref(false)
const editingBudget = ref(null)

// 预算表单
const budgetForm = ref({
  categoryId: '',
  amount: '',
  period: '月'
})

// 计算属性
const budgets = computed(() => {
  return financeStore.budgets.map(budget => {
    const category = financeStore.categories.find(c => c.id === budget.categoryId)
    const used = calculateBudgetUsed(budget)
    
    return {
      ...budget,
      categoryName: category?.name || '未分类',
      used
    }
  })
})

const expenseCategories = computed(() => financeStore.expenseCategories)

// 方法
const goBack = () => {
  router.push('/')
}

const formatCurrency = (amount) => {
  return '¥' + amount.toFixed(2)
}

const calculateBudgetUsed = (budget) => {
  const now = dayjs()
  let startDate, endDate
  
  switch (budget.period) {
    case '日':
      startDate = now.startOf('day').format('YYYY-MM-DD')
      endDate = now.endOf('day').format('YYYY-MM-DD')
      break
    case '周':
      startDate = now.startOf('week').format('YYYY-MM-DD')
      endDate = now.endOf('week').format('YYYY-MM-DD')
      break
    case '月':
      startDate = now.startOf('month').format('YYYY-MM-DD')
      endDate = now.endOf('month').format('YYYY-MM-DD')
      break
    case '年':
      startDate = now.startOf('year').format('YYYY-MM-DD')
      endDate = now.endOf('year').format('YYYY-MM-DD')
      break
  }
  
  return financeStore.expenses
    .filter(expense => {
      return expense.category?.id === budget.categoryId && 
             expense.date >= startDate && 
             expense.date <= endDate
    })
    .reduce((sum, expense) => sum + expense.amount, 0)
}

const addBudget = () => {
  editingBudget.value = null
  budgetForm.value = {
    categoryId: '',
    amount: '',
    period: '月'
  }
  showAddBudgetDialog.value = true
}

const editBudget = (budget) => {
  editingBudget.value = budget
  budgetForm.value = {
    categoryId: budget.categoryId,
    amount: budget.amount,
    period: budget.period
  }
  showAddBudgetDialog.value = true
}

const saveBudget = () => {
  if (!budgetForm.value.categoryId) {
    alert('请选择分类')
    return
  }
  
  if (!budgetForm.value.amount || budgetForm.value.amount <= 0) {
    alert('请输入有效的预算金额')
    return
  }
  
  const budget = {
    id: editingBudget.value?.id || Date.now(),
    categoryId: budgetForm.value.categoryId,
    amount: budgetForm.value.amount,
    period: budgetForm.value.period
  }
  
  if (editingBudget.value) {
    financeStore.updateBudget(budget)
  } else {
    financeStore.addBudget(budget)
  }
  
  showAddBudgetDialog.value = false
}

const deleteBudget = (budgetId) => {
  if (confirm('确定要删除这个预算吗？')) {
    financeStore.deleteBudget(budgetId)
  }
}

// 初始化
onMounted(async () => {
  await financeStore.initializeData()
})
</script>

<style scoped>
.budget {
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

.add-budget-btn {
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

.budget-list {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.budget-list h2 {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 16px;
}

.budget-list .empty {
  text-align: center;
  color: #999;
  padding: 40px 0;
  width: 100%;
}

.budget-items {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.budget-item {
  padding: 16px;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.budget-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.budget-item.over-budget {
  border-color: #f44336;
  background: rgba(244, 67, 54, 0.05);
}

.budget-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.budget-category {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.budget-period {
  font-size: 14px;
  color: #666;
}

.budget-progress {
  margin-bottom: 12px;
}

.progress-bar {
  width: 100%;
  height: 8px;
  background: #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 8px;
}

.progress-fill {
  height: 100%;
  background: #4caf50;
  border-radius: 4px;
  transition: width 0.3s ease;
}

.progress-fill.over-budget {
  background: #f44336;
}

.budget-amounts {
  display: flex;
  justify-content: flex-end;
  font-size: 14px;
}

.used {
  font-weight: bold;
  color: #333;
}

.total {
  color: #666;
  margin-left: 4px;
}

.budget-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
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
  .budget {
    padding: 16px;
  }
  
  .budget-list {
    padding: 16px;
  }
}
</style>
