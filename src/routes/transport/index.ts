import axios, { InternalAxiosRequestConfig } from "axios";
// eslint-disable-next-line @typescript-eslint/no-unused-vars

axios.defaults.withCredentials = true;

// 配置请求
export const service = axios.create({
  // baseURL: "http://127.0.0.1:4523/m1/2240180-0-default",
  timeout: 60000,
  headers: {
    "Content-Type": "application/json",
  },
});

// 配置请求拦截器，请求头设置token
service.interceptors.request.use(
  (options: InternalAxiosRequestConfig) => {
    if (options.data) {
      // console.log(options.url, "请求内容:", options.data);
    }
    return options;
  },
  (error) => Promise.reject(error)
);

// 配置响应拦截
// service.interceptors.response.use(
//   (res) => {
//     return res;
//   },
//   (error) => {
//     // 错误信息处理，例如请求超时等
//     console.error(error.messsage);
//     // return Promise.reject(error.message);
//   }
// );
