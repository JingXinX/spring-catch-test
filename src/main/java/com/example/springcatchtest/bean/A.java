package com.example.springcatchtest.bean;

public class A {
    private B b;

    public A() {
        System.out.println("```````````create A");
    }

    public B getB() {
        return b;
    }

    public void setB(B b) {
        this.b = b;
    }
}
