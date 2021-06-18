### 《力扣算法训练提升》数组篇-打卡数组遍历-力扣414. 第三大的数

### ![搜一搜](https://img-blog.csdnimg.cn/img_convert/f624544138ac5e8f9d6851002393486d.png)

### 数组的基本特性

数组是最简单的数据结构。

数组是用来存储一系列**相同类型**数据，数据**连续存储**，一次性分配内存。

数组中间进行插入和删除，每次必须搬移后面的所有数据以保持连续，时间复杂度 O(N)。

### 数组索引

数组通过索引支持**快速访问**数据，一个长度为N的数组，它的下标从**0**开始到**N-1**结束。

数组元素可以通过数组名称加索引进行访问。元素的索引是放在方括号内，跟在数组名称的后边。

### 数组扩容

数组内存空间是一次性分配，扩容时，申请一块更大的内存空间，再将数据拷贝到新内存空间，时间复杂度为O(N)。

### 数组遍历

```
for (int i = 0; i < arr.length; i++) {
    // 利用arr[i]进行数组元素操作
}
```

### [力扣414. 第三大的数](https://leetcode-cn.com/problems/third-maximum-number/)

给你一个非空数组，返回此数组中 **第三大的数** 。如果不存在，则返回数组中最大的数。

**具体描述**

![第三大的数](https://img-blog.csdnimg.cn/img_convert/8147f9f927fc22f54204a60ed69d8e2d.png)

### 解题思路

定义三个临时变量，分别保存最大值，第二大值，第三大值；

分别为first, second, third；

遍历数组，循环数组，依次更新，每次**更新顺序**为 third，second，first；

```
若a[i] > first，则 first=a[i]，同时更新second，third；

若a[i] > second，则 second=a[i]，同时更新third；

若a[i] > third，则 third=a[i]
```

最后得到第三大的数，**若没有则返回第一大的数**。

**复杂度分析**

```
时间复杂度：O(n)，其中 n 是数组的长度。需要遍历数组一次。

空间复杂度：O(1)
```

**动画模拟**

![第三大的数](https://img-blog.csdnimg.cn/img_convert/eb3f3490f4448da313dd4da545bcf274.gif)

**示例**

```
public int thirdMax(int[] nums) {

    // 处理不够三个元素的情况
    if (nums.length == 1)
        return nums[0];
    if (nums.length == 2)
        return Math.max(nums[0], nums[1]);

    // 定义三个临时变量，分别保存最大值，第二大值，第三大值
    int first = nums[0];
    long second = Long.MIN_VALUE;
    long third = Long.MIN_VALUE;

    // 循环数组，依次更新，每次更新顺序为 third，second，first
    for (int n : nums) {

        // 大于最大值
        if (n > first) {
            third = second;
            second = first;
            first = n;
        } else if (n > second && n != first) {
            // 大于第二大值，但是不等于最大值，等于最大值无需更新
            third = second;
            second = n;
        } else if (n > third && n != first && n != second) {
            // 大于第三大值，但是不等于第一，第二大值
            third = n;
        }
    }
    return third != Long.MIN_VALUE ? (int) third : first;
}
```



关注我了解更多精彩内容

欢迎关注：**囧么肥事** (或搜索：jiongmefeishi)

![关注我](https://img-blog.csdnimg.cn/img_convert/cb3a296f8edbcc70370d4eb569c40634.png)
