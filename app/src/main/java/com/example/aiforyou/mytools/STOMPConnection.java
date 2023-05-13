package com.example.aiforyou.mytools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class STOMPConnection {
    public interface MessageReceivedListener {
        void onNewMessage(String json);
    }

    private final StompClient client;
    private final Gson gson;
    private final MessageReceivedListener listener;

    public STOMPConnection(MessageReceivedListener listener)
    {
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").create();

        client = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://" + "192.168.0.102:8080" + "/ws");
        client.connect();

        this.listener = listener;
    }

    public void sendMessage(Object msg, String mapperEndPoint)
    {
        client.send("/app/" + mapperEndPoint, gson.toJson(msg)).subscribe();
    }

    public void subscribe(int channelId)
    {
        client.topic("/user/" + channelId + "/work").subscribe(
                stompMessage -> listener.onNewMessage(stompMessage.getPayload()));
    }
}