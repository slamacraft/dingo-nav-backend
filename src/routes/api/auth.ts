
import bcrypt from "bcryptjs";
import { Router, Response } from "express";
import { check, validationResult } from "express-validator";
import HttpStatusCodes from "http-status-codes";

import auth from "../../middleware/auth";
import Payload from "../../types/api/Payload";
import Request from "../../types/api/Request";
import User, { IUser } from "../../models/User";
import { sign } from "../../utils/jwtUtil";
import { credentials } from "src/middleware/headerCfg";

const router: Router = Router();

// @route   GET api/auth
// @desc    Get authenticated user given the token
// @access  Private
router.get("/", auth, async (req: Request, res: Response) => {
  const user: IUser = await User.findById(req.userId).select("-password");
  res.json(user);
});

// @route   POST api/auth
// @desc    Login user and get token
// @access  Public
router.post(
  "/",
  [
    check("email", "请输入有效的邮箱").isEmail(),
    check("password", "请输入密码").exists(),
  ],
  credentials,
  async (req: Request, res: Response) => {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res
        .status(HttpStatusCodes.BAD_REQUEST)
        .json({ errors: errors.array() });
    }

    const { email, password } = req.body;
    let user: IUser = await User.findOne({ email });

    if (!user) {
      return res
        .status(HttpStatusCodes.BAD_REQUEST)
        .json({ errMsg: "用户尚未注册" });
    }

    const isMatch = await bcrypt.compare(password, user.password);

    if (!isMatch) {
      return res
        .status(HttpStatusCodes.BAD_REQUEST)
        .json({ errMsg: "密码错误" });
    }

    const payload: Payload = {
      userId: user.id,
    };

    sign(payload, (err, token) => {
      if (err) {
        return res
          .status(HttpStatusCodes.INTERNAL_SERVER_ERROR)
          .json({ errMsg: err.message });
      }
      res.json({
        token: token,
      });
    });
  }
);

export default router;
