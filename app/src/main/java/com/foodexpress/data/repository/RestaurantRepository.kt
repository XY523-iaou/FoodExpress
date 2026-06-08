package com.foodexpress.data.repository

import com.foodexpress.core.model.MenuCategory
import com.foodexpress.core.model.MenuItem
import com.foodexpress.core.model.Restaurant
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

interface RestaurantRepository {
    suspend fun getRestaurants(): Result<List<Restaurant>>
    suspend fun getRestaurantById(id: String): Result<Restaurant>
    suspend fun getMenuItems(restaurantId: String, categoryId: String? = null): Result<List<MenuItem>>
    suspend fun getCategories(restaurantId: String): Result<List<MenuCategory>>
    suspend fun searchRestaurants(query: String): Result<List<Restaurant>>
    suspend fun seedSampleData()
}

@Singleton
class RestaurantRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : RestaurantRepository {

    override suspend fun getRestaurants(): Result<List<Restaurant>> {
        return try {
            val snapshot = firestore.collection("restaurants")
                .whereEqualTo("isOpen", true)
                .get()
                .await()
            val restaurants = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Restaurant::class.java)?.copy(id = doc.id)
            }
            Result.success(restaurants)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRestaurantById(id: String): Result<Restaurant> {
        return try {
            val doc = firestore.collection("restaurants").document(id).get().await()
            val restaurant = doc.toObject(Restaurant::class.java)?.copy(id = doc.id)
                ?: throw Exception("Restaurant not found")
            Result.success(restaurant)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMenuItems(
        restaurantId: String,
        categoryId: String?
    ): Result<List<MenuItem>> {
        return try {
            var query = firestore.collection("menuItems")
                .whereEqualTo("restaurantId", restaurantId)
                .whereEqualTo("isAvailable", true)

            if (categoryId != null) {
                query = query.whereEqualTo("categoryId", categoryId)
            }

            val snapshot = query.get().await()
            val items = snapshot.documents.mapNotNull { doc ->
                doc.toObject(MenuItem::class.java)?.copy(id = doc.id)
            }
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCategories(restaurantId: String): Result<List<MenuCategory>> {
        return try {
            val doc = firestore.collection("restaurants").document(restaurantId).get().await()
            val restaurant = doc.toObject(Restaurant::class.java)
            Result.success(restaurant?.categories ?: emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchRestaurants(query: String): Result<List<Restaurant>> {
        return try {
            val snapshot = firestore.collection("restaurants")
                .get()
                .await()
            val all = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Restaurant::class.java)?.copy(id = doc.id)
            }
            val filtered = all.filter { restaurant ->
                restaurant.name.contains(query, ignoreCase = true) ||
                        restaurant.category.contains(query, ignoreCase = true) ||
                        restaurant.description.contains(query, ignoreCase = true)
            }
            Result.success(filtered)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun seedSampleData() {
        val sampleRestaurants = listOf(
            Restaurant(
                id = "rest_001",
                name = "老北京炸酱面馆",
                description = "正宗老北京风味，传承百年手艺，每日手工制作面条",
                imageUrl = "https://images.unsplash.com/photo-1555126634-323283e090fa?w=400",
                category = "中餐",
                rating = 4.5f,
                reviewCount = 128,
                address = "北京市朝阳区建国路88号",
                deliveryFee = 5.0,
                minOrderAmount = 20.0,
                deliveryTime = "30-40分钟",
                categories = listOf(
                    MenuCategory("cat_001", "人气推荐", 1),
                    MenuCategory("cat_002", "面食", 2),
                    MenuCategory("cat_003", "小菜", 3),
                    MenuCategory("cat_004", "饮品", 4)
                )
            ),
            Restaurant(
                id = "rest_002",
                name = "樱花日式料理",
                description = "新鲜进口三文鱼，匠心手握寿司，体验纯正日式美味",
                imageUrl = "https://images.unsplash.com/photo-1579027989536-b7b1f875659b?w=400",
                category = "日料",
                rating = 4.7f,
                reviewCount = 256,
                address = "上海市静安区南京西路1688号",
                deliveryFee = 8.0,
                minOrderAmount = 30.0,
                deliveryTime = "40-50分钟",
                categories = listOf(
                    MenuCategory("cat_201", "寿司", 1),
                    MenuCategory("cat_202", "刺身", 2),
                    MenuCategory("cat_203", "定食", 3),
                    MenuCategory("cat_204", "饮品", 4)
                )
            ),
            Restaurant(
                id = "rest_003",
                name = "川味轩",
                description = "地道四川麻辣风味，精选汉源花椒，让你一口入魂",
                imageUrl = "https://images.unsplash.com/photo-1563245372-f21724e3856d?w=400",
                category = "中餐",
                rating = 4.3f,
                reviewCount = 89,
                address = "成都市武侯区科华北路56号",
                deliveryFee = 3.0,
                minOrderAmount = 15.0,
                deliveryTime = "25-35分钟",
                categories = listOf(
                    MenuCategory("cat_301", "招牌川菜", 1),
                    MenuCategory("cat_302", "麻辣香锅", 2),
                    MenuCategory("cat_303", "小吃", 3),
                    MenuCategory("cat_304", "主食", 4)
                )
            ),
            Restaurant(
                id = "rest_004",
                name = "汉堡大师",
                description = "精选安格斯牛肉，现点现做，多种口味随心搭配",
                imageUrl = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400",
                category = "快餐",
                rating = 4.1f,
                reviewCount = 312,
                address = "广州市天河区天河路385号",
                deliveryFee = 4.0,
                minOrderAmount = 18.0,
                deliveryTime = "20-30分钟",
                categories = listOf(
                    MenuCategory("cat_401", "超值套餐", 1),
                    MenuCategory("cat_402", "汉堡", 2),
                    MenuCategory("cat_403", "小食", 3),
                    MenuCategory("cat_404", "饮品", 4)
                )
            ),
            Restaurant(
                id = "rest_005",
                name = "泰味椰香",
                description = "东南亚风情美食，冬阴功、咖喱蟹、芒果糯米饭应有尽有",
                imageUrl = "https://images.unsplash.com/photo-1559314809-0d155014e29e?w=400",
                category = "东南亚",
                rating = 4.6f,
                reviewCount = 167,
                address = "深圳市南山区科技园路100号",
                deliveryFee = 6.0,
                minOrderAmount = 25.0,
                deliveryTime = "35-45分钟",
                categories = listOf(
                    MenuCategory("cat_501", "咖喱系列", 1),
                    MenuCategory("cat_502", "汤品", 2),
                    MenuCategory("cat_503", "主食", 3),
                    MenuCategory("cat_504", "甜品", 4)
                )
            ),
            Restaurant(
                id = "rest_006",
                name = "必胜披萨屋",
                description = "意式手工披萨，32小时低温发酵面团，石炉烤制",
                imageUrl = "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=400",
                category = "西餐",
                rating = 4.4f,
                reviewCount = 203,
                address = "杭州市西湖区文三路478号",
                deliveryFee = 7.0,
                minOrderAmount = 35.0,
                deliveryTime = "35-45分钟",
                categories = listOf(
                    MenuCategory("cat_601", "经典披萨", 1),
                    MenuCategory("cat_602", "意面", 2),
                    MenuCategory("cat_603", "沙拉", 3),
                    MenuCategory("cat_604", "饮品", 4)
                )
            )
        )

        val sampleMenuItems = listOf(
            MenuItem("item_001", "招牌炸酱面", "手工拉面配秘制炸酱，黄瓜丝、豆芽、萝卜丝", 18.0, "https://images.unsplash.com/photo-1555126634-323283e090fa?w=300", categoryId = "cat_001", restaurantId = "rest_001", tags = listOf("招牌", "热销"), salesCount = 1520),
            MenuItem("item_002", "红烧牛肉面", "大块牛腩慢炖4小时，汤浓面筋道", 28.0, "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=300", categoryId = "cat_001", restaurantId = "rest_001", tags = listOf("热销"), salesCount = 980),
            MenuItem("item_003", "葱油拌面", "小葱熬制葱油，简单纯粹的老味道", 12.0, "https://images.unsplash.com/photo-1612927601601-663312b78f7e?w=300", categoryId = "cat_002", restaurantId = "rest_001", tags = listOf("素"), salesCount = 650),
            MenuItem("item_004", "拍黄瓜", "蒜泥香油拌制，清爽解腻", 8.0, "https://images.unsplash.com/photo-1599816857075-08a4776609f3?w=300", categoryId = "cat_003", restaurantId = "rest_001"),
            MenuItem("item_005", "酸梅汤", "古法熬制，冰镇酸甜", 6.0, "https://images.unsplash.com/photo-1544145945-f90425340c7e?w=300", categoryId = "cat_004", restaurantId = "rest_001", tags = listOf("冰镇")),

            MenuItem("item_101", "三文鱼刺身", "挪威进口三文鱼，厚切8片", 68.0, "https://images.unsplash.com/photo-1579871494447-9811cf80d66c?w=300", categoryId = "cat_202", restaurantId = "rest_002", tags = listOf("人气", "进口"), salesCount = 890),
            MenuItem("item_102", "豪华寿司拼盘", "三文鱼、金枪鱼、甜虾、鳗鱼等12贯", 128.0, "https://images.unsplash.com/photo-1579027989536-b7b1f875659b?w=300", categoryId = "cat_201", restaurantId = "rest_002", tags = listOf("招牌"), salesCount = 670),
            MenuItem("item_103", "鳗鱼饭定食", "蒲烧鳗鱼配米饭、味噌汤、小菜", 58.0, "https://images.unsplash.com/photo-1509023464722-18d996393ca8?w=300", categoryId = "cat_203", restaurantId = "rest_002", tags = listOf("套餐")),
            MenuItem("item_104", "抹茶拿铁", "京都宇治抹茶粉，浓郁回甘", 22.0, "https://images.unsplash.com/photo-1536256263959-770b48d82b0a?w=300", categoryId = "cat_204", restaurantId = "rest_002"),

            MenuItem("item_201", "水煮鱼", "鲜活草鱼片，麻辣鲜香，配豆芽木耳", 48.0, "https://images.unsplash.com/photo-1569058242253-92a9c755a0ec?w=300", categoryId = "cat_301", restaurantId = "rest_003", tags = listOf("招牌", "麻辣"), salesCount = 2100),
            MenuItem("item_202", "回锅肉", "二刀肉配蒜苗豆豉，色泽红亮", 32.0, "https://images.unsplash.com/photo-1529692236671-f1f6cf9683ba?w=300", categoryId = "cat_301", restaurantId = "rest_003", tags = listOf("经典")),
            MenuItem("item_203", "麻辣香锅", "自选6种食材，秘制麻辣底料炒制", 45.0, "https://images.unsplash.com/photo-1563245372-f21724e3856d?w=300", categoryId = "cat_302", restaurantId = "rest_003", tags = listOf("辣"), salesCount = 780),
            MenuItem("item_204", "担担面", "肉臊花生碎，麻辣微甜", 14.0, "https://images.unsplash.com/photo-1552611052-33e0a5c12f7e?w=300", categoryId = "cat_304", restaurantId = "rest_003"),

            MenuItem("item_301", "经典芝士汉堡", "安格斯牛肉饼+车达芝士+生菜番茄", 25.0, "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=300", categoryId = "cat_402", restaurantId = "rest_004", tags = listOf("经典"), salesCount = 3200),
            MenuItem("item_302", "双层牛肉堡", "双层安格斯牛肉饼+培根+煎蛋", 38.0, "https://images.unsplash.com/photo-1550547660-d9450f859349?w=300", categoryId = "cat_402", restaurantId = "rest_004", tags = listOf("重磅")),
            MenuItem("item_303", "超值单人套餐", "汉堡+薯条+可乐", 32.0, "https://images.unsplash.com/photo-1596662951482-0c4ba74a6df6?w=300", categoryId = "cat_401", restaurantId = "rest_004", tags = listOf("套餐", "超值")),
            MenuItem("item_304", "鸡米花", "外酥里嫩，配番茄酱", 12.0, "https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?w=300", categoryId = "cat_403", restaurantId = "rest_004"),

            MenuItem("item_401", "冬阴功汤", "大虾、蘑菇、香茅、椰奶，酸辣开胃", 38.0, "https://images.unsplash.com/photo-1559314809-0d155014e29e?w=300", categoryId = "cat_502", restaurantId = "rest_005", tags = listOf("招牌"), salesCount = 560),
            MenuItem("item_402", "黄咖喱蟹", "鲜活花蟹配黄咖喱，配法棍面包", 88.0, "https://images.unsplash.com/photo-1455619452474-d2be8b1e70cd?w=300", categoryId = "cat_501", restaurantId = "rest_005", tags = listOf("人气")),
            MenuItem("item_403", "泰式炒河粉", "大虾、豆芽、花生碎，经典Pad Thai", 28.0, "https://images.unsplash.com/photo-1567306226416-28f0efdc88ce?w=300", categoryId = "cat_503", restaurantId = "rest_005"),
            MenuItem("item_404", "芒果糯米饭", "新鲜芒果配椰浆糯米，甜蜜收尾", 18.0, "https://images.unsplash.com/photo-1621293954908-907159247fc8?w=300", categoryId = "cat_504", restaurantId = "rest_005", tags = listOf("甜点")),

            MenuItem("item_501", "玛格丽特披萨", "圣马扎诺番茄+水牛莫扎瑞拉+新鲜罗勒", 48.0, "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=300", categoryId = "cat_601", restaurantId = "rest_006", tags = listOf("经典"), salesCount = 1100),
            MenuItem("item_502", "意式肉酱面", "博洛尼亚肉酱配帕尔马干酪", 35.0, "https://images.unsplash.com/photo-1621996346565-e3dbc646d9a9?w=300", categoryId = "cat_602", restaurantId = "rest_006", tags = listOf("热销")),
            MenuItem("item_503", "凯撒沙拉", "罗马生菜+培根碎+帕玛森芝士+凯撒酱", 28.0, "https://images.unsplash.com/photo-1546793665-c74683f339c1?w=300", categoryId = "cat_603", restaurantId = "rest_006"),
            MenuItem("item_504", "鲜榨橙汁", "100%鲜榨，不加糖不加水", 15.0, "https://images.unsplash.com/photo-1621506289937-a8e4df240d0b?w=300", categoryId = "cat_604", restaurantId = "rest_006", tags = listOf("鲜榨"))
        )

        for (restaurant in sampleRestaurants) {
            val data = mapOf(
                "name" to restaurant.name,
                "description" to restaurant.description,
                "imageUrl" to restaurant.imageUrl,
                "category" to restaurant.category,
                "rating" to restaurant.rating,
                "reviewCount" to restaurant.reviewCount,
                "address" to restaurant.address,
                "deliveryFee" to restaurant.deliveryFee,
                "minOrderAmount" to restaurant.minOrderAmount,
                "deliveryTime" to restaurant.deliveryTime,
                "isOpen" to true,
                "categories" to restaurant.categories.map { cat ->
                    mapOf("id" to cat.id, "name" to cat.name, "sortOrder" to cat.sortOrder)
                }
            )
            firestore.collection("restaurants").document(restaurant.id).set(data).await()
        }

        for (item in sampleMenuItems) {
            val data = mapOf(
                "name" to item.name,
                "description" to item.description,
                "price" to item.price,
                "imageUrl" to item.imageUrl,
                "isAvailable" to true,
                "categoryId" to item.categoryId,
                "restaurantId" to item.restaurantId,
                "tags" to item.tags,
                "salesCount" to item.salesCount
            )
            firestore.collection("menuItems").document(item.id).set(data).await()
        }
    }
}
