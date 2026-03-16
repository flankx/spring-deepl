<template>
  <div class="app">
    <router-view />
    <div class="bottom-nav">
      <div 
        v-for="item in navItems" 
        :key="item.path"
        class="nav-item"
        :class="{ active: $route.path === item.path }"
        @click="$router.push(item.path)"
      >
        <div class="nav-icon">{{ item.icon }}</div>
        <div class="nav-text">{{ item.text }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
const navItems = [
  { path: '/', icon: '🏠', text: '首页' },
  { path: '/record', icon: '+', text: '记账' },
  { path: '/stats', icon: '📊', text: '统计' },
  { path: '/profile', icon: '👤', text: '我的' }
]
</script>

<style scoped>
.app {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 60px;
}

.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  background: white;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  height: 60px;
  z-index: 100;
}

.nav-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.nav-item.active {
  color: #667eea;
}

.nav-icon {
  font-size: 20px;
  margin-bottom: 4px;
}

.nav-text {
  font-size: 12px;
}
</style>
