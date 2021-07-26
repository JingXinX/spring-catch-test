package com.example.springcatchtest.service;

import org.springframework.stereotype.Service;

@Service
public class DivServiceImpl implements DivService
{
    @Override
    public int div(int i, int j) {

        int result = i / j;
        System.out.println("=========>DivServiceImpl被调用了,我们的计算结果：" + result);
        return result;
    }
}
