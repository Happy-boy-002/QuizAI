const { request } = require('../../utils/request')
const app = getApp()

Page({
  data: {
    subject: '',
    mode: 'normal',
    count: 10,
    loading: true,

    questions: [],
    currentIndex: 0,
    currentQuestion: {},

    selectedOption: '',
    isAnswered: false,

    correctCount: 0,
    wrongCount: 0,

    questionDetails: []
  },

  onLoad(options) {
    this.setData({
      subject: options.subject || '全部',
      mode: options.mode || 'normal',
      count: parseInt(options.count) || 10
    })

    this.fetchQuestions()
  },

  fetchQuestions() {
    this.setData({ loading: true })

    const userInfo = wx.getStorageSync('userInfo')

    let url = `${app.globalData.baseUrl}/api/question/random`
    let data = {
      subject: this.data.subject,
      count: this.data.count
    }

    if (this.data.mode === 'wrong') {
      if (!userInfo) {
        wx.showToast({ title: '请先登录', icon: 'none' })
        return
      }
      url = `${app.globalData.baseUrl}/api/wrong/random`
      data = {
        userId: userInfo.id,
        subject: this.data.subject,
        count: this.data.count
      }
    }

    request({
      url: url,
      data: data,
      success: (res) => {
        if (res.data.code === 200 && res.data.data && res.data.data.length > 0) {
          const questions = res.data.data
          this.setData({
            questions,
            currentQuestion: questions[0],
            loading: false,
            questionDetails: []
          })
        } else {
          this.setData({ loading: false })
          wx.showModal({
            title: '提示',
            content: '未找到题目',
            showCancel: false,
            success: () => {
              wx.navigateBack()
            }
          })
        }
      },
      fail: () => {
        this.setData({ loading: false })
        wx.showToast({
          title: '网络请求失败',
          icon: 'none'
        })
      }
    })
  },

  onSelectOption(e) {
    if (this.data.isAnswered) return

    const option = e.currentTarget.dataset.option
    this.setData({ selectedOption: option })
  },

  submitAnswer() {
    const { selectedOption, currentQuestion, isAnswered, mode } = this.data

    if (!selectedOption || isAnswered) return

    const isCorrect = selectedOption === currentQuestion.correctAnswer

    this.setData({
      isAnswered: true,
      correctCount: this.data.correctCount + (isCorrect ? 1 : 0),
      wrongCount: this.data.wrongCount + (isCorrect ? 0 : 1)
    })

    const detail = {
      questionId: currentQuestion.id,
      questionContent: currentQuestion.content,
      optionA: currentQuestion.optionA,
      optionB: currentQuestion.optionB,
      optionC: currentQuestion.optionC,
      optionD: currentQuestion.optionD,
      userAnswer: selectedOption,
      correctAnswer: currentQuestion.correctAnswer,
      isCorrect: isCorrect
    }
    this.data.questionDetails.push(detail)

    const userInfo = wx.getStorageSync('userInfo')
    if (userInfo) {
      if (!isCorrect && mode !== 'wrong') {
        request({
          url: `${app.globalData.baseUrl}/api/wrong/add`,
          method: 'POST',
          data: {
            userId: userInfo.id,
            questionId: currentQuestion.id
          },
          success: (res) => {
            console.log('加入错题本结果:', res.data)
          }
        })
      } else if (isCorrect && mode === 'wrong') {
        wx.showModal({
          title: '提示',
          content: '错题已掌握，是否从错题本删除该题目？',
          success: (modalRes) => {
            if (modalRes.confirm) {
              request({
                url: `${app.globalData.baseUrl}/api/wrong/remove`,
                method: 'POST',
                data: {
                  userId: userInfo.id,
                  questionId: currentQuestion.id
                },
                success: (res) => {
                  console.log('移出错题本结果:', res.data)
                  wx.showToast({ title: '已删除', icon: 'success' })
                }
              })
            }
          }
        })
      }
    }
  },

  nextQuestion() {
    const { currentIndex, questions } = this.data

    if (currentIndex < questions.length - 1) {
      this.setData({
        currentIndex: currentIndex + 1,
        currentQuestion: questions[currentIndex + 1],
        selectedOption: '',
        isAnswered: false,
        aiLoading: false,
        aiAnalysisResult: ''
      })
    } else {
      this.finishPractice()
    }
  },

  finishPractice() {
    const total = this.data.questions.length
    const correct = this.data.correctCount
    const accuracy = Math.round((correct / total) * 100)

    if (this.data.mode === 'wrong') {
      wx.showModal({
        title: '提示',
        content: '错题重练完成 🎉',
        confirmText: '返回',
        showCancel: false,
        success: () => {
          wx.navigateBack()
        }
      })
      return
    }

    const userInfo = wx.getStorageSync('userInfo')
    if (!userInfo) return

    wx.showLoading({ title: '正在保存记录...' })

    request({
      url: `${app.globalData.baseUrl}/api/history/submit`,
      method: 'POST',
      header: { 'Content-Type': 'application/json' },
      data: {
        subject: this.data.subject,
        totalCount: total,
        correctCount: correct,
        questions: this.data.questionDetails
      },
      success: (res) => {
        wx.hideLoading()
        if (res.data.code === 200) {
          wx.showModal({
            title: '练习完成',
            content: `共 ${total} 题，答对 ${correct} 题\n正确率：${accuracy}%\n（已记录每道题的答题详情）`,
            confirmText: '返回首页',
            showCancel: false,
            success: () => {
              wx.navigateBack()
            }
          })
        } else {
          wx.showToast({ title: res.data.msg || '保存失败', icon: 'none' })
        }
      },
      fail: (err) => {
        wx.hideLoading()
        wx.showToast({ title: '网络请求失败', icon: 'none' })
      }
    })
  },

  getAIAnalysis() {
    const currentQ = this.data.currentQuestion;
    const userSelected = this.data.selectedOption;

    if (!userSelected) {
      return wx.showToast({ title: '请先答题', icon: 'none' })
    }

    if (this.data.aiLoading) return;

    this.setData({
      aiLoading: true,
      aiAnalysisResult: ''
    });

    const requestTask = request({
      url: `${app.globalData.baseUrl}/api/ai/explainStream`,
      method: 'GET',
      enableChunked: true,
      data: {
        questionContent: currentQ.content,
        userAnswer: userSelected,
        correctAnswer: currentQ.correctAnswer,
        options: [currentQ.optionA, currentQ.optionB, currentQ.optionC, currentQ.optionD]
      },
      success: (res) => {
        this.setData({ aiLoading: false });
      },
      fail: () => {
        this.setData({ aiLoading: false });
        wx.showToast({ title: '网络请求失败', icon: 'none' });
      }
    });

    let bufferString = '';

    requestTask.onChunkReceived((res) => {
      const uint8Array = new Uint8Array(res.data);
      let text = '';
      for (let i = 0; i < uint8Array.length; i++) {
        text += '%' + ('00' + uint8Array[i].toString(16)).slice(-2);
      }
      try {
        text = decodeURIComponent(text);
      } catch (e) {
        text = String.fromCharCode.apply(null, uint8Array);
      }

      bufferString += text;

      const parts = bufferString.split('\n\n');
      bufferString = parts.pop();

      for (let part of parts) {
        if (part.trim() === '') continue;

        if (part.startsWith('data:')) {
          const content = part.substring(5).trim();
          if (content === '[DONE]') {
            this.setData({ aiLoading: false });
            return;
          }
          this.setData({
            aiAnalysisResult: this.data.aiAnalysisResult + content
          });
        }
      }
    });
  },

  goBack() {
    wx.navigateBack()
  }
})
