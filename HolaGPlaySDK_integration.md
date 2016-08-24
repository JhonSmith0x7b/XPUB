#HolaGPlay接入文档 v1.5.1

> 最后修改时间 : 2016.8.23

##简述

GPlay是可以将游戏转化成即点即玩的SDK, 接入方式**与传统SDK不同**. 接入之前, 你需要了解以下限制 : 

* **不可以修改** cocos java层原生库标准引擎代码, 如有修改需要还原
* **不可以引用**第三方SDK, 如果有引用, 需要去除转由GPlaySDK实现
* **不可以使用**涉及到到android组件(activity, broadcast, service, contentprovide), 依赖cocos原生库的JNI方法
* 符合条件的JNI方法, 必须按照要求打成 jar 包使用
* **屏蔽掉**游戏自身的**热更新机制**

>**为什么会有以上限制?**

>GPlay即点即玩, 是通过剥离掉游戏java层, 动态加载so与res实现的. java层我们提供了**cocos标准引擎**, 所以需要保持您的引擎跟标准引擎一致.

>第三方SDK, 往往涉及到**权限与静态库**, 我们无法为某个游戏定制这些东西, 所以无法实现.

>使用android组件, 涉及到**manifest的设置**, 我们无法为了某个游戏去修改这些东西, 所以无法实现.

>GPlay的游戏资源放在cdn上, 游戏如果使用热更新, 会产生文件与cdn资源不相符, 而产生许多问题. 而且, **GPlay本身的更新方式就类似于热更新功能**.

本文档将介绍如何将你的游戏接入HolaGPlay,主要内容包括

