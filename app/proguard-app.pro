# 保证bean类不被混淆
-keep public class **.*Entity {*;}
-keep public class **.*Bean {*;}

# -------------- arcgis 相关的混淆 --------------
-libraryjars libs/arcgis-android-api.jar
-dontwarn com.esri.**
-keep class com.esri.** {*;}

-libraryjars libs/jackson-core-lgpl-1.9.5.jar
-dontwarn org.codehaus.jackson.**
-keep class org.codehaus.jackson.** {*;}

-libraryjars libs/jackson-mapper-lgpl-1.9.5.jar
-dontwarn org.codehaus.jackson.**
-keep class org.codehaus.jackson.** {*;}

-libraryjars libs/jcifs-1.3.17.jar
-dontwarn jcifs.**
-keep class jcifs.** {*;}

# -------------- 高德 相关混淆--------------
#定位
-keep class com.amap.api.location.**{*;}
-keep class com.loc.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}
# 搜索
-keep class com.amap.api.services.**{*;}