import bcrypt from "bcryptjs";
import config from "config";
import {Response, Router} from "express";
import {check, validationResult} from "express-validator";
import HttpStatusCodes from "http-status-codes";
import nodeCache from "src/cache/nodeCache";

import auth from "src/middleware/auth";
import {sign} from "src/utils/JwtUtil";
import {getParamMap} from "src/utils/UrlUtil";
import User, {IUser} from "../../models/User";
import Payload from "../../types/api/Payload";
import Request from "../../types/api/Request";
import {getAccessToken, getUserByCode, listUserEmail,} from "../transport/github";
import {ServerErr} from "src/types/error/ServerErr";

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
  async (req: Request, res: Response) => {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res
        .status(HttpStatusCodes.BAD_REQUEST)
        .json({errors: errors.array()});
    }

    const {email, password} = req.body;
    let user: IUser = await User.findOne({email});

    if (!user) {
      return res
        .status(HttpStatusCodes.BAD_REQUEST)
        .json({errMsg: "用户尚未注册"});
    }

    const isMatch = await bcrypt.compare(password, user.password);

    if (!isMatch) {
      return res
        .status(HttpStatusCodes.BAD_REQUEST)
        .json({errMsg: "密码错误"});
    }

    const payload: Payload = {
      userId: user.id,
    };

    sign(payload, (err, token) => {
      if (err) {
        return res
          .status(HttpStatusCodes.INTERNAL_SERVER_ERROR)
          .json({errMsg: err.message});
      }
      res.json({
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
  async (req: Request, res: Response, next: any) => {

    // 校验
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res
        .status(HttpStatusCodes.BAD_REQUEST)
        .json({errMsg: errors.array()[0].msg});
    }

    // 获取github用户信息
    let token: string = config.get("github.token");
    if (!token) {
      let code = req.body.code;
      let accessTokenResp = await getAccessToken(code)
      if (accessTokenResp instanceof ServerErr) {
        return next(accessTokenResp)
      }
      console.debug(accessTokenResp.accessToken);
      if (!accessTokenResp.accessToken) {
        return res.json({errMsg: "accessToken获取失败"});
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
            .json({errMsg: err.message});
        }
        result.token = token;
        res.json(result);
      });
    }
    return res.json({
      errMsg: "获取用户信息失败",
    });
  }
);

router.delete("/", auth, (req: Request, res: Response) => {
  nodeCache.getCache().del(req.token);
  res.json({
    success: true,
  });
});

export default router;
