const { request } = require('../../utils/request')
const app = getApp()

Page({
  data: {
    recordId: null,
    detail: null,
    questions: [],
    loading: true
  },

  onLoad(options) {
    const recordId = options.recordId
    if (!recordId) {
      wx.showToast({ title: '参数错误', icon: 'none' })
      return
    }
    this.setData({ recordId })
    this.fetchDetail(recordId)
  },

  fetchDetail(recordId) {
    this.setData({ loading: true })

    request({
      url: `${app.globalData.baseUrl}/api/history/detail/${recordId}`,
      method: 'GET',
      success: (res) => {
        if (res.data.code === 200 && res.data.data) {
          const detail = res.data.data
          this.setData({
            detail,
            questions: detail.questions || [],
            loading: false
          })
        } else {
          this.setData({ loading: false })
          wx.showToast({ title: res.data.msg || '获取详情失败', icon: 'none' })
        }
      },
      fail: (err) => {
        this.setData({ loading: false })
        wx.showToast({ title: '网络请求失败', icon: 'none' })
      }
    })
  },

  formatTime(timeStr) {
    if (!timeStr) return ''
    const date = new Date(timeStr)
    const year = date.getFullYear()
    const month = date.getMonth() + 1
    const day = date.getDate()
    const hour = String(date.getHours()).padStart(2, '0')
    const minute = String(date.getMinutes()).padStart(2, '0')
    return `${year}年${month}月${day}日 ${hour}:${minute}`
  },

  getOptionLabel(index) {
    return String.fromCharCode(65 + index)
  },

  isUserSelected(question, optionLabel) {
    return question.userAnswer === optionLabel
  },

  isCorrectOption(question, optionLabel) {
    return question.correctAnswer === optionLabel
  },

  goBack() {
    wx.navigateBack()
  }
})