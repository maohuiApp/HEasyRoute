package com.moly.hooyee.model;

/**
 * Created by Hooyee on 2018/7/14.
 * mail: hooyee_moly@foxmail.com
 */

public class RouteEntity {
    private String className;
    private String path;
    private String moudle;

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

    public static RouteEntity build(String className, String path, String moudle) {
        return new RouteEntity(className, path);
    }
}
