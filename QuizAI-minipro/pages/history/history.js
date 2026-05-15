const { request } = require('../../utils/request')
const app = getApp()

Page({
  data: {
    list: [],
    loading: true,
    refreshing: false
  },

  onShow() {
    const token = wx.getStorageSync('token')
    if (!token) {
      wx.redirectTo({ url: '/pages/login/login' })
      return
    }
    this.fetchHistoryList()
  },

  fetchHistoryList() {
    const userInfo = wx.getStorageSync('userInfo')
    if (!userInfo) return

    request({
      url: `${app.globalData.baseUrl}/api/history/list`,
      data: { userId: userInfo.id },
      success: (res) => {
        if (res.data.code === 200) {
          this.setData({
            list: res.data.data || [],
            loading: false,
            refreshing: false
          })
        } else {
          this.setData({ loading: false, refreshing: false })
        }
      },
      fail: () => {
        this.setData({ loading: false, refreshing: false })
        wx.showToast({ title: '网络请求失败', icon: 'none' })
      }
    })
  },

  onRefresh() {
    this.setData({ refreshing: true })
    this.fetchHistoryList()
  },

  goToDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/historydetail/historydetail?recordId=${id}`
    })
  },

  formatTime(timeStr) {
    if (!timeStr) return ''
    const date = new Date(timeStr)
    const month = date.getMonth() + 1
    const day = date.getDate()
    const hour = String(date.getHours()).padStart(2, '0')
    const minute = String(date.getMinutes()).padStart(2, '0')
    return `${month}月${day}日 ${hour}:${minute}`
  },

  goBack() {
    wx.navigateBack()
  }
})