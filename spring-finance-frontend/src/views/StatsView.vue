<template>
  <div class="stats">
    <div class="header">
      <button class="back-btn" @click="goBack">
        <span>←</span>
      </button>
      <h1>账单统计</h1>
      <div class="empty"></div>
    </div>

    <div class="date-range">
      <button
        v-for="range in dateRanges"
        :key="range.value"
        class="range-btn"
        :class="{ active: selectedRange === range.value }"
        @click="selectedRange = range.value"
      >
        {{ range.label }}
      </button>
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

    <div class="chart-section">
      <h2>收支趋势</h2>
      <div ref="trendChartRef" class="chart"></div>
    </div>

    <div class="chart-section">
      <h2>支出分类</h2>
      <div ref="expenseChartRef" class="chart"></div>
    </div>

    <div class="chart-section">
      <h2>收入分类</h2>
      <div ref="incomeChartRef" class="chart"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick } from "vue";
import { useRouter } from "vue-router";
import { useFinanceStore } from "../stores/finance";
import * as echarts from "echarts";
import dayjs from "dayjs";

const router = useRouter();
const financeStore = useFinanceStore();

// 状态
const selectedRange = ref("month");
const trendChartRef = ref(null);
const expenseChartRef = ref(null);
const incomeChartRef = ref(null);

// 日期范围选项
const dateRanges = [
  { label: "日", value: "day" },
  { label: "周", value: "week" },
  { label: "月", value: "month" },
  { label: "年", value: "year" },
];

// 计算属性
const totalIncome = computed(() => financeStore.totalIncome);
const totalExpense = computed(() => financeStore.totalExpense);
const balance = computed(() => financeStore.balance);

// 方法
const goBack = () => {
  router.push("/");
};

const formatCurrency = (amount) => {
  return "¥" + amount.toFixed(2);
};

// 初始化图表
const initCharts = async () => {
  await nextTick();

  // 收支趋势图
  if (trendChartRef.value) {
    const trendChart = echarts.init(trendChartRef.value);
    updateTrendChart(trendChart);
  }

  // 支出分类图
  if (expenseChartRef.value) {
    const expenseChart = echarts.init(expenseChartRef.value);
    updateExpenseChart(expenseChart);
  }

  // 收入分类图
  if (incomeChartRef.value) {
    const incomeChart = echarts.init(incomeChartRef.value);
    updateIncomeChart(incomeChart);
  }
};

// 更新收支趋势图
const updateTrendChart = (chart) => {
  const { labels, incomeData, expenseData } = getTrendData();

  const option = {
    tooltip: {
      trigger: "axis",
      axisPointer: {
        type: "cross",
        label: {
          backgroundColor: "#6a7985",
        },
      },
    },
    legend: {
      data: ["收入", "支出"],
      top: 0,
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "3%",
      containLabel: true,
    },
    xAxis: {
      type: "category",
      boundaryGap: false,
      data: labels,
    },
    yAxis: {
      type: "value",
      axisLabel: {
        formatter: "¥{value}",
      },
    },
    series: [
      {
        name: "收入",
        type: "line",
        stack: "Total",
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: "rgba(76, 175, 80, 0.5)" },
            { offset: 1, color: "rgba(76, 175, 80, 0.1)" },
          ]),
        },
        emphasis: {
          focus: "series",
        },
        data: incomeData,
        itemStyle: {
          color: "#4caf50",
        },
      },
      {
        name: "支出",
        type: "line",
        stack: "Total",
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: "rgba(244, 67, 54, 0.5)" },
            { offset: 1, color: "rgba(244, 67, 54, 0.1)" },
          ]),
        },
        emphasis: {
          focus: "series",
        },
        data: expenseData,
        itemStyle: {
          color: "#f44336",
        },
      },
    ],
  };

  chart.setOption(option);
};

// 更新支出分类图
const updateExpenseChart = (chart) => {
  const { categories, values } = getCategoryData("expense");

  const option = {
    tooltip: {
      trigger: "item",
      formatter: "{a} <br/>{b}: ¥{c} ({d}%)",
    },
    legend: {
      orient: "vertical",
      left: "left",
      top: "center",
    },
    series: [
      {
        name: "支出分类",
        type: "pie",
        radius: "60%",
        center: ["60%", "50%"],
        data: categories.map((category, index) => ({
          value: values[index],
          name: category,
        })),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: "rgba(0, 0, 0, 0.5)",
          },
        },
      },
    ],
  };

  chart.setOption(option);
};

// 更新收入分类图
const updateIncomeChart = (chart) => {
  const { categories, values } = getCategoryData("income");

  const option = {
    tooltip: {
      trigger: "item",
      formatter: "{a} <br/>{b}: ¥{c} ({d}%)",
    },
    legend: {
      orient: "vertical",
      left: "left",
      top: "center",
    },
    series: [
      {
        name: "收入分类",
        type: "pie",
        radius: "60%",
        center: ["60%", "50%"],
        data: categories.map((category, index) => ({
          value: values[index],
          name: category,
        })),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: "rgba(0, 0, 0, 0.5)",
          },
        },
      },
    ],
  };

  chart.setOption(option);
};

