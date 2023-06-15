import { Response, Router } from "express";

import Request from "../../types/api/Request";
import {
  getQuestion,
  getQuestionOfToday,
  getQuestionState,
} from "../transport/leetcode";

const router: Router = Router();

router.get(
  "/questionOfToday",
  async (req: Request, res: Response) => {
    let resp = await getQuestionOfToday().then(async (resp) => {
      let quesiont = await getQuestion(resp.questionTitle);
      let state = await getQuestionState(resp.questionTitle);
      return {
        question: quesiont,
        state: state,
      };
    });
    res.json(resp);
  }
);

export default router;
