package com.yuhantrum.websocket.config;

import com.yuhantrum.websocket.handler.SocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

// 빈(Bean)이란? = 자주 사용하는 객체를 Single Ton 객체로 생성해놓고 어디서든 불러서 쓸 수 있는 것을 Spring에서 Bean이라고 부름.
                 // 클래스에서 한 번 만들어놓고 재사용하기 편하기 위해 사용.

// @Configuration은 설정을 위한 어노테이션으로 개발자가 생성한 class를 Bean으로 생성할 때 Single Ton으로 한 번만 생성하고
// @Component는 Bean을 생성할 때 java에서 new로 생성하듯이 생성한다.
@Configuration
@EnableWebSocket       // @Configuration, @EnableWebSocket = 웹소켓에 대해 대부분을 자동설정으로 한다.
                            // WebSocketConfigurer을 상속받는다. = 웹소켓의 추가적인 설정을 상속받는다.
public class WebSocketConfig implements WebSocketConfigurer {    // 구현체에 등록

    // @Autowired = 생성자나 Setter 등을 사용하여 의존성 주입을 하려고 할 때, 해당 빈을 찾아서 주입해준다.
    @Autowired
    SocketHandler socketHandler;

    // 해당 메소드가 부모 클래스에 있는 메소드를 재정의했다는 것을 명시적으로 선언
    @Override
                // registerWebSocketHandlers = WebSocket 요청 처리를 위한 구성을 제공
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry){
        registry.addHandler(socketHandler, "/chating");
    }
}
