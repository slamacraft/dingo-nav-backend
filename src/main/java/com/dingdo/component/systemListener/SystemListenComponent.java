package com.dingdo.component.systemListener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dingdo.common.annotation.Instruction;
import com.dingdo.common.annotation.VerifiAnnotation;
import com.dingdo.enums.RedisEnum;
import com.dingdo.msgHandler.model.ReqMsg;
import com.dingdo.component.schedule.model.SystemTaskInfo;
import com.dingdo.component.schedule.TaskRegister;
import com.dingdo.util.InstructionUtils;
import com.sun.management.OperatingSystemMXBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;

/**
 * 系统运行状态监听组件
 */
@Component
public class SystemListenComponent {
    private static final int CPU_TIME = 30;
    private static final int PERCENT = 100;
    private static final int FAULT_LENGTH = 10;
    private static final String linuxVersion = null;

    private final StringRedisTemplate stringRedisTemplate;
    private final TaskRegister taskRegister;

    @Autowired
    public SystemListenComponent(StringRedisTemplate stringRedisTemplate, TaskRegister taskRegister) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.taskRegister = taskRegister;
    }

    @VerifiAnnotation
    @Instruction(description = "获取系统信息")
    public String getSysInfo(ReqMsg reqMsg, Map<String, String> params) {
        String sysJson = stringRedisTemplate.opsForValue().get(RedisEnum.SYSINFO.toString());
        MonitorInfoBean infoBean = JSONObject.parseObject(sysJson, MonitorInfoBean.class);
        return Objects.requireNonNull(infoBean).toString();
    }


    @VerifiAnnotation
    @Instruction(description = "设置更新系统信息间隔",
            errorMsg = "设置错误，指令的参数格式为:\n" + "时间间隔=【数字】")
    public String setUpdateSysInfoTime(ReqMsg reqMsg, Map<String, String> params) {
        int time = InstructionUtils.getParamValueOfInteger(params, "time", "时间间隔") * 1000;
//        SchedulingRunnable task = new SchedulingRunnable(SystemListenComponent.class,
//                    "updateSysInfo");
//        taskRegister.addCronTask(task, time, 0);
        SystemTaskInfo systemTaskInfo = new SystemTaskInfo("设置更新系统信息间隔",
                SystemListenComponent.class,
                "updateSysInfo",
                "0 0/" + time + " * * * ?");
        taskRegister.addCronTask(systemTaskInfo);
        return "设置系统信息更新时间成功！";
    }


    public void updateSysInfo() {
        try {
            String info = JSON.toJSONString(this.getMonitorInfoBean());
            stringRedisTemplate.opsForValue().set(RedisEnum.SYSINFO.toString(), info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 返回构造好的系统信息实例
     *
     * @return
     * @throws Exception
     */
    public MonitorInfoBean getMonitorInfoBean() throws Exception {
        int kb = 1024 * 1024;
        //可使用内存
        long totalMemory = Runtime.getRuntime().totalMemory() / kb;
        //剩余内存
        long freeMemory = Runtime.getRuntime().freeMemory() / kb;
        //最大可使用内存
        long maxMemory = Runtime.getRuntime().maxMemory() / kb;
        //操作系统
        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        String osName = System.getProperty("os.name");
        //总物理内存
        long totalMemorySize = osmxb.getTotalPhysicalMemorySize() / kb;
        //剩余物理内存
        long freePhysicalMemorySize = osmxb.getFreePhysicalMemorySize() / kb;
        //已使用物理内存
        long usedMemory = (osmxb.getTotalPhysicalMemorySize() - osmxb.getFreePhysicalMemorySize()) / kb;
        //获得总线程数
        ThreadGroup parentThread;
        parentThread = Thread.currentThread().getThreadGroup();
        while (parentThread.getParent() != null) {
            parentThread = parentThread.getParent();
        }
        int totalThread = parentThread.activeCount();
        double cpuRatio;
        if (osName.toLowerCase().startsWith("windows")) {
            cpuRatio = this.getCpuRatioForWindows();
        } else {
            cpuRatio = getCpuRateForLinux();
        }

        //构造系统信息实例
        MonitorInfoBean infoBean = new MonitorInfoBean();
        infoBean.setFreeMemory(freeMemory);
        infoBean.setFreePhysicalMemorySize(freePhysicalMemorySize);
        infoBean.setMaxMemory(maxMemory);
        infoBean.setOsName(osName);
        infoBean.setTotalMemory(totalMemory);
        infoBean.setTotalMemorySize(totalMemorySize);
        infoBean.setTotalThread(totalThread);
        infoBean.setUsedMemory(usedMemory);
        infoBean.setCpuRatio(cpuRatio);
        return infoBean;
    }

    private static double getCpuRateForLinux() {
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader brStat = null;
        StringTokenizer tokenStat;
        try {
            System.out.println("Get usage rate of CUP , linux version: " + linuxVersion);
            Process process = Runtime.getRuntime().exec("top -b -n 1");
            is = process.getInputStream();
            isr = new InputStreamReader(is);
            brStat = new BufferedReader(isr);
            if ("2.4".equals(linuxVersion)) {
                brStat.readLine();
                brStat.readLine();
                brStat.readLine();
                brStat.readLine();
                tokenStat = new StringTokenizer(brStat.readLine());
                tokenStat.nextToken();
                tokenStat.nextToken();
                String user = tokenStat.nextToken();
                tokenStat.nextToken();
                String system = tokenStat.nextToken();
                tokenStat.nextToken();
                String nice = tokenStat.nextToken();
                System.out.println(user + " , " + system + " , " + nice);
                user = user.substring(0, user.indexOf("%"));
                system = system.substring(0, system.indexOf("%"));
                nice = nice.substring(0, nice.indexOf("%"));
                float userUsage = Float.parseFloat(user);
                float systemUsage = Float.parseFloat(system);
                float niceUsage = Float.parseFloat(nice);
                return (userUsage + systemUsage + niceUsage) / 100;
            } else {
                brStat.readLine();
                brStat.readLine();
                tokenStat = new StringTokenizer(brStat.readLine());
                tokenStat.nextToken();
                tokenStat.nextToken();
                tokenStat.nextToken();
                tokenStat.nextToken();
                tokenStat.nextToken();
                tokenStat.nextToken();
                tokenStat.nextToken();
                String cpuUsage = tokenStat.nextToken();
                System.out.println("CPU idle : " + cpuUsage);
                Float usage = new Float(cpuUsage.substring(0, cpuUsage.indexOf("%")));
                return (1 - usage / 100);
            }
        } catch (Exception ioe) {
            System.out.println(ioe.getMessage());
            freeResource(is, isr, brStat);
            return 1;
        } finally {
            freeResource(is, isr, brStat);
        }
    }

    private static void freeResource(InputStream is, InputStreamReader isr,
                                     BufferedReader br) {
        try {
            if (is != null)
                is.close();
            if (isr != null)
                isr.close();
            if (br != null)
                br.close();
        } catch (Exception ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    private double getCpuRatioForWindows() {
        try {
            String procCmd = System.getenv("windir") + "//system32//wbem//wmic.exe process get Caption,CommandLine,KernelModeTime,ReadOperationCount,ThreadCount,UserModeTime,WriteOperationCount";
            // 取进程信息
            long[] c0 = readCpu(Runtime.getRuntime().exec(procCmd));
            Thread.sleep(CPU_TIME);
            long[] c1 = readCpu(Runtime.getRuntime().exec(procCmd));
            if (c0 != null && c1 != null) {
                long idletime = c1[0] - c0[0];
                long busytime = c1[1] - c0[1];
                return (double) (PERCENT * (busytime) / (busytime + idletime));
            } else {
                return 0.0;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0.0;
        }
    }

    /**
     * 读取CPU信息.
     *
     * @param proc
     * @return
     * @author GuoHuang
     */
    private long[] readCpu(final Process proc) {
        long[] retn = new long[2];
        try {
            proc.getOutputStream().close();
            InputStreamReader ir = new InputStreamReader(proc.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line = input.readLine();
            if (line == null || line.length() < FAULT_LENGTH) {
                return null;
            }
            int capidx = line.indexOf("Caption");
            int cmdidx = line.indexOf("CommandLine");
            int rocidx = line.indexOf("ReadOperationCount");
            int umtidx = line.indexOf("UserModeTime");
            int kmtidx = line.indexOf("KernelModeTime");
            int wocidx = line.indexOf("WriteOperationCount");
            long idletime = 0;
            long kneltime = 0;
            long usertime = 0;
            while ((line = input.readLine()) != null) {
                if (line.length() < wocidx) {
                    continue;
                }
                // 字段出现顺序：Caption,CommandLine,KernelModeTime,ReadOperationCount,
                // ThreadCount,UserModeTime,WriteOperation
                String caption = line.substring(capidx, cmdidx - 1).trim().replace(" ", "");
                String cmd = line.substring(cmdidx, kmtidx - 1).trim().replace(" ", "");
                if (cmd.contains("wmic.exe")) {
                    continue;
                }
                String s1 = line.substring(kmtidx, rocidx - 1).trim().replace(" ", "");
                String s2 = line.substring(umtidx, wocidx - 1).trim().replace(" ", "");
                if (caption.equals("System Idle Process") || caption.equals("System")) {
                    if (s1.length() > 0)
                        idletime += Long.parseLong(s1);
                    if (s2.length() > 0)
                        idletime += Long.parseLong(s2);
                    continue;
                }
                if (s1.length() > 0)
                    kneltime += Long.parseLong(s1);
                if (s2.length() > 0)
                    usertime = usertime + Long.parseLong(s2);
            }
            retn[0] = idletime;
            retn[1] = kneltime + usertime;
            return retn;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                proc.getInputStream().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
