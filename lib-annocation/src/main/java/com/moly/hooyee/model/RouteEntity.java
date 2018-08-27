package com.moly.hooyee.model;

/**
 * Created by Hooyee on 2018/7/14.
 * mail: hooyee_moly@foxmail.com
 */

public class RouteEntity {
    private String className;
    private String path;
    private String moudle;   // 暂未使用
    private RouteIntercept intercept;
    private String interceptName;

    public RouteEntity(String className, String path, String interceptName, RouteIntercept intercept) {
        this.className = className;
        this.path = path;
        this.interceptName = interceptName;
        this.intercept = intercept;
    }

    public RouteEntity(String className, String path, String interceptName) {
        this.className = className;
        this.path = path;
        this.interceptName = interceptName;
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

    public RouteIntercept getIntercept() {
        return intercept;
    }

    public void setIntercept(RouteIntercept intercept) {
        this.intercept = intercept;
    }

    public String getInterceptName() {
        return null == interceptName ? "" : interceptName;
    }

    public void setInterceptName(String interceptName) {
        this.interceptName = interceptName;
    }

    public static RouteEntity build(String className, String path, String moudle, String interceptName, RouteIntercept intercept) {
        return new RouteEntity(className, path, interceptName, intercept);
    }

    public static RouteEntity build(String className, String path, String moudle) {
        return new RouteEntity(className, path);
    }
}
