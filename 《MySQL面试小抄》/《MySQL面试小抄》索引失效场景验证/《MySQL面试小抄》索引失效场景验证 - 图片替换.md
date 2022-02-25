

![](https://img-blog.csdnimg.cn/img_convert/f624544138ac5e8f9d6851002393486d.png)

我是肥哥，一名不专业的面试官！

我是囧囧，一名积极找工作的小菜鸟！

> **囧囧表示：小白面试最怕的就是面试官问的知识点太笼统，自己无法快速定位到关键问题点！！！**

----



本期主要面试考点

```
面试官考点之什么情况下会索引失效？
面试官考点之简单说说你工作中遇到的索引失效场景？
```

以下索引失效的常见场景

>1、like通配符，左侧开放情况下，全表扫描
>2、or条件筛选，可能会导致索引失效
>3、where中对索引列使用mysql的内置函数，一定失效
>4、where中对索引列进行运算（如，+、-、*、/），一定失效
>5、类型不一致，隐式的类型转换，导致的索引失效
>6、where语句中索引列使用了负向查询，可能会导致索引失效。负向查询包括：NOT、!=、<>、!<、!>、NOT IN、NOT LIKE等，其中：!< !> SQLServer语法。
>7、索引字段可以为null，使用is null或is not null时，可能会导致索引失效
>8、隐式字符编码转换导致的索引失效
>9、联合索引中，where中索引列违背最左匹配原则，一定会导致索引失效
>10、MySQL优化器的最终选择，不走索引



![索引失效验证1](https://img-blog.csdnimg.cn/img_convert/fdaee4537f455cdee728cafbc6c9d3b0.png)

![索引失效验证1](https://img-blog.csdnimg.cn/img_convert/dab3e4dd86a42e6499fa054873f685e5.png)



### 验证准备

准备数据表，同时建立普通索引 idx_user_name

```
CREATE TABLE `t_user` (
  `id` int(11) NOT NULL,
  `user_name` varchar(32) CHARACTER DEFAULT NULL COMMENT '用户名',
  `address` varchar(255) CHARACTER DEFAULT NULL COMMENT '地址',
  `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

插入1万条数据（注意：数据多些，mysql不走索引情况之一是**数据量非常少**，MySQL**查询优化器**认为全表扫描比使用索引更快，导致索引失效，explain检查是否使用索引时，发现无法走索引）

```
-- 创建存储过程，插入10000用户信息
CREATE PROCEDURE user_insert()
-- 定义存储过程开始
BEGIN
	-- 定义变量 i ，int 类型，默认值为 1
	DECLARE i INT DEFAULT 1;
	
	WHILE i <= 10000
		-- 定义循环内执行命令
		DO INSERT INTO t_user(id, user_name, address, create_time) VALUES(i, CONCAT('mayun', i), CONCAT('浙江杭州', i), now());
		SET i=i+1;
	END WHILE;
	
	COMMIT;
END;
-- 定义存储过程结束


-- 调用存储工程
CALL user_insert();
```



### 一、OR索引失效验证

好多人说where条件中使用 or ，那么索引一定失效，是否正确？

![or查询](https://img-blog.csdnimg.cn/img_convert/ebd5987cae7ca16b874292c659249771.png)

> OR 连接的是同一个字段，相同走索引

```
explain select * from t_user where user_name = 'mayun10' or user_name = 'mayun1000'
```

![or查询走索引情况](https://img-blog.csdnimg.cn/img_convert/ebe300829092cdf115cee9b5ccb88d9c.png)

> OR 连接的是两个不同字段，不同索引失效

```
explain select * from t_user where user_name = 'mayun10' or address = '浙江杭州12'
```

![or查询索引失效情况](https://img-blog.csdnimg.cn/img_convert/d79413e9c57d7ccdc218274574e619bc.png)

> 给address列增加索引

```
alter table t_user add index idx_address (address)
```

> OR 连接的是两个不同字段，如果两个字段皆有索引，走索引
>

![or查询走索引情况-两边字段有索引](https://img-blog.csdnimg.cn/img_convert/cf33f854ad45d4de6d1e351c5f6c5c1b.png)



**验证总结**

or 可能会导致索引失效，并非一定，这里涉及到MySQL **index merge** 技术。

> 1、MySQL5.0之前，查询时一个表一次只能使用一个索引，无法同时使用多个索引分别进行条件扫描。
>
> 
>
> 2、但是从5.1开始，MySQL引入了 index merge 优化技术，对同一个表可以使用多个索引分别进行条件扫描。然后将它们各自的结果进行合并(intersect/union)。

**or索引生效有哪些情况？**

第一种 or两边连接的是**同一个索引字段**

第二种 or两边连接的是两个索引字段，即**两个字段分别都建立了索引**

### 二、LIKE通配符索引失效验证

一个最常见的查询场景，建立idx_user_name索引

```
select * from t_user where user_name like '%mayun100%';
```

>  这条查询是否走索引？

![like不走索引](https://img-blog.csdnimg.cn/img_convert/9e8c11d5c736e09dbdce4ffca3db0a1a.png)

```
select * from t_user where user_name like 'mayun100%';
```

>  这条查询是否走索引？

![like走索引](https://img-blog.csdnimg.cn/img_convert/299ca6fc7324e10e593ca32135b3402b.png)

**验证总结**

```
like 通配符特性是可以左右开闭匹配查询

当左边开放使用 % 或者 _ 匹配的时候都不会走索引,会进行全表扫描
```

> 为什么左开情况下会索引失效?请介绍一下原理！

我们知道建立索引后，MySQL会建立一棵有序的B+Tree,索引树是有序的，索引列进行查询匹配时是从左到右进行匹配。使用 % 和 _ 匹配，这表示左边匹配值是不确定的。不确定，意味着充满可能，**怎么比较？**

当然只能一个一个的比较，那就相当于，全匹配了，全匹配在优化器看来，与其走索引树查询，再进行不断的回表操作，还不如直接进行全表扫描划算！

### 三、where中对索引列使用mysql的内置函数

> 建立 idx_age 索引，

```
alter table t_user add index idx_age(age);
```

> 不使用内置函数

```
explain select * from t_user where age = 80
```

![内置函数验证-不使用内置函数](https://img-blog.csdnimg.cn/img_convert/2ea0a35d5a1cddc38c950eb440d9c392.png)

> 使用内置函数

```
explain select * from t_user where abs(age) = 80
```

![内置函数验证-内置函数](https://img-blog.csdnimg.cn/img_convert/6e762ab24d2f654b608f28dec0a01678.png)

**验证总结**

>如果对索引字段做了函数操作，可能会破坏索引值的有序性，因此优化器就决定放弃走树搜索功能。
>
>MySQL 无法再使用索引快速定位功能，而只能使用全索引扫描。

### 四、where中对索引列进行运算（如，+、-、*、/），一定失效

> 不涉及索引列的运算

```
alter table t_user add index idx_age(age);
explain select * from t_user where age = 80;
```

![内置函数验证-不使用内置函数](https://img-blog.csdnimg.cn/img_convert/2ea0a35d5a1cddc38c950eb440d9c392.png)

> 索引列进行运算操作

```
explain select * from t_user where age + 5 = 80
```

![索引运算失效](https://img-blog.csdnimg.cn/img_convert/ba72960b6f419edba01ba8034aed74de.png)

### 五、类型不一致，隐式的类型转换，导致的索引失效

```
alter table t_user add index idx_user_name(user_name);

explain select * from t_user where user_name = 'mayun1';
```

![类型不一致验证之正常类型](https://img-blog.csdnimg.cn/img_convert/d95f4dbd6f8fc8530bd014dc4053dc49.png)

修改数据，再次explain

```
update t_user set user_name = '100' where user_name = 'mayun1';
explain select * from t_user where user_name = 100;
```

user_name = 100 ，因为user_name 字段定义的是varchar，索引在where进行匹配时会先隐式调用 case() 函数进行类型转换 将匹配条件变成，**user_name = '100'**

![类型不一致验证之类型转换](https://img-blog.csdnimg.cn/img_convert/c9a148971d95dd177388b1ee947b51ad.png)



### 六、where语句中索引列使用了负向查询，可能会导致索引失效。

> 负向查询包括：NOT、!=、<>、!<、!>、NOT IN、NOT LIKE等，其中：!< !> SQLServer语法。

```
alter table t_user add index idx_age(age);
explain select * from t_user where age in (100, 50);
```

![反向选择之in](https://img-blog.csdnimg.cn/img_convert/08ddf1876ddb1cb73b407a0e5848ac7a.png)

```
explain select * from t_user where age not in (100, 50);
```

![反向选择之not_in](https://img-blog.csdnimg.cn/img_convert/f7f7c224b7c7338b5e4643706c4a085a.png)

### 七、索引字段可以为null，使用is null或is not null时，可能会导致索引失效

**第一种情况，表结构规定允许user_name 字段可以为null**

![null可以为空](https://img-blog.csdnimg.cn/img_convert/1190666537145bcc317bcb2794505dbb.png)

```
explain select * from t_user where user_name is null;
```

![null可以为空校验之is_null](https://img-blog.csdnimg.cn/img_convert/c2d205da5711773bc0c8d7520dc9edeb.png)

```
explain select * from t_user where user_name is not null;
```

![null可以为空校验之is_not_null](https://img-blog.csdnimg.cn/img_convert/4c04744d04e91f4d9f0395041d51ca09.png)

**第二种情况，表结构规定user_name 字段不可以为null**

![null不允许为null](https://img-blog.csdnimg.cn/img_convert/0eac42d920909363b181b77a563736f8.png)

```
explain select * from t_user where user_name is null;
```

![null不允许为null之null](https://img-blog.csdnimg.cn/img_convert/608cddb196a3262e6a77f62412e49625.png)

```
explain select * from t_user where user_name is not null;
```

![null不允许为null之is_not_null](https://img-blog.csdnimg.cn/img_convert/fdf32f1593562196bc72d77e89eaab30.png)

### 八、隐式字符编码转换导致的索引失效

当两个表进行连接JOIN 时，如果两张表的字符编码不同，可能会导致索引失效。

这个索引失效场景尚未遇到，网上很多文章说会导致索引失效，查阅发现大量的博客说UTF8mb4字符集的表mb4与UTF8字符集的表utf8 关联会产生索引失效的问题，但是我根据大量博文所述操作，发现暂时还是无法复现，读者可自行查阅。

> 如果读者复现到此场景，欢迎评论讨论或关注如果读者复现到此场景，欢迎评论或关注公众号`囧么肥事`讨论

### 九、联合索引中，where中索引列违背最左匹配原则，一定会导致索引失效

> 创建联合做引 idx_user_name_deposit， **遵循最左匹配原则**

```
alter table t_user add index idx_user_name_deposit(user_name, deposit);

explain select * from t_user where user_name like 'mayun86%'
```

![最左匹配之a](https://img-blog.csdnimg.cn/img_convert/f87dd3a7ebb7b0c47c6fca20583f9c3c.png)

> **遵循最左匹配之 a b 类型**

```
explain select * from t_user where user_name like 'mayun86%' and deposit = 5620.26;
```

![最左匹配之ab](https://img-blog.csdnimg.cn/img_convert/f978c19249a3965ae6631daa59619fca.png)

> **调换索引位置，测试联合索引书写规则**

```
explain select * from t_user where deposit = 5620.26 and user_name like 'mayun86%';
```

![最左匹配之ba](https://img-blog.csdnimg.cn/img_convert/e208beee12b38f82d8692a6a23fbeef7.png)

>违反最左匹配原则

```
explain select * from t_user where deposit = 5620.26;
```

![最左匹配之b](https://img-blog.csdnimg.cn/img_convert/054200407d8e01a390e662a97ba96903.png)

**验证总结**

联合索引依据最左匹配原则建立索引树，在查询时依据联合索引顺序依次匹配索引值，查询时如果违背最左匹配原则，将导致索引失效。

```
假设建立索引 idx_a_b_c，相当于建立了 (a), (a,b), (a,b,c)三个索引

查询匹配时匹配顺序是 a b c 

查询时如果没有 a 字段筛选，那么索引将失效
```

> **举栗子，走索引情况**

```
select * from test where a=1 
select * from test where a=1 and b=2 
select * from test where a=1 and b=2 and c=3
```

> **索引失效呢？**

```
select * from test where b=2 and c=3
```

联合索引如果要走索引，查询条件中必须要**包含第一个索引**，否则索引失效

```
select * from test where b=1 and a=1

select * from test where m='222' and a=1
```

**这两条查询走索引的原因是什么？**

最左前缀指的是查询时匹配索引列要按照联合索引创建的顺序，但是在书写时不需要严格按照联合索引创建的顺序，MySQL优化器会自动调整，所以上面两条查询索引有效！

### 十、MySQL优化器的最终选择，不走索引

```
explain select * from t_user where age > 59;
```

![优化器不走](https://img-blog.csdnimg.cn/img_convert/5340bb0802595a28caebe23a55ad2b09.png)

```
explain select * from t_user where age > 99;
```

![优化器走](https://img-blog.csdnimg.cn/img_convert/4e7608929f86e86e9938721c2be89a62.png)

**验证总结**

MySQL查询索引失效的情况有很多，即使其他情况都规避，但是在经过了优化器的确定查询方案的时候，依然可能索引失效。

优化器会考虑**查询成本**，来确认它认为的最佳方案来执行查询

当数据量较少，或者需要访问行很多的时候

优化器会认为**走索引树来进行回表**，还不如直接进行全表扫描的时候，优化器将会**抛弃**走索引树。





----



**推荐MySQL相关休闲阅读**：

第一段，索引面试题推荐阅读一：[【来自面试官一面MySQL索引的连续灵魂拷问】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247483708&idx=1&sn=2bba08c79535caad22571efa8f698aa6&chksm=cf31e8eaf84661fc013c8fffd5580f5793c157c5639afdf1a3daa2381be4f6a3347690c770b7#rd)

第二段，索引面试题推荐阅读二：[【来自面试官二面MySQL索引的连续灵魂拷问】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247483727&idx=1&sn=de099056011f1da943a0f42843e0c75b&chksm=cf31e899f846618f8d8135ace8b3ca3987a255295b603c463eecd5321806a51cc45e5815ece3#rd)

第三段，索引失效场景面试题推荐阅读：[【面试官：说说你遇到的MySQL索引失效场景吧，你是如何解决的？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247483801&idx=1&sn=14f7f9e7023abf045c3322a00815f79b&chksm=cf31e84ff8466159498f5db66ed3c028587caabdeabe57aaa5f4612c00ea236e6c6e5bf77a66#rd)

第四段，查询缓存面试题推荐阅读：[【面试官：什么场景下会导致MySQL缓存失效？生产环境到底要不要开启MySQL缓存？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247483836&idx=1&sn=24d090fc782b3855ab4c8c20d139892e&chksm=cf31e86af846617cbee4c7e2deb62fc18fe6e5099e2c20f7cd029a1dcbf9fb4646cfb02a4474#rd)

第五段，待更新？推荐休闲阅读：[【囧么肥事】](https://mp.weixin.qq.com/mp/appmsgalbum?__biz=Mzg3NjU0NDE4NQ==&action=getalbum&album_id=2218140423993212933#wechat_redirect)



更多精彩内容，欢迎关注微信公众号：**囧么肥事** (或搜索：jiongmefeishi)

![囧么肥事](https://img-blog.csdnimg.cn/img_convert/cb3a296f8edbcc70370d4eb569c40634.png)

