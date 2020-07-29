package com.yuhantrum.websocket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

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

    // @RequestMapping 어노테이션은 컨트롤러가 처리할 요청 URL을 명시하는데 사용되며, 클래스나 메소드에 적용된다.
    // 맵핑이란? 사전적 의미로
    @RequestMapping("/chat") // 여기에 /chat을 적었기 때문에 주소는 localhost:3000/chat이 된다.
    public ModelAndView chat(){
        ModelAndView mv = new ModelAndView(); // 인스턴스화
        mv.setViewName("chat"); // view의 이름(view 경로)
        return mv; // 반환값으로 ModelAndView 객체를 반환
    }
}
