<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '72px' : '230px'" class="aside">
      <!-- Logo 区域 -->
      <div class="logo-area" :class="{ collapsed: isCollapse }">
        <div class="logo-icon">
          <svg viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect width="40" height="40" rx="10" fill="url(#logoGrad)"/>
            <path d="M12 16h16M12 20h12M12 24h8" stroke="#fff" stroke-width="2.5" stroke-linecap="round"/>
            <defs>
              <linearGradient id="logoGrad" x1="0" y1="0" x2="40" y2="40">
                <stop stop-color="#fff"/>
                <stop offset="1" stop-color="#d1fae5"/>
              </linearGradient>
            </defs>
          </svg>
        </div>
        <transition name="fade">
          <div v-show="!isCollapse" class="logo-text">
            <span class="logo-title">QuizAI</span>
            <span class="logo-sub">Admin Console</span>
          </div>
        </transition>
      </div>

      <!-- 导航菜单 -->
      <div class="nav-wrapper">
        <div class="nav-section-label" v-show="!isCollapse">主菜单</div>
        <div
          v-for="item in menuItems"
          :key="item.path"
          class="nav-item"
          :class="{ active: isActive(item.path) }"
          @click="navigate(item.path)"
        >
          <div class="nav-item-bg"></div>
          <div class="nav-item-accent"></div>
          <el-icon class="nav-icon" :size="20">
            <component :is="item.icon" />
          </el-icon>
          <span class="nav-label" v-show="!isCollapse">{{ item.label }}</span>
          <div v-if="isActive(item.path) && !isCollapse" class="active-dot"></div>
        </div>
      </div>

      <!-- 底部折叠按钮 -->
      <div class="collapse-btn" @click="isCollapse = !isCollapse">
        <el-icon :size="18" class="collapse-icon">
          <DArrowLeft v-if="!isCollapse" />
          <DArrowRight v-else />
        </el-icon>
      </div>
    </el-aside>

    <!-- 主体区域 -->
    <el-container class="main-container">
      <!-- 头部 -->
      <el-header class="header">
        <div class="header-breadcrumb">
          <el-icon class="breadcrumb-icon"><HomeFilled /></el-icon>
          <span class="breadcrumb-sep">/</span>
          <span class="breadcrumb-current">{{ currentPageName }}</span>
        </div>
        <div class="header-right">
          <div class="header-time">{{ currentTime }}</div>
          <el-dropdown @command="handleCommand" trigger="click">
            <div class="user-avatar">
              <div class="avatar-circle">A</div>
              <span class="avatar-name">Admin</span>
              <el-icon class="avatar-arrow"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  <span>退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区 -->
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  User, Menu, Document, ArrowDown, DArrowLeft, DArrowRight,
  HomeFilled, SwitchButton
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const isCollapse = ref(false)
const currentTime = ref('')

const menuItems = [
  { path: '/user', label: '用户管理', icon: User },
  { path: '/category', label: '分类管理', icon: Menu },
  { path: '/question', label: '题目管理', icon: Document },
]

const isActive = (path) => route.path === path

const navigate = (path) => {
  router.push(path)
}

const currentPageName = computed(() => {
  const item = menuItems.find(m => m.path === route.path)
  return item ? item.label : '仪表盘'
})

let timer = null
const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('zh-CN', { hour12: false })
}
onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
})
onUnmounted(() => {
  clearInterval(timer)
})

const handleCommand = (command) => {
  if (command === 'logout') {
    localStorage.removeItem('adminToken')
    localStorage.removeItem('adminInfo')
    router.push('/login')
  }
}
</script>

<style scoped>
/* ========== 布局容器 ========== */
.layout-container {
  height: 100vh;
  background: #f0f2f5;
}

/* ========== 侧边栏 ========== */
.aside {
  position: relative;
  background: linear-gradient(175deg, #10b981 0%, #059669 40%, #047857 100%);
  overflow: hidden;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 20px rgba(0, 0, 0, 0.12);
}

/* 侧边栏微妙纹理 */
.aside::before {
  content: '';
  position: absolute;
  inset: 0;
  background:
    radial-gradient(ellipse at 50% 0%, rgba(255, 255, 255, 0.15) 0%, transparent 60%),
    radial-gradient(ellipse at 80% 100%, rgba(255, 255, 255, 0.08) 0%, transparent 50%);
  pointer-events: none;
  z-index: 0;
}

/* ========== Logo 区域 ========== */
.logo-area {
  position: relative;
  z-index: 1;
  padding: 24px 20px;
  display: flex;
  align-items: center;
  gap: 14px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);
  transition: padding 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.logo-area.collapsed {
  padding: 20px 16px;
  justify-content: center;
}

.logo-icon {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.15));
}

