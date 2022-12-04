# Add any ProGuard configurations specific to this
# extension here.

-keep public class org.dreamerslab.smtp.Smtp {
    public *;
 }
-keeppackagenames gnu.kawa**, gnu.expr**

-optimizationpasses 4
-allowaccessmodification
-mergeinterfacesaggressively

-repackageclasses 'org/dreamerslab/smtp/repack'
-flattenpackagehierarchy
-dontpreverify

-dontwarn java.lang.invoke.LambdaMetafactory

-dontshrink
-keep class javax.** {*;}
-keep class com.sun.** {*;}
-keep class myjava.** {*;}
-keep class org.apache.harmony.** {*;}
-dontwarn java.awt.**
-dontwarn java.beans.Beans
-dontwarn javax.security.**
-dontwarn com.sun.mail.handlers.handler_base