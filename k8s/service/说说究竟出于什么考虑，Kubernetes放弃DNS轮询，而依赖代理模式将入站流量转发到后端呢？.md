###  说说究竟出于什么考虑，Kubernetes放弃DNS轮询，而依赖代理模式将入站流量转发到后端呢？

> 面试官："说说究竟出于什么考虑，Kubernetes放弃DNS轮询，而依赖代理模式将入站流量转发到后端呢？"
>
> 面试官："简单描述一下什么是service？"
>
> 面试官："k8s为什么需要服务机制呢？理由？"
>
> 面试官："提到DNS轮询就不得不说DNS缓存了，那什么是DNS缓存呢?"

![img](https://img-blog.csdnimg.cn/img_convert/1e6eb5329506cac17e548ace1c606dfa.png)



### 囧么肥事-胡说八道



![img](https://img-blog.csdnimg.cn/img_convert/df1576c00d4e1bd94c74e9df2e445e2f.png)



![img](https://img-blog.csdnimg.cn/img_convert/3f59250a69d7add4d0af9f54a6a1d673.png)



### 面试官："简单描述一下什么是service？"

k8s定义服务是一种**将运行在一组 Pods 上的应用程序公开为网络服务**的抽象方法。Kubernetes 为 Pods 提供自己的 IP 地址，并**为一组 Pod 提供相同的 DNS 名**， 并且可以在它们之间进行负载均衡。而且使用 Kubernetes，你**无需修改应用程序**，即可使用不熟悉的服务发现机制，黑盒式使用，简单易上手。

![img](https://img-blog.csdnimg.cn/img_convert/799dceaa42fe931f821d3657067aee14.gif)



### 面试官："k8s为什么需要服务机制呢？理由？"

k8s需要服务的动机是：Pod 是**非永久性资源，面临各种适应性创建和销毁**，服务机制的**目标**是，不管k8s集群因为什么情况，导致的Pods为了适配集群环境状态，而进行创建和销毁 Pod 后，k8s 依然能正确的匹配集群状态。

以 Deployment 工作负载为例，它可以动态创建和销毁 Pod ，k8s 设计架构决定了**每个 Pod 都有自己的 IP 地址**，在 Deployment 中，在**某一时刻**运行的 Pod 集合可能与**稍后**运行该应用程序的 Pod 集合不同。

> 这么说吧，9点10分提供服务的 Pod 集合可能到9点20分已经全部都被替换成新的 Pod 集合了
>
> 给你提供理发服务的技师，虽然都能给你修剪一个漂漂亮亮的发型，但是提供服务的技师可能今天是马克波波，明天也许就是麦克菠菠了

这导致了一个问题：如果一组 Pod（称为“后端”）为集群内的其他 Pod（称为“前端”）提供功能， 那么前端如何找出并跟踪要连接的 IP 地址，**以便前端可以使用提供工作负载的后端部分**？

![img](https://img-blog.csdnimg.cn/img_convert/609536fc304a7f92561ff0cd8c23f67c.gif)



举个例子，前端需要调用一个图片压缩的后端服务，这个图片压缩的后端服务假设叫 ImageResizeServer，它运行了 3 个副本，这些副本是可互换的。然而组成这一组后端程序的 Pod 实际上可能会发生变化，对于前端来说，前端不需要关心它们调用了哪个后端副本，前端客户端**不应该也没必要知道**，而且也不需要跟踪这一组后端的状态。你随意，爱咋咋地，只要最终给我处理好了就行。Service 定义的抽象能够解耦"前端"和"后端"服务关联，Service在其中起到了桥梁作用。

![img](https://img-blog.csdnimg.cn/img_convert/d66557ff5d3917701f2e871eb3d96676.gif)





### 面试官："为什么Kubernetes放弃DNS轮询，而依赖代理模式将入站流量转发到后端呢？"

前面简单介绍了什么是Service，以及它在 k8s中的作用，维持 k8s 集群Pod匹配稳态。

下面进入正题😂😂😂

```
为什么不使用 DNS 轮询？
```



很多人面试时不时就会有人被问到为什么 Kubernetes 依赖代理将入站流量转发到后端，而不是其他方法呢？

> 例如，是否可以配置具有多个 A 值（或 IPv6 为 AAAA）的 DNS 记录，并依靠轮询名称解析？

![img](https://img-blog.csdnimg.cn/img_convert/c04641694a5159218ee9a5634957be36.gif)



**提到DNS轮询就不得不说DNS缓存了，那什么是DNS缓存呢?**

DNS缓存指DNS返回了正确的IP之后，系统就会将这个**结果临时储存起**来。并且它会为缓存设定一个**失效时间** (例如N小时)，在这N小时之内，当你再次访问这个网站时，系统就会直接从你电脑本地的DNS缓存中把结果交还给你，而不必再去询问DNS服务器，**变相“加速”了网址的解析**。在超过N小时之后，系统会自动再次去询问DNS服务器获得新的结果。所以，当你修改了 DNS 服务器，并且不希望电脑继续使用之前的DNS缓存时，就需要手动去清除本地的缓存了。

**k8s使用服务代理主要几个原因**：

- DNS 不遵守记录 TTL，在**TTL值到期后，依然对结果进行缓存**。
- 有些应用程序仅执行一次 DNS 查找，但是却会**无限期地缓存结果**。
- 即使应用和库进行了适当的重新解析，**DNS 记录上的 TTL 值低或为零也可能会给 DNS 带来高负载**，从而使管理变得困难。

> 需要注意的是，在 Kubernetes 集群中，每个 Node 运行一个 `kube-proxy` 进程。而`kube-proxy` 负责为 Service 实现了一种 VIP（虚拟 IP）的形式，而不是 ExternalName 的形式。

![img](https://img-blog.csdnimg.cn/img_convert/098e33a5ee28010a163ae4cfed6fcff8.png)


k8s系列所有问题更新记录：[GitHub](https://gitee.com/jiongmefeishi/JMFS-Interview-Notebook-Kubernetes) , [Gitee](https://gitee.com/jiongmefeishi/JMFS-Interview-Notebook-Kubernetes)

