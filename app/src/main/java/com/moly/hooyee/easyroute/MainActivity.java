package com.moly.hooyee.easyroute;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.moly.hooyee.annocation.Route;
import com.moly.hooyee.route.api.EasyRoute;

@Route(path = "test", intercept = {DefaultIntercept.class, LoginIntercept.class})
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EasyRoute.init(this);

        findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyRoute.navigation(getApplicationContext(), "/test/a1");
            }
        });

        findViewById(R.id.btn_test2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyRoute.navigation(getApplicationContext(), "moudle2/t2");
            }
        });

        findViewById(R.id.btn_test3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyRoute.navigation(getApplicationContext(), "/intercept/t1");
            }
        });
    }
}
