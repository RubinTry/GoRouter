<div align=center>
    <a href="https://github.com/RubinTry/GoRouter">
        <img src="https://gorouter-1258359008.cos.ap-shanghai.myqcloud.com/GoRouter.png"/>
    </a>
</div>

```
The most lightweight Framework about router in android.
```






[中文文档](https://github.com/RubinTry/GoRouter/blob/readme/README_CN.md)

[![GitHub license](https://img.shields.io/github/license/RubinTry/GoRouter)](https://www.apache.org/licenses/LICENSE-2.0)


#### Latest version

|module|GoRouter-api|GoRouter-compiler|GoRouter-annotation|
|:---:|:---:|:---:|:---:|
version|[![Version](https://img.shields.io/badge/Version-1.0.10-blue)](https://bintray.com/logcat305/maven/gorouter-api/_latestVersion)|[![Version](https://img.shields.io/badge/Version-1.0.5-orange)](https://bintray.com/logcat305/maven/gorouter-compiler/_latestVersion)|[![Version](https://img.shields.io/badge/Version-1.0.3-brightgreen)](https://bintray.com/logcat305/maven/gorouter-annotation/_latestVersion)



#### [Example](https://github.com/RubinTry/GoRouter/tree/master/GoRouterExample)


#### Feature
1. **Support for multi-modules**
2. **Full Activity and Fragment support**
3. **InstantRun support**
4. **MultiDex support**
5. **AndroidX Support**
6. **Support kotlin**



#### How to use?

Fragment
```java

   //Navigate to a fragment right away
   GoRouter.getInstance().build("routeKey1")
                   .setFragmentContainer(fragment's containerId)
                   .go()


     //With data
     Bundle data = new Bundle()
     data.putInt(key , value);
     GoRouter.getInstance().build("routeKey1" , data)
                   .setFragmentContainer(fragment's containerId)
                   .go();               

    //Here you will navigation to RouteFragment  
    
        
```
Target Fragment
```java
   @Route(url = "routeKey1")
   public class RouteFragment extends Fragment {

      @Nullable
      @Override
      public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

          value = getArguments().getInt(key)

          return YourView;
      }    
   }    
```


Activity

```java
   //Navigation to an Activity right away
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

Fragment

##### Layout

fragment1
```xml
   <TextView
        android:id="@+id/tv"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="fragment1"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        android:transitionName="sharedFragment"
        android:textSize="50sp"></TextView>
```

fragment2
```xml
   <TextView
        android:id="@+id/tv2"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="fragment2"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        android:transitionName="sharedFragment"
        android:textSize="50sp"></TextView>
```
```java
   //Fragment1
   //Navigate to a fragment with animation right away
   GoRouter.getInstance()
                        .build("fragment2 's routeKey")
                        .addSharedFragment(tv , ViewCompat.getTransitionName(tv) , "tag" , containerId , true)
                        .go();


    //Here you will navigation to RouteFragment  
    
        
```


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
   .go(ActivityOptions.makeSceneTransitionAnimation(this , tv , "sharedActivity").toBundle())
```