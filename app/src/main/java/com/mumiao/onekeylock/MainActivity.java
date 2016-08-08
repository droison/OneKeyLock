package com.mumiao.onekeylock;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


public class MainActivity extends Activity {
    private DevicePolicyManager policyManager;
    private ComponentName componentName;

    public static final int MY_REQUEST_CODE = 0x112;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //获取设备管理服务
        policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

        //AdminReceiver 继承自 DeviceAdminReceiver
        componentName = new ComponentName(this, AdminReceiver.class);

        if (policyManager.isAdminActive(componentName)) {
            policyManager.lockNow();// 锁屏
            android.os.Process.killProcess(android.os.Process.myPid());
        } else {
            activeManage(); //获取权限
        }
    }

    private void activeManage() {
        // 启动设备管理(隐式Intent) - 在AndroidManifest.xml中设定相应过滤器
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);

        //权限列表
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);

        //描述(additional explanation)
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "激活后就能一键锁屏了");

        startActivityForResult(intent, MY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 获取权限成功，立即锁屏并finish自己，否则继续获取权限
        if (requestCode == MY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            policyManager.lockNow();
            finish();
        } else {
            //activeManage();
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
