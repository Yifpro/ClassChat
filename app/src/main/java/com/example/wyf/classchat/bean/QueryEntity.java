package com.example.wyf.classchat.bean;

/**
 * Created by WYF on 2017/9/22.
 */

public class QueryEntity {
    public String targetSelector;
    public String methodName;
    public String methodParms;


    public QueryEntity(String targetSelector, String methodName,
                       String methodParms) {
        this.targetSelector = targetSelector;
        this.methodName = methodName;
        this.methodParms = methodParms;
    }
}
