package com.dingdo.extendService.gameService.wolfKillerService.impl;

import com.dingdo.Component.SendMsgComponent;
import com.dingdo.Component.componentBean.MsgBean;
import com.dingdo.Component.gameComponent.GameComponent;
import com.dingdo.Component.gameComponent.gameBean.wolfKillerBean.Werewolves;
import com.dingdo.Component.gameComponent.gameBean.wolfKillerBean.WolfKillerGame;
import com.dingdo.Component.gameComponent.gameBean.wolfKillerBean.WolfKillerGamerData;
import com.dingdo.common.annotation.Instruction;
import com.dingdo.extendService.gameService.gameEnum.WolfKillerIdentityEnum;
import com.dingdo.extendService.gameService.wolfKillerService.WolfKillerService;
import com.dingdo.model.msgFromCQ.ReceiveMsg;
import com.dingdo.util.InstructionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WolfKillerServiceImpl implements WolfKillerService {

//    private Map<Long, WolfKillerGroupData> groupDataMap = new HashMap<>();
//
//    private Map<Long, Long> userId2GroupIdMap = new HashMap<>();

    @Autowired
    private GameComponent gameComponent;

    @Autowired
    private SendMsgComponent sendMsgComponent;

    /**
     * 加入游戏的指令方法
     *
     * @param receiveMsg
     * @param params
     * @return
     */
    @Instruction(name = "wolfKiller", descrption = "狼人杀")
    public String instructionJoinGame(ReceiveMsg receiveMsg, Map<String, String> params) {
        Long groupId = receiveMsg.getGroup_id();

        WolfKillerGame wolfKillerGameData = (WolfKillerGame) gameComponent.getGameData(groupId, "狼人杀");
        if (wolfKillerGameData != null) {
            if (wolfKillerGameData.getGameStatus() == 1) {
                return "游戏已经开始了哦，请等待下一局游戏";
            }
            return joinGame(receiveMsg, wolfKillerGameData);
        }

        wolfKillerGameData = new WolfKillerGame();
        wolfKillerGameData.setGroupId(receiveMsg.getGroup_id());
        wolfKillerGameData.setGameName("狼人杀");
        wolfKillerGameData.setGameTime(System.currentTimeMillis());

        return joinGame(receiveMsg, wolfKillerGameData);
    }

    /**
     * 加入游戏
     *
     * @param receiveMsg
     * @param wolfKillerGame
     * @return
     */
    private String joinGame(ReceiveMsg receiveMsg, WolfKillerGame wolfKillerGame) {
        Long userId = receiveMsg.getUser_id();

        Map<Long, WolfKillerGamerData> gamerDataMap = wolfKillerGame.getGamerDataMap();
        WolfKillerGamerData wolfKillerGamerData = gamerDataMap.get(userId);
        if (wolfKillerGamerData != null) {
            return "你已经在游戏里了哦，请不要重复加入游戏";
        }
        wolfKillerGamerData = new WolfKillerGamerData();
        wolfKillerGamerData.setNickName(receiveMsg.getSender().getNickname());
        wolfKillerGamerData.setUserId(userId);
        String card = receiveMsg.getSender().getCard();
        if (StringUtils.isBlank(card)) {
            wolfKillerGamerData.setCard(receiveMsg.getSender().getNickname());
        } else {
            wolfKillerGamerData.setCard(card);
        }
        wolfKillerGamerData.setNumber(gamerDataMap.size() + 1);

        gamerDataMap.put(userId, wolfKillerGamerData);

        gameComponent.getGameList().add(wolfKillerGame);

        return " [CQ:at,qq=" + userId + "]" + "报名狼人杀成功~现在已经有" + gamerDataMap.size() + "人报名啦！\n" +
                "之后请发送  .准备  进行游戏准备\n" +
                "所有玩家准备完成后即可开始";
    }


    /**
     * 设置身份
     *
     * @param receiveMsg
     * @param params
     * @return
     */
    @Instruction(name = "setDuty", descrption = "设置身份")
    public String setDutyMap(ReceiveMsg receiveMsg, Map<String, String> params) {
        Long groupId = receiveMsg.getGroup_id();
        Long userId = receiveMsg.getUser_id();
        // 校验是否正在准备狼人杀
        if (!hasGameInGroup(groupId)) {
            return "现在还没有正在准备的狼人杀";
        }
        if (!gamerIsInThisGame(groupId, userId)) {
            return "你不是本局狼人杀的参与者，无法设置";
        }

        // 本局游戏的职业数量
        WolfKillerGame wolfKillerGame = (WolfKillerGame) gameComponent
                .getGameData(receiveMsg.getGroup_id(), "狼人杀");
        Map<String, Integer> setIdentityMap = wolfKillerGame.getOccupationSettingMap();

        // 所有职业
        List<WolfKillerIdentityEnum> identityEnumList = WolfKillerIdentityEnum.getidentityEnumList();
        Map<String, WolfKillerIdentityEnum> allOccupationMap = identityEnumList.stream()
                .collect(Collectors.toMap(WolfKillerIdentityEnum::getDutyCN, p -> p));

        // 设置本局身份配置
        for (Map.Entry<String, String> entry : params.entrySet()) {
            WolfKillerIdentityEnum occupation = allOccupationMap.get(entry.getKey());
            if (occupation != null) {
                Integer occupationCount = InstructionUtils
                        .getParamValueOfInteger(params, occupation.getDutyEN(), occupation.getDutyCN());
                setIdentityMap.put(occupation.getDutyCN(), occupationCount);
            }
        }

        return "设置成功！本局的身份配置为:\n" + wolfKillerGame.getOccupationSetting();
    }


    /**
     * 准备游戏
     *
     * @param receiveMsg
     * @param params
     * @return
     */
    @Instruction(name = "ready", descrption = "准备")
    public String ready(ReceiveMsg receiveMsg, Map<String, String> params) {
        Long groupId = receiveMsg.getGroup_id();
        Long userId = receiveMsg.getUser_id();

        // 校验是否可以准备
        if (!hasGameInGroup(groupId)) {
            return "现在还没有正在准备中的狼人杀";
        }
        if (!gamerIsInThisGame(groupId, userId)) {
            return "你不是本局狼人杀的参与者，无法准备";
        }

        WolfKillerGame wolfKillerGame = (WolfKillerGame) gameComponent
                .getGameData(receiveMsg.getGroup_id(), "狼人杀");
        WolfKillerGamerData gamerData = wolfKillerGame.getGamerDataMap().get(receiveMsg.getUser_id());
        int status = gamerData.getStatus();
        if (status == 1) {
            return "你已经准备了";
        }

        // 更改参与者状态为准备完成
        gamerData.setStatus(1);

        // 通知玩家等待
        MsgBean msgBean = new MsgBean();
        msgBean.setMsg("准备完成，请等待其他玩家~");
        msgBean.setTargetId(userId);
        msgBean.setTargetType("private");

        sendMsgComponent.getMsgQueue().add(msgBean);

        return "准备完成";
    }

    @Instruction(name = "startGame", descrption = "开始游戏")
    public String startGame(ReceiveMsg receiveMsg, Map<String, String> params) {
        Long groupId = receiveMsg.getGroup_id();
        WolfKillerGame wolfKillerGame = (WolfKillerGame) gameComponent
                .getGameData(receiveMsg.getGroup_id(), "狼人杀");

        return wolfKillerGame.startGame();
    }

    @Instruction(name = "killTarget", descrption = "迫害")
    public String killTarget(ReceiveMsg receiveMsg, Map<String, String> params){
        WolfKillerGame wolfKillerGame = (WolfKillerGame) gameComponent
                .getGameData(receiveMsg.getGroup_id(), "狼人杀");
        Werewolves werewolves = (Werewolves)wolfKillerGame.getOccupationMap().get(receiveMsg.getUser_id());
        Long target = InstructionUtils.getParamValueOfLong(params, "target", "玩家");
        werewolves.setTargetId(target);

        return "准备迫害玩家";
    }

    /**
     * 确认本群是否在玩狼人杀
     *
     * @param groupId
     * @return
     */
    private boolean hasGameInGroup(Long groupId) {
        WolfKillerGame wolfKillerGame = (WolfKillerGame) gameComponent
                .getGameData(groupId, "狼人杀");
        return wolfKillerGame != null ? true : false;
    }

    /**
     * 确认本人是本群狼人杀的参与者
     *
     * @param groupId
     * @param userId
     * @return
     */
    private boolean gamerIsInThisGame(Long groupId, Long userId) {
        if (!hasGameInGroup(groupId)) {
            return false;
        }
        WolfKillerGame wolfKillerGame = (WolfKillerGame) gameComponent
                .getGameData(groupId, "狼人杀");
        WolfKillerGamerData wolfKillerGamerData = wolfKillerGame.getGamerDataMap().get(userId);
        return wolfKillerGamerData == null ? false : true;
    }

    private WolfKillerGamerData getGamerData(Long groupId, Long userId) {
        WolfKillerGame wolfKillerGame = (WolfKillerGame) gameComponent
                .getGameData(groupId, "狼人杀");
        return wolfKillerGame.getGamerDataMap().get(userId);
    }
}
