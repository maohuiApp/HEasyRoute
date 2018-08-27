package com.moly.hooyee.model;

/**
 * @author maohui
 * @date Created on 2018/8/27.
 * @description
 * @added
 */

public class DefaultIntercept implements RouteIntercept {
    @Override
    public boolean intercept() {
        return false;
    }
}
