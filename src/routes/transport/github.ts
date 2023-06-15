import config from "config";
import {
  GetAccessTokenResp,
  GetUserResp,
  ListUserEmailResp,
  UserEmail,
} from "src/types/transport/Github";
import { service } from "./index";

const githubBaseUrl = "https://github.com";
const getAccessTokenUrl = "/login/oauth/access_token";
const getUserUrl = "https://api.github.com/user";
const listUserEmailUrl = "https://api.github.com/user/emails";

export function getAccessToken(code: string): Promise<GetAccessTokenResp> {
  return service({
    url: githubBaseUrl + getAccessTokenUrl,
    method: "POST",
    data: JSON.stringify({ 
      client_id: config.get("github.appId"),
      client_secret: config.get("github.appSecret"),
      code: code,
    }),
  }).then((resp) => {
    if (resp) {
      return { accessToken: resp.data };
    }
    return { accessToken: undefined };
  });
}

export function getUserByCode(accessToken: string): Promise<GetUserResp> {
  return service({
    url: getUserUrl,
    method: "GET",
    headers: {
      Authorization: `Bearer ${accessToken}`,
      Accept: "application/vnd.github+json",
      "X-GitHub-Api-Version": "2022-11-28",
    },
  }).then((resp) => {
    return {
      id: resp.data.id, // 用户id
      name: resp.data.login, // 用户名称
      avatarUrl: resp.data.avatar_url, // 用户头像
    };
  });
}

export function listUserEmail(accessToken: string): Promise<ListUserEmailResp> {
  return service({
    url: listUserEmailUrl,
    method: "GET",
    headers: {
      Authorization: `Bearer ${accessToken}`,
      Accept: "application/vnd.github+json",
      "X-GitHub-Api-Version": "2022-11-28",
    },
  }).then((resp) => {
    let list: UserEmail[] = (resp.data as any[]).map((it) => {
      return {
        email: it.email, // 邮箱地址
        primary: it.primary, // 是否为主邮箱
        verified: it.verified, // 是否验证
        visibility: it.visibility, // 可见性 null | private
      };
    });
    return {
      list: list,
    };
  });
}
