package com.zhitu.jt808server;

import com.zhitu.jt808server.server.ServerLauncher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Jt808serverApplication {

	public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext ctx = SpringApplication.run(Jt808serverApplication.class, args);
        ctx.getBean(ServerLauncher.class).start();
    }

}
