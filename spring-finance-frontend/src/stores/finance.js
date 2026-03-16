import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { useLocalStorage } from '../utils/storage'
import { financeService } from '../services/financeService'

export const useFinanceStore = defineStore('finance', () => {
  // 状态
  const categories = ref([])
  const expenses = ref([])
  const incomes = ref([])
  const budgets = ref([])
  const accounts = ref([])
  const loading = ref(false)
  
  // 本地存储
  const savedCategories = useLocalStorage('categories', [])
  const savedExpenses = useLocalStorage('expenses', [])
  const savedIncomes = useLocalStorage('incomes', [])
  const savedBudgets = useLocalStorage('budgets', [])
  const savedAccounts = useLocalStorage('accounts', [])
  
  // 初始化数据
  const initializeData = async () => {
    loading.value = true
    try {
      // 尝试从后端获取数据
      const [categoriesData, expensesData, incomesData] = await Promise.all([
        financeService.getCategories(),
        financeService.getExpenses(),
        financeService.getIncomes()
      ])
      
      categories.value = categoriesData
      expenses.value = expensesData
      incomes.value = incomesData
      
      // 同步到本地存储
      savedCategories.value = categoriesData
      savedExpenses.value = expensesData
      savedIncomes.value = incomesData
    } catch (error) {
      console.error('Failed to fetch data from backend:', error)
      // 从本地存储恢复数据
      categories.value = savedCategories.value
      expenses.value = savedExpenses.value
      incomes.value = savedIncomes.value
      budgets.value = savedBudgets.value
      accounts.value = savedAccounts.value
    } finally {
      loading.value = false
    }
  }
  
  // 分类相关操作
  const addCategory = async (category) => {
    try {
      const newCategory = await financeService.createCategory(category)
      categories.value.push(newCategory)
      savedCategories.value = categories.value
      return newCategory
    } catch (error) {
      console.error('Failed to add category:', error)
      // 本地添加
      category.id = Date.now()
      categories.value.push(category)
      savedCategories.value = categories.value
      return category
    }
  }
  
  const updateCategory = async (category) => {
    try {
      const updatedCategory = await financeService.updateCategory(category.id, category)
      const index = categories.value.findIndex(c => c.id === category.id)
      if (index !== -1) {
        categories.value[index] = updatedCategory
        savedCategories.value = categories.value
      }
      return updatedCategory
    } catch (error) {
      console.error('Failed to update category:', error)
      // 本地更新
      const index = categories.value.findIndex(c => c.id === category.id)
      if (index !== -1) {
        categories.value[index] = category
        savedCategories.value = categories.value
      }
      return category
    }
  }
  
  const deleteCategory = async (categoryId) => {
    try {
      await financeService.deleteCategory(categoryId)
      categories.value = categories.value.filter(c => c.id !== categoryId)
      savedCategories.value = categories.value
    } catch (error) {
      console.error('Failed to delete category:', error)
      // 本地删除
      categories.value = categories.value.filter(c => c.id !== categoryId)
      savedCategories.value = categories.value
    }
  }
  
  // 支出相关操作
  const addExpense = async (expense) => {
    try {
      const newExpense = await financeService.createExpense(expense)
      expenses.value.push(newExpense)
      savedExpenses.value = expenses.value
      return newExpense
    } catch (error) {
      console.error('Failed to add expense:', error)
      // 本地添加
      expense.id = Date.now()
      expenses.value.push(expense)
      savedExpenses.value = expenses.value
      return expense
    }
  }
  
  const updateExpense = async (expense) => {
    try {
      const updatedExpense = await financeService.updateExpense(expense.id, expense)
      const index = expenses.value.findIndex(e => e.id === expense.id)
      if (index !== -1) {
        expenses.value[index] = updatedExpense
        savedExpenses.value = expenses.value
      }
      return updatedExpense
    } catch (error) {
      console.error('Failed to update expense:', error)
      // 本地更新
      const index = expenses.value.findIndex(e => e.id === expense.id)
      if (index !== -1) {
        expenses.value[index] = expense
        savedExpenses.value = expenses.value
      }
      return expense
    }
  }
  
  const deleteExpense = async (expenseId) => {
    try {
      await financeService.deleteExpense(expenseId)
      expenses.value = expenses.value.filter(e => e.id !== expenseId)
      savedExpenses.value = expenses.value
    } catch (error) {
      console.error('Failed to delete expense:', error)
      // 本地删除
      expenses.value = expenses.value.filter(e => e.id !== expenseId)
      savedExpenses.value = expenses.value
    }
  }
  
  // 收入相关操作
  const addIncome = async (income) => {
    try {
      const newIncome = await financeService.createIncome(income)
      incomes.value.push(newIncome)
      savedIncomes.value = incomes.value
      return newIncome
    } catch (error) {
      console.error('Failed to add income:', error)
      // 本地添加
      income.id = Date.now()
      incomes.value.push(income)
      savedIncomes.value = incomes.value
      return income
    }
  }
  
  const updateIncome = async (income) => {
    try {
      const updatedIncome = await financeService.updateIncome(income.id, income)
      const index = incomes.value.findIndex(i => i.id === income.id)
      if (index !== -1) {
        incomes.value[index] = updatedIncome
        savedIncomes.value = incomes.value
      }
      return updatedIncome
    } catch (error) {
      console.error('Failed to update income:', error)
      // 本地更新
      const index = incomes.value.findIndex(i => i.id === income.id)
      if (index !== -1) {
        incomes.value[index] = income
        savedIncomes.value = incomes.value
      }
      return income
    }
  }
  
  const deleteIncome = async (incomeId) => {
    try {
      await financeService.deleteIncome(incomeId)
      incomes.value = incomes.value.filter(i => i.id !== incomeId)
      savedIncomes.value = incomes.value
    } catch (error) {
      console.error('Failed to delete income:', error)
      // 本地删除
      incomes.value = incomes.value.filter(i => i.id !== incomeId)
      savedIncomes.value = incomes.value
    }
  }
  
  // 预算相关操作
  const addBudget = (budget) => {
    budget.id = Date.now()
    budgets.value.push(budget)
    savedBudgets.value = budgets.value
  }
  
  const updateBudget = (budget) => {
    const index = budgets.value.findIndex(b => b.id === budget.id)
    if (index !== -1) {
      budgets.value[index] = budget
      savedBudgets.value = budgets.value
    }
  }
  
  const deleteBudget = (budgetId) => {
    budgets.value = budgets.value.filter(b => b.id !== budgetId)
    savedBudgets.value = budgets.value
  }
  
  // 账户相关操作
  const addAccount = (account) => {
    account.id = Date.now()
    accounts.value.push(account)
    savedAccounts.value = accounts.value
  }
  
  const updateAccount = (account) => {
    const index = accounts.value.findIndex(a => a.id === account.id)
    if (index !== -1) {
      accounts.value[index] = account
      savedAccounts.value = accounts.value
    }
  }
  
  const deleteAccount = (accountId) => {
    accounts.value = accounts.value.filter(a => a.id !== accountId)
    savedAccounts.value = accounts.value
  }
  
  // 计算属性
  const totalExpense = computed(() => {
    return expenses.value.reduce((sum, expense) => sum + expense.amount, 0)
  })
  
  const totalIncome = computed(() => {
    return incomes.value.reduce((sum, income) => sum + income.amount, 0)
  })
  
  const balance = computed(() => {
    return totalIncome.value - totalExpense.value
  })
  
  const expenseCategories = computed(() => {
    return categories.value.filter(c => c.type === 'EXPENSE')
  })
  
  const incomeCategories = computed(() => {
    return categories.value.filter(c => c.type === 'INCOME')
  })
  
  return {
    // 状态
    categories,
    expenses,
    incomes,
    budgets,
    accounts,
    loading,
    
    // 计算属性
    totalExpense,
    totalIncome,
    balance,
    expenseCategories,
    incomeCategories,
    
    // 方法
    initializeData,
    addCategory,
    updateCategory,
    deleteCategory,
    addExpense,
    updateExpense,
    deleteExpense,
    addIncome,
    updateIncome,
    deleteIncome,
    addBudget,
    updateBudget,
    deleteBudget,
    addAccount,
    updateAccount,
    deleteAccount
  }
})
