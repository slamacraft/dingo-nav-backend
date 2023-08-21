export interface Resp<T> {
    code: number
    msg: string
    data: T
}

export const OK = {
    code: 200,
    msg: "ok"
} as Resp<undefined>
