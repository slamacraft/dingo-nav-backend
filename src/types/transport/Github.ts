export interface GetAccessTokenResp {
  accessToken: string;
}

export interface GetUserResp {
  id: number;   // 用户id
  name: string; // 用户名称
  avatarUrl: string;    // 用户头像
}

export interface ListUserEmailResp {
  list: UserEmail[];
}

export interface UserEmail {
  email: string; // 邮箱地址
  primary: boolean; // 是否为主邮箱
  verified: boolean; // 是否验证
  visibility: string; // 可见性 null | private
}
