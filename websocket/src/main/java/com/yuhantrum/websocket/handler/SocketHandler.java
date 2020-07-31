package com.yuhantrum.websocket.handler;

import com.fasterxml.jackson.databind.util.JSONPObject;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// @Component는 Bean을 생성할 때 java에서 new로 생성하듯이 생성한다.
@Component                 // TextWebSocketHandler 상속
public class SocketHandler extends TextWebSocketHandler { // 구현체에 등록할 것들

    // HashMap = 맵을 구현한다. Key와 value를 묶어 하나의 entry로 저장한다는 특징을 갖는다.
    // Map 인터페이스의 한 종류로 ( "Key", value) 로 이뤄져 있다.
    // key 값을 중복이 불가능 하고 value는 중복이 가능. value에 null값도 사용 가능하다.
    // 멀티쓰레드에서 동시에 HashMap을 건드려 Key - value값을 사용하면 문제가 될 수 있다. 멀티쓰레드에서는 HashTable을 쓴다

    // HashMap<String, WebSocketSession> sessionMap = new HashMap<>(); // 웹소켓 세션을 담아둘 맵
    List<HashMap<String, Object>> rls = new ArrayList<>(); // 웹소켓 세션을 담아둘 리스트 -- roomListSessions


    // 파일 업로드
    private static final String FILE_UPLOAD_PATH = "C:/DOWNLOAD/";
    static int fileUploadIdx = 0;
    static String fileUploadSession = "";

    // 방구분, 해당 방에 존재하는 session값들에게만 메세지를 발송

