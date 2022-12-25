## AutoSaveRestore

## 项目描述

[![](https://jitpack.io/v/yizems/AutoSaveRestore.svg)](https://jitpack.io/#yizems/AutoSaveRestore)


还在傻傻的写 `onSaveInstanceState` 和 `onRestoreInstanceState` 方法么?

还在为项目中 大量的 未保存和恢复处理的变量 引起的错误 头疼?

**说明**

1. 本项目适用于非`ViewModel`项目的纯`Kotlin`项目或者`Kotlin``Java`混编项目
2. 本项目适用于`Activity`和`Fragment`的保存和恢复, 仅支持`androidx`
3. 纯`Java`项目推荐[PrototypeZ/SaveState](https://github.com/PrototypeZ/SaveState)这个库,
   其实如果你不在意引入了`kotlin`的依赖, 那么使用本库也没有问题
4. 如果你是使用的ViewModel,请使用SavedHandler 实现自动保存和恢复,官方实现的就挺好的了,
   当然也可以使用这个库加强一下[line/lich/savedstate](https://github.com/line/lich/tree/master/savedstate)
5. 目前仅支持`kotlin` `1.6.21-1.0.6`

## 使用方法

- gradle配置

```gradle
// root/build.gradle 添加ksp
classpath "com.google.devtools.ksp:symbol-processing-gradle-plugin:1.6.21-1.0.6"

// 项目build.gradle 添加ksp
apply plugin: 'com.google.devtools.ksp'

// dependencies 添加依赖
implementation 'com.github.yizems.AutoSaveRestore:core:1.0.0'
ksp 'com.github.yizems.AutoSaveRestore:ksp:1.0.0'
```

- kotlin

```kotlin
// 在类上加上注解:@AutoSaveRestore
//使用 delegate
var a: Int? by SavedDelegates.nullable { 10 }

var b: IntArray by SavedDelegates.notNull {
    IntArray(10) { it }
}
// 然后就没了, 非常简单,而且加注解是为了类型的安全检查,用来检查非Bundle支持的类型防止引起闪退
```

-java

```java
@AutoSaveRestore
private int a=0;

@AutoSaveRestore
private String b=0;

//在Activity.onCreate/Fragment.createView方法中, 
// 如果你有base类,建议加在super.onCreate之前
// 如果你的base类有添加这行代码, 那么子类就不需要添加了. 都添加也没关系,不会重复执行属性恢复
        SavedDelegateHelper.registerForJava()
```

然后,你的这些变量,在页面被销毁重建时,就会被自动恢复了!!

## 效果

打开手机 开发者模式中的 不保留活动

![screen](./screen/screen.gif)

## 实现说明

- `kotlin` 没有使用到反射, `Java` 如果是`protect` 以及 `private` 的变量,则需要使用反射, `Java`反射的性能还好(`kotlin`反射效率就太差了),
  建议使用 默认修饰符(包可见)
- `ksp` 的目的有两个, 1 检验`kotlin`代码的类型安全,2 生成`java`代码的辅助代码,并做安全检查
- 其实`kotlin`类上可以不加注解`AutoSaveRestore`, 但是加上注解,可以检查类型安全,防止引起闪退,所以还是建议加上注解

## TODO

- [ ] 支持`kotlin` `1.7.0`
- [ ] 考虑是否实现整个类的自动保存和恢复, 但是这样会导致类的变量被保存, 有些变量可能不需要保存,另外就是不支持Bundle写入的变量怎么办没想好
- [ ] kotlin compiler插件实现kotlin的属性保存和恢复, 这样就可以避免使用反射, 一个注解搞定,也就不用加 Delegate了

## 其他说明

- 关于`Kotlin`代码入侵高的问题,暂时没有想好怎么解决, `AGP4`到`AGP7.0`之间变化太大,属实没空研究这块,所以选择使用ksp实现,否则 `Gradle Plugin`
  可能是最佳选择

## 更新日志

### 1.0.0

- 大量重构
- 完成ksp自动生成代码,支持Java代码
- 移除 kotlin 反射
- 支持kotlin 1.6.21-1.0.6
- 主要类变化: cn.yzl.saved.delegate.SavedDelegates -> cn.yzl.auto.save.AutoSaveDelegates

### 0.0.5

- 提供统一代理方法类`SavedDelegates`
- 优化代理从 `SavedRegister`中获取bundle后的操作,只读取一次,以前是每次都读取
- 提供`SavedDelegates.lateInit` 代理,替换`latainit bar`

### 0.0.1

项目初步完成,还没有进行比较全面的测试(数据类型方面)

## 实现思路

1. 熟悉 savestate 库,强烈建议大家去看看
2. 使用属性委托在get方法中,查询 savedStateRegistry 中保存的数据
3. 为savedStateRegistry 注册 SavedStateRegistry.SavedStateProvider
4. 在 SavedStateRegistry.SavedStateProvider 中,使用反射,读取 activity/fragment 中使用了委托对象的属性
5. 获取到对象属性后,获取值和类型,调用Bundle 相应的方法写入即可


## LICENSE

MIT License

Copyright (c) 2021 yizems

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.