.logo-icon svg {
  width: 100%;
  height: 100%;
}

.logo-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
  overflow: hidden;
  white-space: nowrap;
}

.logo-title {
  font-size: 18px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 0.5px;
  line-height: 1.2;
}

.logo-sub {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.7);
  font-weight: 400;
  letter-spacing: 1px;
  text-transform: uppercase;
}

/* ========== 导航区域 ========== */
.nav-wrapper {
  flex: 1;
  padding: 16px 12px;
  position: relative;
  z-index: 1;
  overflow-y: auto;
  overflow-x: hidden;
}

.nav-wrapper::-webkit-scrollbar {
  width: 3px;
}

.nav-wrapper::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 3px;
}

.nav-section-label {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.55);
  text-transform: uppercase;
  letter-spacing: 2px;
  padding: 0 12px 10px;
  font-weight: 600;
}

.nav-item {
  position: relative;
  display: flex;
  align-items: center;
  height: 46px;
  padding: 0 16px;
  margin-bottom: 4px;
  border-radius: 10px;
  cursor: pointer;
  color: rgba(255, 255, 255, 0.85);
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  user-select: none;
  overflow: hidden;
}

.nav-item-bg {
  position: absolute;
  inset: 0;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0);
  transition: background 0.3s ease;
}

.nav-item-accent {
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 0;
  border-radius: 0 4px 4px 0;
  background: #fff;
  transition: height 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.nav-item:hover {
  color: #fff;
  border-color: rgba(255, 255, 255, 0.25);
}

.nav-item:hover .nav-item-bg {
  background: rgba(255, 255, 255, 0.12);
}

.nav-item.active {
  color: #047857;
  border-color: transparent;
}

.nav-item.active .nav-item-bg {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.nav-item.active .nav-item-accent {
  height: 24px;
  background: #10b981;
}

.nav-icon {
  position: relative;
  z-index: 1;
  flex-shrink: 0;
  transition: transform 0.25s ease;
}

.nav-item:hover .nav-icon {
  transform: scale(1.08);
}

.nav-item.active .nav-icon {
  color: #047857;
}

.nav-label {
  position: relative;
  z-index: 1;
  margin-left: 12px;
  font-size: 14px;
  font-weight: 500;
  white-space: nowrap;
  transition: opacity 0.2s ease;
}

.active-dot {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #10b981;
  box-shadow: 0 0 6px rgba(16, 185, 129, 0.4);
}

/* ========== 折叠按钮 ========== */
.collapse-btn {
  position: relative;
  z-index: 1;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-top: 1px solid rgba(255, 255, 255, 0.15);
  cursor: pointer;
  color: rgba(255, 255, 255, 0.6);
  transition: all 0.25s ease;
}

.collapse-btn:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
}

.collapse-icon {
  transition: transform 0.3s ease;
}

/* ========== 头部 ========== */
.main-container {
  flex-direction: column;
}

.header {
  height: 56px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 28px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04), 0 1px 2px rgba(0, 0, 0, 0.03);
  position: relative;
  z-index: 10;
}

.header-breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #909399;
}

.breadcrumb-icon {
  color: #10b981;
}

.breadcrumb-sep {
  color: #c0c4cc;
}

.breadcrumb-current {
  color: #303133;
  font-weight: 500;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.header-time {
  font-size: 13px;
  color: #909399;
  font-variant-numeric: tabular-nums;
  letter-spacing: 0.5px;
}

.user-avatar {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 10px;
  border-radius: 8px;
  transition: background 0.2s ease;
}

.user-avatar:hover {
  background: #f5f7fa;
}

.avatar-circle {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: linear-gradient(135deg, #10b981, #059669);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 700;
  box-shadow: 0 2px 6px rgba(16, 185, 129, 0.3);
}

.avatar-name {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.avatar-arrow {
  color: #909399;
  font-size: 12px;
  transition: transform 0.2s ease;
}

/* ========== 内容区 ========== */
.main {
  background: #f0f2f5;
  padding: 24px;
  min-height: 0;
}

/* ========== 过渡动画 ========== */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
