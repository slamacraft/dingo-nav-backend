import axios, { InternalAxiosRequestConfig } from "axios";
// eslint-disable-next-line @typescript-eslint/no-unused-vars

axios.defaults.withCredentials = true

// 配置请求
export const service = axios.create({
  // baseURL: "http://127.0.0.1:4523/m1/2240180-0-default",
  timeout: 5000,
  headers: {
    "Content-Type": "application/json;charset=utf-8",
    Authorization: "123"
  },
});

// 配置请求拦截器，请求头设置token
service.interceptors.request.use(
  (options: InternalAxiosRequestConfig) => {

    options.withCredentials = true
    return options;
  },
  (error) => Promise.reject(error)
);

// 配置响应拦截
service.interceptors.response.use(
  (res) => {
    const code: number = res.data.code;
    // eslint-disable-next-line eqeqeq
    if (code && code != 200) {
      // 发送请求失败,将信息返回出去
      return Promise.reject(res.data);
    }
    return res.data;
  },
  (error) => {
    // 错误信息处理，例如请求超时等
    alert(error.messsage)
    return Promise.reject(error);
  }
);