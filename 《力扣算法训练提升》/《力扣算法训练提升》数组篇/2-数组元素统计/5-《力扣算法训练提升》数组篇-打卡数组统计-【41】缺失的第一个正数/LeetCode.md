### 《力扣算法训练提升》数组篇-打卡数组统计-【41】缺失的第一个正数

![打卡](https://img-blog.csdnimg.cn/img_convert/6fd889ef33e9edf076ababf4607a1578.png)

**如何判断数组元素是否重复或者缺失？**

如果数组是1~n的正数，那么可以通过修改原数组状态来进行判断；

第一次访问元素，将元素反转为负数；

第二次访问元素，判断元素状态为负数，那么此元素为重复元素；

全部状态修改完后，遍历数组，当判断元素为正数时，那么这个位置 i 就是缺失元素！

![秒懂](https://img-blog.csdnimg.cn/img_convert/3618963c66c170b07832faee34821729.gif)

### 力扣【41.缺失的第一个正数】

给你一个未排序的整数数组 `nums` ，请你找出其中没有出现的最小的正整数。

请你实现时间复杂度为 `O(n)` 并且只使用常数级别额外空间的解决方案。

**具体描述**

![算法描述](https://img-blog.csdnimg.cn/img_convert/3ede5a22c07985cc6edcde1d486dbfdb.png)

### 解题讨论

![最小的缺失数](https://img-blog.csdnimg.cn/img_convert/fe12278765bebaf2506eb61662b82789.png)

**缺失数**

![示例](https://img-blog.csdnimg.cn/img_convert/ba3175bb5947d7b55be88a3de6dd42e1.gif)

**归纳**

缺失数一定出现在 [1, N+1] 范围内


```
取得数组长度为 N

第一次遍历，将所有非正数修改为 N+1
第二次遍历，打标记，将元素属于 1 ~ N 范围内的数反转为负数
第三次遍历，元素大于0，则缺失数为 下标 + 1
```

![完整过程](https://img-blog.csdnimg.cn/img_convert/605a3d6b5c847b51df3a42c963794e64.png)

**复杂度分析**

```
时间复杂度：O(n)。遍历 nums 需要时间 O(n)。

空间复杂度：O(1)。不计算返回结果集，所用空间是原空间。
```

**动画模拟**

![缺失的第一个正数动画](https://img-blog.csdnimg.cn/img_convert/eacc502927965f85344ff716e8494c9d.gif)

**示例**

```
public static int firstMissingPositive(int[] nums) {

    // 缺失数一定出现在 [1, N+1] 范围内
    // 取得数组长度为 N
    // 第一次遍历，将所有非正数修改为 N+1
    int N = nums.length;
    for (int i = 0; i < nums.length; i++) {
        if (nums[i] <= 0) {
            nums[i] = N + 1;
        }
    }

    // 第二次遍历，打标记，将元素属于 1 ~ N 范围内的数反转为负数
    for (int n : nums) {
        if (Math.abs(n) <= N) {
            nums[Math.abs(n) - 1] = - Math.abs(nums[Math.abs(n) - 1]) ;
        }
    }

    // 第三次遍历，元素大于0，则缺失数为 下标 + 1
    for (int i = 0; i < nums.length; i++) {
        if (nums[i] > 0) {
            return i  + 1;
        }
    }

    return N + 1;
}
```



### 短话长说

图解算法(更新中...)，有兴趣的童鞋，欢迎一起从小白开始零基础刷力扣，共同进步！

![短话长说](https://img-blog.csdnimg.cn/img_convert/6040c8afe2a1300128dc451ffc78e779.gif)



**力扣修炼体系题目，题目分类及推荐刷题顺序及题解**

**目前暂定划分为四个阶段：**

```
算法低阶入门篇--武者锻体

算法中级进阶篇--武皇炼心

算法高阶强化篇--武帝粹魂

算法奇技淫巧篇--战斗秘典

以上分类原谅我有个修仙梦...
```

**缺漏内容，正在努力整理中...**

