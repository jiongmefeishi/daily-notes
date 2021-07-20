### 《力扣算法训练提升》图解数组篇-打卡数组统计-【665】非递减数列

### 本篇是[@囧么肥事](https://leetcode-cn.com/u/jiongmefeishi/)短话长说的第3篇，没看错就是最不像题解的题解

### 今日打卡题目

![打卡](https://img-blog.csdnimg.cn/img_convert/02bf8c9dc8c06bad0fa95906358bd646.png)



给你一个长度为 n 的整数数组，请你判断在 最多 改变 1 个元素的情况下，该数组能否变成一个非递减数列。

我们是这样定义一个非递减数列的： 对于数组中任意的 i (0 <= i <= n-2)，总满足 nums[i] <= nums[i + 1]。



**具体描述**

![算法描述](https://img-blog.csdnimg.cn/img_convert/407ce117dc6fa356dbbce84413d376af.png)

### 解题讨论

![算法讨论](https://img-blog.csdnimg.cn/img_convert/42a40bd4a1c77d8d6f0d31e27e48bb6d.png)



**低谷填坑示例**

**对于数组构造峰谷折线图**

![示例1](https://img-blog.csdnimg.cn/img_convert/9b86e904e951c9ca129e771012cdff44.png)

**4 1 6 形成低谷，其中 1 为谷底**

![示例2](https://img-blog.csdnimg.cn/img_convert/eb1f10b213c40a06bb3ed96b03c73bc1.png)

**两端谷峰分别为 4， 6**

![示例3](https://img-blog.csdnimg.cn/img_convert/f29d768839996e7e6ea94bc4d1a6e7fc.png)

**填平谷底可选取值范围，两端峰值内闭合区域，为了方便，选取两端峰值作为可选填坑值**

![示例4](https://img-blog.csdnimg.cn/img_convert/0036028c19c1fb593ae55de08526b137.png)

**选取左端峰值作为填坑值**

![示例5](https://img-blog.csdnimg.cn/img_convert/65a2d2931d8169689d5826f82051cd9c.png)

**选取右端峰值作为填坑值**

![示例6](https://img-blog.csdnimg.cn/img_convert/89e27af05e1459657f34bd606893aa94.png)



**讨论归纳**


```
遍历数组，遇到低谷区域，尝试取两端峰值填平低谷。

从右往左遍历，以a[i] 为开始  a[i]~a[N-1] 范围内的数都是递增序列
a[i] < a[L] ? L--
a[i] >= a[L] ? 更新 a[i] = a[L]-1, L--

从左往右遍历，以a[i] 为结束  a[0]~a[i]   范围内的数都是递增序列
a[i] < a[R] ? R++
a[i] >= a[R] ? 更新 a[i] = a[R]+1, R++
```



### 动画模拟

![动画模拟](https://img-blog.csdnimg.cn/img_convert/bdc2abcf2f9cc0cc67a69ef645f08b4b.gif)

**示例一：遍历数组，遇到低谷区域，尝试取两端峰值填平低谷**

```
public boolean checkPossibility(int[] nums) {

    // 4 2 3 4
    // 从右往左遍历，以a[i] 为开始  a[i]~a[N-1] 范围内的数都是递增序列
    // a[i] < a[L] ? L--
    // a[i] >= a[L] ? 更新 a[i] = a[L]-1, L--

    // 从左往右遍历，以a[i] 为结束  a[0]~a[i]   范围内的数都是递增序列
    // a[i] < a[R] ? R++
    // a[i] >= a[R] ? 更新 a[i] = a[R]+1, R++

	// 备份数组
    int[] copy = Arrays.copyOf(nums, nums.length);

    int L = nums.length - 1;
    int R = 0;

    int countR = 0;
    // 从左往右遍历
    for (int i = 1; i < nums.length; i++) {
        if (nums[i] < nums[R]) {
            nums[i] = nums[R];
            countR++;
        }
        R++;
    }

    nums = copy;
    int countL = 0;
    // 从右往左遍历
    for (int i = L - 1; i >= 0; i--) {
        if (nums[i] > nums[L]) {
            nums[i] = nums[L];
            countL++;
        }
        L--;
    }

    return countL <= 1 || countR <= 1;
}
```

**复杂度分析**

```
时间复杂度：O(n)。遍历数组，需要O(n)时间。
空间复杂度：O(n)。需要额外空间。
```

通过示例一可以发现，我们借用了O(n) 的数组**存储给定原始数组状态**，遍历数组的时候**更新修改**数组中的值。

实际上，我们**不需要真正的去更改数组中元素的值**，借用窗口边界，我们只需要更新边界即可！



通过两个临时变量 L, R 作为窗口的边界

遍历数组，更新 L  R 边界值

    从左遍历 以 R 为界，左边满足非递减数列
    从右遍历 以 L 为界，右边满足非递减数列
**示例二：空间优化，节省空间，只更新边界值**

```
// 省去 O(n) 空间，只更新边界
public boolean checkPossibility(int[] nums) {

    // 更新 R L 边界
    // 从左遍历 以 R 为界，左边满足非递减数列
    // 从右遍历 以 L 为界，右边满足非递减数列

    int L = nums[nums.length - 1];
    int R = nums[0];

    int countR = 0;
    // 从左往右遍历
    for (int i = 0; i < nums.length; i++) {
        if (nums[i] < R) {
            countR++;
            continue;
        }
        R = nums[i];
    }

    int countL = 0;
    // 从右往左遍历
    for (int i = nums.length - 1; i >= 0; i--) {
        if (nums[i] > L) {
            countL++;
            continue;
        }
        L = nums[i];
    }

    return countL <= 1 || countR <= 1;
}
```

**复杂度分析**

```
时间复杂度：O(n)。 遍历数组，需要O(n)时间。
空间复杂度：O(1)。需要常量级额外空间。
```

![勇敢牛牛](https://img-blog.csdnimg.cn/img_convert/df27b359ef7567c4992267f0c2e002a3.png)

实际上，读者可以发现，这里我分别遍历了两次数组，分别是上坡填坑和下坡填坑。

```
从左往右遍历，保证了一路上去都是上坡或者平坡，遇坑填坑。
上坡遇坑，我们选择低峰值作为填坑数！


从右往左遍历，保证了一路下来都是下坡或者平坡，遇坑填坑。
下坡遇坑，我们选择高峰值作为填坑数！
```

这里可以再简化为一次遍历

```
// 7 1 2 3 5
对于首位数较大，或者是 a[i] >= a[i-2] 时，我们选择高峰值置换 a[i-1]
// 本例首位数较大，置换后
// 1 1 2 3 5


否则，使用低峰值置换当前数 a[i] = a[i-1]
// 1 2 3 7 5
// 低峰值置换后
// 1 2 3 3 5
```

```
public boolean checkPossibility(int[] nums) {

    int countR = 0;
    // 从左往右遍历
    for (int i = 1; i < nums.length; i++) {
        if (nums[i] < nums[i - 1]) {
            // 7 1 2 3 5
            // 1 2 4 7 6
            if (i == 1 || nums[i] >= nums[i - 2]) {
            	//选择高峰值置换 a[i-1]
                nums[i - 1] = nums[i];
            } else {
            	//低峰值置换当前数 a[i] = a[i-1]
                nums[i] = nums[i - 1];
            }
            countR++;
        }
    }

    return countR <= 1;
}
```

![啦啦啦](https://img-blog.csdnimg.cn/img_convert/9282ea5c372a0ebb1178f49bdec84e40.gif)

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

