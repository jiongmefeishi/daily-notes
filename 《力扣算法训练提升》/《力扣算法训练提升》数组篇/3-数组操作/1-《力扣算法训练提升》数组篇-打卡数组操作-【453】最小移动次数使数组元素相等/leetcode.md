### 《力扣算法训练提升》图解数组篇-打卡数组统计-【435】最小移动次数使数组元素相等

### 今天是[@囧么肥事](https://leetcode-cn.com/u/jiongmefeishi/)短话长说的第1天，没错最不像题解的题解

### 今日打卡题目[41. 缺失的第一个正数](https://leetcode-cn.com/problems/first-missing-positive/)

![打卡](https://img-blog.csdnimg.cn/img_convert/508a11830f319d6d40c4d0942745024d.gif)

### 囧么肥事今日打卡题目

**力扣【435.最小移动次数使数组元素相等】**

给定一个长度为 *n* 的 **非空** 整数数组，每次操作将会使 *n* - 1 个元素增加 1。找出让数组所有元素相等的最小操作次数。

**具体描述**

![算法描述](https://img-blog.csdnimg.cn/img_convert/de7ffe7e904f263cd9fc84de1df5607c.png)

### 解题讨论

![讨论分析](https://img-blog.csdnimg.cn/img_convert/47889b06e3afbc4f1ffe3a7ecd392a1b.png)



**跨步计算示例**

![示例](https://img-blog.csdnimg.cn/img_convert/65e2253a144fb0d6d1ed6a236989508e.png)



**讨论归纳**


```
第一步：排序
第二步：遍历数组，计算跨步，即最大值和最小值差值
第三步：累加跨步
```

![1-跨步](https://img-blog.csdnimg.cn/img_convert/4d93991183df2bf57270f661cb912ed9.png)

### 动画模拟

![跨步](https://img-blog.csdnimg.cn/img_convert/686424ab5565eab0ea16a4bfe87c17ca.gif)

**示例一：跨步计算**

```
// 排序后计算跨步，最大值到最小值跨步累加就是操作次数
public static int minMoves(int[] nums) {
    // 4 1 9 3

    //  1  3  4  9      排序后
    //  9 11 12  9      跨步：8
    // 12 14 12 12      跨步：3
    // 14 14 14 14      跨步：2

    Arrays.sort(nums);
    int count = 0;
    int min = nums[0];
    int step = 0;
    for (int i = nums.length - 1; i > 0; i--) {
        // 计算跨步 = 最大最小差值
        step = nums[i] - min;
        // 累加跨步
        count += step;
        // 更新 nums[i - 1]
        nums[i - 1] = nums[i - 1] + count;
        // 更新最小数
        min += step;
    }
    return count;
}
```

**复杂度分析**

```
时间复杂度：O(nlog(n))。 排序需要 O(nlog(n)) 的时间。
空间复杂度：O(1)。需要常量级额外空间。
```

**示例二：计算相对跨步**

```
// 省略数组元素修改
// 计算相对跨步
public static int minMoves(int[] nums) {
    Arrays.sort(nums);
    int count = 0;
    for (int i = nums.length - 1; i > 0; i--) {
        count += nums[i] - nums[0];
    }
    return count;
}
```

**复杂度分析**

```
时间复杂度：O(nlog(n))。 排序需要 O(nlog(n)) 的时间。
空间复杂度：O(1)。需要常量级额外空间。
```

![勇敢牛牛](https://img-blog.csdnimg.cn/img_convert/b71852cc132fa37428ddc415f88be6bf.png)

### 短话长说

图解算法(手动更新中...)

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

**缺漏内容，正在努力整理中...喜欢的小伙伴随手一个赞呗**
