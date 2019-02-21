package com.example.projectmanagement.webSocket;


import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@ServerEndpoint("/talk")
public class talkSocket {
    private volatile static Map<Session, String> sessionAndUserNameMap = new HashMap<>();

    @OnOpen
    public void open(Session session) {
        sessionAndUserNameMap.put(session, "");
    }

    @OnMessage
    public void message(Session session, String jsonString) throws IOException {
       /* JSONObject jsonObject = JSONObject.fromObject(jsonString);*/
      /*  talkMessageEntity temp = (talkMessageEntity) JSONObject.toBean(jsonObject, talkMessageEntity.class);*/
    /*    String newJsonString=JSONObject.fromObject(temp).toString();*/
        Set<Session> sessionSet = sessionAndUserNameMap.keySet();
        for (Session session1 : sessionSet) {
            if (!session1.getId().equals(session.getId())) {
                sendMessage(session1,jsonString);
            }
        }
    }

    public void sendMessage(Session session, String msg) throws IOException {
        session.getBasicRemote().sendText(msg);
    }

    @OnClose
    public void close(Session session) {
        sessionAndUserNameMap.remove(session);
    }
}
