import config from "config";
// 允许跨域访问
// 配置响应头
import { NextFunction, Response } from "express";

import Request from "../types/api/Request";

export function credentials(_: Request, res: Response, next: NextFunction) {
  // Get token from header
  res.setHeader(
    "Access-Control-Allow-Origin",
    config.get("Access-Control-Allow-Origin")
  );
  res.setHeader("Access-Control-Allow-Credentials", "true");
  next();
}
