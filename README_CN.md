<div align=center>
    <a href="https://github.com/RubinTry/GoRouter">
        <img src="https://cdn.rubintry.cn/image/GoRouter.png"/>
    </a>
</div>

```
最轻量级的安卓路由框架
```

[Javadoc](https://rubintry.cn/go-router-doc/)


[![GitHub license](https://img.shields.io/github/license/RubinTry/GoRouter)](https://www.apache.org/licenses/LICENSE-2.0)


#### 最新版本

|module|GoRouter-api|GoRouter-compiler|GoRouter-annotation|
|:---:|:---:|:---:|:---:|
version|[![Version](https://img.shields.io/badge/Version-1.0.33-blue)](https://bintray.com/logcat305/maven/gorouter-api/_latestVersion)|[![Version](https://img.shields.io/badge/Version-1.0.5-orange)](https://bintray.com/logcat305/maven/gorouter-compiler/_latestVersion)|[![Version](https://img.shields.io/badge/Version-1.0.3-brightgreen)](https://bintray.com/logcat305/maven/gorouter-annotation/_latestVersion)



<!-- ### [示例代码](https://github.com/RubinTry/GoRouterExample) -->
<br/>


#### 特性
1. **支持多模块开发**
2. **完美支持Activity**
3. **支持获取fragment实例**
4. **支持InstantRun support**
5. **支持MultiDex support（google）**
6. **支持AndroidX**
7. **支持kotlin混合编译**




#### 如何引入?
config.gradle
```groovy
    dependencies{
        //annotation
        implementation 'cn.rubintry:gorouter-annotation:1.0.3'
         //core api
        implementation 'cn.rubintry:gorouter-api:1.0.33'
        //Java
        annotationProcessor  'cn.rubintry:gorouter-compiler:1.0.5'
        //Kotlin
        kapt  'cn.rubintry:gorouter-compiler:1.0.5'
    }
```

#### 如何构建你的工程?

gradle.properties
```xml
    //如果你需要将module作为application来运行，你需要将moduleIsApplication设为true
    moduleIsApplication = false
```

mainModule.gradle
```groovy
apply plugin: 'com.android.application'
apply from: '../config.gradle'

dependencies {
    
    if(!moduleIsApplication.toBoolean().booleanValue()){
        implementation project(path: ':anothermodule')
    }
}
```


anotherModule.gradle
```groovy
    if(moduleIsApplication.toBoolean().booleanValue()){
        apply plugin: 'com.android.application'
    }else{
        apply plugin: 'com.android.library'
    }
    apply from: '../config.gradle'
    android {

    defaultConfig {
        if(moduleIsApplication.toBoolean().booleanValue()){
            applicationId "子模块的包名"
        }
        ...
    }

    sourceSets{
        main {
            if(moduleIsApplication.toBoolean().booleanValue()){
                //如果你想将其以application的方式来运行module，使用默认生成的AndroidManifest.xml即可
                manifest.srcFile 'src/main/AndroidManifest.xml'
            }else{
                //若你想将其以library的方式来运行module，你需要在以下目录下创建一个AndroidManifest.xml文件并按照以下格式编写
                manifest.srcFile 'src/main/manifest/AndroidManifest.xml'
            }
        }
    }
}
```

以library方式运行module时所需的AndroidManifest.xml
```xml
    <?xml version="1.0" encoding="utf-8"?>
    <manifest xmlns:android="http://schemas.android.com/apk/res/android"
        package="你的子模块包名">

        <application>
            <activity android:name="你的子模块里的activity">
            </activity>
        </application>

    </manifest>
```


# 如何使用此框架?

## 简单使用
<br/>

Fragment
```java

   //获得一个fragment实例
   Fragment instance = GoRouter.getInstance().build("fragmentRouteKey").go()
        
```



Activity篇

```java
   //导航到指定activity
   GoRouter.getInstance().build("routeKey2").go()


   //携带数据
   Bundle data = new Bundle()
   data.putInt(key , value);
   GoRouter.getInstance().build("routeKey2" , data).go()
```

目标 Activity
```java
    /**
    * @author logcat
    */
    @Route(url = "routeKey2")
    public class LoginActivity extends AppCompatActivity {



        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            value = getIntent().getInt(key)
        }
    }
```


#### 带有共享元素的跳转



Activity篇

##### Layout

activity1
```xml
   <TextView
        android:id="@+id/tv"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="activity1"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        android:transitionName="sharedActivity"
        android:textSize="50sp"></TextView>
```

activity2
```xml
   <TextView
        android:id="@+id/tv1"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="activity1"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        android:transitionName="sharedActivity"
        android:textSize="50sp"></TextView>
```

```java
   //Activity1
   //以带有共享元素动画的方式跳转

   tv = findViewById(R.id.tv);
   GoRouter.getInstance().build("activity1 's routeKey")
   .go(this , ActivityOptionsCompat.makeSceneTransitionAnimation(this , tv , tv.getTransitionName()).toBundle())
```


## 进阶用法
<br/>
通过暴露服务进行解耦

```java
    /**
      * 把这个服务暴露在公共库中
      *
      */
    interface SimpleService implements IProvider{
       
        void simple(Context context);
    }



     /**
      * 在模块B中实现这个服务并加以处理
      *
      */
    @Route(url = "Simple/SimpleService")
    public class SimpleServiceImpl implements SimpleService{
        @override
        public void init(Context context){
            //这里可以做一些初始化操作
        }

        public void simple(Context context){
            //做一些处理
        }
    }


     /**
      * 模块A中的一个activity
      *
      */
    public class SimpleActivity extends AppCompatActivity{
        
        @ovserride
        public void oncreate(Bundle savedInstanceState){

            SimpleService service = GoRouter.getInstance().build("Simple/SimpleService").go(SimpleService.class);

            if(service != null){
                service.simple(this.getApplicationContext());
            }
        }
    }
```