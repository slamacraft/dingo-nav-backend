import bcrypt from "bcryptjs";
import {Response, Router} from "express";
import {check, validationResult} from "express-validator";
import gravatar from "gravatar";
import HttpStatusCodes from "http-status-codes";
// import jwt from "jsonwebtoken";
import {sign} from "src/utils/JwtUtil";
import User, {IUser} from "../../models/User";
import Payload from "../../types/api/Payload";
import Request from "../../types/api/Request";
import auth from "src/middleware/auth";
import userWidget from "src/models/UserWidget";
import {OK} from "api/Response";

const router: Router = Router();

/**
 * 获取用户信息
 */
router.get("/", auth, async (req: Request, res: Response) => {
    const user: IUser = await User.findById(req.userId).select("-password");
    res.json(user);
});

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
    async (req: Request, res: Response) => {
        const errors = validationResult(req);
        if (!errors.isEmpty()) {
            return res
                .status(HttpStatusCodes.BAD_REQUEST)
                .json({errors: errors.array()});
        }

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

        const payload: Payload = {
            userId: user.id,
        };

        sign(payload, (err, token) => {
            if (err) throw err;
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

router.delete("/",
    auth,
    [
        check("id", "id不能为空").isEmail(),
    ],
    async (req: Request, res: Response) => {
        let id = req.body.id
        await userWidget.logicDeleteById(id)
        res.json(OK)
    }
)

export default router;
