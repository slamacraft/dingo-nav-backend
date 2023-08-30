import bcrypt from "bcryptjs";
import config from "config";
import {Router} from "express";
import {check} from "express-validator";
import HttpStatusCodes from "http-status-codes";
import nodeCache from "src/cache/nodeCache";

import auth from "src/middleware/auth";
import {sign} from "src/utils/JwtUtil";
import {getParamMap} from "src/utils/UrlUtil";
import User, {IUser} from "../../models/User";
import {Req} from "api/Req";
import {getAccessToken, getUserByCode, listUserEmail,} from "../transport/github";
import {ServerErr} from "src/types/error/ServerErr";
import {Res} from "api/Res";
import validator from "src/middleware/validator";

const router: Router = Router();

/**
 * 登录接口
 */
router.post(
  "/",
  [
    check("email", "请输入有效的邮箱").isEmail(),
    check("password", "请输入密码").exists(),
  ],
  validator,
  async (req: Req, res: Res) => {

    const {email, password} = req.body;
    let user: IUser = await User.findOne({email});

    if (!user) {
      return res
        .status(HttpStatusCodes.BAD_REQUEST)
        .error("用户尚未注册", 403);
    }

    const isMatch = await bcrypt.compare(password, user.password);

    if (!isMatch) {
      return res
        .status(HttpStatusCodes.BAD_REQUEST)
        .error("密码错误", 403);
    }

    sign({
      userId: user.id,
    }, (err, token) => {
      if (err) {
        return res
          .status(HttpStatusCodes.INTERNAL_SERVER_ERROR)
          .error(err.message)
      }
      res.success({
        id: user.id,
        name: user.name,
        email: user.email,
        avatar: user.avatar,
        token: token,
      });
    });
  }
);

router.post(
  "/github",
  [check("code", "Code不能为空").exists()],
  validator,
  async (req: Req, res: Res, next: any) => {
    // 获取github用户信息
    let token: string = config.get("github.token");
    if (!token) {
      let code = req.body.code;
      let accessTokenResp = await getAccessToken(code)
      if (accessTokenResp instanceof ServerErr) {
        return next(accessTokenResp)
      }
      if (!accessTokenResp.accessToken) {
        return res.error("accessToken获取失败");
      }
      token = getParamMap(accessTokenResp.accessToken).get("access_token");
    }

    let githubUserPromise = getUserByCode(token)
    let listUserEmailPromise = listUserEmail(token)

    let githubUserResp = await githubUserPromise;
    if (githubUserResp instanceof ServerErr) {
      return next(githubUserResp)
    }
    let listUserEmailResp = await listUserEmailPromise;
    if (listUserEmailResp instanceof ServerErr) {
      return next(listUserEmailResp)
    }

    if (githubUserResp && listUserEmailResp) {
      // 获取到用户信息后，查询本地是否有保存，
      // 没有保存就让用户去注册
      // 有保存直接返回token
      let userEmail = listUserEmailResp.list.filter((it) => it.primary)[0];
      let saveUser = await User.findOne({email: userEmail.email});

      let result: any = {
        email: userEmail.email,
        name: githubUserResp.name,
        avatar: githubUserResp.avatarUrl,
      };

      if (!saveUser) {
        let newUser = new User({
          email: userEmail.email,
          name: githubUserResp.name,
          avatar: githubUserResp.avatarUrl,
          password: "Unit",
        });
        await newUser.save();
        result.id = newUser.id;
      } else {
        result.id = saveUser.id;
      }
      return sign({userId: result.id}, (err, token) => {
        if (err) {
          return res
            .status(HttpStatusCodes.INTERNAL_SERVER_ERROR)
            .error(err.message);
        }
        result.token = token;
        res.success(result);
      });
    }
    return res.json({
      errMsg: "获取用户信息失败",
    });
  }
);

router.delete("/", auth, (req: Req, res: Res) => {
  nodeCache.getCache().del(req.token);
  res.success();
});

export default router;
