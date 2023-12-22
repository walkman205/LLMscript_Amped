package com.gpt.controller;

import com.gpt.service.GptServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint("/chat")
@Component
public class GptSocketServer {
    private static GptServiceImpl gptServiceImpl;

    @Autowired
    private void setGptServiceImpl(GptServiceImpl gptServiceImpl) {
        GptSocketServer.gptServiceImpl = gptServiceImpl;
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("WebSocket connection opened: " + session.getId());
        try {
            session.getBasicRemote().sendText("******任务开始******");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Received message from " + session.getId() + ": " + message);
        gptServiceImpl.generate(session);
    }
}
