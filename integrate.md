# 关于项目HBuilder-Integrate-AS中，Java文件的配置及用途

## 分享支付相关Java文件
* WXEntryActivity.java 此类为微信登录授权插件配置，内容不可删除，类名不可更改。添加此文件时须在Androidmanifest.xml中注册当前activity。
* WXPayEntryActivity.java 此类为微信支付插件配置，内容不可删除，类名不可更改。添加此文件时须在Androidmanifest.xml中注册当前activity。

## 插件相关文件
* PGPlugintest.java 5+ SDK 扩展插件示例，对应5+插件js为assets\apps\H5Plugin\www\js\test.js，对应uni-app中5+插件js为 插件目录\在uni-app中使用5+插件demo\uni-plugin-demo\common\plugins.js

## 集成相关文件
* SDK_WebApp.java 以Widget的方式集成5+ SDK，集成方式参考[Android平台以Widget方式集成HTML5+SDK方法](http://ask.dcloud.net.cn/article/81)
* SDK_WebView.java 以WebView方式集成5+ SDK，集成方式参考[Android平台以WebView方式集成HTML5+SDK方法](http://ask.dcloud.net.cn/article/80)
* 其余Java文件为集成代码。可忽略。