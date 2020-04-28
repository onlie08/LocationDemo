package com.sf.location.demo;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.sfmap.api.location.SfMapLocation;
import com.sfmap.api.location.SfMapLocationClient;
import com.sfmap.api.location.SfMapLocationClientOption;
import com.sfmap.api.location.SfMapLocationListener;

public class LocationActivity extends Activity implements SfMapLocationListener {
    private static String[] PERMISSIONS_REQUEST = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private SfMapLocationClient locationClient = null;
    private SfMapLocationClientOption locationOption = null;
    private TextView tvLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermission();
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        tvLocation = findViewById(R.id.tv_location);
        findViewById(R.id.btn_start_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLocation();
            }
        });
        findViewById(R.id.btn_stop_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopLocation();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != locationClient) {
            locationClient.destroy();
            locationClient = null;
            locationOption = null;
        }
    }

    @Override
    public void onLocationChanged(SfMapLocation location) {
        if(location!=null&&location.getErrorCode()!=0){
            //定位发生异常
            Log.e("LocationChangeError", "Location error code：" + location.getErrorCode());
            return ;
        }
        tvLocation.setText("定位结果 lat:"+ location.getLatitude() + " lon:" + location.getLongitude());
    }

    public void startLocation() {
        if(locationClient==null){
            locationClient = new SfMapLocationClient(this.getApplicationContext());
            locationOption = new SfMapLocationClientOption();
            // 设置定位模式为低功耗模式
            locationOption.setLocationMode(SfMapLocationClientOption.SfMapLocationMode.High_Accuracy);
            // 设置定位间隔为2秒
            locationOption.setInterval(2 * 1000);
            locationOption.setNeedAddress(true);
            // 设置定位监听
            locationClient.setLocationListener(this);
            locationClient.setLocationOption(locationOption);
            locationClient.startLocation();
        }
    }

    public void stopLocation() {
        if (locationClient != null) {
            locationClient.stopLocation();
            locationClient = null;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (this.checkPermission(Manifest.permission.READ_PHONE_STATE, Process.myPid(), Process.myUid())
                    != PackageManager.PERMISSION_GRANTED || this.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Process.myPid(), Process.myUid())
                    != PackageManager.PERMISSION_GRANTED || this.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, Process.myPid(), Process.myUid())
                    != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(PERMISSIONS_REQUEST, 1);
            }
        }
    }
}
