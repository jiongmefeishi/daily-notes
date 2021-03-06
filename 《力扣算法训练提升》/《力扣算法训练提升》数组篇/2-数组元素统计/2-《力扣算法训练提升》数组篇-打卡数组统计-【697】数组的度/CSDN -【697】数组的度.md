![关注我](https://img-blog.csdnimg.cn/img_convert/f624544138ac5e8f9d6851002393486d.png)

### 《力扣算法训练提升》数组篇-打卡数组统计-【697】数组的度

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

给定一个非空且只包含非负数的整数数组 nums，数组的度的定义是指数组里任一元素出现频数的最大值。

你的任务是在 nums 中找到与 nums 拥有相同大小的度的最短连续子数组，返回其长度。

**具体描述**

![算法描述](https://img-blog.csdnimg.cn/img_convert/d41f59f7e83f6ceef30aea830c7b8eaf.png)



### 解题思路：哈希结构

**数组的度：指的是数组中各元素出现次数的最大值**

例如题目中给定的：

```
[1,2,2,3,1,4,2]

1 出现 2 次， 2 出现 3 次， 3 出现 1 次， 4 出现 1 次

其中元素出现最大次数为3

所以数组的度为 3
```

**求：和原数组的度相同的最短连续子数组最短长度**

那么这个连续子数组必定以这个元素开头，以这个元素结束。

假设这个元素是 num，必然包含了原数组中的全部 num，且两端恰为 num `第一次出现位置`和`最后一次出现的位置`。

最短连续子数组必然形式

```
[num，... ，num]
```

**主要求解内容**

```
1、数组的度

2、元素第一次出现的位置

3、元素最后一次出现的位置
```

**定义三个哈希结构（哈希表）**

```
countMap：数组每个元素出现次数

leftIndex：记录数组元素第一次出现位置

rightIndex：记录数组元素最后一次出现位置
```

第一次遍历数组，记录每个元素出现次数，每个元素`第一次出现位置`，每个元素`最后一次出现位置`，已经`数组的度`。

第二次遍历哈希表



计算每个`符合数组度的元素`的最短长度

```
最短长度 = 元素最后位置 - 第一次出现位置 + 1
```



**复杂度分析**

```
时间复杂度：O(n)。遍历 nums 需要时间 O(n)，需要遍历原数组和哈希表各一次，检查每个数字需要时间 O(n)。

空间复杂度：O(n)，其中 n 是原数组的长度，最坏情况下，哈希表和原数组等大。
```

**动画模拟**

![数组的度](https://img-blog.csdnimg.cn/img_convert/8756fafbe74d2accfcbab5579c22287d.gif)

**示例**

```
public int findShortestSubArray(int[] nums) {

    // 记录最短连续子数组长度
    int min = Integer.MAX_VALUE;
    // 记录最大数组度
    int maxCount = 0;

    // 存储每个元素对应出现次数
    Map<Integer, Integer> count = new HashMap<>();
    // 存储每个元素首次出现位置
    Map<Integer, Integer> leftIndex = new HashMap<>();
    // 存储每个元素最后一次出现的位置
    Map<Integer, Integer> rightIndex = new HashMap<>();

    // 第一次遍历，遍历nums[]，记录每个元素出现次数，出现位置，以及数组的度
    for (int i = 0; i < nums.length; i++) {
        if (count.get(nums[i]) == null) {
            count.put(nums[i], 1);
            leftIndex.put(nums[i], i);
        } else {
            count.put(nums[i], count.get(nums[i]) + 1);
        }
        rightIndex.put(nums[i], i);
        maxCount = Math.max(maxCount, count.get(nums[i]));
    }

    // 第二次遍历，遍历 count 根据数组的度，计算最小连续子数组长度
    for (Integer key: count.keySet()) {
        if (count.get(key) == maxCount) {
            // 连续子数组长度=右边界-左边界+1
            min = Math.min(min, rightIndex.get(key) - leftIndex.get(key) + 1);
        }
    }
    return min;
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

