# FoodExpress - 外卖点单应用

一个基于 Kotlin + Jetpack Compose + Firebase 开发的外卖点单 Android 应用，包含用户端和商家管理端。

## 功能

**用户端**

- 邮箱注册/登录，角色选择（顾客/商家）
- 首页餐厅列表，支持分类筛选和搜索
- 餐厅详情页，按分类浏览菜品
- 购物车增删改查，跨页面状态保持
- 下单结算，填写收货地址和备注
- 订单列表和实时状态追踪
- 个人中心

**商家端**

- 管理面板：今日/本周订单统计和营收概览
- 菜单管理：菜品查看、上架/下架
- 订单管理：新订单通知、接单/拒单、状态流转
- 营业/打烊状态切换

## 技术栈

Kotlin 2.0, Jetpack Compose (Material 3), Navigation Compose, Dagger Hilt, Firebase Auth, Cloud Firestore, Firebase Storage, Kotlin Coroutines + Flow, Coil

架构采用 MVVM + Clean Architecture，分 UI / Domain / Data 三层，通过 Repository 模式封装数据访问。

## 项目结构

```
app/src/main/java/com/foodexpress/
├── di/                    # Hilt 模块 (AppModule, RepositoryModule)
├── navigation/            # 路由定义和导航图
├── core/model/            # 数据模型
├── data/repository/       # AuthRepository, RestaurantRepository, OrderRepository, CartManager
├── feature/
│   ├── auth/              # 登录、注册、角色选择
│   ├── customer/          # home, restaurant, cart, order, profile
│   └── merchant/          # dashboard, menu, orders
└── ui/theme/              # Material 3 主题配置
```

## 数据模型

```
users/{uid}           → name, email, role
restaurants/{id}      → name, category, rating, deliveryFee, categories[]
menuItems/{id}        → name, price, imageUrl, categoryId, restaurantId
orders/{id}           → customerId, restaurantId, status, items[], totalAmount
```

## 运行

需要 Android Studio 和 JDK 17。

1. 创建 [Firebase 项目](https://console.firebase.google.com/)，启用 Auth（邮箱/密码）、Firestore、Storage
2. 下载 `google-services.json` 放到 `app/` 目录
3. Android Studio 打开项目，Gradle Sync
4. 运行到模拟器或真机（API 26+）
5. 首页点击"添加"按钮填充示例数据（6 家餐厅、24 道菜品）

## License

MIT
