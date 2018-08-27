package com.moly.hooyee.easyroute;

import com.moly.hooyee.model.RouteIntercept;

/**
 * @author maohui
 * @date Created on 2018/8/27.
 * @description
 * @added
 */

public class LoginIntercept implements RouteIntercept {
    @Override
    public boolean intercept() {
        return true;
    }
}
