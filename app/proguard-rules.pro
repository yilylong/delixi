# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\work\AndroidSDK/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
#混淆配置开始==================

#这个选项是 是否混淆的开关  不混淆输入的类文件 （gson 出错“missing type parameters” 必须加上这句）
#-dontobfuscate

# 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#指定代码的压缩级别
-optimizationpasses 5

# 不优化输入的类文件
-dontoptimize

#混淆时不会产生形形色色的类名（大小写混合）
-dontusemixedcaseclassnames

#不忽略非公共的库类。
-dontskipnonpubliclibraryclasses

#不忽略非公共的库类的类成员
-dontskipnonpubliclibraryclassmembers

#不压缩输入的类文件
-dontshrink

-verbose

#忽略警告
-ignorewarnings

# 不警告
-dontwarn

#过滤内部类
-keepattributes InnerClasses,LineNumberTable,SourceFile

#禁止优化泛型和反射（这个很重要）
-keepattributes Signature

#过滤掉注解
-keepattributes *Annotation*

#是否混淆第三方jar
-dontskipnonpubliclibraryclasses

#混淆时是否做预校验
-dontpreverify

#优化时允许访问并修改有修饰符的类和类的成员
-allowaccessmodification

#重新包装所有重命名的类文件中放在给定的单一包中
-renamesourcefileattribute com.twentyfirstcbh.epaper



#系统四大组件及继承类不要混淆
-keep public class * extends android.app.Activity
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.app.View
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.database.sqlite.SQLiteDatabase
-keep public class com.android.vending.licensing.ILicensingService
-keep class android.net.http.SslError
-keep class android.webkit.**{*;}
-keep class m.framework.**{*;}

#V4包
-dontwarn android.support.v4.**
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment
#v7包
-dontwarn android.support.v7.**
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }
-keep public class * extends android.support.v7.**

#Javascript
-keepclassmembers class com.twentyfirstcbh.epaper.activity.Content$OnWebViewMethodListener {
   public *;
}
-keepattributes *Annotation*
-keepattributes *JavascriptInterface*
-keep public class com.twentyfirstcbh.epaper.JavascriptInterface

#本地方法不混淆(这里编译一直报错 还没找到原因)
#-keepclasseswithmembernames class * {
#   native <methods>;
#}

#防止自定义view出错
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#枚举
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
#R资源文件
-keep class **.R$* {
    *;
}
#序列化的对象
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keep class * implements java.io.Serializable {
}
-keepnames class * implements java.io.Serializable




#工程里面引用的第三方库
#-libraryjars ..\\Android_PullToRefrsh_library
#-dontwarn com.handmark.pulltorefresh.library.**
#-keep class com.handmark.pulltorefresh.library.**{*;}
#
#-libraryjars ..\\CBDialogLibrary
#-dontwarn com.cb.cbdialog.**
#-keep class com.cb.cbdialog.**{*;}
#
#-libraryjars ..\\ViewPagerIndicator_Library
#-dontwarn com.shizhefei.**
#-keep class com.shizhefei.**{*;}
#
#-libraryjars ..\\ChangeSkinLibrary
#-dontwarn com.zhy.changeskin.**
#-keep class com.zhy.changeskin.**{*;}



#=====================以下是第三方库需keep的

##ShareSDK
#-keep class cn.sharesdk.onekeyshare.**{*;}
#-keep class cn.sharesdk.**{*;}
#-keep class com.sina.**{*;}
#
##OKHTTP
#-dontwarn com.squareup.okhttp.**
#-keep class com.squareup.okhttp.**{*;}
#
##个推
#-dontwarn com.igexin.**
#-keep class com.igexin.**{*;}
#
##百度
#-dontwarn com.baidu.**
#-keep class com.baidu.**{*;}
#
##腾讯
#-keep public class com.tencent.bugly.**{*;}
#
##换肤
#-keep class com.zhy.changeskin.**{*;}
#
##OKhttp工具库
#-keep class com.zhy.http.okhttp.**{*;}
#
##gson反射出错需要加上这些
#-keep class com.google.gson.** {*;}
#-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.JsonObject { *; }
#-keep class com.google.gson.examples.android.model.** { *; }
