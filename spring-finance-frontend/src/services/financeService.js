import axios from "axios";

// 创建 axios 实例
const apiClient = axios.create({
  baseURL: "http://localhost:8080/api/finance",
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
});

// 分类相关 API
const getCategories = async () => {
  try {
    const response = await apiClient.get("/categories");
    return response.data;
  } catch (error) {
    throw error;
  }
};

const createCategory = async (category) => {
  try {
    const response = await apiClient.post("/categories", category);
    return response.data;
  } catch (error) {
    throw error;
  }
};

const updateCategory = async (id, category) => {
  try {
    const response = await apiClient.put(`/categories/${id}`, category);
    return response.data;
  } catch (error) {
    throw error;
  }
};

const deleteCategory = async (id) => {
  try {
    await apiClient.delete(`/categories/${id}`);
  } catch (error) {
    throw error;
  }
};

// 支出相关 API
const getExpenses = async () => {
  try {
    const response = await apiClient.get("/expenses");
    return response.data;
  } catch (error) {
    throw error;
  }
};

const getExpensesByDateRange = async (startDate, endDate) => {
  try {
    const response = await apiClient.get("/expenses/date-range", {
      params: { startDate, endDate },
    });
    return response.data;
  } catch (error) {
    throw error;
  }
};

const createExpense = async (expense) => {
  try {
    const response = await apiClient.post("/expenses", expense);
    return response.data;
  } catch (error) {
    throw error;
  }
};

const updateExpense = async (id, expense) => {
  try {
    const response = await apiClient.put(`/expenses/${id}`, expense);
    return response.data;
  } catch (error) {
    throw error;
  }
};

const deleteExpense = async (id) => {
  try {
    await apiClient.delete(`/expenses/${id}`);
  } catch (error) {
    throw error;
  }
};

const getTotalExpense = async (startDate, endDate) => {
  try {
    const response = await apiClient.get("/expenses/total", {
      params: { startDate, endDate },
    });
    return response.data;
  } catch (error) {
    throw error;
  }
};

// 收入相关 API
const getIncomes = async () => {
  try {
    const response = await apiClient.get("/incomes");
    return response.data;
  } catch (error) {
    throw error;
  }
};

const getIncomesByDateRange = async (startDate, endDate) => {
  try {
    const response = await apiClient.get("/incomes/date-range", {
      params: { startDate, endDate },
    });
    return response.data;
  } catch (error) {
    throw error;
  }
};

const createIncome = async (income) => {
  try {
    const response = await apiClient.post("/incomes", income);
    return response.data;
  } catch (error) {
    throw error;
  }
};

const updateIncome = async (id, income) => {
  try {
    const response = await apiClient.put(`/incomes/${id}`, income);
    return response.data;
  } catch (error) {
    throw error;
  }
};

const deleteIncome = async (id) => {
  try {
    await apiClient.delete(`/incomes/${id}`);
  } catch (error) {
    throw error;
  }
};

const getTotalIncome = async (startDate, endDate) => {
  try {
    const response = await apiClient.get("/incomes/total", {
      params: { startDate, endDate },
    });
    return response.data;
  } catch (error) {
    throw error;
  }
};

// 财务汇总 API
const getSummary = async (startDate, endDate) => {
  try {
    const response = await apiClient.get("/summary", {
      params: { startDate, endDate },
    });
    return response.data;
  } catch (error) {
    throw error;
  }
};

// 导出服务
export const financeService = {
  // 分类相关
  getCategories,
  createCategory,
  updateCategory,
  deleteCategory,

  // 支出相关
  getExpenses,
  getExpensesByDateRange,
  createExpense,
  updateExpense,
  deleteExpense,
  getTotalExpense,

  // 收入相关
  getIncomes,
  getIncomesByDateRange,
  createIncome,
  updateIncome,
  deleteIncome,
  getTotalIncome,

  // 财务汇总
  getSummary,
};
