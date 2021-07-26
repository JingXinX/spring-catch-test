package com.example.springcatchtest;

import com.example.springcatchtest.service.DivService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.SpringVersion;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
//@RunWith(SpringRunner.class)
public class SpringCatchTestApplicationTests {
    @Autowired
    private DivService divService;
    @Test
    public void contextLoads() {
        System.out.println("Spring版本："+ SpringVersion.getVersion()+"Boot版本:"+ SpringBootVersion.getVersion());
        divService.div(10,2);

    }

}
