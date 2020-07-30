package com.yuhantrum.websocket.controller;

import com.yuhantrum.websocket.vo.Room;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

// JDK14

/* 1) 어노테이션이란?
     1. @를 이용한 주석, 자바코드에 주석을 달아 특별한 의미를 부여하는 것을 뜻함.
     2. 컴파일러가 특정 오류를 억제하도록 지시하는 것과 같이 프로그램 코드의 일부가 아닌 프로그램에 관한 데이터를 제공,
        코드에 정보를 추가하는 정형화된 방법.

   2) 어노테이션의 용도
     1. @overlide 어노테이션처럼 컴파일러를 위한 정보를 제공하기 위한 용도
     2. 스프링 프레임워크의 @Controller 어노테이션처럼 런타임에 리플렉션을 이용해서 특수 기능을 추가하기 위한 용도
     3. 컴파일 과정에 어노테이션 정보로부터 코드를 생성하기 위한 용도
        ※ 자바 리플렉션 : 다른 언어에는 존재하지 않는 특별한 기능, 컴파일 시간이 아닌 실행 시간에 동적으로 특정 클래스의 정보를
                        객체를 통해 분석 및 추출해내는 프로그래밍 기법
 */

/* 컨트롤러를 구현할때는 요청 URL 매핑을 @RequestMapping 어노테이션을 이용하여 설정한다.
   Spring MVC의 Controller 클래스 선언을 단순화 시켜줌.
   @Controller 어노테이션은 클래스 타입에 적용되며, @Controller를 붙이면 해당 클래스를 웹 요청을 처리하는 컨트롤러로 사용할 수 있다. */
@Controller
public class MainController{    // chat 파일을 넘겨주는 view 컨트롤러

    // roomlist를 ArrayList로 저장
    List<Room> roomList = new ArrayList<Room>();

    // 방번호는 공유되어야하기 때문에 static
    static int roomNumber = 0;

    // @RequestMapping 어노테이션은 컨트롤러가 처리할 요청 URL을 명시하는데 사용되며, 클래스나 메소드에 적용된다.
    @RequestMapping("/chat") // 여기에 /chat을 적었기 때문에 주소는 localhost:3000/chat이 된다.
    public ModelAndView chat(){
        ModelAndView mv = new ModelAndView(); // 인스턴스화
        mv.setViewName("chat"); // view의 이름(view 경로)
        return mv; // 반환값으로 ModelAndView 객체를 반환
    }

    // 방 페이지 @Return (방으로 접근시킬 뷰페이지)
    @RequestMapping("/room") // 여기에 /chat을 적었기 때문에 주소는 localhost:3000/room이 된다
    public ModelAndView room(){
        ModelAndView mv = new ModelAndView(); // 인스턴스화
        mv.setViewName("room"); // view의 이름(view의 경로)
        return mv; // 반환값으로 ModelAndView 객체를 반환
    }

    // param = object 요소에 의해 호출되는 플러그인의 매개변수를 정의할 때 사용한다.
    // @ResponseBody - 스프링에서 비동기 처리를 하는 경우 사용. 메소드에서 리턴되는 값은 View를 통해 출력되지않고 HTTP ResponseBody에 직접 쓰여지게됨
    // @ReQuestParam - 단일 파라미터를 전달받을 때 사용한다.

    // 방 생성 @param params @return
    @RequestMapping("/createRoom")
    public @ResponseBody List<Room> createRoom(@RequestParam HashMap<Object, Object> params){

        // 룸네임을 String으로 전달받아 저장
        String roomName = (String) params.get("roomName");

        // 만약 룸네임이 null이 아니고 좌우공백 제거했을 때 ""이 아니라면
        if(roomName != null && !roomName.trim().equals("")){
            Room room = new Room(); // 인스턴스
            room.setRoomNumber(++roomNumber); // 룸넘버 받고 ++
            room.setRoomName(roomName); // 룸네임은 받은 룸네임
            roomList.add(room); // 룸리스트에 room 저장
        }
        return roomList; // 반환 roomlist
    }

    // 방 정보 가져오기 @param params @return
    @RequestMapping("/getRoom")
    public @ResponseBody List<Room> getRoom(@RequestParam HashMap<Object, Object> params){
        return roomList;
    }

    // 채팅방 @Return
    @RequestMapping("/moveChating")
    public ModelAndView chating(@RequestParam HashMap<Object, Object> params){
        ModelAndView mv = new ModelAndView();

        // roomNumber를 받아 문자열을 해석하여 정수를 리턴(ParseInt)해 받고 해당 값을 10진수의 Integer형으로 반환해 저장.
        int roomNumber = Integer.parseInt((String) params.get("roomNumber"));

        // filter = 특정 조건으로 스트림의 컨텐츠를 필러링하는 것
        // 전달받은 파라미터 값으로 방이 생성되었는지 filter로 체크후
        List<Room> new_list = roomList.stream().filter(o->o.getRoomNumber()==roomNumber).collect(Collectors.toList());

        // 존재하는 방이면 해당방으로 이동
        if(new_list != null && new_list.size()>0){
            mv.addObject("roomName", params.get("roomName"));
            mv.addObject("roomNumber", params.get("roomNumber"));
            mv.setViewName("chat");

        }else{ // 외에 다른 방법으로 이동되었으면 이동X
            mv.setViewName("room");
        }
        return mv;
    }
}
