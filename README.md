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

In any fragment
```java

   //Navigation to any fragment right away
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
Another Fragment
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


In any Activity

```java
   //Navigation to any Activity right away
   GoRouter.getInstance().build("routeKey2").go()


   //With data
   Bundle data = new Bundle()
   data.putInt(key , value);
   GoRouter.getInstance().build("routeKey2" , data).go()
```


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