package com.example.springcatchtest;

import com.example.springcatchtest.bean.A;
import com.example.springcatchtest.bean.B;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MainClass {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        A a = applicationContext.getBean("a",A.class);
        B b = applicationContext.getBean("b",B.class);
    }
}
