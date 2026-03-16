import localforage from "localforage";
import { ref, watch } from "vue";

// 配置 localforage
localforage.config({
  name: "spring-finance",
  storeName: "finance_data",
});

// 自定义 Hook，用于在 Vue 组件中使用本地存储
export function useLocalStorage(key, initialValue) {
  const storedValue = ref(initialValue);

  // 从本地存储加载数据
  const loadFromStorage = async () => {
    try {
      const value = await localforage.getItem(key);
      if (value !== null) {
        storedValue.value = value;
      }
    } catch (error) {
      console.error("Failed to load data from local storage:", error);
    }
  };

  // 保存数据到本地存储
  const saveToStorage = async (value) => {
    try {
      await localforage.setItem(key, value);
    } catch (error) {
      console.error("Failed to save data to local storage:", error);
    }
  };

  // 监听值的变化，自动保存到本地存储
  watch(
    storedValue,
    (newValue) => {
      saveToStorage(newValue);
    },
    { deep: true },
  );

  // 初始化时加载数据
  loadFromStorage();

  return storedValue;
}

// 导出 localforage 实例，用于其他地方直接使用
export { localforage };
