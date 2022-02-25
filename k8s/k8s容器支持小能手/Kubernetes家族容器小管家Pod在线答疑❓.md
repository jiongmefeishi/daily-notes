###  Kubernetes家族容器小管家Pod在线答疑❓

不知道学习k8s的小伙伴们有没有跟我一样的疑问？

k8s为什么不是直接运行容器，而是让Pod介入？

Pod又是什么？为什么在应用容器化如此普遍的情况下k8s要推出Pod概念？

![img](https://img-blog.csdnimg.cn/img_convert/6a19020d9136139d60bc72f3ff48719b.gif)


### 小葵花Pod课堂开课啦

本文主要理解的一个核心点，什么是Pod？我们先不关注Pod怎么使用，怎么调度，如何实现最佳实践。这些问题后续继续讨论，在不懂为什么k8s要有Pod的情况下，去先深究最佳实践没有实际意义。

### 囧么肥事-k8s专场

![img](https://img-blog.csdnimg.cn/img_convert/2f62e468a79eb225c015de49eabd1a41.png)


![img](https://img-blog.csdnimg.cn/img_convert/af3993f7bf19b7fa272ea1b9476b18aa.png)


### Pod官方定义

*Pod* 是可以在 Kubernetes 中创建和管理的、最小的可部署的计算单元。

> *Pod* （就像在鲸鱼荚或者豌豆荚中）是一组（一个或多个） 容器
>
> 这些容器共享存储、网络、以及怎样运行这些容器的声明。
>
> Pod 中的内容总是并置（colocated）的并且一同调度，在共享的上下文中运行。
>
> Pod 所建模的是特定于应用的“逻辑主机”，
>
> 其中包含一个或多个应用容器，
>
> 这些容器是相对紧密的耦合在一起的。

### Linux命名空间、cgroups

***\*namespace\****

Linux **namespace** 提供了一种**内核级别隔离系统资源**的方法，通过将系统的**全局资源**放在**不同的namespace**中来实现资源隔离的目的.Linux 中**六类系统资源的隔离机制**：

● Mount: 隔离**文件系统挂载点**

● UTS: 隔离**主机名和域名信息**

● IPC: 隔离**进程间通讯**

● PID: 隔离**进程的 ID**

● NetWork: 隔离**网络资源**

● User: 隔离**用户和用户组的 ID**

**cgroups**

**cgroups** 限制一个进程组**能够使用的资源上限**，包括 `CPU`,内存,磁盘,网络带宽等，同时可以设置进程优先级,以及将进程挂起和恢复等。

### docker 角度理解Pod

Pod 的共享上下文包括一组 Linux 名字空间、控制组（cgroup）和可能一些其他的隔离 方面，即用来隔离 Docker 容器的技术。 在 Pod 的上下文中，每个独立的应用可能会进一步实施隔离。

就 Docker 概念的术语而言，Pod 类似于共享名字空间和文件系统卷的一组 Docker 容器。

### Pod 中多个容器如何协调？

Pod 被设计成支持形成**内聚服务单元的多个协作过程**（形式为容器）。

Pod 中的容器被自动安排到集群中的**同一物理机或虚拟机上**，并可以**一起进行调度**。

容器之间可以**共享资源和依赖、彼此通信、协调何时以及何种方式终止自身**。

例如，你可能有一个容器，为共享卷中的文件提供 Web 服务器支持，以及一个单独的 “sidecar（挂斗）”容器负责从远端更新这些文件，如下图所示：

![img](https://img-blog.csdnimg.cn/img_convert/38f2b8a188fb725856b0dceddfee7ec3.png)

### 如何理解Pod共享上下文？

一个Pod的共享上下文是**Linux命名空间、cgroups和其它潜在隔离内容的集合**。 在Pod中，容器共享一个IP地址和端口空间，它们可以通过localhost发现彼此。

在**同一个Pod**中的容器，可以使用**System V 或POSIX信号**进行标准的**进程间通信和共享内存**。

在**不同Pod**中的容器，拥有**不同的IP地址**，因此不能够直接在进程间进行通信。容器间通常**使用Pod IP地址进行通信**。

Pod 的上下文可以理解成多个linux命名空间的联合：

● PID 命名空间（同一个Pod中应用可以看到其它进程）

● 网络 命名空间（同一个Pod的中的应用对相同的IP地址和端口有权限）

● IPC 命名空间（同一个Pod中的应用可以通过VPC或者POSIX进行通信）

● UTS 命名空间（同一个Pod中的应用共享一个主机名称）



![img](https://img-blog.csdnimg.cn/img_convert/670745a33b7038ecfc1ae06e97382d80.gif)

 **《k8s-企业级容器应用托管》- 持续更新**

第一段：**认识k8s是什么？**推荐阅读：[《云原生新时代弄潮儿k8s凭什么在容器化方面独树一帜？》](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484066&idx=1&sn=441fcae466eb5b5fba2fa29f007d7c07&chksm=cf31eb74f8466262ccc258fe1d21fbd8d65e73221c211b704d216d5116a15ffcc4f4cacf5b31#rd)

第二段：**认识k8s家庭成员？**推荐阅读：[《趁着同事玩游戏偷偷认识k8s一家子补补课》](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484077&idx=1&sn=2ba024c0e121f7ac83e7264bdf7b4dff&chksm=cf31eb7bf846626d02c59837a2f903ed848d8e0f117c80af16b364e858005c57849f0bb82e47#rd)

第三段：**认识k8s容器小管家Pod？**推荐阅读：[《Kubernetes家族容器小管家Pod在线答疑❓》](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484110&idx=1&sn=cae2e84fb16b9fe5d8a7727c20009b3b&chksm=cf31eb18f846620e3dd1b7b8b9008fd5960363bc6bd3de679225ea5e45f9a48e93d210ccd572#rd)

第四段：待更新？推荐休闲阅读：[《囧么肥事》](https://mp.weixin.qq.com/mp/appmsgalbum?__biz=Mzg3NjU0NDE4NQ==&action=getalbum&album_id=2218140423993212933#wechat_redirect)