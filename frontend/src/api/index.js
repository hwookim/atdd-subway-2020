import Vue from 'vue'
import axios from 'axios'
import VueAxios from 'vue-axios'

const ApiService = {
  init() {
    Vue.use(VueAxios, axios)
  },
  getHeader() {
    if (localStorage.getItem('token')) {
      return {
        Authorization: `Bearer ${localStorage.getItem('token')}` || ''
      }
    } else {
      return {}
    }
  },
  get(uri) {
    return Vue.axios.get(`${uri}`, {
      headers: this.getHeader()
    })
  },
  getWithParams(uri, params) {
    return Vue.axios.get(`${uri}`, {
      headers: this.getHeader(),
      params
    })
  },
  login(uri, config) {
    return Vue.axios.post(`${uri}`, {}, config)
  },
  post(uri, params) {
    return Vue.axios.post(`${uri}`, params, {
      headers: this.getHeader()
    })
  },
  update(uri, params) {
    return Vue.axios.put(uri, params, {
      headers: this.getHeader()
    })
  },
  delete(uri) {
    return Vue.axios.delete(uri, {
      headers: this.getHeader()
    })
  }
}

ApiService.init()

export default ApiService
