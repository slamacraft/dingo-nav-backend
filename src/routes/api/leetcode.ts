import { Response, Router } from "express";

import Request from "../../types/Request";
import { getQuestionOfToday } from "../transport/leetcode";

const router: Router = Router();

// @route   GET api/auth
// @desc    Get authenticated user given the token
// @access  Private
router.get("/", async (req: Request, res: Response) => {
  let resp = await getQuestionOfToday().catch((err) => {
    return err
  });
  console.log(resp);
  res.json(resp);
});

export default router;
