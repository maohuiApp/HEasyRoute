package com.moly.hooyee.easyroute;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.moly.hooyee.annocation.Route;

@Route(path = "/intercept/t1", intercept = {LoginIntercept.class})
public class TestInterceptActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_intercept);
    }
}
