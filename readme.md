# 个人任务管理APP（kotlin安卓项目）

## 项目环境

`Android Studio Koala 功能更新版 | 2024.1.2`

`gradle 8.7`

`JDK 17`

本安卓程序基于kotlin语言编写，分为 **任务管理**、**日历查看** 以及 **AI对话** 三个功能模块

## 任务管理

- 刚进首页会有弹窗提示之前的未完成任务，可以自定义选择已完成或者挪到当天

- 下方第一个tab栏为任务管理模块，可以对任务进行增删改查
- 页面显示一个ListView列表，展示当天任务，下方有添加任务按钮，点进去可以填表单来新增任务
- 点击列表中具体任务可查看详情，在详情页中可以对该任务进行修改删除操作



## 日历模块

- 在该模块中可以查看当月的日历，支持左右滑动，展示其他月份
- 若有任务，则当天会显示一个黄色的list列表，点击进入详情页，可查看当天具体任务信息



## AI对话模块

- 接入Kimi大模型，需要去Kimi的开发者平台申请密钥拿到Key
- 程序中利用retrofit进行网络请求，利用自定义的recycleView来模拟实现双方对话的布局
