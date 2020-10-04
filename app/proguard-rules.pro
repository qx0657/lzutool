# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# 代码混淆压缩比，在0~7之间
-optimizationpasses 5

# 混合时不使用大小写混合，混合后的类名为小写
-dontusemixedcaseclassnames

# 指定不去忽略非公共库的类
-dontskipnonpubliclibraryclasses

# 不做预校验，preverify是proguard的四个步骤之一，Android不需要preverify，去掉这一步能够加快混淆速度。
-dontpreverify

-verbose

#混淆字典配置
#####方法名
-obfuscationdictionary proguard_keywords_methodname.txt
#####类名
-classobfuscationdictionary proguard_keywords_classname.txt
#####包名
-packageobfuscationdictionary proguard_keywords_packagename.txt

#google推荐算法
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*

# 避免混淆Annotation、内部类、泛型、匿名类
-keepattributes *Annotation*,InnerClasses,Signature,EnclosingMethod

# 重命名抛出异常时的文件名称
-renamesourcefileattribute SourceFile

# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable

# 处理support包
-dontnote android.support.**
-dontwarn android.support.**



# 保留四大组件，自定义的Application等这些类不被混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Appliction
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

# 保留本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保留枚举类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保留Parcelable序列化类不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

#保留自定义的xlog文件夹下面的类、类成员和方法不被混淆
-keep class com.test.xlog.** {
    <fields>;
    <methods>;
}

#assume no side effects:删除android.util.Log输出的日志
-assumenosideeffects class android.util.Log {
    public static *** v(...);
    public static *** d(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

#混淆使用
-keep class androidx.appcompat.app.AlertDialog {*;}
-keep class androidx.appcompat.app.AlertController {*;}
#-keep class androidx.appcompat.widget.SearchView {*;}
#-keep class * extends android.app.Dialog

#保留自定义视图类
-keep class com.qx.pjbj.view.MySearchView {*;}
-keep class com.qx.pjbj.view.OvalImageView {*;}

#实体
-keep class com.qx.pjbj.data.PjNote {*;}
-keep class com.qx.pjbj.data.MyUserInfo {*;}

#第三方库
#BlankJ AndroidUtilCode
-keep class com.blankj.utilcode.** { *; }
-keepclassmembers class com.blankj.utilcode.** { *; }
-dontwarn com.blankj.utilcode.**
#assume no side effects:删除android.util.Log输出的日志
#-assumenosideeffects class com.blankj.utilcode.util.LogUtils {
#百度埋点
-keep class com.baidu.mobstat.** { *; }
-keep class com.baidu.bottom.** { *; }
#jsoup
-keeppackagenames org.jsoup.nodes

#保留@keep注解的
-dontskipnonpubliclibraryclassmembers
-printconfiguration
-keep,allowobfuscation @interface androidx.annotation.Keep
-keep @androidx.annotation.Keep class *
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}
