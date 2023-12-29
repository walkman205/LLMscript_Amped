package com.gpt.service;

import com.gpt.pojo.CheckBean;
import com.gpt.utils.GptAiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class GptServiceImpl {

    public CheckBean checkBean;

    //所有question
    public ArrayList<File> questionFiles;

    //所有preprompt
    public ArrayList<File> prepromptFiles;

    @Autowired
    private GptAiUtil gptAiUtil;

    private Boolean stopFlag = false;


    /**
     * 获取 prompt 文件列表
     */
    public HashMap<String, ArrayList<String>> check() {
        HashMap<String, ArrayList<String>> repMaps = new HashMap<>();
        File questions = new File(checkBean.getQuestionPath());
        if (questions.exists()) {
            ArrayList<File> allFiles = getAllFiles(questions);
            this.questionFiles = allFiles;
            ArrayList<String> fileNames = new ArrayList<>();
            if (allFiles.size() > 0) {
                for (File currentFile : allFiles) {
                    fileNames.add(currentFile.getName());
                }
            }
            repMaps.put("questions", fileNames);
        }

        File preprompts = new File(checkBean.getPrepromptPath());
        if (preprompts.exists()) {
            ArrayList<File> allFiles = getAllFiles(preprompts);
            this.prepromptFiles = allFiles;
            ArrayList<String> fileNames = new ArrayList<>();
            if (allFiles.size() > 0) {
                for (File currentFile : allFiles) {
                    fileNames.add(currentFile.getName());
                }
            }
            repMaps.put("preprompts", fileNames);
        }

        return repMaps;
    }

    public static ArrayList<File> getAllFiles(File folder) {
        File[] files = folder.listFiles(); // 获取文件夹中所有文件和子文件夹
        ArrayList<File> currentFiles = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                //if (!file.isDirectory() && file.getName().contains(".prompt")) {
                if (!file.isDirectory() && file.getName().contains(".txt")) {
                    // 如果是子文件夹，则输出文件名
                    currentFiles.add(file);
                }
            }
        }
        return currentFiles;
    }

    /**
     * 调用 GPT 生成结果
     */
    public void generate(Session session) {
        stopFlag = false;
        System.out.println(File.separator);
        if (questionFiles == null) {
            try {
            //    session.getBasicRemote().sendText("请先check!");
                session.getBasicRemote().sendText("Please check first!");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }


        //******1.获取上级目录地址，创建report文件夹
        File file = questionFiles.get(0);
        String inputFilePath = file.getParentFile().getParentFile().getPath();
        String parentPath = file.getParentFile().getParentFile().getParentFile().getPath();
        //创建out文件夹
        File outputFile = new File(parentPath+File.separator+"output_files");
        if (!outputFile.exists()) {
            outputFile.mkdirs();
        }
        String outputFilePath = outputFile.getPath();
        String reportFilePath = outputFilePath + File.separator + "report_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        //创建report文件夹
        File reportFile = new File(reportFilePath);
        if (!reportFile.exists()) {
            reportFile.mkdirs();
        }
        //创建report数据集
        ArrayList<Map<String, String>> reportList = new ArrayList<>();


        //获取keys
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(new File(inputFilePath + File.separator + "keys.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String gpt35Key = properties.getProperty("gpt35Key");
        String gpt4Key = properties.getProperty("gpt4Key");
        String gpt4FileKey = properties.getProperty("gpt4FileKey");
        gptAiUtil.setGpt35Key(gpt35Key);
        gptAiUtil.setGpt4Key(gpt4Key);
        gptAiUtil.setGpt4FileKey(gpt4FileKey);
        String localUrl = properties.getProperty("localUrl");
        String cloudUrl = properties.getProperty("cloudUrl");
        gptAiUtil.setLocalUrl(localUrl);
        gptAiUtil.setCloudUrl(cloudUrl);

        //******2.循环读取preprompt文件
        for (File prepromptFile : prepromptFiles) {
            String prepromptFileName = prepromptFile.getName();
            String prePrompt;
            try {
            //    session.getBasicRemote().sendText("******开始处理 " + prepromptFileName);
                session.getBasicRemote().sendText("******start processing " + prepromptFileName);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                List<String> prepromptFileLines = Files.readAllLines(Paths.get(prepromptFile.getPath()));
                StringBuilder sb = new StringBuilder();
                for (String prepromptFileLine : prepromptFileLines) {
                    sb.append(prepromptFileLine);
                }
                prePrompt = sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
                try {
                //    session.getBasicRemote().sendText(prepromptFileName + " 获取处理失败");
                    session.getBasicRemote().sendText(prepromptFileName + " failed to process");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                continue;
            }

            //创建preprompt文件夹
            File prepromptReportFile = new File(reportFilePath + File.separator + prepromptFileName.replace(".txt", ""));
            //当前preprompt文件夹路径
            String prepromptReportPath = prepromptReportFile.getPath();
            if (!prepromptReportFile.exists()) {
                prepromptReportFile.mkdirs();
            }

            //******3.循环读取 question 文件并调用开启的模型
            for (File promptFile : questionFiles) {
                //获取文件信息
                String name = promptFile.getName().replaceAll(".txt", "");
                HashMap<String, String> reportMap = new HashMap<>();
                reportList.add(reportMap);
                reportMap.put("name", name);
                //获取预测结果
                if (name.contains("_good")) {
                    reportMap.put("forecast", "PASS");
                } else {
                    reportMap.put("forecast", "FAIL");
                }

                //获取prompt信息
                //System.out.println("开始处理 " + name);
                System.out.println("start processing " + name);
                StringBuilder promptStr;
                try {
                    List<String> promptFileStr = Files.readAllLines(Paths.get(promptFile.getPath()));
                    promptStr = new StringBuilder();
                    for (String str : promptFileStr) {
                        promptStr.append(str);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    //System.out.println(name + " 处理失败");
                    System.out.println(name + " processing failed");
                    try {
                    //    session.getBasicRemote().sendText(name + " 处理失败");
                        session.getBasicRemote().sendText(name + " processing failed");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    continue;
                }

                //开始调用模型

                //GPT3.5
                if (stopFlag) {
                    stopMsg(session);
                    return;
                }
                if (checkBean.getCheckData().get("gpt3") != null && checkBean.getCheckData().get("gpt3")) {
                    try {
                    //    System.out.println("开始调用chatgpt3.5");
                        System.out.println("start using chatgpt3.5");
                        String sendRes = gptAiUtil.send(1, promptStr.toString(), prePrompt, promptFile);
                        try {
                            if (sendRes != null) {
                                Files.write(Paths.get(prepromptReportPath + File.separator + name + "_gpt35.txt"), sendRes.getBytes());
                            } else {
                                try {
                                //    session.getBasicRemote().sendText(name + " 调用模型gpt3.5处理失败");
                                    session.getBasicRemote().sendText(name + " Failed in using model gpt3.5");
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }
                            //获取模型结果
                            String result = sendRes.substring(sendRes.indexOf("Verdict: ") + 9, sendRes.indexOf("Verdict: ") + 13);
                            reportMap.put("gpt3.5", result);
                            //session.getBasicRemote().sendText(name + " 调用模型gpt3.5处理成功");
                            session.getBasicRemote().sendText(name + " successful for model 3.5");
                        } catch (IOException e) {
                            e.printStackTrace();
                            //System.out.println("写入失败");
                            System.out.println("failed to write report");
                            try {
                            //    session.getBasicRemote().sendText(name + " 调用模型gpt3.5处理失败");
                                session.getBasicRemote().sendText(name + " Failed to use model gpt3.5");
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    //    System.out.println(name + "调用模型gpt3.5处理失败");
                        System.out.println(name + "failed in using model 3.5");
                        try {
                        //    session.getBasicRemote().sendText(name + " 调用模型gpt3.5处理失败,原因：" + e.getMessage());
                            session.getBasicRemote().sendText(name + " failed in using model gpt3.5, reason:" + e.getMessage());
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }

                //gpt4.0
                if (stopFlag) {
                    stopMsg(session);
                    return;
                }
                if (checkBean.getCheckData().get("gpt4") != null && checkBean.getCheckData().get("gpt4")) {
                    try {
                    //    System.out.println("开始调用chatgpt4.0");
                        System.out.println("start using chatgpt4.0");
                        String sendRes = gptAiUtil.send(2, promptStr.toString(), prePrompt, promptFile);
                        try {
                            if (sendRes != null) {
                                Files.write(Paths.get(prepromptReportPath + File.separator + name + "_gpt4.txt"), sendRes.getBytes());
                            } else {
                                try {
                                //    session.getBasicRemote().sendText(name + " 调用模型gpt4.0处理失败");
                                    session.getBasicRemote().sendText(name + " failed in using gpt 4.0");
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            //System.out.println("写入失败");
                            System.out.println("failed to write report");
                            try {
                            //    session.getBasicRemote().sendText(name + " 调用模型gpt4.0处理失败");
                                session.getBasicRemote().sendText(name + " failed in using model gpt4.0");
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                        //获取模型结果
                        String result = sendRes.substring(sendRes.indexOf("Verdict: ") + 9, sendRes.indexOf("Verdict: ") + 13);
                        reportMap.put("gpt4.0", result);
                    //    session.getBasicRemote().sendText(name + " 调用模型gpt4.0处理成功");
                        session.getBasicRemote().sendText(name + " successful for gpt4.0");
                    } catch (Exception e) {
                        e.printStackTrace();
                    //    System.out.println(name + "调用模型gpt4.0处理失败");
                        System.out.println(name + "failed in using model gpt4.0");
                        try {
                        //    session.getBasicRemote().sendText(name + " 调用模型gpt4.0处理失败，原因：" + e.getMessage());
                            session.getBasicRemote().sendText(name + " fail in using model 4.0, reason:" + e.getMessage());
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }


                //gpt4v
                if (stopFlag) {
                    stopMsg(session);
                    return;
                }
                if (checkBean.getCheckData().get("gpt4v") != null && checkBean.getCheckData().get("gpt4v")) {
                    try {
                        System.out.println("开始调用chatgpt4v");
                        String sendRes = gptAiUtil.send(3, promptStr.toString(), prePrompt, promptFile);
                        try {
                            if (sendRes != null) {
                                Files.write(Paths.get(prepromptReportPath + File.separator + name + "_gpt4v.txt"), sendRes.getBytes());
                            } else {
                                try {
                                    session.getBasicRemote().sendText(name + " 调用模型gpt4v处理失败");
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println("写入失败");
                            try {
                                session.getBasicRemote().sendText(name + " 调用模型gpt4v处理失败");
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                        //获取模型结果
                        String result = sendRes.substring(sendRes.indexOf("Verdict: ") + 9, sendRes.indexOf("Verdict: ") + 13);
                        reportMap.put("gpt4.0", result);
                        session.getBasicRemote().sendText(name + " 调用模型gpt4.0处理成功");
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println(name + "调用模型gpt4.0处理失败");
                        try {
                            session.getBasicRemote().sendText(name + " 调用模型gpt4.0处理失败，原因：" + e.getMessage());
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }


                //local
                if (stopFlag) {
                    stopMsg(session);
                    return;
                }
                if (checkBean.getCheckData().get("local") != null && checkBean.getCheckData().get("local")) {
                    try {
                    //    System.out.println("开始调用local");
                        System.out.println("start using local model");
                        String sendRes = gptAiUtil.send(4, promptStr.toString(), prePrompt, promptFile);
                        try {
                            if (sendRes != null) {
                                Files.write(Paths.get(prepromptReportPath + File.separator + name + "_local_" + checkBean.getLocalName() + ".txt"), sendRes.getBytes());
                            } else {
                                try {
                                //    session.getBasicRemote().sendText(name + " 调用模型local处理失败");
                                    session.getBasicRemote().sendText(name + " failed to use local model");
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        //    System.out.println("写入失败");
                            System.out.println("failed to write report");
                            try {
                            //    session.getBasicRemote().sendText(name + " 调用模型local处理失败");
                                session.getBasicRemote().sendText(name + " failed to use local model");
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                        //获取模型结果
                        String result = sendRes.substring(sendRes.indexOf("Verdict: ") + 9, sendRes.indexOf("Verdict: ") + 13);
                        reportMap.put("local", result);
                    //    session.getBasicRemote().sendText(name + " 调用模型local处理成功");
                        session.getBasicRemote().sendText(name + " successful for local model");
                    } catch (Exception e) {
                        e.printStackTrace();
                    //    System.out.println(name + "调用模型local处理失败");
                        System.out.println(name + "failed to use local model");
                        try {
                        //    session.getBasicRemote().sendText(name + " 调用模型local处理失败，原因：" + e.getMessage());
                            session.getBasicRemote().sendText(name + " failed to use local model, reason:" + e.getMessage());
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }


                //cloud
                if (stopFlag) {
                    stopMsg(session);
                    return;
                }
                if (checkBean.getCheckData().get("cloud") != null && checkBean.getCheckData().get("cloud")) {
                    try {
                    //    System.out.println("开始调用cloud");
                        System.out.println("start using cloud");
                        String sendRes = gptAiUtil.send(5, promptStr.toString(), prePrompt, promptFile);
                        try {
                            if (sendRes != null) {
                                Files.write(Paths.get(prepromptReportPath + File.separator + name + "_cloud_" + checkBean.getCloudName() + ".txt"), sendRes.getBytes());
                            } else {
                                try {
                                //    session.getBasicRemote().sendText(name + " 调用模型cloud处理失败");
                                    session.getBasicRemote().sendText(name + " failed to use cloud model");
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        //    System.out.println("写入失败");
                            System.out.println("failed to write report");
                            try {
                            //    session.getBasicRemote().sendText(name + " 调用模型cloud处理失败");
                                session.getBasicRemote().sendText(name + " failed to use cloud model");
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                        //获取模型结果
                        String result = sendRes.substring(sendRes.indexOf("Verdict: ") + 9, sendRes.indexOf("Verdict: ") + 13);
                        reportMap.put("cloud", result);
                    //    session.getBasicRemote().sendText(name + " 调用模型cloud处理成功");
                        session.getBasicRemote().sendText(name + " successful for cloud model");
                    } catch (Exception e) {
                        e.printStackTrace();
                    //    System.out.println(name + "调用模型cloud处理失败");
                        System.out.println(name + "failed to use cloud model");
                        try {
                        //    session.getBasicRemote().sendText(name + " 调用模型cloud处理失败，原因：" + e.getMessage());
                            session.getBasicRemote().sendText(name + " failed to use cloud model, reason:" + e.getMessage());
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }


            }


            try {
                //******4.生成报告
                File aiReport = new File(prepromptReportPath + File.separator + "report.csv");
                StringBuilder sb = new StringBuilder("\"question\\model\",\"forecast\",\"GPT3.5\",\"GPT4.0\",\"GPT4V\",\"LOCAL_" + checkBean.getLocalName() + "\",\"CLOUD_" + checkBean.getCloudName() + "\"\n");
                for (Map<String, String> reportMap : reportList) {
                    sb.append("\"").append(reportMap.get("name")).append("\",")
                            .append("\"").append(reportMap.get("forecast") != null ? reportMap.get("forecast") : "").append("\",")
                            .append("\"").append(reportMap.get("gpt3.5") != null ? reportMap.get("gpt3.5") : "").append("\",")
                            .append("\"").append(reportMap.get("gpt4.0") != null ? reportMap.get("gpt4.0") : "").append("\",")
                            .append("\"").append(reportMap.get("gpt4V") != null ? reportMap.get("gpt4V") : "").append("\",")
                            .append("\"").append(reportMap.get("local") != null ? reportMap.get("local") : "").append("\",")
                            .append("\"").append(reportMap.get("cloud") != null ? reportMap.get("cloud") : "").append("\"\n");
                }

                Files.write(Paths.get(aiReport.getPath()), sb.toString().getBytes());

                try {
                //    session.getBasicRemote().sendText("最终报告生成完成");
                    session.getBasicRemote().sendText("Final report generated successfully");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
                try {
                //    session.getBasicRemote().sendText("******报告生成失败******");
                    session.getBasicRemote().sendText("******failed to generate report******");
                    return;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } finally {
                reportList.clear();
            }

        }


        try {
        //    session.getBasicRemote().sendText("******报告生成成功，任务结束******");
            session.getBasicRemote().sendText("******successfully generating report，mission complete******");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void stop() {
        stopFlag = true;
    }

    public void stopMsg(Session session) {
        try {
        //    session.getBasicRemote().sendText("******程序中断，任务结束******");
            session.getBasicRemote().sendText("******software terminated，mission complete******");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
