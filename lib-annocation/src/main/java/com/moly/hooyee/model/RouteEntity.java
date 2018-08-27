package com.moly.hooyee.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hooyee on 2018/7/14.
 * mail: hooyee_moly@foxmail.com
 */

public class RouteEntity {
    private String className;
    private String path;
    private String moudle;   // 暂未使用
    private List<RouteIntercept> intercept;
    private List<String> interceptName;

    public RouteEntity(String className, String path, List<RouteIntercept> intercept) {
        this.className = className;
        this.path = path;
        this.intercept = intercept;
    }

    public RouteEntity(String className, String path) {
        this.className = className;
        this.path = path;
    }

    @Override
    public String toString() {
        return moudle + "&" + path;
    }

    public String getClassName() {
        return className;
    }

    public String getPath() {
        return path;
    }

    public String getMoudle() {
        return moudle;
    }

    public List<RouteIntercept> getIntercept() {
        return intercept;
    }

    public void setIntercept(List<RouteIntercept> intercept) {
        this.intercept = intercept;
    }

    public List<String> getInterceptName() {
        return null == interceptName ? new ArrayList<String>() : interceptName;
    }

    public void setInterceptName(List<String> interceptName) {
        this.interceptName = interceptName;
    }

    public static RouteEntity build(String className, String path, List<RouteIntercept> intercept) {
        return new RouteEntity(className, path, intercept);
    }

    public static RouteEntity build(String className, String path) {
        return new RouteEntity(className, path);
    }
}
