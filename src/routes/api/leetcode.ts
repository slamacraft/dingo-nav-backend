import {Router} from "express";

import {Req} from "api/Req";
import {getQuestion, getQuestionOfToday,} from "../transport/leetcode";
import {Res} from "api/Res";

const router: Router = Router();

router.get(
  "/questionOfToday",
  async (req: Req, res: Res) => {
    let resp = await getQuestionOfToday().then(async (resp) => {
      let question = await getQuestion(resp.questionTitle);
      return {
        question: question,
      };
    });
    res.success(resp);
  }
);

export default router;
