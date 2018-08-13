package com.moly.hooyee.route.api;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.moly.hooyee.model.RouteEntity;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Hooyee on 2018/7/14.
 * mail: hooyee_moly@foxmail.com
 */

public class EasyRoute {
    private static final String ROUTE_ROOT_PAKCAGE = "com.hooyee.easy.route";
    private static final Map<String, RouteEntity> sActivities = new ConcurrentHashMap<>();

    public static void init(Context context) {
        try {
            Set<String> routerMap = ClassUtils.getFileNameByPackageName(context, ROUTE_ROOT_PAKCAGE);
            if (routerMap != null) {
                for (String clazz : routerMap) {
                    if (clazz.startsWith(ROUTE_ROOT_PAKCAGE + ".EasyRouter")) {
                        Class c = Class.forName(clazz);
                        Method m = c.getMethod("load", Map.class);
                        m.invoke(null, sActivities);
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void navigation(Context context, String path) {
        RouteEntity entity = sActivities.get(path);
        if (entity != null) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(context, entity.getClassName()));
            context.startActivity(intent);
        }
    }
}
