package com.yuhantrum.websocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/* 참고 링크 : https://engkimbs.tistory.com/753
 Spring Boot는 스프링 프레임워크에서 어플리케이션을 만들 때 주로 사용하는 설정들을 자동으로 설정한다.
 이 기능은 @SpringBootApplication을 붙임으로서 사용할 수 있다.
 @SpringBootApplication 어노테이션은 @Configuration, @EnableAutoConfiguration, @ComponentScan을 디폴트 속성으로 함께 사용하는 것과 같다.*/
@SpringBootApplication
public class WebsocketApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebsocketApplication.class, args);
	}

}
