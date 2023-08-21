import {Response, Router} from "express";
// import jwt from "jsonwebtoken";
import {Req} from "api/Req";
import auth from "src/middleware/auth";
import UserWidget from "src/models/UserWidget";
import {check} from "express-validator";
import validator from "src/middleware/validator";

const router: Router = Router();

router.get("/", auth,
    [check("id", "Id不能为空").notEmpty()],
    validator,
    async (req: Req, res: Response) => {
        const userWidget = await UserWidget.findOne({
            id: req.body.id,
            isDelete: false
        })
        res.json(userWidget);
    })

/**
 * 获取用户自定义套件列表
 */
router.get("/list", auth,
    async (req: Req, res: Response) => {
        const userWidget = await UserWidget.find({
            userId: req.userId,
            isDelete: false
        })
        res.json({
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
    async (req: Req, res: Response) => {
        let {title, desc, html} = req.body
        req.userId = "1"
        let widget = await UserWidget.insertMany({
            userId: req.userId,
            title: title,
            desc: desc,
            html: html
        })

        return res.json(widget[0])
    })

router.delete("/", auth,
    [
        check("id", "Id不能为空").notEmpty()
    ],
    validator,
    async (req: Req, res: Response) => {
        await UserWidget.logicDeleteById(req.body.id)

        return res.json({
            "msg": "ok"
        })
    })


export default router;
