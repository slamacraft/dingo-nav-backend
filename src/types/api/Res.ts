export interface ResJson<T> {
    code: number
    msg: string
    data: T
}

export const OK = {
    code: 200,
    msg: "ok"
} as ResJson<undefined>



export type Res = Response
