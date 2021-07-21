### 《力扣算法训练提升》图解数组篇-打卡数组统计-【283】移动零
### 本篇是[@囧么肥事](https://leetcode-cn.com/u/jiongmefeishi/)短话长说的第4篇，没看错就是最不像题解的题解

### 今日打卡题目【283】移动零


![打卡](https://img-blog.csdnimg.cn/img_convert/e515042249a97c97ca3e8e00956daa39.png)

### 囧么肥事今日打卡题目

**力扣【283.移动零】**

给定一个数组 `nums`，编写一个函数将所有 `0` 移动到数组的末尾，同时保持非零元素的相对顺序。



**具体描述**

![算法描述](https://img-blog.csdnimg.cn/img_convert/bcfd0200b77819c46bdd3c4a444bdd83.png)

### 解题讨论

![算法讨论](https://img-blog.csdnimg.cn/img_convert/dafc4536868a258ec2267ef6863a352f.png)



### 讨论归纳

假设不考虑题目空间要求，利用辅助数组

> 遍历原数组，将非 0 数 填充进辅助数组
>
> 遍历完毕后，余位补 0

![辅助数组](https://img-blog.csdnimg.cn/img_convert/be1bf83794158ba623e0d74491bd94a5.png)

**动画模拟**

![额外数组动画](https://img-blog.csdnimg.cn/img_convert/c4a9febd793738588ee5c43e38b13fd2.gif)

**思考：题目要求不能使用额外数组**

不能使用额外数组，操作原数组，**双指针交换数组元素**。

```
声明两个指针L,R

L指向 0 位，R指向非 0 位

遍历数组

满足 a[R] > 0 , 则交换 a[L] , a[R], L++, R++
否则 a[R] <= 0, R++
```

### 动画模拟

![双指针动画](https://img-blog.csdnimg.cn/img_convert/7f6b5c4e47eba23dccb8994948d88310.gif)



**示例：双指针**

```
//输入: [0,1,0,3,12]
//输出: [1,3,12,0,0]

// 双指针
// 0 1 0 3 12       L=0, R=0 初始化
// 0 1 0 3 12       L=0, R=1 交换 arr[0], arr[1]
// 1 0 0 3 12       L=1, R=3 交换 arr[1], arr[2]
// 1 3 0 0 12       L=2, R=4 交换 arr[2], arr[4]
// 1 3 12 0 0
public static void moveZeroes(int[] nums) {
    int L = 0;
    int R = 0;
    while (R < nums.length) {
        if (nums[R] != 0) {
            swap(nums, L++, R);
        }
        R++;
    }
}

// 交换L，R下标对应数据
public static void swap(int[] arr, int L, int R) {
    int tmp = arr[L];
    arr[L] = arr[R];
    arr[R] = tmp;
}
```

**复杂度分析**

```
时间复杂度：O(n)。 遍历数组，需要O(n)时间。
空间复杂度：O(1)。需要常量级额外空间。
```

![勇敢牛牛](https://img-blog.csdnimg.cn/img_convert/0335e345b091bd18d5cab13658aa10e9.gif)


### 短话长说

图解算法(手动更新中...)

![短话长说](https://img-blog.csdnimg.cn/img_convert/6040c8afe2a1300128dc451ffc78e779.gif)



**囧么肥事算法打卡之路，暂定划分为四个阶段：**

```
算法低阶入门篇--武者锻体

算法中级进阶篇--武皇炼心

算法高阶强化篇--武帝粹魂

算法奇技淫巧篇--战斗秘典


以上分类原谅我有个修仙梦...
打怪升级进行中...
```

**缺漏内容[@囧么肥事](https://leetcode-cn.com/u/jiongmefeishi/)正在努力整理中...喜欢的小伙伴随手一个赞呗**