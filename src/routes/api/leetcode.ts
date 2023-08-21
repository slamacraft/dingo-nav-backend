import { Response, Router } from "express";

import {Req} from "api/Req";
import {
  getQuestion,
  getQuestionOfToday,
} from "../transport/leetcode";

const router: Router = Router();

router.get(
  "/questionOfToday",
  async (req: Req, res: Response) => {
    let resp = await getQuestionOfToday().then(async (resp) => {
      let question = await getQuestion(resp.questionTitle);
      return {
        question: question,
      };
    });
    res.json(resp);
  }
);

export default router;
