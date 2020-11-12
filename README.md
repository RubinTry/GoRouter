<div align=center>
    <a href="https://github.com/RubinTry/GoRouter">
        <img src="https://cdn.rubintry.cn/image/GoRouter.png"/>
    </a>
</div>

```
The most lightweight Framework about router in android.
```






[中文文档](https://github.com/RubinTry/GoRouter/blob/master/README_CN.md)

[Javadoc](https://rubintry.cn/go-router-doc/)

[![GitHub license](https://img.shields.io/github/license/RubinTry/GoRouter)](https://www.apache.org/licenses/LICENSE-2.0)


#### Latest version

|module|GoRouter-api|GoRouter-compiler|GoRouter-annotation|
|:---:|:---:|:---:|:---:|
version|[![Version](https://img.shields.io/badge/Version-1.0.29-blue)](https://bintray.com/logcat305/maven/gorouter-api/_latestVersion)|[![Version](https://img.shields.io/badge/Version-1.0.5-orange)](https://bintray.com/logcat305/maven/gorouter-compiler/_latestVersion)|[![Version](https://img.shields.io/badge/Version-1.0.3-brightgreen)](https://bintray.com/logcat305/maven/gorouter-annotation/_latestVersion)



### [Example Code](https://github.com/RubinTry/GoRouterExample)


#### Feature
1. **Support for multi-modules**
2. **Full Activity support**
3. **Support get Fragment instance**
4. **InstantRun support**
5. **MultiDex support**
6. **AndroidX Support**
7. **Support kotlin**



#### How to implement?
config.gradle
```groovy
    dependencies{
    implementation 'cn.rubintry:gorouter-api:1.0.29'
    //Java
    annotationProcessor  'cn.rubintry:gorouter-compiler:1.0.5'
    //Kotlin
    kapt  'cn.rubintry:gorouter-compiler:1.0.5'
    }
```


#### How to build your project?

gradle.properties
```xml
    //If you need run sub module as Application, you should make moduleIsApplication true
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
            applicationId "Your sub module's package name"
        }
        ...
    }

    sourceSets{
        main {
            if(moduleIsApplication.toBoolean().booleanValue()){
                //If you run this module as application , you can use the default AndroidManifest.xml
                manifest.srcFile 'src/main/AndroidManifest.xml'
            }else{
                //If you run this module as library , you should create a new AndroidManifest liked follows
                manifest.srcFile 'src/main/manifest/AndroidManifest.xml'
            }
        }
    }
}
```

The AndroidManifest.xml when you run module as library
```xml
    <?xml version="1.0" encoding="utf-8"?>
    <manifest xmlns:android="http://schemas.android.com/apk/res/android"
        package="your package name">

        <application>
            <activity android:name="your activity in another module">
            </activity>
        </application>

    </manifest>
```

#### How to use?

Fragment
```java

   //Get a fragment instance
   Fragment instance = GoRouter.getInstance().build("fragmentRouteKey").go()
        
```



Activity

```java
   //Navigation to an Activity immediately
   GoRouter.getInstance().build("routeKey2").go()


   //With data
   Bundle data = new Bundle()
   data.putInt(key , value);
   GoRouter.getInstance().build("routeKey2" , data).go()
```

Target Activity
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


#### With sharedElements


Activity

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
   //navigation to another activity with animation.

   tv = findViewById(R.id.tv);

   GoRouter.getInstance().build("activity1 's routeKey")
   .go(this , ActivityOptionsCompat.makeSceneTransitionAnimation(this , tv , tv.getTransitionName()).toBundle())
```