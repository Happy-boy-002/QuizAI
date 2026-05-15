const { request } = require('../../utils/request')
const app = getApp()

Page({
  data: {
    userInfo: null,
    totalPractice: 0,
    accuracy: '0.0',
    trendData: [],
    isTrendModalVisible: false
  },

  onShow() {
    const token = wx.getStorageSync('token')
    if (!token) {
      wx.redirectTo({ url: '/pages/login/login' })
      return
    }
    const userInfo = wx.getStorageSync('userInfo')
    if (userInfo) {
      this.setData({ userInfo })
      this.fetchUserStats(userInfo.id)
    }
  },

  fetchUserStats(userId) {
    request({
      url: `${app.globalData.baseUrl}/api/record/stats?userId=${userId}`,
      method: 'GET',
      success: (res) => {
        if (res.data.code === 200 && res.data.data) {
          const stats = res.data.data
          this.setData({
            totalPractice: stats.totalPractice || 0,
            accuracy: stats.accuracy || '0.0',
            trendData: stats.trendData || []
          })
        }
      }
    })
  },

  goToWrongPage() {
    wx.navigateTo({
      url: '/pages/wrong/wrong'
    })
  },

  goToHistory() {
    wx.navigateTo({
      url: '/pages/history/history'
    })
  },

  goToUserInfo() {
    wx.navigateTo({
      url: '/pages/userinfo/userinfo'
    })
  },

  showTrendModal() {
    if (!this.data.trendData || this.data.trendData.length === 0) {
      wx.showToast({ title: '暂无答题数据', icon: 'none' })
      return
    }
    this.setData({ isTrendModalVisible: true })
  },

  closeTrendModal() {
    this.setData({ isTrendModalVisible: false })
  },

  preventBubble() {
    // 阻止事件冒泡，防止点击内容区关闭弹窗
  },

  showDevToast() {
    wx.showToast({
      title: '功能开发中',
      icon: 'none'
    })
  },

  logout() {
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          wx.removeStorageSync('token')
          wx.removeStorageSync('userInfo')
          wx.reLaunch({
            url: '/pages/login/login'
          })
        }
      }
    })
  }
})