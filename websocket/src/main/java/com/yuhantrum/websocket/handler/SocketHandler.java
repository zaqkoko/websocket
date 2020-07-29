package com.yuhantrum.websocket.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;

// @Component는 Bean을 생성할 때 java에서 new로 생성하듯이 생성한다.
@Component                 // TextWebSocketHandler 상속
public class SocketHandler extends TextWebSocketHandler { // 구현체에 등록할 것들

    // HashMap = 맵을 구현한다. Key와 value를 묶어 하나의 entry로 저장한다는 특징을 갖는다.
    // Map 인터페이스의 한 종류로 ( "Key", value) 로 이뤄져 있다.
    // key 값을 중복이 불가능 하고 value는 중복이 가능. value에 null값도 사용 가능하다.
    // 멀티쓰레드에서 동시에 HashMap을 건드려 Key - value값을 사용하면 문제가 될 수 있다. 멀티쓰레드에서는 HashTable을 쓴다
    HashMap<String, WebSocketSession> sessionMap = new HashMap<>(); // 웹소켓 세션을 담아둘 맵

    @Override // 해당 메소드가 부모 클래스에 있는 메소드를 재정의했다는 것을 명시적으로 선언
    public void handleTextMessage(WebSocketSession session, TextMessage message){
        // 메세지, 웹소켓 클라이언트가 데이터를 전송 (상속받은 TextWebSocketHandler가 handleTextMessage를 실행시킨다)

        String msg = message.getPayload();

        // for (keySet을 이용한 Enhanced For-Loops, Map의 key 값을 이용하여 Value를 가져옴)
        for(String key : sessionMap.keySet()){
            WebSocketSession wss = sessionMap.get(key);
            try{
                wss.sendMessage(new TextMessage(msg));
            }catch (Exception e){
                // 메소드 getMessage, toString과는 다르게 리턴값이 없다. 이 메소드를 호출하면 메소드가 내부적으로 예외 결과를 화면에 출력한다.
                e.printStackTrace();
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 웹소켓이 연결되면 동작한다.

        // super : 부모 클래스로부터 상속받은 필드나 메소드를 자식 클래스에서 참조하는데 사용하는 참조 변수. (까먹지말자)
        super.afterConnectionEstablished(session);
        sessionMap.put(session.getId(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 웹소켓이 종료되면 동작한다.

        sessionMap.remove(session.getId());
        super.afterConnectionClosed(session, status);
    }
}
