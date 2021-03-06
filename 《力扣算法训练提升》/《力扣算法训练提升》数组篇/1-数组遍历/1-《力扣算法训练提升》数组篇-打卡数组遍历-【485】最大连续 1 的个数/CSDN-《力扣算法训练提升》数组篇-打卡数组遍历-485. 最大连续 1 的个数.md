![搜一搜](https://img-blog.csdnimg.cn/img_convert/f624544138ac5e8f9d6851002393486d.png)

### 《力扣算法训练提升》数组篇-打卡数组遍历-485. 最大连续 1 的个数

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

### [485. 最大连续 1 的个数](https://leetcode-cn.com/problems/max-consecutive-ones/)

![最大连续1个数](https://img-blog.csdnimg.cn/img_convert/3153b523950b4f3060db034bb16f3bed.png)

### 解题思路一：计数

遍历数组，并使用**计数器**记录最大的连续 1 的个数maxCount和当前的连续 1 的个数curCount。

如果当前元素是0，更新之前记录的连续 1 的最大个数maxCount，并将当前的连续 1 的个数curCount置为0。

因为数组的最后一个元素可能是 1，且最长连续 1 的子数组可能出现在数组的末尾，所以遍历数组结束之后，需要再次使用当前的连续 1 的个数curCount来更新最大的连续 1 的个数maxCount。

**复杂度分析**

```
时间复杂度：O(n)，其中 n 是数组的长度。需要遍历数组一次。

空间复杂度：O(1)
```

**动画模拟**

![[485]最大连续1的个数-标记法](https://img-blog.csdnimg.cn/img_convert/904a13b2b4957a675b6dd26859e4a000.gif)

**示例**

```
public static int findMaxConsecutiveOnes(int[] nums) {

    // 记录最大个数
    int maxCount = 0;
    // 记录连续1个数
    int count = 0;

    for (int i = 0; i < nums.length; i++) {
        if (nums[i] == 0) {
            // 取最大连续数
            maxCount = Math.max(maxCount, count);
            // 重置连续个数
            count = 0;
        } else {
            count++;
        }
    }
    maxCount = Math.max(maxCount, count);
    return maxCount;
}
```

### 解题思路二：滑动窗口

1、规定窗口左右边界left， right，最大连续个数maxCount;

2、右边界遇1扩张，right++；

3、右边界遇0，记录窗口大小，即**当前窗口最大连续个数**。**更新**最大连续个数max(maxCount, right - left)；

4、右边界扩张right++，更新左边界left = right；

**复杂度分析**

```
时间复杂度：O(n)，其中 n 是数组的长度。需要遍历数组一次。

空间复杂度：O(1)
```

**动画模拟**

![[485]最大连续1的个数-滑动窗口](https://img-blog.csdnimg.cn/img_convert/65e7d8878a441b78e82743392bf8c656.gif)

**示例**

```
public static int findMaxConsecutiveOnes(int[] nums) {

    int left  = 0;
    int right = 0;
    int maxCount = 0;
    // 循环窗口关闭条件：右侧窗口抵达数组边界
    while (right < nums.length){

        if (nums[right] == 1) {
            // 如果元素是1，窗口扩增
            right++;
            continue;
        } else {
            // 如果元素是0，记录窗口大小，即当前窗口最大连续个数
            maxCount = Math.max(maxCount, right - left);
            right++;
            // 重置左窗口起点
            left = right;
        }
    }

    maxCount = Math.max(maxCount, right - left);
    return maxCount;
}
```

**关注我了解更多精彩内容**

**欢迎微信搜索：**囧么肥事 (或搜索：jiongmefeishi)

![关注我](https://img-blog.csdnimg.cn/img_convert/cb3a296f8edbcc70370d4eb569c40634.png)
