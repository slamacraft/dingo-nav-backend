export interface GetQuestionOfTodayResp {
  questionTitle: string;
}

export interface GetQuestionResp {
  url: string;
  id: string; // 题目id
  translatedTitle: string; // 题目中文标题
  level: string; // 题目难度
  context: string; // 题目内容
  topicTags: string[]; // 题目主题
}

export interface GetQuestionStateResp {
  totalAccepted: string; // 通过次数
  totalSubmission: string; // 提交次数
  acRate: string; // ac率
}
