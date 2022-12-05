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

-keep class com.sun.activation.registries.* { *; }
-keep class javax.activation.* { *; }
-keep class com.sun.mail.** {*;}

-dontwarn java.lang.invoke.LambdaMetafactory
-dontwarn com.sun.mail.handlers.handler_base