* 接入步骤
 * [获取配置信息](#preloadGroups)
 * [集成SDK](#0)
 * [运行游戏](#2)
 * [场景分组](#3)
 * [接入接口](#4)
 * [后续工作](#5)

* [接入FAQ](#FAQ)

##接入步骤


<h4 id ="0" >1. 获取配置信息</h4>
如果是首次接入游戏, 游戏开发者需要前往[Holaverse开发者中心](http://developers.holaverse.com/auth/login)注册账号, 注册结束将鼠标移到右上角头像, 选择**我的组织**. 注册结束后, 会默认创建一个组织, 打开该组织. 在右边树形菜单里选择**所有游戏**, 点击**创建游戏**, 输入游戏名称, 并且选择**游戏引擎**为**GplayCocosXXX(对应引擎语言)**, 之后去所有游戏即可看见创建的游戏, **游戏key**即为 **app_id** , 点击**修改游戏**, 即可查看 **secret** .
在**支付回调地址**那里, 如果游戏有验证服务器, 请填写自己的回调地址(具体配置, 请参考[服务端文档](https://coding.net/u/holaverse/p/HolaGPlaySDK/git/blob/master/doc/HolaGPlaySDK_API_Server.md)), 也可以填写GPlay游戏默认的回调地址`http://developers.holaverse.com/holaverse/pay/callback`.

>**说明** : 如果长时间收不到**激活邮件**, 请查看**邮箱垃圾箱**.

| 参数 | 名称 | 说明 |
| -----|:----:| :----|
| app_id  | 游戏id   |初始化SDK的时候传入,用于标识游戏   |
| secret   | 服务端秘钥    |用来验证支付回调是否合法  |

<smith id = "1" />
###2. 集成HolaGPlaySDK库

[cocos2dx-js 3.x](#js3.x)

[cocos2dx-js 2.x](#js2.x)

[cocos2dx-lua 3.x](#lua3.x)

[cocos2dx-lua 2.x](#lua2.x)

[cocos2dx-cpp 3.x](#cpp3.x)

[cocos2dx-cpp 2.x](#cpp2.x)

---

---

<smith id = "js3.x"/>
#### cocos2dx-js 3.x
<h4 id="preloadGroups">加载场景资源接口</h4>

* 获取到**HolaGPlaySDK.zip**,解压得到`gplay_for_js,api_js`等一系列文件夹
* 复制 `gplay_for_js/` 文件夹到 `游戏目录/frameworks/runtime-src/proj.android/jni`.

* 修改`gplay_for_js/Android.mk`文件, 注释掉`cocos2d-x 3.7+`的代码, 打开`cocos2d-x 2.1.4 - 2.2.6`被注释的代码

        #cocos2d-x 3.7+
        #注释掉这句代码
        #LOCAL_STATIC_LIBRARIES := cocos2d_js_static

        #cocos2d-x 3.0 - 3.6
        #LOCAL_STATIC_LIBRARIES := cocos_jsb_static

        #cocos2d-x 2.1.4 - 2.2.6
        #打开这句代码的代码的注释
        LOCAL_STATIC_LIBRARIES := scriptingcore-spidermonkey

* 编辑**jni**目录下的 **android.mk** 文件,增加对HolaGPlaySDK的依赖

        #在文件中原 LOCAL_WHOLE_STATIC_LIBRARIES 相关语句之下添加
        LOCAL_WHOLE_STATIC_LIBRARIES+= gplay_static
        #...
        #在文件中原 import-module 相关语句之下添加
        $(call import-module, jni/gplay_for_js)

* 修改游戏`Android`工程目录下的编译脚本`build_native.sh`，往`NDK_MODULE_PATH`添加`APP_ANDROID_ROOT`路径

        NDK_MODULE_PATH=${APP_ANDROID_ROOT}: ...

* 修改`cocos2dx/platform/CCFileUtils.h`文件, 在`class`的`public`里添加一个方法

        static void setDelegate(CCFileUtils *delegate) {
             if (s_sharedFileUtils != delegate)
                 delete s_sharedFileUtils;
             s_sharedFileUtils = delegate;
            }

* 修改`cocos2dx/platform/android/CCFileUtilsAndroid.h`文件, 构造函数的访问权限变为**protected**

          class CC_DLL CCFileUtilsAndroid : public CCFileUtils
          {
          //修改为protected
          protected:
            friend class CCFileUtils;
            CCFileUtilsAndroid();
          }
          
        
* 为游戏添加 HolaGPlaySDK 的 js 层接口

    * 复制`api_js`中的**hgsdk.js**文件到游戏 `src/` 中
    * 添加 **hgsdk.js** 路径到**js引用列表**中. 

* 修改`proj.android/jni/Application.mk`文件, **添加或者修改**这句, 使项目编译`armeabi`和`armeabi-v7a`架构
        
        APP_ABI := armeabi armeabi-v7a

[进行下一步接入](#2)

---

---
<smith id = "js2.x"/>
####cocos2dx-js 2.x

* 获取到**HolaGPlaySDK.zip**,解压得到`gplay_for_js,api_js`等一系列文件夹
* 复制 `gplay_for_js/` 文件夹到 `游戏目录/frameworks/runtime-src/proj.android/jni`.

* 修改`gplay_for_js/Android.mk`文件, 注释掉`cocos2d-x 3.7+`的代码, 打开`cocos2d-x 2.1.4 - 2.2.6`被注释的代码

        #cocos2d-x 3.7+
        #注释掉这句代码
        #LOCAL_STATIC_LIBRARIES := cocos2d_js_static

        #cocos2d-x 3.0 - 3.6
        #LOCAL_STATIC_LIBRARIES := cocos_jsb_static

        #cocos2d-x 2.1.4 - 2.2.6
        #打开这句代码的代码的注释
        LOCAL_STATIC_LIBRARIES := scriptingcore-spidermonkey

* 编辑**jni**目录下的 **android.mk** 文件,增加对HolaGPlaySDK的依赖

        #在文件中原 LOCAL_WHOLE_STATIC_LIBRARIES 相关语句之下添加
        LOCAL_WHOLE_STATIC_LIBRARIES+= gplay_static
        #...
        #在文件中原 import-module 相关语句之下添加
        $(call import-module, jni/gplay_for_js)

* 修改游戏`Android`工程目录下的编译脚本`build_native.sh`，往`NDK_MODULE_PATH`添加`APP_ANDROID_ROOT`路径

        NDK_MODULE_PATH=${APP_ANDROID_ROOT}: ...

* 修改`cocos2dx/platform/CCFileUtils.h`文件, 在`class`的`public`里添加一个方法

        static void setDelegate(CCFileUtils *delegate) {
             if (s_sharedFileUtils != delegate)
                 delete s_sharedFileUtils;
             s_sharedFileUtils = delegate;
            }

* 修改`cocos2dx/platform/android/CCFileUtilsAndroid.h`文件, 构造函数的访问权限变为**protected**

          class CC_DLL CCFileUtilsAndroid : public CCFileUtils
          {
          //修改为protected
          protected:
            friend class CCFileUtils;
            CCFileUtilsAndroid();
          }
          
        
* 为游戏添加 HolaGPlaySDK 的 js 层接口

    * 复制`api_js`中的**hgsdk.js**文件到游戏 `src/` 中
    * 添加 **hgsdk.js** 路径到**js引用列表**中. 

* 修改`proj.android/jni/Application.mk`文件, **添加或者修改**这句, 使项目编译`armeabi`和`armeabi-v7a`架构
        
        APP_ABI := armeabi armeabi-v7a

[进行下一步接入](#2)

---

---

<smith id = "lua3.x"/>
####cocos2dx-lua 3.x

* 获取到**HolaGPlaySDK.zip**,解压得到`gplay_for_lua,api_lua`等一系列文件夹
* 复制 `gplay_for_lua/` 文件夹到 `游戏目录/frameworks/runtime-src/proj.android/jni/`
* <span style = "color:red">引擎版本在3.3以下(3.3/3.2/3.1)</span>需要修改`gplay_for_lua/Android.mk`文件, 注释掉`cocos2dx 3.3+`的代码, 打开`cocos2dx 3.0/3.1/3.2`中被注释的代码

        #cocos2dx 3.0/3.1/3.2
        #打开3.0-3.2代码
        LOCAL_STATIC_LIBRARIES := cocos_lua_static
        #cocos2dx 3.3+
        #注释掉3.3代码
        #LOCAL_STATIC_LIBRARIES := cocos2d_lua_static
        #cocos2dx 2.x
        #LOCAL_STATIC_LIBRARIES += cocos_lua_static
        #quick lua 2.x
        #LOCAL_STATIC_LIBRARIES += quickcocos2dx
     
* <span style="color:red">基于quick-lua引擎的游戏</span>,编辑**jni**目录下的 **android.mk** 文件,增加对HolaGPlaySDK的依赖

        #在文件中原 LOCAL_WHOLE_STATIC_LIBRARIES 相关语句之下添加
        LOCAL_WHOLE_STATIC_LIBRARIES += gplay_static
        #...
        #在文件中原 import-module 相关语句之下添加
        $(call import-module, jni/gplay_for_lua)

* <span style="color:red">基于cocos2dx-lua引擎的游戏</span>,编辑**jni**目录下的 **android.mk** 文件,增加对HolaGPlaySDK的依赖

        #在文件中原 LOCAL_WHOLE_STATIC_LIBRARIES 相关语句之下添加
        LOCAL_WHOLE_STATIC_LIBRARIES += gplay_static
        #...
        #在文件中原 import-module 相关语句之下添加
        $(call import-module, ../runtime-src/proj.android/jni/gplay_for_lua)
        
* <span style="color:red">引擎版本低于3.4(3.4/3.3/3.2/3.1/3.0)的游戏</span>需要修改`cocos2dx/platform/CCFileUtils.h`文件, 在`class`的`public`里添加一个方法

        static void setDelegate(FileUtils *delegate) {
         if (s_sharedFileUtils != delegate)
             delete s_sharedFileUtils;
         s_sharedFileUtils = delegate;
        }

* <span style="color:red">引擎版本低于3.2(3.2/3.1/3.0)的游戏</span>,需要修改`cocos2dx/platform/android/CCFileUtilsAndroid.h`文件, 构造函数的访问权限修改为`protected`

        class CC_DLL FileUtilsAndroid : public FileUtils
        {
        //修改访问权限
        protected :
            friend class FileUtils;
            FileUtilsAndroid();

* 为游戏添加 HolaGPlaySDK 的 lua 层接口
            
    * 复制 `api_lua` 中的 **hgsdk.lua** 文件到游戏 `src/` 中
    * 在代码中进入游戏以后, 引用 require("hgsdk") 

* 修改`proj.android/jni/Application.mk`文件, **添加或者修改**这句, 使项目编译`armeabi`和`armeabi-v7a`架构
        
        APP_ABI := armeabi armeabi-v7a

[进行下一步接入](#2)

---

---

<smith id = "lua2.x"/>
####cocos2dx-lua 2.x

* 获取到**HolaGPlaySDK.zip**,解压得到`gplay_for_lua,api_lua`等一系列文件夹
* 复制 `gplay_for_lua/` 文件夹到 `游戏目录/frameworks/runtime-src/proj.android/jni/`
* 修改`gplay_for_lua/Android.mk`文件, 注释掉`cocos2dx 3.x`的代码, **根据引擎版本**打开`cocos2dx 2.x`或者`quick lua 2.x`中被注释的代码

        #cocos2dx 3.x
        #LOCAL_STATIC_LIBRARIES := cocos2d_lua_static
        #根据引擎类型, 打开2.x 或者 quick 2.x的注释
        #cocos2dx 2.x
        #LOCAL_STATIC_LIBRARIES += cocos_lua_static

        #quick lua 2.x
        #LOCAL_STATIC_LIBRARIES += quickcocos2dx

* 编辑**jni**目录下的 **android.mk** 文件,增加对HolaGPlaySDK的依赖

        #在文件中原 LOCAL_WHOLE_STATIC_LIBRARIES 相关语句之下添加
        LOCAL_WHOLE_STATIC_LIBRARIES += gplay_static
        #...
        #在文件中原 import-module 相关语句之下添加
        $(call import-module, jni/gplay_for_lua)

* 修改游戏`Android`工程目录下的编译脚本`build_native.sh`，往`NDK_MODULE_PATH`添加`APP_ANDROID_ROOT`路径

        NDK_MODULE_PATH=${APP_ANDROID_ROOT}: ...
        
* 修改`cocos2dx/platform/CCFileUtils.h`文件, 在`class`的`public`里添加一个方法

        static void setDelegate(CCFileUtils *delegate) {
             if (s_sharedFileUtils != delegate)
                 delete s_sharedFileUtils;
             s_sharedFileUtils = delegate;
            }

* 修改`cocos2dx/platform/android/CCFileUtilsAndroid.h`文件, 构造函数的访问权限变为**protected**

          class CC_DLL CCFileUtilsAndroid : public CCFileUtils
          {
          //修改为protected
          protected:
            friend class CCFileUtils;
            CCFileUtilsAndroid();
          }

* 修改游戏入口脚本加载路径, 通常在 `AppDelegate.cpp`中, **不使用完整路径**

        string fullPath = CCFileUtils::sharedFileUtils()-fullPathForFilename("hello.lua");
        //luaEngine->executeScriptFile(fullPath.c_str());
        //修改为
        luaEngine->executeScriptFile("hello.lua");

  或者
  
        string path = CCFileUtils::sharedFileUtils()->fullPathForFilename("scripts/main.lua");
        //pEngine->executeScriptFile(path.c_str());
        //修改为
        pEngine->executeScriptFile("scripts/main.lua");

* 为游戏添加 HolaGPlaySDK 的 lua 层接口
            
    * 复制 `api_lua` 中的 **hgsdk.lua** 文件到游戏 `src/` 中
    * 在代码中进入游戏以后, 引用 require("hgsdk") 
    * 引擎版本低于2.2.2的游戏, 还需要引用 require("json")
    
* 修改`proj.android/jni/Application.mk`文件, **添加或者修改**这句, 使项目编译`armeabi`和`armeabi-v7a`架构
        
        APP_ABI := armeabi armeabi-v7a

[进行下一步接入](#2)

---

---

<smith id = "cpp3.x"/>
####cocos2dx-cpp 3.x

* 获取到**HolaGPlaySDK.zip**,解压得到`gplay_for_cpp`等一系列文件夹
* 复制 `gplay_for_cpp/` 文件夹到 `游戏目录/frameworks/runtime-src/proj.android/jni/` 

* 编辑**jni**目录下的 **android.mk** 文件,增加对HolaGPlaySDK的依赖

        #在文件中原 LOCAL_WHOLE_STATIC_LIBRARIES 相关语句之下添加
        LOCAL_WHOLE_STATIC_LIBRARIES += gplay_static
        #...
        #在文件中原 import-module 相关语句之下添加
        $(call import-module, ../proj.android/jni/gplay_for_cpp)

* <span style="color:red">引擎版本低于3.4(3.4/3.3/3.2/3.1/3.0)的游戏</span>,需要修改`cocos2dx/platform/CCFileUtils.h`文件, 在`class`的`public`里添加一个方法

        static void setDelegate(FileUtils *delegate) {
         if (s_sharedFileUtils != delegate)
             delete s_sharedFileUtils;
         s_sharedFileUtils = delegate;
        }

* <span style="color:red">引擎版本低于3.2(3.2/3.1/3.0)的游戏</span>,需要修改`cocos2dx/platform/android/CCFileUtilsAndroid.h`文件, 构造函数的访问权限修改为`protected`

        class CC_DLL FileUtilsAndroid : public FileUtils
        {
        //修改访问权限
        protected :
            friend class FileUtils;
            FileUtilsAndroid();
          
* 修改`proj.android/jni/Application.mk`文件, **添加或者修改**这句, 使项目编译`armeabi`和`armeabi-v7a`架构

        APP_ABI := armeabi armeabi-v7a

[进行下一步接入](#2)


---

---

<smith id = "cpp2.x"/>
####cocos2dx-cpp 2.x

* 获取到**HolaGPlaySDK.zip**,解压得到`gplay_for_cpp`等一系列文件夹
* 复制 `gplay_for_cpp/` 文件夹到 `游戏目录/frameworks/runtime-src/proj.android/jni/` 

* 编辑**jni**目录下的 **android.mk** 文件,增加对HolaGPlaySDK的依赖

        #在文件中原 LOCAL_WHOLE_STATIC_LIBRARIES 相关语句之下添加
        LOCAL_WHOLE_STATIC_LIBRARIES += gplay_static
        #...
        #在文件中原 import-module 相关语句之下添加
        $(call import-module, jni/gplay_for_cpp)
        
* 修改游戏`Android`工程目录下的编译脚本`build_native.sh`，往`NDK_MODULE_PATH`添加`APP_ANDROID_ROOT`路径

        NDK_MODULE_PATH=${APP_ANDROID_ROOT}: ...

* 修改`cocos2dx/platform/CCFileUtils.h`文件, 在`class`的`public`里添加一个方法

        static void setDelegate(CCFileUtils *delegate) {
             if (s_sharedFileUtils != delegate)
                 delete s_sharedFileUtils;
             s_sharedFileUtils = delegate;
            }

* 修改`cocos2dx/platform/android/CCFileUtilsAndroid.h`文件, 构造函数的访问权限变为**protected**

          class CC_DLL CCFileUtilsAndroid : public CCFileUtils
          {
          //修改为protected
          protected:
            friend class CCFileUtils;
            CCFileUtilsAndroid();
          }
          
* 修改`proj.android/jni/Application.mk`文件, **添加或者修改**这句, 使项目编译`armeabi`和`armeabi-v7a`架构

      APP_ABI := armeabi armeabi-v7a

[进行下一步接入](#2)

---

---

>**阶段测试 :debug模式打包apk,编译可以顺利通过,游戏正常运行,则可以进行下一步接入工作**

<smith id = "2" />
###3. 通过 GPlay Tool 运行游戏,验证是否有适配问题
打开接入工具**GPlay Tool**,导入上一步生成的**apk**, 将**全部资源**文件拖入`boot_scene`, 连接**Android手机**,点击**运行**, `Android`设备上,将会以**GPlay模式启动游戏**,进行游戏功能测试
 
>**阶段测试 : 游戏正常运行,没有崩溃和异常,则进入步骤4, 并且, 提供给我们一个资源包进行测试.若游戏抛出`Failed to find static method id of xxx`错误,请参考下方的导入外部jar包说明**

#### 

>**说明** : 你仍可以使用一些`JNI`方法, GPlay支持动态加载一个`jar`文件, 且支持`Java`对`Jni`调用(包括自定义`Jni`接口), 但此jar包必须符合以下条件 (具体导入操作请参考[工具使用文档](GPlayTool_Integration.md))
>> * `jar`必须转成`dex`格式;
>> * `jar`中不依赖`Android SDK`任何组件以及UI控件;
>> * 如果`Java`中有对`Jni`的调用, jar必须完整包含`Jni`所依赖的`Java`类与方法;
>> * 如果游戏中有对`Java`方法的调用, `jar`必须正确包含此`Java`的类名引擎与方法定义;
>> * `jar`包不得与宿主App产生类冲突.
        
             

<smith id = "3" />
###4. 场景资源分组

* 使用**GPlay Tool**进行场景资源分组
    * 组的概念 : 组并不代表一个场景,组是场景的一部分.比如在装备页面,武器可以是一个组,防具是另一个组.这样做的好处是在别的地方我们还可以使用这些组.在抽取武器页面,可以直接使用武器组的资源了.
    * 组的加载 : 为了维持游戏的正常运行,进入一个场景会加载若干个组,这些组只有在**第一次加载时才会下载**,之后直接从本地调用.
    * 组的划分 : **组是资源的最小单位**,这意味着组内的资源全部加载后才会展现游戏画面,最优的分组是刚好满足当前场景所需要的资源,但太过碎片的分组又会增加维护的难度,分组人员应该权衡这些因素.
    * **`Boot_Scene`** : `Boot_Scene`是基础分组,**不需要进行preload操作,进入游戏,自动开始加载.**所以此分组中应该包括**所有代码,所有脚本,第一个场景所需要的资源**.

* 调用场景切换接口

    进入某个场景之前调用`preloadGroup` , 在回调中**执行进入场景的代码**. (具体使用方法请参考对应引擎版本的接口文档, 或者查看DEMO示例代码).

* 自动分割场景

  我们提供了自动分割场景功能, 具体使用方法操作请参考[工具使用文档](https://coding.net/u/holaverse/p/HolaGPlaySDK/git/blob/master/doc/GPlayTool_Integration.md)

* 测试场景划分是否遗漏
    
    **关闭**工具**静默下载**开关,点击**运行**启动游戏,工具会按照当前的场景分组重新运行游戏, 但**不会主动后台下载资源**, 每次进入新的场景都会触发前台loading界面.**期间可能由于场景遗漏了必需的资源而导致游戏崩溃**, 游戏开发者可以通过查看游戏 debug 日志可以获得提示, 找到当前场景需要加载到却没有配置的资源, 加入当前场景. 调整场景分组后继续点击运行再次启动游戏测试,**直到没有崩溃**.

* 测试场景划分是否流畅

    **打开**工具**静默下载**开关, 点击**运行**启动游戏, 工具会按照当前的场景分组重新运行游戏, 并且**自动静默下载后续资源**, 观察游戏进入前台 loading 的情况, 如果次数过多, 则需要调整优化. 原则上将最先用到的几个场景分得细一点, 可以达到比较完美的游戏效果,**基本不存在loading**.

>**阶段测试 : 通过GPlay Tool运行游戏, 没有崩溃, 没有无法接受的前台 loading 状况**
>>*  **说明** :
    * 这个过程需要多次反复测试以达到最佳效果
    * 在 preloadGroup 回调之前, 请勿再次调用此接口
    * 不要预加载当前场景以外资源, 这些资源可能还没有被下载到

<smith id = "4" />
###5. 调用 HolaGPlaySDK 接口

* 客户端接入
    * 添加SDK后, 能获得**hgsdk对象**.通过该对象发起请求, 调用登陆,支付,统计等接口.

* 服务端接入
    * 使用 HolaGPlaySDK 的支付接口, 游戏开发者通过 HolaGPlaySDK 工作人员获取到 secret , 客户端支付成功后该回调接口会被调用, 返回支付信息.

>**说明 :** 接口详细内容,请查看对应引擎的接口文档.

<smith id = "5"/>
###6. 后续工作
* 打包
    
    打包release版本APK,在工具中更换**release版本APK**,点击**发布**, 工具将根据最新的场景划分,使用新APK中的资源和release代码,生成一套**GPlay资源包**. 

* 测试

    通过 GPlayTool **运行**进行初步测试. 并将资源包提供给HolaGPlaySDK工作人员, 进行**正式测试**.
    
* 上线

    测试通过后,将**GPlay资源包**提交给HolaGPlaySDK工作人员,并且根据Holagame工作人员的要求,提供**上线所需素材**.经过审核,即可上线.

* 游戏版本更新

    使用**GPlay Tool**导入准备上架的 APK, (小版本升级也需要生成对应的 APK), 更新以后, 工具会自动从分场景配置中删除新版本中不再使用的资源, 其余配置保持不变. 

    游戏开发者根据新版本的资源变动, 回到**步骤4**, 将新版本分场景调整到最优.配置游戏的当前线上地址(由HolaGPlaySDK工作人员提供)到工具游戏配置的**发布地址**栏目中, 点击**发布**,此时生成GPlay资源包将包含对当前线上版本资源的**增量升级包**, 上一版本的用户可以通过下载升级包 来升级游戏场景分组.

    将**GPlay资源包**提供给HolaGPlaySDK工作人员进行部署.

>* **说明:**
    * GPlay 手机页游模式下, 不管是资源, 脚本还是游戏 so 的变化, 都将统一使用增量更新
    * 增量更新的用户可以快速开始新版本的游戏, 按需升级不同的场景分组.
    * <span style = "color : red">GPlay 模式的更新机制统一了游戏的大小版本更新, 开发者需要屏蔽游戏自身的热更新逻辑</span>

<smith id = "FAQ" />
##接入FAQ

[FAQ](https://coding.net/u/holaverse/p/HolaGPlaySDK/git/blob/master/doc/FAQ.md)



