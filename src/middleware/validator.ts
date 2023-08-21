import {Req} from "api/Req";
import {NextFunction, Response} from "express";
import {validationResult} from "express-validator";
import HttpStatusCodes from "http-status-codes";

export default function (req: Req, res: Response, next: NextFunction) {
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    return res
      .status(HttpStatusCodes.BAD_REQUEST)
      .json({errors: errors.array()});
  }
  next()
}