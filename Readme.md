@[toc]
## 关于BroadcastReceiver
即广播消息接收器，类似于事件编程中的监听器，监听源是Android应用中的其他组件。使用BroadcastRecevier组件接收广播消息，只需要继承BroadcastReceiver并重写onReceiver方法。其他控件通过sendBroadcast()、sendStickyBroadcast()或sendOrderdBroadcast()方法发送广播消息时，

## BroadcastReceiver的两种注册方式
通常有两种方式来注册

- AndroidManifest文件中 静态注册
	

```xml
	<receiver android:name=“.MyReceiver”>
		<intent-filter>
			<action android:name=""/>
		</intent-filter>
	</receiver>
```

- 代码动态注册

```JAVA
IntentFilter filter = new IntentFilter();
filter.addAction("");
registerReceiver(new MyReceiver(),filter);
```
Action属性的值为一个字符串，它代表了系统中已经定义了一系列常用的动作。
[Android 外部启动activity，自定义action，action常量大全](https://www.cnblogs.com/guop/p/5067342.html)
也可以自己定义Action的含义，如：
```xml
       <receiver android:name=".MainActivity$MyBroadcastReceiver">
            <intent-filter android:priority="0">
                <action android:name="com.zhiyuan.receiver.SHOW"></action>
            </intent-filter>
        </receiver>
```

如果BroadcastReceiver的onReceiver()方法不能在10秒内执行完成，Android会认为该程序无响应。弹出ANR对话框。

## 如何发送广播
 发送广播非常简单，只需要调用Context的sendBroadcast(Intent intent)方法
 

```java
	Intent intent = new Intent();
	intent.setAction("");
	intent.putExtra("","");
	//发送广播
	sendBroadcast(intent);
```
MyReceiver代码如下

```java
	public class MyReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context,Intent intent){
			/**
			* 执行相应操作
			**/
		}
	}
```
当符合当前MyReceiver监听的Action被触发时，MyReceiver的onReceive()方法会被回调。

## 广播类型
BroadcastReceiver分为两种：

- Normal Broadcast（普通广播）：
	异步执行，其他接收者并行接收，传递效率高，无法终止Broadcast Intent 的传播
- Ordered Broadcast（有序广播）：
	按照接收者的优先级接收。优先级生命在<intent-filter.../>元素的android:priority属性，数越大优先级别越高（-1000~1000）。也可以调用IntentFilter的setPriority()设置优先级。这种方法可以终止Broadcast Intent 的传播。并且可以将数据传递给下一个接收者。setResultExtras(Bundle)将处理结果存入Broadcast中，然后传给下一个接收者。使用abortBroadcast()取消广播的继续传播

- sticky Broadcast （粘性广播）：
sticky粘性的意思。这种广播一般不会终止，只要有符合条件的广播接收者能接收广播，那么就会发送给他广播。永远不会终止发送广播，除非某个广播接收者告诉它不要再发送广播了。粘性消息在发送后就一直存在于系统的消息容器里面，等待对应的处理器去处理，如果暂时没有处理器处理这个消息则一直在消息容器里面处于等待状态，粘性广播的Receiver如果被销毁，那么下次重建时会自动接收到消息数据。

## 代码实践 
所以广播组件在什么场景才能用到呢？例如：但我执行某个下载任务时，用户并不会在前台进程中一直等待下载任务的完成，所以我们需要用到Service来执行该下载任务，等Service执行完成该任务时，通过广播通知Activity中UI的更新。下面我写了一个Demo来模拟这种情况
Demo地址：https://github.com/Dream97/ReceiverDemo.git
运行效果：
![在这里插入图片描述](https://img-blog.csdn.net/20180922010357841?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM0MjYxMjE0/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
效果说明：

- 首先是一个触发Service的Activity，里面有通过内部类的实现的广播接收器
- 触发Service后需要等待Service任务的完成
- Service的任务完成后，通过广播，打开浏览器浏览指定网页

Activity代码：

```java
public class MainActivity extends AppCompatActivity {
    private Button button;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.bt);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,MyService.class);
                startService(intent); //启动Service
                textView.setText("请等待3秒");
            }
        });
        textView = findViewById(R.id.text);
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.zhiyuan.receiver.SHOW");
        registerReceiver(new MyBroadcastReceiver(),filter);
    }

    public  class MyBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            textView.setText("后台服务执行成功");
            Intent intent2 = new Intent();
            intent2.setAction("android.intent.action.VIEW");
            intent2.addCategory("android.intent.category.DEFAULT");
            intent2.setData(Uri.parse("https://blog.csdn.net/qq_34261214"));
            startActivity(intent2);
        }
    }
}

```
Service代码：

```java
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
```
Service继承IntentService，想了解Service的可以看《[Android四大组件之Service](https://blog.csdn.net/qq_34261214/article/details/82670424)》,其中在写这段代码的时候发现通过静态注册BroadcastReceiver的时候，内部类中需要需要用static来修饰，否则报错，所以客观来说，还是动态注册方便一些

## 资料
[1] [Android广播错误.MainActivity$MyReceiver; no empty constructor](https://blog.csdn.net/xzongyuan/article/details/39991509)
[2] 疯狂Android讲义
[3] [Android 粘性广播StickyBroadcast的使用](https://blog.csdn.net/u011506413/article/details/54095148?utm_source=copy )
