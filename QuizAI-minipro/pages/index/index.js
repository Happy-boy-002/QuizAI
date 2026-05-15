const { request } = require('../../utils/request')
const app = getApp()

Page({
  data: {
    userInfo: null,
    leaderboard: [],
    
    // 使用带有图标的对象数组让 UI 更生动
    subjectList: [
      { name: '全部', icon: '📚' }
    ],
    selectedSubject: '全部',
    
    counts: [5, 10, 20],
    selectedCount: 10
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
    }
    this.fetchCategories()
    this.fetchLeaderboard()
  },

  fetchLeaderboard() {
    request({
      url: `${app.globalData.baseUrl}/api/record/leaderboard`,
      method: 'GET',
      success: (res) => {
        if (res.data.code === 200 && res.data.data) {
          this.setData({ leaderboard: res.data.data })
        }
      }
    })
  },

  fetchCategories() {
    request({
      url: `${app.globalData.baseUrl}/api/category/list`,
      method: 'GET',
      success: (res) => {
        if (res.data.code === 200 && res.data.data) {
          const categories = res.data.data.map(c => ({
            name: c.name,
            icon: c.icon || '📚'
          }))
          this.setData({
            subjectList: [{ name: '全部', icon: '📚' }, ...categories]
          })
        }
      }
    })
  },

  onSelectSubject(e) {
    const subject = e.currentTarget.dataset.subject
    this.setData({ selectedSubject: subject })
  },

  onSelectCount(e) {
    const count = e.currentTarget.dataset.count
    this.setData({ selectedCount: count })
  },

  startPractice() {
    const { selectedSubject, selectedCount } = this.data
    wx.navigateTo({
      url: `/pages/practice-enhanced/practice-enhanced?subject=${selectedSubject}&count=${selectedCount}`
    })
  },

  goToWrongPage() {
    wx.navigateTo({
      url: '/pages/wrong/wrong'
    })
  }
})
