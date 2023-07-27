import {Response, Router} from "express";
// import jwt from "jsonwebtoken";
import Request from "../../types/api/Request";
import auth from "src/middleware/auth";
import UserWidget from "src/models/UserWidget";
import {check, validationResult} from "express-validator";
import HttpStatusCodes from "http-status-codes";

const router: Router = Router();

router.get("/", auth,
    [check("id", "Id不能为空").notEmpty()],
    async (req: Request, res: Response) => {
        const errors = validationResult(req);
        if (!errors.isEmpty()) {
            return res
                .status(HttpStatusCodes.BAD_REQUEST)
                .json({errors: errors.array()});
        }
        const userWidget = await UserWidget.findById(req.body.id)
        res.json(userWidget);
    })

/**
 * 获取用户自定义套件列表
 */
router.get("/list", auth, async (req: Request, res: Response) => {
    const userWidget = await UserWidget.find({
        userId: req.userId
    })
    res.json(userWidget);
});

router.put("/", auth,
    [
        check("title", "套件标题不能为空").notEmpty(),
        check("desc", "套件描述不能为空").notEmpty(),
        check("html", "套件内容不能为空").notEmpty(),
    ],
    async (req: Request, res: Response) => {

})


export default router;
