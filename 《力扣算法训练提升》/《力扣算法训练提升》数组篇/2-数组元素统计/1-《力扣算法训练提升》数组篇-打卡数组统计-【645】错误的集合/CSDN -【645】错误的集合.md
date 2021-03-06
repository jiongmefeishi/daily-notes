![关注我](https://img-blog.csdnimg.cn/img_convert/f624544138ac5e8f9d6851002393486d.png)

### 《力扣算法训练提升》数组篇-打卡数组统计-【645】错误的集合

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

### 数组统计

**如何统计数组中元素出现次数？**

如果给定的数组元素全都是非负数，那么针对数组中元素出现个数的统计，我们可以借用额外的辅助数组来达到统计目的。

```
for (int i = 0; i < nums.length; i++) {
	help[nums[i]]++;
}
```

如何快速给定元素在数组中出现的位置？左边xx位置？右边xx位置？

针对元素出现在数组左边第xx位置，右边第xx位置，依然是借助辅助数组。

借用left[]，right[] ，分别记录数组从左边开始数，第几个位置，右边数第几个位置；

```
for (int i = 0; i < nums.length; i++) {
	left[nums[i]] = i;
}
for (int i = nums.length - 1; i >= 0 ; i--) {
	right[nums[i]] = nums.length - 1 - i;
}
```

**如何判断数组元素是否重复或者缺失？**

如果数组是1~n的正数，那么可以通过修改原数组状态来进行判断；

第一次访问元素，将元素反转为负数；

第二次访问元素，判断元素状态为负数，那么此元素为重复元素；

全部状态修改完后，遍历数组，当判断元素为正数时，那么这个位置 i 就是缺失元素！

### [力扣【645. 错误的集合】](https://leetcode-cn.com/problems/set-mismatch/)

集合 s 包含从 1 到 n 的整数。

不幸的是，因为数据错误，导致集合里面某一个数字复制了成了集合里面的另外一个数字的值，导致集合 丢失了一个数字 并且 有一个数字重复 。

给定一个数组 nums 代表了集合 S 发生错误后的结果。

请你找出重复出现的整数，再找到丢失的整数，将它们以数组的形式返回。

**具体描述**

![算法描述](https://img-blog.csdnimg.cn/img_convert/7cf2aaed5ca03d91db7c31d4bcffb314.png)



### 解题思路一：辅助数组

遍历原数组，利用辅助数组help[] ， 存储每个数字出现的次数。

例如help[nums[i]]存储数字 i 出现的次数。

遍历辅助数组

```
次数大于1的是重复数
```

```
次数等于0的是缺失数
```

**复杂度分析**

```
时间复杂度：O(n)。遍历 nums 需要时间 O(n)，检查每个数字需要时间 O(n)。

空间复杂度：O(n)。数组 arr 最多需要存储 1 到 n 共 n 个数字的出现次数。
```

**动画模拟**



![方法一](https://img-blog.csdnimg.cn/20210623092343657.gif)

**示例**

```
// 辅助数组
public static int[] findErrorNums1(int[] nums) {

    // 辅助数组，数据是从 1~N，所以长度 +1
    int[] help = new int[nums.length + 1];

    // 根据nums 修改辅助数组，统计nums 元素出现次数
    for (int num : nums) {
        help[num]++;
    }

    // 遍历辅助数组
    // 次数大于1的是重复数
    // 次数等于0的是缺失数
    int[] res = new int[2];
    for (int i = 1; i < help.length; i++) {
        if (help[i] > 1) {
            res[0] = i;
        }
        if (help[i] == 0) {
            res[1] = i;
        }
    }

    return res;
}
```

### 解题思路二：修改原数组元素状态

题目给定数组nums[] ，存储的数字都是正数，且处于 1 到 n 之间。

遍历 nums[] ，根据 i 找到 nums[|i|]，如果是第一次访问 nums[∣i∣]，将它**反转为负数**。

再次遇到时，查询已经变为负数，那么这个数就是**重复数**。

完成遍历后所有出现过的数字对应索引处的数字都是负数，只有缺失数字 j 对应的索引处仍然是正数。

再次遍历nums[]，找到数组中正值元素所在位置 + 1，即为**缺失数**。



**复杂度分析**

```
时间复杂度：O(n)。遍历 nums 需要时间 O(n)，检查每个数字需要时间 O(n)。

空间复杂度：O(1)。使用恒定的额外空间。
```

**动画模拟**

![方法二](https://img-blog.csdnimg.cn/img_convert/dce620524d2a988f1b255496e8f58b8e.gif)

**示例**

```
// 将第一次遇到的数变为负数
// 再次遇到时，查询已经变为负数，那么这个数就是重复数
public static int[] findErrorNums2(int[] nums) {

    //        1 2 4 3 3 3 4
    // a[0]  -1 2 4 3 3 3 4
    // a[1]  -1 -2 4 3 3 3 4
    // a[2]  -1 -2 4 -3 3 3 4

    int[] res = new int[2];
    for (int num : nums) {
        if (nums[Math.abs(num) - 1] < 0) {
            // 重复数
            res[0] = Math.abs(num);
        } else {
            // 第一次访问元素，置为0
            nums[Math.abs(num) - 1] *= -1;
        }
    }

    // 遍历数组，找到数组中正值元素所在位置，即为缺失数
    for (int i = 0; i < nums.length; i++) {
        if (nums[i] > 0) {
            res[1] = i + 1;
        }
    }

    return res;
}
```



### 短话长说

学算法，不知道从哪开始？先学什么？什么阶段该刷什么题？

按照力扣题目类别结构化排序刷题，从低阶到高阶，图解算法(更新中...)，有兴趣的童鞋，欢迎一起从小白开始零基础刷力扣，共同进步！

感兴趣搜索：囧么肥事 (或搜索：jiongmefeishi)

**公众号**回复：678，获取已分类好的部分刷题顺序，后续内容会持续更新，感兴趣的小伙伴自由拿取！

另外，有关分类，求小伙伴们不要再问我最后一类的起名了，奇技淫巧是个褒义词，意思是指新奇的技艺和作品。



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

![扫码关注](https://img-blog.csdnimg.cn/img_convert/cb3a296f8edbcc70370d4eb569c40634.png)

