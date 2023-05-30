import e, { NextFunction, Response } from "express";
import HttpStatusCodes from "http-status-codes";

import {ServerErr} from "../types/error/ServerErr";
import Request from "../types/api/Request";

export default function <Err extends Error>(
  err: Err,
  req: Request,
  res: Response,
  next: NextFunction
) {
  console.error(err)
  let errCode =
    err instanceof ServerErr
      ? err.errCode
      : HttpStatusCodes.INTERNAL_SERVER_ERROR;
  res.status(errCode).json({ message: err.message });
}
