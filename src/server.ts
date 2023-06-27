import bodyParser from "body-parser";
import config from "config";
import express, {Errback, NextFunction, Request, Response} from "express";
import "module-alias/register";

import HttpStatusCodes from "http-status-codes";
import {ServerErr} from "src/types/error/ServerErr";
import connectDB from "../config/database";
import auth from "./routes/api/auth";
import leetcode from "./routes/api/leetcode";
import profile from "./routes/api/profile";
import user from "./routes/api/user";
import * as process from "process";

const app = express();

// Connect to MongoDB
connectDB();

//设置跨域访问
app.all("*", function (req, res, next) {
  res.header("Access-Control-Allow-Credentials", "true");
  res.header(
    "Access-Control-Allow-Origin",
    req.headers.origin
      ? req.headers.origin
      : config.get("Access-Control-Allow-Origin")
  );
  res.header(
    "Access-Control-Allow-Headers",
    "Content-Type, Content-Length, Authorization, Accept, X-Requested-With, yourHeaderFeild"
  );
  res.header("Access-Control-Allow-Methods", "PUT, POST, GET, DELETE, OPTIONS");
  if (req.method === "OPTIONS") {
    res.sendStatus(200);
  } else {
    next();
  }
});

// Express configuration
app.set("port", process.env.PORT || 5000);
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: false}));

// @route   GET /
// @desc    Test Base API
// @access  Public
app.get("/", (_req: any, res: { send: (arg0: string) => void }) => {
  res.send("API Running");
});

app.use("/api/auth", auth);
app.use("/api/user", user);
app.use("/api/profile", profile);
app.use("/api/leetcode", leetcode);

app.use((err: Errback, req: Request, res: Response, next: NextFunction) => {
  console.error(err);
  let errCode =
    err instanceof ServerErr
      ? err.errCode
      : HttpStatusCodes.INTERNAL_SERVER_ERROR;
  res.status(errCode).json({errMsg: err});
});

const port = app.get("port");
const server = app.listen(port, () =>
  console.log(`Server started on port ${port}`)
);

export default server;
