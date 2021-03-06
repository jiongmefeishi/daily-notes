![关注我](https://img-blog.csdnimg.cn/img_convert/f624544138ac5e8f9d6851002393486d.png)

### 《力扣算法训练提升》数组篇-打卡数组遍历-力扣【628】三个数的最大乘积

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

### [力扣【628】三个数的最大乘积](https://leetcode-cn.com/problems/maximum-product-of-three-numbers/)

给你一个整型数组 `nums` ，在数组中找出由三个数组成的最大乘积，并输出这个乘积。

**具体描述**

![【628】三个数的最大乘积](https://img-blog.csdnimg.cn/img_convert/f66b16ff128036d90186946e21853968.png)

### 解题思路

本题`类似【414.第三大的数】`，414求解的是数组中第三大的数，我们可以使用相同的思想。

本题求解：数组中三个数的最大乘积。

假设数组是有序，那么最大乘积出现的`可能性`：

1、如果元素都是非负数或者都是非正数

```
最大乘积=最大的三个数的乘积
```

2、如果元素有正有负

```
最大乘积既可能是三个最大正数的乘积

也可能是两个最小负数（即绝对值最大）与最大正数的乘积
```

所以，为了求出三个数的最大乘积，我们需要得到数组中三个最大正数的乘积和两个最小负数与最大正数的乘积。

比较之下选择最大值。

**目标**

1、得到数组中的最大值，第二大值，第三大值

2、得到数组中最小值，次小值

**具体求第一，第二，第三大值过程**

定义三个临时变量，分别保存最大值，第二大值，第三大值；

分别为first, second, third；

遍历数组，循环数组，依次更新，每次**更新顺序**为 third，second，first；

```
若a[i] > first，则 first=a[i]，同时更新second，third；

若a[i] > second，则 second=a[i]，同时更新third；

若a[i] > third，则 third=a[i]
```

最后得到的first，second，third就是数组中的最大值，第二大值，第三大值；

同理，得到数组中最小值 minOne，次小值 minTow

**复杂度分析**

```
时间复杂度：O(n)，其中 n 是数组的长度。需要遍历数组一次。

空间复杂度：O(1)
```

### 动画模拟

![【628】三个数的最大乘积](https://img-blog.csdnimg.cn/img_convert/dc4c9f461699ccd52116254d01b98f13.gif)

### 示例

```
public static int maximumProduct(int[] nums) {

    // 定义三个临时变量，分别保存最大值，第二大值，第三大值
    int first = Integer.MIN_VALUE;
    int second = Integer.MIN_VALUE;
    int third = Integer.MIN_VALUE;

    int minOne = Integer.MAX_VALUE;
    int minTow = Integer.MAX_VALUE;

    for (int num : nums) {

        // 循环数组，依次更新，每次更新顺序为 third，second，first
        // 找到最大三个数
        if (num > first) {
            third = second;
            second = first;
            first = num;
        } else if (num > second) {
            third = second;
            second = num;
        } else if (num > third) {
            third = num;
        }

        // 找到最小两个数
        if (num < minOne) {
            minTow = minOne;
            minOne = num;
        } else if (num < minTow) {
            minTow = num;
        }

    }

    return Math.max(minOne * minTow * first, first * second * third);
}
```



关注我看动画，解算法

欢迎微信搜索：囧么肥事 (或搜索：jiongmefeishi)

![囧么肥事](https://img-blog.csdnimg.cn/img_convert/cb3a296f8edbcc70370d4eb569c40634.png)