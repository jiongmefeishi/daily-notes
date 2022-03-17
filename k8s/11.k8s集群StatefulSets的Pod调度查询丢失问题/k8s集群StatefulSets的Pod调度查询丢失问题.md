
### k8s集群StatefulSets的Pod调度查询丢失问题?

```
考点之简单介绍下StatefulSets 和 Deployment 之间有什么本质区别？特定场景该如何做出选择呢？
```

```
考点之你能辩证的说说看StatefulSets 和 Deployment具体有哪些区别嘛？
```

```
考点之你了解k8s集群StatefulSets的Pod调度查询丢失问题吗？k8s集群中StatefulSet管理的Pod已经完成调度并启动，为什么还是无法查询Pod 的 DNS 命名？
```

![](https://img-blog.csdnimg.cn/img_convert/88b0197aca9d2317471a96e6573b1882.gif)

### 囧么肥事-胡说八道

![](https://img-blog.csdnimg.cn/img_convert/64bb6ae882304e8d317de2104bd04634.png)

![](https://img-blog.csdnimg.cn/img_convert/0bed3d46352edf5dee08633ef1982781.png)

### 简单介绍下StatefulSets 和 Deployment 之间有什么本质区别？

首先、`StatefulSet` 和`Deployment` 都是用来管理应用的工作负载 API 对象，都管理基于各自相同容器规约的一组 Pod，同时负责各自管理的Pod 集合的部署和扩缩、更新回滚等。

但和 `Deployment` **本质上不同**的是，`StatefulSets` 用来管理**有状态应用**，而`Deployment` 负责管理**无状态应用**。

如果应用程序**不需要任何稳定的**标识符或有序的部署、删除或伸缩，则应该使用 由一组**无状态的副本控制器**提供的工作负载来部署应用程序，比如 `Deployment`或者`ReplicaSet`。

如果希望使用**存储卷为工作负载提供持久存储**，可以使用 `StatefulSet` 作为解决方案的一部分。 

尽管 StatefulSet 中的单个 Pod 仍可能出现故障， 但**持久的 Pod 标识符** 可以更容易将**现有卷**与于重新调度的Pod进行绑定。



说道这里，下面简单介绍一下，应用分类：

**应用通常可以分为两大类：有状态与无状态**

**无状态应用**

```
简单理解就是没有特殊状态的服务

服务和数据分离，本身不存储数据

各个请求对于服务器来说统一无差别处理

请求可以随机发送到任意一台server上

请求自身携带了所有服务端所需要的所有参数

服务自身不存储跟请求相关的任何数据
```

**有状态应用**

```
容器数据需要持久化保持

对于有数据存储功能的服务

每个实例都需要有自己独立的持久化存储

或者指多线程类型的服务、队列

mysql数据库、kafka、zookeeper等
```

如果server是有状态的，客户端需要始终把请求发到同一台server才行，



同时，这里理解StatefulSets 两个关键点：**1、稳定的 2、有序的**

```
“稳定的”意味着 Pod 调度或重调度的整个过程是有持久性的
```

```
“有序的”意味着 Pod 调度或重调度的整个过程是需要保持顺序的
```

关于Deployment 的具体情况，可参考：[【跟k8s工作负载Deployments的缘起缘灭】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484214&idx=1&sn=7a0a593abbcd34347351bcc3ecd6785a&chksm=cf31eae0f84663f61600e14108ebd7b0db326d26d1f78ca7686e685c4de4728af80b54de3b4c#rd)

### 你能辩证的说说看StatefulSets 和 Deployment具体有哪些区别嘛？

**Deployment管理的Pod特点**

Deployment被设计用来管理**无状态服务**的pod，每个pod完全一致

- 无序性：无状态服务的多个Pod副本创建和销毁是**无序**的，可以并行创建或销毁，相互之间不必等待，除了需要遵守规约中定义的副本个数之外，没有其他制约。
- 随机性：无状态服务的多个Pod副本的名称是**随机**的，pod被重新启动调度后，它的名称与IP都会发生变化，替换为一个新的副本。
- 共享性：无状态服务的多个Pod副本**共享存储卷**。Deployment中Pod基于template定义存储卷，所有副本集共用一个存储卷。

![](https://img-blog.csdnimg.cn/img_convert/ffb00caab57e9b89a4f497c6f5ed4de3.gif)

**StatefulSets管理的Pod特点**

StatefulSets 被设计用来管理**有状态**的应用，StatefulSet 管理的 Pod **具有唯一的标识**，该标识包括顺序标识、稳定的网络标识和稳定的存储。 并且该标识和 Pod 是绑定，**不管它被调度在哪个节点上，最终都会被绑定这个唯一标识**。

- 唯一性：对于具有 N 个副本的 StatefulSet，它管理的每个 Pod 将被分配一个整数序号，**该序号在 StatefulSet 上是唯一的**。
- 顺序性：**顺序标识**、Pod 调度过程，无论是启动、销毁、更新都需要严格遵守顺序。**有序优雅的部署和缩放，有序自动的滚动更新**。
- 稳定的网络标识：Pod主机名、DNS地址不会随着Pod被重新调度而发生变化。
- 稳定的持久化存储：Pod被重新调度后，不会删除原有的PV，重新调度成功后，**继续挂载绑定原有的PV**，从而保证了数据的完整性和一致性。

### 你了解过k8s集群StatefulSets的Pod调度查询丢失问题吗？

k8s集群中`StatefulSet`管理的Pod已经完成调度并启动，为什么还是无法查询Pod 的 DNS 命名？如果需要在 Pod 调度完成之后及时发现，该怎么做？

**先看看StatefulSet是如何为每个Pod分配DNS的呢？**

`StatefulSet` 中管理的每个 Pod 会根据 **StatefulSet 的名称**和 以及为 Pod 的分配的**有序索引（序号）**，派生出它的主机名。 

组合主机名的格式为：

```
$(StatefulSet 名称)-$(序号)
```

 `StatefulSet` 使用 `Headless Services` 控制内部 Pod 的网络域。

通过`Headless Service为Pod`编号，在DNS服务器中生成带有编号的DNS记录，从而可以达到通过Pod名字定位到相应的服务

管理域的服务的格式为： 

```
$(服务名称).$(命名空间).svc.cluster.local
```

其中 `cluster.local` 是集群域。 一旦每个 Pod 创建成功，就会得到一个匹配的 DNS 子域，格式为： `$(pod 名称).$(所属服务的 DNS 域名)`，其中所属服务由 `StatefulSet` 的 `serviceName` 域来设定。

**了解完分配和组成，接下来说一下为什么会出现查询失败的情况呢？**

第一种情况，Pod尚在创建过程中，这时候查询，DNS命名还未分配成功

第二种情况，Pod已经创建成功，取决于集群域内部 DNS 的配置，可能无法查询一个刚刚启动的 Pod 的 DNS 命名。原因是k8s有个**负缓存**的概念。

**负缓存 (在 DNS 中较为常见)** 

```
之前失败的查询结果会被记录和重用至少若干秒钟

默认缓存时长为 30s

查询过程

第一次查询结果是失败
结果记录到负缓存中，标注失败

在缓存周期内查询，直接从负缓存中取，结果是失败
```

**如何及时发现创建的Pod的DNS命名？**

如果需要在 Pod 被创建之后及时发现它们，有以下选项：

- 直接查询 `Kubernetes API`（比如，利用 `watch` 机制）而不是依赖于 DNS 查询
- 缩短 `Kubernetes DNS` 驱动的**缓存时长**（修改 `CoreDNS` 的 `ConfigMap`，目前DNS缓存时长为 30 秒）



![](https://img-blog.csdnimg.cn/img_convert/420684d322028960ca7fb057873d14f8.gif)


----


> 获取更多干货，欢迎关注微信公众号：囧么肥事

Kubernetes 推荐学习书

> Kubernetes权威指南PDF
> 链接: https://pan.baidu.com/s/11huLHJkCeIPZqSyLEoUEmQ 提取码:sa88


----

![](https://img-blog.csdnimg.cn/70b8bb984a494cc983d24c03ce1820bc.png)