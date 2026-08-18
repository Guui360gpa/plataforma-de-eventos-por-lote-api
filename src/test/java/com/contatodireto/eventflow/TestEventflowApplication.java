package com.contatodireto.eventflow;

import org.springframework.boot.SpringApplication;

public class TestEventflowApplication {

    public static void main(String[] args) {
        SpringApplication.from(EventflowApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
