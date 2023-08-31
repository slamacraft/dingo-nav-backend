import bcrypt from "bcryptjs";
import {Response, Router} from "express";
import {check, param} from "express-validator";
import gravatar from "gravatar";
import HttpStatusCodes from "http-status-codes";
// import jwt from "jsonwebtoken";
import {sign} from "src/utils/JwtUtil";
import User, {IUser} from "../../models/User";
import {Req} from "api/Req";
import auth from "src/middleware/auth";
import userWidget from "src/models/UserWidget";
import {OK, Res} from "api/Res";
import validator from "src/middleware/validator";

const router: Router = Router();

/**
 * 获取用户信息
 */
router.get("/", auth, async (req: Req, res: Res) => {
  const user: IUser = await User.findById(req.userId).select("-password");
  res.success(user);
});

router.get("/list", auth,
  async (req: Req, res: Res) => {
    const user: IUser[] = await User.find({
      isDeleted: false
    }).select("-password");
    res.success({
      list: user
    });
  }
)

router.get("/page", auth,
  [
    param(["pageNum", "pageSize"], "页数和页大小不能为空").notEmpty(),
    param(["pageNum", "pageSize"], "页数和页大小不能是非数值").isNumeric({no_symbols: true}),
    param(["pageNum", "pageSize"], "页数和页大小不能小于1").toInt().if((input: number, meta: any) => input >= 1)
  ],
  validator,
  async (req: Req, res: Res) => {
    let {pageNumStr, pageSizeStr} = req.params
    let pageNum = Number.parseInt(pageNumStr)
    let pageSize = Number.parseInt(pageSizeStr)
    const user: IUser[] = await User.find().skip((pageNum - 1) * pageSize).limit(pageNum).select("-password");
    res.success({
      list: user
    });
  }
)

/**
 * 注册用户
 */
router.put(
  "/",
  [
    check("email", "请输入有效的邮箱").isEmail(),
    check("name", "请输入用户名").isLength({max: 20}),
    check("password", "请输入最少6位字符的密码").isLength({min: 6}),
  ],
  validator,
  async (req: Req, res: Res) => {
    const {email, password} = req.body;
    let user: IUser = await User.findOne({email});

    if (user) {
      return res.status(HttpStatusCodes.BAD_REQUEST).json({
        errMsg: "用户已注册，请勿重复注册",
      });
    }

    const avatar = gravatar.url(email, {
      s: "200",
      r: "pg",
      d: "mm",
    }); // 生成头像
    // 密码加盐与加密
    const salt = await bcrypt.genSalt(10);
    const hashed = await bcrypt.hash(password, salt);

    // Build user object based on TUser
    user = new User({
      email,
      password: hashed,
      avatar,
    });

    await user.save();

    sign({
      userId: user.id,
    }, (err, token) => {
      if (err) throw err;
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

router.delete("/", auth,
  [
    check("id", "id不能为空").notEmpty(),
  ],
  validator,
  async (req: Req, res: Res) => {
    let id = req.body.id
    await userWidget.logicDeleteById(id)
    res.success(OK)
  }
)

export default router;
