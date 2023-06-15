/**
 * 登录验证层
 */
import config from "config";
import { NextFunction, Response } from "express";
import HttpStatusCodes from "http-status-codes";
import jwt from "jsonwebtoken";

import Payload from "../types/api/Payload";
import Request from "../types/api/Request";
import nodeCache from "src/cache/nodeCache";

export default function (req: Request, res: Response, next: NextFunction) {
  // Get token from header
  const token = req.header("Authorization");

  // Check if no token
  if (!token) {
    return res
      .status(HttpStatusCodes.UNAUTHORIZED)
      .json({ msg: "No token, authorization denied" });
  }
  // Verify token
  try {
    // 先使用jwt的过期时间去校验，防止大量垃圾请求访问缓存
    const payload: Payload | any = jwt.verify(token, config.get("jwtSecret"));
    req.userId = payload.userId;
    req.token = token
    if (nodeCache.getCache().has(token)) {
      return next();
    }
    res.status(HttpStatusCodes.UNAUTHORIZED).json({ msg: "token已过期" });
  } catch (err) {
    res.status(HttpStatusCodes.UNAUTHORIZED).json({ msg: "非法的token" });
  }
}
