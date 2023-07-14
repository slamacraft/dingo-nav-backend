import {Response, Router} from "express";
// import jwt from "jsonwebtoken";
import Request from "../../types/api/Request";
import auth from "src/middleware/auth";
import UserWidget from "src/models/UserWidget";

const router: Router = Router();

/**
 * 获取用户自定义套件列表
 */
router.get("/list", auth, async (req: Request, res: Response) => {
    const userWidget = await UserWidget.find({
        userId: req.userId
    })
    res.json(userWidget);
});


export default router;