    @Override // 해당 메소드가 부모 클래스에 있는 메소드를 재정의했다는 것을 명시적으로 선언
    public void handleTextMessage(WebSocketSession session, TextMessage message){
        // 메세지, 웹소켓 클라이언트가 데이터를 전송 (상속받은 TextWebSocketHandler가 handleTextMessage를 실행시킨다)

        /* 현재의 방번호를 가져오고 방번호+세션정보를 관리하는 rls리스트 컬렉션에서 데이터를 조회한 후에
         * 해당 HashMap을 임시 맵에 파싱하여 roomNumber의 키값을 제외한 모든 세션 키값들을 웹소켓을 통해 메세지를 보내준다.*/

        String msg = message.getPayload(); // JSON 형태의 String 메시지를 받는다.

        // message.getPayload()를 통해 받은 문자열을 Json 파싱을 위해 obj에 저장
        JSONObject obj = jsonToObjectParser(msg); // JSON 데이터를 JSONObject로 파싱

        // rN에 roomNumber 문자열로 저장
        String rN = (String)obj.get("roomNumber"); // 방의 번호를 받는다.
        String msgType = (String)obj.get("type"); // 메시지 타입을 확인한다.
        HashMap<String, Object> temp = new HashMap<String, Object>();

        // 세션 리스트의 사이즈가 0보다 크면
        if(rls.size() > 0){

            // 세션 리스트 사이즈만큼 for문
            for(int i=0; i<rls.size(); i++){

                // 세션 리스트의 저장된 방번호를 가져와서
                String roomNumber = (String) rls.get(i).get("roomNumber");

                // 같은 값의 방번호가 존재한다면
                if(roomNumber.equals(rN)){

                    // 해당 방번호의 세션리스트의 존재하는 모든 object값을 가져온다.
                    temp = rls.get(i);
                    fileUploadIdx = i;
                    fileUploadSession = (String)obj.get("sessionId");
                    break;
                }
            }

            // 메시지타입이 파일업로드가 아닐 때만 전송
            if(!msgType.equals("fileUpload")) {

                // 해당 방의 세션들만 찾아서 메세지를 발송
                for (String k : temp.keySet()) {

                    // // 방번호일 경우에는 건너뜀
                    if (k.equals("roomNumber")) {
                        continue;
                    }

                    WebSocketSession wss = (WebSocketSession) temp.get(k);

                    // wss가 있다면
                    if (wss != null) {
                        try {
                            wss.sendMessage(new TextMessage(obj.toJSONString()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        /*
        // for (keySet을 이용한 Enhanced For-Loops, Map의 key 값을 이용하여 Value를 가져옴)
        for(String key : sessionMap.keySet()){
            WebSocketSession wss = sessionMap.get(key);
            try{
                wss.sendMessage(new TextMessage(obj.toJSONString()));
            }catch (Exception e){
                // 메소드 getMessage, toString과는 다르게 리턴값이 없다. 이 메소드를 호출하면 메소드가 내부적으로 예외 결과를 화면에 출력한다.
                e.printStackTrace();
            }
        }*/
    }

    @Override        // 바이너리 메세지 발송
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message){

        // ByteBuffer = 채널이 데이터를 읽고 쓰는 버퍼는 모두 ByteBuffer.
        ByteBuffer byteBuffer = message.getPayload();
        String fileName = "temp.jpg"; // temp.jpg로 저장
        File dir = new File(FILE_UPLOAD_PATH);
        if(!dir.exists()){
            dir.mkdirs(); // mkdirs() = 한 번에 여러 디렉토리 생성. (mkdir() = 한 번에 하나의 디렉토리만 생성)
        }

        File file = new File(FILE_UPLOAD_PATH, fileName);
        FileOutputStream out = null; // FileOutputStream = 파일을 바이트 단위의 출력을 내보냄.
        FileChannel outChannel = null; // 정적 메소드인 open() 을 호출해 얻거나, IO 의 FileInputStream, FileOutputStream의 getChannel()을 호출해서 얻을 수 있다.

        try{
            byteBuffer.flip(); // byteBuffer를 읽기 위해 셋팅
            out = new FileOutputStream(file, true); // 생성을 위해 OutputStream을 연다.
            outChannel = out.getChannel(); // 채널을 연다.
            byteBuffer.compact(); // 파일 복사
            outChannel.write(byteBuffer); // 파일을 쓴다.
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if(out != null){
                    out.close();
                }
                if(outChannel != null){
                    outChannel.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        byteBuffer.position(0); // 파일을 저장하면서 position 값이 변경되었으므로 0으로 초기화.

        // 파일쓰기가 끝나면 이미지를 발송
        HashMap<String, Object> temp = rls.get(fileUploadIdx);

        for(String k : temp.keySet()){
            if(k.equals("roomNumber")){
                continue;
            }
            WebSocketSession wss = (WebSocketSession) temp.get(k);
            try{
                wss.sendMessage(new BinaryMessage(byteBuffer)); // 초기화된 버퍼를 발송한다.
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    // suppressWarnings - 컴파일러가 경고하는 내용 중 제외시킬 때 사용(unchecked) - 검증되지않은 연산자관련 경고 억제
    @SuppressWarnings("unchecked")
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 웹소켓이 연결되면 동작한다.

        // super : 부모 클래스로부터 상속받은 필드나 메소드를 자식 클래스에서 참조하는데 사용하는 참조 변수. (까먹지말자)
        super.afterConnectionEstablished(session);

        boolean flag = false;
        // getUri() = 웹 Resource 를 얻는다. Uri는 요청 대상 Resource를 식별하는 정보를 포함한다.
        String url = session.getUri().toString();
        System.out.println(url);

        // /chating/ 으로 split (split() = 문자열 분할)
        String roomNumber = url.split("/chating/")[1];

        // 방 사이즈 조사
        int idx = rls.size();
        if(rls.size() > 0){
            for(int i=0; i<rls.size(); i++){
                String rN = (String) rls.get(i).get("roomNumber");
                if(rN.equals(roomNumber)){
                    flag = true;
                    idx = i;
                    break;
                }
            }
        }

        if(flag){ // 존재하는 방이라면 세션만 추가
            HashMap<String, Object> map = rls.get(idx);
            map.put(session.getId(), session);
        }else{ // 최초 생성하는 방이라면 방번호화 세션 추가
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("roomNumber", roomNumber);
            map.put(session.getId(), session);
            rls.add(map);
        }

        // 생성된 세션을 저장하면 발신메시지 타입은 getId라고 명시 후 생성된 세션ID값을 클라이언트로 발송
        // 클라이언트에서는 type값을 통해 메세지와 초기 설정값을 구분함.
        // 세션 등록이 끝나면 발급받은 세션ID값의 메세지를 발송한다.
        JSONObject obj = new JSONObject();
        obj.put("type", "getId");
        obj.put("sessionId", session.getId());
        session.sendMessage(new TextMessage(obj.toJSONString()));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 웹소켓이 종료되면 동작한다.

        if(rls.size()>0){ // 소켓이 종료되면 해당 세션 값들을 찾아서 지운다.
            for(int i=0; i<rls.size(); i++){
                rls.get(i).remove(session.getId());
            }
        }
        super.afterConnectionClosed(session, status);
    }

    // Json 형태의 문자열을 변수로 받아서 Json Simple의 파서를 활용해 JSONObject로 파싱처리를 해주는 함수
    // 31행 참고) Json 파싱을 하기 위해 jsonToObjectParser에 넣어 JSONObject 값으로 받아 강제 문자열 형태로 보내준다.
    private static JSONObject jsonToObjectParser(String jsonStr){
        JSONParser parser = new JSONParser();
        JSONObject obj = null;
        try{
            obj = (JSONObject) parser.parse(jsonStr);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return obj;
    }



}
