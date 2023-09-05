import {Router} from "express";
// import jwt from "jsonwebtoken";
import {Req} from "api/Req";
import auth from "src/middleware/auth";
import UserWidget from "src/models/UserWidget";
import {check} from "express-validator";
import validator from "src/middleware/validator";
import {Res} from "api/Res";

const router: Router = Router();

router.get("/", auth,
    [check("id", "Id不能为空").notEmpty()],
    validator,
    async (req: Req, res: Res) => {
        const userWidget = await UserWidget.findOne({
            id: req.body.id,
            isDelete: false
        })
        res.success(userWidget);
    })

/**
 * 获取用户自定义套件列表
 */
router.get("/list", auth,
    async (req: Req, res: Res) => {
        const userWidget = await UserWidget.find({
            userId: req.userId,
            isDelete: false
        })
        res.success({
          list: userWidget
        });
    });

router.put("/", auth,
    [
        check("title", "套件标题不能为空").notEmpty(),
        check("desc", "套件描述不能为空").notEmpty(),
        check("html", "套件内容不能为空").notEmpty(),
    ],
    validator,
    async (req: Req, res: Res) => {
        let {title, desc, html} = req.body
        req.userId = "1"
        let widget = await UserWidget.insertMany({
            userId: req.userId,
            title: title,
            desc: desc,
            html: html
        })

        return res.success(widget[0])
    })

router.delete("/", auth,
    [
        check("id", "Id不能为空").notEmpty()
    ],
    validator,
    async (req: Req, res: Res) => {
        await UserWidget.logicDeleteById(req.body.id)

        return res.success()
    })


export default router;
