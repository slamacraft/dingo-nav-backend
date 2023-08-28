import {Response} from "express";

export interface ResJson<T> {
  code: number
  msg: string
  data?: T
}

export const OK = {
  code: 200,
  msg: "ok"
} as ResJson<undefined>

export type Res = Response & {
  result<T>(resJson: ResJson<T>): Res
  error(msg?: string, code?: number): Res
  success(data?: any): Res
}
