<template>
  <div class="record">
    <div class="header">
      <button class="back-btn" @click="goBack">
        <span>←</span>
      </button>
      <h1>{{ isExpense ? '记支出' : '记收入' }}</h1>
      <div class="empty"></div>
    </div>
    
    <div class="amount-input">
      <div class="currency-symbol">¥</div>
      <input 
        type="number" 
        v-model.number="form.amount" 
        placeholder="0.00" 
        class="amount-field"
        step="0.01"
        min="0"
      />
    </div>
    
    <div class="form-section">
      <div class="form-item">
        <div class="label">分类</div>
        <div class="value" @click="showCategoryPicker = true">
          {{ selectedCategory?.name || '请选择分类' }}
          <span class="arrow">→</span>
        </div>
      </div>
      
      <div class="form-item">
        <div class="label">日期</div>
        <div class="value" @click="showDatePicker = true">
          {{ formatDate(form.date) }}
          <span class="arrow">→</span>
        </div>
      </div>
      
      <div class="form-item">
        <div class="label">备注</div>
        <input 
          type="text" 
          v-model="form.description" 
          placeholder="请输入备注" 
          class="input-field"
        />
      </div>
    </div>
    
    <div class="type-toggle">
      <button 
        class="toggle-btn" 
        :class="{ active: isExpense }"
        @click="isExpense = true"
      >
        支出
      </button>
      <button 
        class="toggle-btn" 
        :class="{ active: !isExpense }"
        @click="isExpense = false"
      >
        收入
      </button>
    </div>
    
    <button class="submit-btn" @click="submitForm">
      保存
    </button>
    
    <!-- 分类选择器 -->
    <div v-if="showCategoryPicker" class="picker-overlay" @click="showCategoryPicker = false">
      <div class="picker-content" @click.stop>
        <div class="picker-header">
          <h3>选择分类</h3>
          <button class="close-btn" @click="showCategoryPicker = false">✕</button>
        </div>
        <div class="category-list">
          <div 
            v-for="category in filteredCategories" 
            :key="category.id"
            class="category-item"
            @click="selectCategory(category)"
          >
            <div class="category-name">{{ category.name }}</div>
            <div class="category-desc">{{ category.description || '' }}</div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 日期选择器 -->
    <div v-if="showDatePicker" class="picker-overlay" @click="showDatePicker = false">
      <div class="picker-content" @click.stop>
        <div class="picker-header">
          <h3>选择日期</h3>
          <button class="close-btn" @click="showDatePicker = false">✕</button>
        </div>
        <div class="date-picker">
          <input 
            type="date" 
            v-model="form.date" 
            class="date-input"
            @change="showDatePicker = false"
          />
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
const isExpense = ref(true)
const showCategoryPicker = ref(false)
const showDatePicker = ref(false)
const selectedCategory = ref(null)

// 表单数据
const form = ref({
  amount: '',
  date: dayjs().format('YYYY-MM-DD'),
  description: ''
})

// 计算属性
const filteredCategories = computed(() => {
  return isExpense.value 
    ? financeStore.expenseCategories 
    : financeStore.incomeCategories
})

// 方法
const goBack = () => {
  router.push('/')
}

const selectCategory = (category) => {
  selectedCategory.value = category
  showCategoryPicker.value = false
}

const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD')
}

const submitForm = async () => {
  if (!form.value.amount || form.value.amount <= 0) {
    alert('请输入有效的金额')
    return
  }
  
  if (!selectedCategory.value) {
    alert('请选择分类')
    return
  }
  
  const transaction = {
    amount: form.value.amount,
    date: form.value.date,
    description: form.value.description,
    category: {
      id: selectedCategory.value.id
    }
  }
  
  try {
    if (isExpense.value) {
      await financeStore.addExpense(transaction)
    } else {
      await financeStore.addIncome(transaction)
    }
    
    // 重置表单
    form.value = {
      amount: '',
      date: dayjs().format('YYYY-MM-DD'),
      description: ''
    }
    selectedCategory.value = null
    
    // 跳转到首页
    router.push('/')
  } catch (error) {
    console.error('Failed to save transaction:', error)
    alert('保存失败，请重试')
  }
}

// 初始化
onMounted(async () => {
  await financeStore.initializeData()
  
  // 自动选择第一个分类
  if (filteredCategories.value.length > 0) {
    selectedCategory.value = filteredCategories.value[0]
  }
})
</script>

<style scoped>
.record {
  padding: 20px;
  max-width: 600px;
  margin: 0 auto;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
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

.amount-input {
  display: flex;
  align-items: baseline;
  margin-bottom: 40px;
}

.currency-symbol {
  font-size: 32px;
  font-weight: bold;
  color: #333;
  margin-right: 12px;
}

.amount-field {
  flex: 1;
  border: none;
  font-size: 48px;
  font-weight: bold;
  color: #333;
  outline: none;
  padding: 0;
}

.form-section {
  flex: 1;
  margin-bottom: 40px;
}

.form-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.form-item:last-child {
  border-bottom: none;
}

.label {
  font-size: 16px;
  color: #333;
}

.value {
  font-size: 16px;
  color: #666;
  display: flex;
  align-items: center;
  cursor: pointer;
}

.arrow {
  margin-left: 8px;
  font-size: 14px;
}

.input-field {
  border: none;
  font-size: 16px;
  color: #333;
  outline: none;
  text-align: right;
  flex: 1;
  margin-left: 20px;
}

.type-toggle {
  display: flex;
  margin-bottom: 24px;
  border-radius: 8px;
  overflow: hidden;
  background: #f5f5f5;
}

.toggle-btn {
  flex: 1;
  padding: 12px;
  border: none;
  background: transparent;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.toggle-btn.active {
  background: #667eea;
  color: white;
}

.submit-btn {
  width: 100%;
  padding: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

/* 选择器样式 */
.picker-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  z-index: 1000;
}

.picker-content {
  background: white;
  border-radius: 12px 12px 0 0;
  width: 100%;
  max-height: 70vh;
  overflow-y: auto;
}

.picker-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.picker-header h3 {
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

.category-list {
  padding: 16px;
}

.category-item {
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
}

.category-item:last-child {
  border-bottom: none;
}

.category-item:hover {
  background: #f5f5f5;
  border-radius: 8px;
}

.category-name {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.category-desc {
  font-size: 14px;
  color: #999;
}

.date-picker {
  padding: 16px;
}

.date-input {
  width: 100%;
  padding: 12px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 16px;
}

@media (max-width: 480px) {
  .record {
    padding: 16px;
  }
  
  .amount-field {
    font-size: 36px;
  }
  
  .currency-symbol {
    font-size: 24px;
  }
}
</style>
