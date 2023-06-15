import {
  GetQuestionOfTodayResp,
  GetQuestionResp,
  GetQuestionStateResp,
} from "../../types/transport/Leetcode";
import { service } from "./index";

const baseUrl = "https://leetcode.cn/graphql/";

export function getQuestionOfToday(): Promise<GetQuestionOfTodayResp> {
  return service({
    url: baseUrl,
    method: "POST",
    withCredentials: true,
    headers: {
      Origin: "https://leetcode.cn",
    },
    data: JSON.stringify({
      operationName: "questionOfToday",
      variables: {},
      query:
        "\n    query questionOfToday {\n  todayRecord {\n    date\n    userStatus\n    question {\n      questionId\n      frontendQuestionId: questionFrontendId\n      difficulty\n      title\n      titleCn: translatedTitle\n      titleSlug\n      paidOnly: isPaidOnly\n      freqBar\n      isFavor\n      acRate\n      status\n      solutionNum\n      hasVideoSolution\n      topicTags {\n        name\n        nameTranslated: translatedName\n        id\n      }\n      extra {\n        topCompanyTags {\n          imgUrl\n          slug\n          numSubscribed\n        }\n      }\n    }\n    lastSubmission {\n      id\n    }\n  }\n}\n    ",
    }),
  }).then((resp) => {
    const questionTitle = resp.data.data.todayRecord[0].question.titleSlug;
    return { questionTitle: questionTitle };
  });
}

export function getQuestion(leetcodeTitle: string): Promise<GetQuestionResp> {
  return service({
    url: baseUrl,
    method: "POST",
    data: JSON.stringify({
      operationName: "questionData",
      variables: {
        titleSlug: leetcodeTitle,
      },
      query:
        "query questionData($titleSlug: String!) {  question(titleSlug: $titleSlug) {    questionId    questionFrontendId    boundTopicId    title    titleSlug    content    translatedTitle    translatedContent    isPaidOnly    difficulty    likes    dislikes    isLiked    similarQuestions    contributors {      username      profileUrl      avatarUrl      __typename    }    langToValidPlayground    topicTags {      name      slug      translatedName      __typename    }    companyTagStats    codeSnippets {      lang      langSlug      code      __typename    }    stats    hints    solution {      id      canSeeDetail      __typename    }    status    sampleTestCase    metaData    judgerAvailable    judgeType    mysqlSchemas    enableRunCode    envInfo    book {      id      bookName      pressName      source      shortDescription      fullDescription      bookImgUrl      pressImgUrl      productUrl      __typename    }    isSubscribed    isDailyQuestion    dailyRecordStatus    editorType    ugcQuestionId    style    __typename  }}",
    }),
  }).then((resp) => {
    const question = resp.data.data.question;
    const topicTags = (question.topicTags as Array<any>).map(
      (it) => it.translatedName
    );
    return {
      url: `https://leetcode.cn/problems/${leetcodeTitle}/`,
      id: question.questionFrontendId, // 题目id
      translatedTitle: question.translatedTitle, // 题目中文标题
      level: question.difficulty, // 题目难度
      context: question.translatedContent, // 题目内容
      topicTags: topicTags,
    };
  });
}

export function getQuestionState(
  leetcodeTitle: string
): Promise<GetQuestionStateResp> {
  return service({
    url: baseUrl,
    method: "POST",
    data: JSON.stringify({
      operationName: "questionStats",
      variables: {
        titleSlug: leetcodeTitle,
      },
      query:
        "query questionStats($titleSlug: String!) {  question(titleSlug: $titleSlug) {    stats  }}",
    }),
  }).then((resp) => {
    const state = JSON.parse(resp.data.data.question.stats);
    return {
      totalAccepted: state.totalAccepted, // 通过次数
      totalSubmission: state.totalSubmission, // 提交次数
      acRate: state.acRate, // ac率
    };
  });
}
