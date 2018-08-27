package com.moly.hooyee.easyroute;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.moly.hooyee.annocation.Route;

@Route(path = "/test/a1", intercept = LoginIntercept.class)
public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }
}
