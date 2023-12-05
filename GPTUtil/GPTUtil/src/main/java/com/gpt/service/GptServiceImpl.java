package com.gpt.service;

import com.gpt.utils.GptAiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class GptServiceImpl {

    public String path;

    public ArrayList<File> files;

    @Autowired
    private GptAiUtil gptAiUtil;

    /**
     * 获取 prompt 文件列表
     */
    public List check(String path) {
        if (path != null) {
            this.path = path;
        }
        File file = new File(this.path);
        if (file.exists()) {
            ArrayList<File> allFiles = getAllFiles(file);
            this.files = allFiles;
            ArrayList<String> fileNames = new ArrayList<>();
            if (allFiles.size() > 0) {
                for (File currentFile : allFiles) {
                    fileNames.add(currentFile.getName());
                }
            }
            return fileNames;
        } else {
            return null;
        }
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
    public void generate(String prePrompt, Session session) {
        if (files == null) {
            try {
                session.getBasicRemote().sendText("请先check!");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        String lastPath = files.get(0).getPath().split("\\\\questions")[0];
        String reportPath = lastPath + "\\report_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String questionReportPath = reportPath + "\\report";
        //创建report文件夹
        File reportFile = new File(reportPath);
        if (!reportFile.exists()) {
            reportFile.mkdirs();
        }
        File questionReportFile = new File(questionReportPath);
        if (!questionReportFile.exists()) {
            questionReportFile.mkdirs();
        }

        //循环文件

        ArrayList<Map<String, String>> reportList = new ArrayList<>();



        for (File promptFile : files) {
            //获取文件信息
            String name = promptFile.getName();
            HashMap<String, String> reportMap = new HashMap<>();
            reportList.add(reportMap);
            reportMap.put("name", name);
            //获取预测结果
            if (name.contains("_good")) {
                reportMap.put("forecast", "PASS");
            } else {
                reportMap.put("forecast", "FAIL");
            }

            String path = promptFile.getPath();
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


            //循环调用模型
            try {
                System.out.println("开始调用chatgpt3.5");
                String sendRes = gptAiUtil.send(1, promptStr.toString(), prePrompt, promptFile);
                try {
                    if (sendRes != null) {
                        Files.write(Paths.get(questionReportPath + "\\" + name + "_gpt35.report"), sendRes.getBytes());
                        session.getBasicRemote().sendText(name + " 调用模型gpt3.5处理成功");
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
                    session.getBasicRemote().sendText(name + " 调用模型gpt3.5处理失败");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }


            //循环调用模型
            try {
                System.out.println("开始调用chatgpt4.0");
                String sendRes = gptAiUtil.send(2, promptStr.toString(), prePrompt, promptFile);
                try {
                    if (sendRes != null) {
                        Files.write(Paths.get(questionReportPath + "\\" + name + "_gpt4.report"), sendRes.getBytes());
                        session.getBasicRemote().sendText(name + " 调用模型gpt4.0处理成功");
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
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(name + "调用模型gpt4.0处理失败");
                try {
                    session.getBasicRemote().sendText(name + " 调用模型gpt4.0处理失败");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

//
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
            session.getBasicRemote().sendText("调用模型任务完成，开始生成最终报告");
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        //生成报告
        File aiReport = new File(reportPath + "\\report.csv");
        StringBuilder sb = new StringBuilder("\"question\\model\",\"forecast\",\"GPT3.5\",\"GPT4.0\",\"GPT4.0PICTURE\",\"OTHER\"\n");
        for (Map<String, String> reportMap : reportList) {
            sb.append("\"").append(reportMap.get("name")).append("\",")
                    .append("\"").append(reportMap.get("forecast") != null ? reportMap.get("forecast") : "").append("\",")
                    .append("\"").append(reportMap.get("gpt3.5") != null ? reportMap.get("gpt3.5") : "").append("\",")
                    .append("\"").append(reportMap.get("gpt4.0") != null ? reportMap.get("gpt4.0") : "").append("\",")
                    .append("\"").append(reportMap.get("gpt4.0picture") != null ? reportMap.get("gpt4.0picture") : "").append("\",")
                    .append("\"").append(reportMap.get("other") != null ? reportMap.get("other") : "").append("\"\n");
        }


        try {
            Files.write(Paths.get(aiReport.getPath()), sb.toString().getBytes());
        } catch (IOException e) {
            try {
                session.getBasicRemote().sendText("******报告生成失败，任务结束******");
                return;
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }

        try {
            session.getBasicRemote().sendText("******报告生成成功，任务结束******");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
