package com.zhiyuan.receiverdemo;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class MyService extends IntentService {


    /**
     * 使用Eclipse如果没有添加这个无参构造函数的话会报一个运行时错误： java.lang.InstantiationException
     */
    public MyService() {
        /**
         * 这里只需要传入一个字符串就可以了
         */
        super("MyService");

    }

    /**
     * 必须实现的抽象方法，我们的业务逻辑就是在这个方法里面去实现的 在这个方法里实现业务逻辑，我们就不用去关心ANR的问题
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG, "onHandleIntent: 执行服务" );
        /**
         * 因为这个方法是在子线程里面处理的，所以这里我们不能直接在子线程里面弹Toast
         * 我们这里使用handler来帮助我们处理Toast
         */
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent intent1 = new Intent();
        intent1.setAction("com.zhiyuan.receiver.SHOW");
        sendBroadcast(intent1);
    }

    /**
     * 为了验证onHandleIntent执行后，服务会不会自动销毁，我们在这里重写onDestroy方法
     * 如果会自动销毁，那么在"IntetnService Running"出现后，应该会出现"IntetnService Stop"
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
