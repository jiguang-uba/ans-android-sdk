#################################################################
########################### 易观混淆 ############################
#################################################################

# 混淆到包名下
-dontwarn com.analysys.push.**
# 保证API不混淆
-keep class com.analysys.push.PushAgent{
 *;
}
-keep class com.analysys.push.Constants{
 *;
}