// 获取趋势数据
const getTrendData = () => {
  const now = dayjs();
  let labels = [];
  let incomeData = [];
  let expenseData = [];

  switch (selectedRange.value) {
    case "day":
      // 最近7天
      for (let i = 6; i >= 0; i--) {
        const date = now.subtract(i, "day");
        labels.push(date.format("MM-DD"));
        incomeData.push(getDailyIncome(date));
        expenseData.push(getDailyExpense(date));
      }
      break;
    case "week":
      // 最近4周
      for (let i = 3; i >= 0; i--) {
        const startOfWeek = now.subtract(i, "week").startOf("week");
        labels.push(startOfWeek.format("MM-DD"));
        incomeData.push(getWeeklyIncome(startOfWeek));
        expenseData.push(getWeeklyExpense(startOfWeek));
      }
      break;
    case "month":
      // 最近6个月
      for (let i = 5; i >= 0; i--) {
        const date = now.subtract(i, "month");
        labels.push(date.format("YYYY-MM"));
        incomeData.push(getMonthlyIncome(date));
        expenseData.push(getMonthlyExpense(date));
      }
      break;
    case "year":
      // 最近3年
      for (let i = 2; i >= 0; i--) {
        const year = now.subtract(i, "year").year();
        labels.push(year.toString());
        incomeData.push(getYearlyIncome(year));
        expenseData.push(getYearlyExpense(year));
      }
      break;
  }

  return { labels, incomeData, expenseData };
};

// 获取分类数据
const getCategoryData = (type) => {
  const transactions =
    type === "expense" ? financeStore.expenses : financeStore.incomes;
  const categoryMap = new Map();

  transactions.forEach((transaction) => {
    const categoryName = transaction.category?.name || "未分类";
    const amount = transaction.amount;

    if (categoryMap.has(categoryName)) {
      categoryMap.set(categoryName, categoryMap.get(categoryName) + amount);
    } else {
      categoryMap.set(categoryName, amount);
    }
  });

  const categories = Array.from(categoryMap.keys());
  const values = Array.from(categoryMap.values());

  return { categories, values };
};

// 获取日收入
const getDailyIncome = (date) => {
  const startOfDay = date.startOf("day").format("YYYY-MM-DD");
  const endOfDay = date.endOf("day").format("YYYY-MM-DD");

  return financeStore.incomes
    .filter((income) => income.date >= startOfDay && income.date <= endOfDay)
    .reduce((sum, income) => sum + income.amount, 0);
};

// 获取日支出
const getDailyExpense = (date) => {
  const startOfDay = date.startOf("day").format("YYYY-MM-DD");
  const endOfDay = date.endOf("day").format("YYYY-MM-DD");

  return financeStore.expenses
    .filter((expense) => expense.date >= startOfDay && expense.date <= endOfDay)
    .reduce((sum, expense) => sum + expense.amount, 0);
};

// 获取周收入
const getWeeklyIncome = (startOfWeek) => {
  const endOfWeek = startOfWeek.endOf("week");
  const startDate = startOfWeek.format("YYYY-MM-DD");
  const endDate = endOfWeek.format("YYYY-MM-DD");

  return financeStore.incomes
    .filter((income) => income.date >= startDate && income.date <= endDate)
    .reduce((sum, income) => sum + income.amount, 0);
};

// 获取周支出
const getWeeklyExpense = (startOfWeek) => {
  const endOfWeek = startOfWeek.endOf("week");
  const startDate = startOfWeek.format("YYYY-MM-DD");
  const endDate = endOfWeek.format("YYYY-MM-DD");

  return financeStore.expenses
    .filter((expense) => expense.date >= startDate && expense.date <= endDate)
    .reduce((sum, expense) => sum + expense.amount, 0);
};

// 获取月收入
const getMonthlyIncome = (date) => {
  const year = date.year();
  const month = date.month() + 1;

  return financeStore.incomes
    .filter((income) => {
      const incomeDate = dayjs(income.date);
      return incomeDate.year() === year && incomeDate.month() + 1 === month;
    })
    .reduce((sum, income) => sum + income.amount, 0);
};

// 获取月支出
const getMonthlyExpense = (date) => {
  const year = date.year();
  const month = date.month() + 1;

  return financeStore.expenses
    .filter((expense) => {
      const expenseDate = dayjs(expense.date);
      return expenseDate.year() === year && expenseDate.month() + 1 === month;
    })
    .reduce((sum, expense) => sum + expense.amount, 0);
};

// 获取年收入
const getYearlyIncome = (year) => {
  return financeStore.incomes
    .filter((income) => dayjs(income.date).year() === year)
    .reduce((sum, income) => sum + income.amount, 0);
};

// 获取年支出
const getYearlyExpense = (year) => {
  return financeStore.expenses
    .filter((expense) => dayjs(expense.date).year() === year)
    .reduce((sum, expense) => sum + expense.amount, 0);
};

// 监听日期范围变化
watch(selectedRange, () => {
  initCharts();
});

// 初始化
onMounted(async () => {
  await financeStore.initializeData();
  initCharts();

  // 监听窗口大小变化，调整图表大小
  window.addEventListener("resize", () => {
    if (trendChartRef.value) {
      echarts.getInstanceByDom(trendChartRef.value)?.resize();
    }
    if (expenseChartRef.value) {
      echarts.getInstanceByDom(expenseChartRef.value)?.resize();
    }
    if (incomeChartRef.value) {
      echarts.getInstanceByDom(incomeChartRef.value)?.resize();
    }
  });
});
</script>

<style scoped>
.stats {
  padding: 20px;
  max-width: 800px;
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

.date-range {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
}

.range-btn {
  flex: 1;
  padding: 10px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background: white;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.range-btn.active {
  background: #667eea;
  color: white;
  border-color: #667eea;
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

.chart-section {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin-bottom: 20px;
}

.chart-section h2 {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 16px;
}

.chart {
  width: 100%;
  height: 300px;
}

@media (max-width: 480px) {
  .stats {
    padding: 16px;
  }

  .summary-card {
    padding: 16px;
  }

  .summary-item .amount {
    font-size: 18px;
  }

  .chart-section {
    padding: 16px;
  }

  .chart {
    height: 250px;
  }
}
</style>
