<div align=center>
    <a href="https://github.com/RubinTry/GoRouter">
        <img src="https://gorouter-1258359008.cos.ap-shanghai.myqcloud.com/logo.png"/>
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
version|[![Version](https://img.shields.io/badge/Version-1.0.10-blue)](https://bintray.com/logcat305/maven/gorouter-api/_latestVersion)|[![Version](https://img.shields.io/badge/Version-1.0.4-orange)](https://bintray.com/logcat305/maven/gorouter-compiler/_latestVersion)|[![Version](https://img.shields.io/badge/Version-1.0.3-brightgreen)](https://bintray.com/logcat305/maven/gorouter-annotation/_latestVersion)



#### [Example](https://github.com/RubinTry/GoRouter/tree/master/GoRouterExample)


#### Feature
1. **Support for multi-modules**
2. **Support for Activity and Fragment**
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

    //Here you will navigation to RouteFragment      
```
Another Fragment
```java
   @Route(url = "routeKey1")
   public class RouteFragment extends Fragment {
```


