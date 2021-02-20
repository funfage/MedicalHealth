import axios from 'axios'
import { MessageBox, Message } from 'element-ui'
import store from '@/store'
import { getToken } from '@/utils/auth'

// create an axios instance
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API, // url = base url + request url
  // withCredentials: true, // send cookies when cross-domain requests
  timeout: 5000 // 设置请求超时时间
})

// 请求拦截
service.interceptors.request.use(
  config => {
    if (store.getters.token) {
      // 这里的token和后端的保持一致
      config.headers['token'] = getToken()
    }
    return config
  },
  error => {
    console.log(error)
    return Promise.reject(error)
  }
)

// 响应拦截
service.interceptors.response.use(
  response => {
    const res = response.data // response.data里面的数据才是后台返回给我们的数据  400  401  200  500
    if (res.code === 401) {
      // 身份过期
      MessageBox.confirm('用户登陆身份已过期，请重新登陆', '系统提示', {
        confirmButtonText: '重新登陆',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        store.dispatch('user/logout').then(() => { // 跳到登陆页面重新登陆
          location.reload()
        })
      })
    } else if (res.code === 500) {
      Notification.error({
        title: '服务器内部出现异常，请联系管理员'
      })
      return Promise.reject('error')// 记录错
    } else if (res.code === 400) {
      Notification.error({
        title: res.msg
      })
      return Promise.reject('error')// 记录错
    } else if (res.code !== 200) { // 可能是其它参数出错
      Notification.error({
        title: res.msg
      })
      return Promise.reject('error')// 记录错
    } else {
      // 以上验证通过之后再放行
      return res
    }
  },
  error => {
    console.log('err' + error) // for debug
    Message({
      message: error.message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default service
