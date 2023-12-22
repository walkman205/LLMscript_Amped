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
        System.out.println(File.separator);
        if (questionFiles == null) {
            try {
                session.getBasicRemote().sendText("请先check!");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }


        //******1.获取上级目录地址，创建report文件夹
        String lastFile = questionFiles.get(0).getPath();
        int end = lastFile.indexOf(File.separator + "questions");
        String lastPath = lastFile.substring(0, end);
        String questionReportPath = lastPath + File.separator + "report_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        //创建report文件夹
        File reportFile = new File(questionReportPath);
        if (!reportFile.exists()) {
            reportFile.mkdirs();
        }
        File questionReportFile = new File(questionReportPath);
        if (!questionReportFile.exists()) {
            questionReportFile.mkdirs();
        }
        //创建report数据集
        ArrayList<Map<String, String>> reportList = new ArrayList<>();


        //获取keys
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(new File(lastPath + File.separator + "keys.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String gpt35Key = properties.getProperty("gpt35Key");
        String gpt4Key = properties.getProperty("gpt4Key");
        String gpt4FileKey = properties.getProperty("gpt4FileKey");
        gptAiUtil.setGpt35Key(gpt35Key);
        gptAiUtil.setGpt4Key(gpt4Key);
        gptAiUtil.setGpt4FileKey(gpt4FileKey);

        //******2.循环读取preprompt文件
        for (File prepromptFile : prepromptFiles) {
            String prepromptFileName = prepromptFile.getName();
            String prePrompt;
            try {
                session.getBasicRemote().sendText("******开始处理 " + prepromptFileName);
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
                    session.getBasicRemote().sendText(prepromptFileName + " 获取处理失败");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                continue;
            }

            //创建preprompt文件夹
            File prepromptReportFile = new File(questionReportPath + File.separator + prepromptFileName.replace(".txt", ""));
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
                System.out.println("开始处理 " + name);
                StringBuilder promptStr;
                try {
                    List<String> promptFileStr = Files.readAllLines(Paths.get(promptFile.getPath()));
                    promptStr = new StringBuilder();
                    for (String str : promptFileStr) {
                        promptStr.append(str);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println(name + " 处理失败");
                    try {
                        session.getBasicRemote().sendText(name + " 处理失败");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    continue;
                }

                //开始调用模型

                //GPT3.5
                if (checkBean.getCheckData().get("gpt3") != null && checkBean.getCheckData().get("gpt3")) {
                    try {
                        System.out.println("开始调用chatgpt3.5");
                        String sendRes = gptAiUtil.send(1, promptStr.toString(), prePrompt, promptFile);
                        try {
                            if (sendRes != null) {
                                Files.write(Paths.get(prepromptReportPath + File.separator + name + "_gpt35.txt"), sendRes.getBytes());
                            } else {
                                try {
                                    session.getBasicRemote().sendText(name + " 调用模型gpt3.5处理失败");
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }
                            //获取模型结果
                            String result = sendRes.substring(sendRes.indexOf("Verdict: ") + 9, sendRes.indexOf("Verdict: ") + 13);
                            reportMap.put("gpt3.5", result);
                            session.getBasicRemote().sendText(name + " 调用模型gpt3.5处理成功");
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println("写入失败");
                            try {
                                session.getBasicRemote().sendText(name + " 调用模型gpt3.5处理失败");
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println(name + "调用模型gpt3.5处理失败");
                        try {
                            session.getBasicRemote().sendText(name + " 调用模型gpt3.5处理失败,原因：" + e.getMessage());
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }

                //gpt4.0
                if (checkBean.getCheckData().get("gpt3") != null && checkBean.getCheckData().get("gpt4")) {
                    try {
                        System.out.println("开始调用chatgpt4.0");
                        String sendRes = gptAiUtil.send(2, promptStr.toString(), prePrompt, promptFile);
                        try {
                            if (sendRes != null) {
                                Files.write(Paths.get(prepromptReportPath + File.separator + name + "_gpt4.txt"), sendRes.getBytes());
                            } else {
                                try {
                                    session.getBasicRemote().sendText(name + " 调用模型gpt4.0处理失败");
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println("写入失败");
                            try {
                                session.getBasicRemote().sendText(name + " 调用模型gpt4.0处理失败");
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


                //gpt4v
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

//            try {
//                System.out.println("开始调用chatgpt4.0图片文件形式");
//                gptAiUtil.send(3, file.getPath() + "\\report\\gpt4_report.txt", promptStr.toString(), prePromptStr.toString(), promptFile);
//            } catch (Exception e) {
//                e.printStackTrace();
//                System.out.println(file.getName() + "调用模型gpt4处理失败");
//                continue;
//            }

            }

            try {
                //******4.生成报告
                File aiReport = new File(prepromptReportPath + File.separator + "report.csv");
                StringBuilder sb = new StringBuilder("\"question\\model\",\"forecast\",\"GPT3.5\",\"GPT4.0\",\"GPT4.0PICTURE\",\"OTHER\"\n");
                for (Map<String, String> reportMap : reportList) {
                    sb.append("\"").append(reportMap.get("name")).append("\",")
                            .append("\"").append(reportMap.get("forecast") != null ? reportMap.get("forecast") : "").append("\",")
                            .append("\"").append(reportMap.get("gpt3.5") != null ? reportMap.get("gpt3.5") : "").append("\",")
                            .append("\"").append(reportMap.get("gpt4.0") != null ? reportMap.get("gpt4.0") : "").append("\",")
                            .append("\"").append(reportMap.get("other") != null ? reportMap.get("other") : "").append("\"\n");
                }

                Files.write(Paths.get(aiReport.getPath()), sb.toString().getBytes());

                try {
                    session.getBasicRemote().sendText("最终报告生成完成");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
                try {
                    session.getBasicRemote().sendText("******报告生成失败******");
                    return;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }finally{
                reportList.clear();
            }

        }


        try {
            session.getBasicRemote().sendText("******报告生成成功，任务结束******");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
