## Time Cat 时光猫

![](https://github.com/triline3/timecat/blob/master/app/src/main/res/mipmap-xhdpi/bannar.png?raw=true)

本项目为“发现杯”大奖赛参赛作品，只进行交流学习，原则上不允许用于商业用途。

开发团队为“Time Cat时光猫团队”，队长为[中山大学数学学院（珠海）林学渊](https://github.com/LinXueyuanStdio)。

项目不包含`data/`目录，如有需要，请联系团队队长
  - 邮箱：`linxy59@mail2.sysu.edu.cn`
  - QQ：`761516186`

本仓库无法编译成功，因为没有`data/`目录，需要体验APP请下载[参赛版APP](https://github.com/triline3/timecat/blob/master/app/for_test/release/com.time.cat.apk?raw=true)

| 图标 | 设计者及说明 |
|:---:|:---:|
| <img width="100px" src="https://github.com/triline3/timecat/blob/master/app/src/main/res/mipmap-hdpi/ic_launcher.png?raw=true" /> | 本项目的图标设计者为[林学渊](https://github.com/LinXueyuanStdio)，设计者保留所有权利，禁止用于商业。|


| 应用截图 | 应用截图 | 应用截图 |
|:---:|:---:|:---:|
| ![](https://github.com/triline3/timecat/blob/master/psFiles/作品照片1.png?raw=true) | ![](https://github.com/triline3/timecat/blob/master/psFiles/作品照片2.png?raw=true) | ![](https://github.com/triline3/timecat/blob/master/psFiles/作品照片3.png?raw=true) |
| ![](https://github.com/triline3/timecat/blob/master/psFiles/作品照片4.png?raw=true) | ![](https://github.com/triline3/timecat/blob/master/psFiles/作品照片5.png?raw=true) | ![](https://github.com/triline3/timecat/blob/master/psFiles/作品照片6.png?raw=true) |
| ![](https://github.com/triline3/timecat/blob/master/psFiles/作品照片7.png?raw=true) | ![](https://github.com/triline3/timecat/blob/master/psFiles/作品照片11.png?raw=true) | ![](https://github.com/triline3/timecat/blob/master/psFiles/作品照片9.png?raw=true) |


本项目用到的开源项目的许可证将稍后进行整理。

开发进度：

- [x] 通过辅助模式，实现单击、长按、双击来进行选词；
- [x] 通过系统复制进行选词；
- [x] 通过辅助模式，实现全局复制进行选词；
- [x] 使用5.0以上的系统接口，进行截图OCR进行选词；
- [x] 通过悬浮窗、通知栏进行控制；
- [x] 日历视图
- [x] 直接在选词界面添加日程
- [x] 直接在截图界面添加日程
- [x] 替换图标，处理文字
- [x] 用户注册与登录
- [x] 侧滑栏管理
- [x] 主题系统
- [x] 看板系统
- [x] 成就系统
- [x] 桌面小部件
- [x] 反馈功能，用阿里云
- [x] 后台报表，用阿里云
- [x] 番茄钟界面
- [x] 番茄钟滴答声
- [x] 番茄钟统计数据
- [x] 课程表视图
- [x] 课程表时间段调节自定义
- [x] 计划视图
- [x] 计划视图弹出子菜单
- [x] 看板系统支持拖拽
- [x] 笔记、日程、生物钟、计划四大模块布置
- [x] 长按下面导航栏图标更换视图
- [x] 截图笔记
- [x] OCR可使用用户自定义的接口
- [x] 捐赠二维码，实现捐赠捐款
- [x] 内测QQ群跳转
- [x] 整体架构迁移到MVP架构


<details>
  <summary>开发过程积累的相关文章</summary>

[【Android TimeCat】 解决Gradle :Resolve dependencies :classpath的办法](http://xichen.pub/2018/03/06/2018-03-06-Android-TimeCat-%E8%A7%A3%E5%86%B3Gradle-Resolvedependencies-classpath%E7%9A%84%E5%8A%9E%E6%B3%95/)

[【Android TimeCat】 切换软键盘和标签键盘时界面跳动](http://xichen.pub/2018/03/06/2018-03-06-Android-TimeCat-%E5%88%87%E6%8D%A2%E8%BD%AF%E9%94%AE%E7%9B%98%E5%92%8C%E6%A0%87%E7%AD%BE%E9%94%AE%E7%9B%98%E6%97%B6%E7%95%8C%E9%9D%A2%E8%B7%B3%E5%8A%A8/)

[【Android TimeCat】 原地归并排序](http://xichen.pub/2018/03/01/2018-03-01-Android-TimeCat-%E5%8E%9F%E5%9C%B0%E5%BD%92%E5%B9%B6%E6%8E%92%E5%BA%8F/)

[【Android TimeCat】 给刷新按钮添加旋转动画](http://xichen.pub/2018/03/01/2018-03-01-Android-TimeCat-%E7%BB%99%E5%88%B7%E6%96%B0%E6%8C%89%E9%92%AE%E6%B7%BB%E5%8A%A0%E6%97%8B%E8%BD%AC%E5%8A%A8%E7%94%BB/)

[【Android TimeCat】 MVP架构演进](http://xichen.pub/2018/02/28/2018-02-28-Android-TimeCat-MVP%E6%9E%B6%E6%9E%84%E6%BC%94%E8%BF%9B/)

[【Android TimeCat】 RecyclerView的卡顿问题](http://xichen.pub/2018/02/27/2018-02-27-Android-TimeCat-RecyclerView%E7%9A%84%E5%8D%A1%E9%A1%BF%E9%97%AE%E9%A2%98/)

[【Android TimeCat】 当RxJava遇到Retrofit（二）api注解@Path, @Url等](http://xichen.pub/2018/02/26/2018-02-27-Android-TimeCat-%E5%BD%93RxJava%E9%81%87%E5%88%B0Retrofit%EF%BC%88%E4%BA%8C%EF%BC%89api%E6%B3%A8%E8%A7%A3@Path,%20@Url%E7%AD%89/)

[【Android TimeCat】 当RxJava遇到Retrofit（一）Retrofit入门](http://xichen.pub/2018/02/26/2018-02-27-Android-TimeCat-%E5%BD%93RxJava%E9%81%87%E5%88%B0Retrofit%EF%BC%88%E4%B8%80%EF%BC%89Retrofit%E5%85%A5%E9%97%A8/)

[【Android TimeCat】 RxJava的使用（四）线程控制 —— Scheduler](http://xichen.pub/2018/02/26/2018-02-26-Android-TimeCat-RxJava%E7%9A%84%E4%BD%BF%E7%94%A8%EF%BC%88%E5%9B%9B%EF%BC%89%E7%BA%BF%E7%A8%8B%E6%8E%A7%E5%88%B6%20%E2%80%94%E2%80%94%20Scheduler/)

[【Android TimeCat】 RxJava的使用（三）对象转换器——map、flatMap](http://xichen.pub/2018/02/26/2018-02-26-Android-TimeCat-RxJava%E7%9A%84%E4%BD%BF%E7%94%A8%EF%BC%88%E4%B8%89%EF%BC%89%E5%AF%B9%E8%B1%A1%E8%BD%AC%E6%8D%A2%E5%99%A8%E2%80%94%E2%80%94map%E3%80%81flatMap/)

[【Android TimeCat】 RxJava的使用（二）Action](http://xichen.pub/2018/02/26/2018-02-26-Android-TimeCat-RxJava%E7%9A%84%E4%BD%BF%E7%94%A8%EF%BC%88%E4%BA%8C%EF%BC%89Action/)

[【Android TimeCat】 RxJava的使用（一）基本用法](http://xichen.pub/2018/02/26/2018-02-26-Android-TimeCat-RxJava%E7%9A%84%E4%BD%BF%E7%94%A8%EF%BC%88%E4%B8%80%EF%BC%89%E5%9F%BA%E6%9C%AC%E7%94%A8%E6%B3%95/)

[【Android TimeCat】 Android抽象布局——include、merge 、ViewStub](http://xichen.pub/2018/02/26/2018-02-26-Android-TimeCat-Android%E6%8A%BD%E8%B1%A1%E5%B8%83%E5%B1%80%E2%80%94%E2%80%94include%E3%80%81merge%20%E3%80%81ViewStub/)

[【Android TimeCat】 OrmLite框架入门与封装（三）封装](http://xichen.pub/2018/02/26/2018-02-26-Android-TimeCat-OrmLite%E6%A1%86%E6%9E%B6%E5%85%A5%E9%97%A8%E4%B8%8E%E5%B0%81%E8%A3%85%EF%BC%88%E4%B8%89%EF%BC%89%E5%B0%81%E8%A3%85/)

[【Android TimeCat】 OrmLite框架入门与封装（二）高级操作](http://xichen.pub/2018/02/26/2018-02-26-Android-TimeCat-OrmLite%E6%A1%86%E6%9E%B6%E5%85%A5%E9%97%A8%E4%B8%8E%E5%B0%81%E8%A3%85%EF%BC%88%E4%BA%8C%EF%BC%89%E9%AB%98%E7%BA%A7%E6%93%8D%E4%BD%9C/)

[【Android TimeCat】 OrmLite框架入门与封装（一）快速入门](http://xichen.pub/2018/02/26/2018-02-26-Android-TimeCat-OrmLite%E6%A1%86%E6%9E%B6%E5%85%A5%E9%97%A8%E4%B8%8E%E5%B0%81%E8%A3%85%EF%BC%88%E4%B8%80%EF%BC%89%E5%BF%AB%E9%80%9F%E5%85%A5%E9%97%A8/)

[【Android TimeCat】 制作捐赠二维码，实现捐赠收款](http://xichen.pub/2018/02/26/2018-02-26-Android-TimeCat-%E5%88%B6%E4%BD%9C%E6%8D%90%E8%B5%A0%E4%BA%8C%E7%BB%B4%E7%A0%81%EF%BC%8C%E5%AE%9E%E7%8E%B0%E6%8D%90%E8%B5%A0%E6%94%B6%E6%AC%BE/)

[【Android TimeCat】 跳转QQ加群](http://xichen.pub/2018/02/26/2018-02-26-Android-TimeCat-%E8%B7%B3%E8%BD%ACQQ%E5%8A%A0%E7%BE%A4/)

[【Android TimeCat】 Android Studio 高效配置](http://xichen.pub/2018/02/26/2018-02-26-Android-TimeCat-Android%20Studio%E9%AB%98%E6%95%88%E9%85%8D%E7%BD%AE/)

[【Android TimeCat】 Android Studio拾色器](http://xichen.pub/2018/02/26/2018-02-26-Android-TimeCat-Android%20Studio%E6%8B%BE%E8%89%B2%E5%99%A8/)

[【Android TimeCat】 懒加载需求下的BaseFragment封装](http://xichen.pub/2018/02/26/2018-02-26-Android-TimeCat-%E6%87%92%E5%8A%A0%E8%BD%BD%E9%9C%80%E6%B1%82%E4%B8%8B%E7%9A%84BaseFragment%E5%B0%81%E8%A3%85/)

[【Android TimeCat】 Android中使用矢量图（SVG, VectorDrawable）](http://xichen.pub/2018/02/25/2018-02-25-Android-TimeCat-%E4%BD%BF%E7%94%A8%E7%9F%A2%E9%87%8F%E5%9B%BE%EF%BC%88SVG,%20VectorDrawable%EF%BC%89/)

[【Android TimeCat】 Android中用intent传递对象的三种方法](http://xichen.pub/2018/02/25/2018-02-25-Android-TimeCat-Android%E4%B8%AD%E7%94%A8intent%E4%BC%A0%E9%80%92%E5%AF%B9%E8%B1%A1%E7%9A%84%E4%B8%89%E7%A7%8D%E6%96%B9%E6%B3%95/)

[【Android TimeCat】 比较Fragment中获取Context对象的两种方法](http://xichen.pub/2018/02/25/2018-02-25-Android-TimeCat-%E6%AF%94%E8%BE%83Fragment%E4%B8%AD%E8%8E%B7%E5%8F%96Context%E5%AF%B9%E8%B1%A1%E7%9A%84%E4%B8%A4%E7%A7%8D%E6%96%B9%E6%B3%95/)

[【Android TimeCat】 Java 回调及其在项目中的运用](http://xichen.pub/2018/02/25/2018-02-25-Android-TimeCat-java%E5%9B%9E%E8%B0%83%E6%9C%BA%E5%88%B6/)

[【Android TimeCat】快速构建APP BottomNavigationView + ViewPager + Fragment](http://xichen.pub/2018/02/25/2018-02-25-Android-TimeCat-BottomNavigationView+ViewPager+Fragment%E5%BF%AB%E9%80%9F%E6%9E%84%E5%BB%BAApp/)


[开发《全能分词》（又名《锤子Bigbang》）的心路历程](http://www.jianshu.com/p/6e068fca111b)

[通过辅助模式获取点击的文字](http://www.jianshu.com/p/60758b3f2c7c)

[使用辅助服务实现全局复制](http://www.jianshu.com/p/c34cbef4d68e)

[使用辅助服务监听系统按键](http://www.jianshu.com/p/03904692b76b)

[如何通过Xposed框架获取点击的文字](http://www.jianshu.com/p/d7083c6e83bb)

[使用Xposed框架实现全局复制](http://www.jianshu.com/p/9dda421d23e4)

[在onLayout中实现简单的微动效](http://www.jianshu.com/p/93463ab36df9)

[如何使用Android的拖拽接口实现拖拽功能](http://www.jianshu.com/p/5001d0b42e10)

[通过ContentProvider多进程共享SharedPreferences数据](http://www.jianshu.com/p/bdebf741221e)

[Android上如何实现矩形区域截屏](http://www.jianshu.com/p/0462dae4c808)

[Android如何判断NavigationBar是否显示（获取屏幕真实的高度）](http://www.jianshu.com/p/84d951b3f079)

[如何在Bitmap截取任意形状](http://www.jianshu.com/p/d64cf9f69d05)

[4种获取前台应用的方法（肯定有你不知道的）](http://www.jianshu.com/p/a513accd40cd)

[android7.0 通过代码 分享图片到朋友圈](http://www.jianshu.com/p/5b0e0310d93f)

[Android中如何正确的获得所有App列表](http://www.jianshu.com/p/aee07cbb0cae)

[Android的supportV7中默认按钮的颜色设置](http://www.jianshu.com/p/98214d31318d)

[Android沉浸式与SearchView的坑](http://www.jianshu.com/p/f5d6bf2fc634)

[Android中“强制停止”和广播保活的一个小坑](http://www.jianshu.com/p/c632f5de465f)

[Xposed大法好,教你实现ForceTouch炫酷功能](http://www.jianshu.com/p/e7ea5e3bdb47)

[如何实现android炫酷悬浮球菜单](http://www.jianshu.com/p/56abca9fb592)
</details>
