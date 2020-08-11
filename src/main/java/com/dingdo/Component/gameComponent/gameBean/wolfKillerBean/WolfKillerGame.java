//package com.dingdo.Component.gameComponent.gameBean.wolfKillerBean;
//
//import com.dingdo.Component.componentBean.MsgBean;
//import com.dingdo.Component.gameComponent.gameBean.BasicGame;
//import org.apache.commons.lang3.StringUtils;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class WolfKillerGame extends BasicGame {
//
//    // 游戏玩家信息
//    protected Map<Long, WolfKillerGamerData> gamerDataMap = new HashMap<>();
//
//    // 本局游戏的身份数量表
//    protected Map<String, Integer> occupationSettingMap = new HashMap<>();
//
//    protected Map<Long, Object> occupationMap = new HashMap<>();
//
//    // 第几天
//    protected int day = 0;
//    // 是否是晚上
//    protected boolean isNight = true;
//
//    @Override
//    public String startGame() {
//        StringBuffer gamerCheckTip = new StringBuffer();
//        for (Map.Entry<Long, WolfKillerGamerData> gamerData : gamerDataMap.entrySet()) {
//            int status = gamerData.getValue().getStatus();
//            if (status == 0) {
//                gamerCheckTip.append(" [CQ:at,qq=" + gamerData.getKey() + "]");
//            }
//        }
//        if (StringUtils.isBlank(gamerCheckTip)) {
//            return "游戏开始啦！";
//        }
//
//        gamerCheckTip.insert(0, "还有以下玩家没有准备:");
//        return gamerCheckTip.toString();
//    }
//
//    @Override
//    public String stopGame() {
//        return "";
//    }
//
//    @Override
//    public String gameOver() {
//        // 平民数量
//        long civilianCount = gamerDataMap.values().stream().map(WolfKillerGamerData::getDuty)
//                .filter(item -> item == 0 ? true : false
//                ).count();
//
//        // 狼人数量
//        long werewolvesCount = gamerDataMap.values().stream().map(WolfKillerGamerData::getDuty)
//                .filter(item -> item == 1 ? true : false
//                ).count();
//
//        // 神职数量
//        long godCount = gamerDataMap.values().stream().map(WolfKillerGamerData::getDuty)
//                .filter(item -> item == 1 ? true : false
//                ).count();
//        if (werewolvesCount == 0) {
//            return "好人胜利了！";
//        }
//        if (civilianCount == 0 || godCount == 0) {
//            return "狼人胜利啦！";
//        }
//
//        return "";
//    }
//
//    @Override
//    public String triggerBehavior() {
//        return "";
//    }
//
//    @Override
//    public String gameEvent() {
//        return "";
//    }
//
//    @Override
//    public String mainGame() {
//        String gameResult = "";
//        while (StringUtils.isBlank(gameResult)) {
//            werewolvesEvent();
//            while (!werewolvesEventEnd());
//            gameResult = gameOver();
//        }
//        return "";
//    }
//
//    private void dayEvent() {
//
//    }
//
//    private void nightEvent() {
//
//    }
//
//    private void occupationEvent() {
//
//    }
//
//    /**
//     * 狼人的行动触发器
//     *
//     * @param userId
//     * @param targetId
//     */
//    private void werewolvesTrigger(String userId, Long targetId) {
//        Werewolves werewolves = (Werewolves) occupationMap.get(userId);
//        if (werewolves == null) {
//            werewolves = new Werewolves();
//            werewolves.setTargetId(targetId);
//            occupationMap.put(userId, werewolves);
//        }
//        StringBuffer tip = new StringBuffer();
//
//        tip.append("你的队友 " + gamerDataMap.get(userId).getCard() + " 想要迫害：\n" +
//                "玩家" + gamerDataMap.get(targetId).getNumber() + " " + gamerDataMap.get(targetId).getCard());
//
//        for (Map.Entry<Long, WolfKillerGamerData> gamerDataEntry : gamerDataMap.entrySet()) {
//            WolfKillerGamerData gamer = gamerDataEntry.getValue();
//            if ("狼人".equals(gamer.getOccupation())) {
//                List<WolfKillerGamerData> othersSurvivalWerewolves = getOthersSurvivalWerewolves(gamer.getUserId());
//                for (WolfKillerGamerData otherWerewolves : othersSurvivalWerewolves) {
//                    MsgBean msgBean = new MsgBean();
//                    msgBean.setMsg(tip.toString());
//                    msgBean.setSourceId(otherWerewolves.getUserId());
//                    msgBean.setTargetType("private");
//                    msgQueue.add(msgBean);
//                }
//            }
//        }
//    }
//
//    private boolean isWerewolves(Long id) {
//        if (gamerDataMap.get(id).getOccupation().equals("狼人")) {
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * 狼人的事件
//     */
//    private void werewolvesEvent() {
//        MsgBean eventTip = new MsgBean();
//        eventTip.setTargetId(groupId);
//        eventTip.setTargetType("group");
//        eventTip.setMsg("狼人正在考虑迫害对象");
//        msgQueue.add(eventTip);
//
//        // 提示操作
//        StringBuffer msgTip = new StringBuffer();
//        msgTip.append("请选择你今晚的迫害对象：\n");
//        if (day == 0) {
//            msgTip.append("发送  .迫害 玩家=【玩家编号】  继续\n" +
//                    "例如 .迫害 玩家=1");
//        }
//        StringBuffer gamerListMsg = new StringBuffer();
//
//        // 提醒剩余的玩家情况
//        gamerListMsg.append("剩余玩家列表:\n");
//        List<WolfKillerGamerData> gamerList = getSurvivor();
//
//        // 将提示发送给狼人玩家
//        for (Map.Entry<Long, WolfKillerGamerData> gamerDataEntry : gamerDataMap.entrySet()) {
//            if (gamerDataEntry.getValue().getOccupation().equals("狼人")) {
//                continue;
//            }
//            MsgBean msgTip1 = new MsgBean();
//            msgTip1.setTargetId(gamerDataEntry.getKey());
//            msgTip1.setTargetType("private");
//            msgTip1.setMsg(msgTip.toString());
//
//            MsgBean msgTip2 = new MsgBean();
//            for (WolfKillerGamerData gamerData : gamerList) {
//                gamerListMsg.append("编号" + gamerData.getNumber() + ":");
//                if (gamerData.getUserId() == gamerDataEntry.getKey()) {
//                    gamerListMsg.append("编号" + gamerData.getNumber() + ":我\n");
//                    continue;
//                }
//                gamerListMsg.append(gamerData.getCard());
//                if (gamerData.getOccupation().equals("狼人")) {
//                    gamerListMsg.append("<-队友");
//                }
//                gamerListMsg.append("\n");
//            }
//            msgTip2.setTargetId(gamerDataEntry.getKey());
//            msgTip2.setTargetType("private");
//            msgTip2.setMsg(gamerListMsg.toString());
//
//            msgQueue.add(msgTip1);
//            msgQueue.add(msgTip2);
//        }
//    }
//
//    /**
//     * 狼人事件结束
//     * @return
//     */
//    private boolean werewolvesEventEnd(){
//        List<Werewolves> werewolvesList = occupationMap.values().stream()
//                .filter(item -> item instanceof Werewolves ? true : false)
//                .map(item -> (Werewolves)item)
//                .collect(Collectors.toList());
//        long targetCount = werewolvesList.stream().map(Werewolves::getTargetId).distinct().count();
//        long selectedCount = werewolvesList.stream().map(Werewolves::getTargetId)
//                .filter(item -> item > 0 ? true : false).count();
//        if(selectedCount == werewolvesList.size() && targetCount == 1){
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * 获取所有幸存的玩家
//     *
//     * @return
//     */
//    private List<WolfKillerGamerData> getSurvivor() {
//        return gamerDataMap.values().stream().filter(item -> {
//            if (item.getStatus() == 1) {    // 存活的玩家
//                return true;
//            }
//            return false;
//        }).sorted(new Comparator<WolfKillerGamerData>() {
//            @Override
//            public int compare(WolfKillerGamerData o1, WolfKillerGamerData o2) {    // 按编号大小排序
//                return Integer.compare(o1.getNumber(), o2.getNumber());
//            }
//        }).collect(Collectors.toList());
//    }
//
//    /**
//     * 获取其他还存活的狼人玩家
//     *
//     * @param userId
//     * @return
//     */
//    private List<WolfKillerGamerData> getOthersSurvivalWerewolves(String userId) {
//        List<WolfKillerGamerData> survivor = getSurvivor();
//        return survivor.stream().filter(item -> {
//            return "狼人".equals(item.getOccupation()) && item.getUserId() != userId ? true : false;
//        }).collect(Collectors.toList());
//    }
//
//    public Map<Long, WolfKillerGamerData> getGamerDataMap() {
//        return gamerDataMap;
//    }
//
//    public void setGamerDataMap(Map<Long, WolfKillerGamerData> gamerDataMap) {
//        this.gamerDataMap = gamerDataMap;
//    }
//
//    public Map<String, Integer> getOccupationSettingMap() {
//        return occupationSettingMap;
//    }
//
//    public void setOccupationSettingMap(Map<String, Integer> occupationSettingMap) {
//        this.occupationSettingMap = occupationSettingMap;
//    }
//
//    public Map<Long, Object> getOccupationMap() {
//        return occupationMap;
//    }
//
//    public void setOccupationMap(Map<Long, Object> occupationMap) {
//        this.occupationMap = occupationMap;
//    }
//
//    public String getOccupationSetting() {
//        StringBuffer result = new StringBuffer();
//        for (Map.Entry<String, Integer> occupation : occupationSettingMap.entrySet()) {
//            if (occupation.getValue() > 0) {
//                result.append(occupation.getKey() + "=" + occupation.getValue() +"\n");
//            }
//        }
//        return result.toString();
//    }
//}
