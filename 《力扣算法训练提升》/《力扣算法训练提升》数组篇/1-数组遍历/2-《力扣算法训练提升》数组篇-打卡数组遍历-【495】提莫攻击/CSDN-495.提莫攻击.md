![搜一搜](https://img-blog.csdnimg.cn/img_convert/f624544138ac5e8f9d6851002393486d.png)

### 《力扣算法训练提升》数组篇-打卡数组遍历-495.提莫攻击

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

### [力扣495. 提莫攻击](https://leetcode-cn.com/problems/teemo-attacking/)

在《英雄联盟》的世界中，有一个叫 “提莫” 的英雄，他的攻击可以让敌方英雄艾希（编者注：寒冰射手）进入中毒状态。现在，给出提莫对艾希的攻击时间序列和提莫攻击的中毒持续时间，你需要输出艾希的中毒状态总时长。

你可以认为提莫在给定的时间点进行攻击，并立即使艾希处于中毒状态。

**具体描述**

![提莫攻击1](https://img-blog.csdnimg.cn/img_convert/1a7b54f656699819033bb907900f3a8e.png)

![提莫攻击2](https://img-blog.csdnimg.cn/img_convert/40fca9ee931fe4ae54308a2487effd0f.png)



### 解题思路：计算下一次中毒持续时间

将全程进攻过程看做一条完整的时间段，计算下一次中毒持续时间有**两种可能情况**

第一种情况：**相互时间段不重合**

```
中毒开始时间  >=  上一次中毒结束时间

那么下一次中毒持续时间为完整时间段
```

```
下一次中毒持续时间 time=duration
```

第二种情况：**部分时间段重合**

```
中毒开始时间 < 上一次中毒结束时间

那么下一次中毒持续时间为部分不重叠时间段
```

```
下一次中毒持续时间 time=arr[i+1] - arr[i]
```

**复杂度分析**

```
时间复杂度：O(n)，其中 n 是数组的长度。需要遍历数组一次。

空间复杂度：O(1)
```

**动画模拟**

第一种情况：**相互时间段不重合**

![第一种情况](https://img-blog.csdnimg.cn/img_convert/e5f83ad4c4799f3ae2bcfc6a2eb24b2f.gif)

第二种情况：**部分时间段重合**

![第二种情况](https://img-blog.csdnimg.cn/img_convert/32c25317d5972d2a73e0de88daec7f9d.gif)

**示例**

```
public static int findPoisonedDuration(int[] timeSeries, int duration) {
    if (timeSeries.length == 0) {
        return 0;
    }

    // 第一次中毒持续时间
    int durationTime = duration;
    for (int i = 0; i < timeSeries.length - 1; i++) {
        // 计算下一次中毒持续时间
        if (timeSeries[i + 1] >= timeSeries[i] + duration) {
            // 下一次中毒开始时间大于等于上一次中毒结束时间，那么下一次中毒持续时间为完整时间段
            durationTime += duration;
        } else {
            // 下一次中毒开始时间小于上一次中毒结束时间，那么下一次中毒时间需要减去公共时间段
            durationTime += timeSeries[i + 1] - timeSeries[i];
        }
    }

    return durationTime;
}
```

### 解题简化：下一次中毒持续时间

每一次考虑下一次中毒时间只有两种可能
第一种，不与上一段时间重合，自己就是完整的持续时间段，这是最大持续时间；
第二种，部分时间与上一段时间重合，需要减去重合时间段，这是较小持续时间；

**第二种情况下，算得的较小持续时间一定是  小于 第一种情况的，即 duration 的**
**所以，取两种情况的最小值，就是下一次中毒持续时间**

**示例**

```
public static int findPoisonedDuration2(int[] timeSeries, int duration) {
    if (timeSeries.length == 0) {
        return 0;
    }

    // 第一次中毒持续时间
    int durationTime = duration;
    for (int i = 0; i < timeSeries.length - 1; i++) {
        durationTime += Math.min(duration, timeSeries[i + 1] - timeSeries[i]);
    }
    return durationTime;
}
```



**关注我了解更多精彩内容**

**欢迎微信搜索：**囧么肥事 (或搜索：jiongmefeishi)

![关注我](https://img-blog.csdnimg.cn/img_convert/cb3a296f8edbcc70370d4eb569c40634.png)


