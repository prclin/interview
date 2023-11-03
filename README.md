# 博晓多科技笔试题

接口文档：https://m3nlxzkfx5.apifox.cn

## 1.解析店铺名称&匹配店铺名称

位置：测试文件 InterviewApplicationTests.java中的TestCSV方法

实现：

构造tag树用作匹配tag，逐行解析csv并反序列化为对象，使用HashSet记录storeId以去重，并同时输出结果

## 2.拉取破价链接&截图上传

在原有字段基础上，新增state字段(0未上传 1已上传)，标识是否完成截图上传

上传截图时，更新state为1

使用redis set缓存已被拉取的破价链接，set key 过期时间为30m，拉取时过滤掉set key中掉的破价链接

set key过期后未及时上传截图的破价链接依然能被拉取

截图保存在classpath的static目录下，在加上前host:port前缀后可直接访问

> 布隆过滤器去重，有误判率，且无法在查询数据时排除已拉取的破价链接，舍弃