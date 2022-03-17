### k8s集群线上某些特殊情况强制删除 StatefulSet 的 Pod 隐患考虑？

```
考点之什么情况下，需要强制删除 StatefulSet 的 Pod?
```

```
考点之如果 StatefulSet 操作不当可能会引发什么很严重的后果？
```

```
考点之如果遇到Pod 长时间处于 'Terminating' 或者 'Unknown' 状态情况，有什么安全一些的处理手段吗？
```

![img](https://img-blog.csdnimg.cn/img_convert/18d2a7f0e4c1a141655503d9cb8fb609.gif)



### 囧么肥事-胡说八道

![img](https://img-blog.csdnimg.cn/img_convert/34db4c8167452b8bb065036210051a6c.png)

![img](https://img-blog.csdnimg.cn/img_convert/0e5a8c6dd696f71984d4caa746c9030d.png)



### 线上某些特殊情况下可能需要强制删除 StatefulSet 的 Pod？

**什么情况下，需要强制删除 StatefulSet 的 Pod?**

**正常情况下**

`StatefulSet` 常规场景下，不需要强制删除 `StatefulSet` 管理的 Pod。 

`StatefulSet` 控制器会负责创建、 扩缩和删除 `StatefulSet` 管理的 Pods。

它尝试确保指定数量的从序数 `0 到 N-1 的 Pod` 处于活跃状态并准备就绪。

`StatefulSet` 遵循`At Most One`（最多一个）规则，确保在任何时候，集群中**最多只有一个**具有给定标识的 Pod。

![img](https://img-blog.csdnimg.cn/img_convert/8346404210a328cc09ae33a1af6ecf1b.gif)



**特殊情况下**

所谓特殊情况下必须进行强制删除，SS感知到当某个**节点不可达**时，**不会引发自动删除 Pod**。在无法访问的节点上运行的 Pod 在超时 后会进入'`Terminating`' 或者 '`Unknown`' 状态，另外当用户尝试体面地删除无法访问的节点上的 Pod 时 Pod 也可能会进入这些状态。

如果你发现 StatefulSet 的某些 Pod 长时间处于 '`Terminating`' 或者 '`Unknown`' 状态

无法自己完成正常的调度，为了k8s集群的稳定服务，这个时候可能需要**手动干预**，以**强制的手段从 API 服务器中删除这些 Pod**。

### 如果StatefulSet 操作不当可能会引发什么很严重的后果？

 应谨慎进行**手动强制删除**操作，因为它可能会**违反 StatefulSet 固有的至多一个的规则**。

`StatefulSets` 用于运行**分布式和集群级**的应用，这些应用需要**稳定的网络标识和可靠的存储**。 

这些应用通常配置为具有**固定标识固定数量**的成员集合，每个Pod都是**唯一的，独立的**，你可以理解为每个人的身份证编号都是唯一的。

具有相同身份的多个成员（Pod）可能是灾难性的，**可能导致数据丢失** (例如：票选系统中的脑裂场景)。

而强制删除，可能就会导致SS出现多个Pod使用同一张身份证。

**违反了”每人一证“原则。**

![img](https://img-blog.csdnimg.cn/img_convert/763ee3f7a736c19940416b4be0feffa7.png)



**问题来了，为什么就会出现多个相同标识的Pod呢？**

原来，不同于Pod体面终止的是，在进行强制删除过程中，API 服务器**不会**等待来自 kubelet 对 Pod **已终止的确认消息**，它会**立即从 API 服务器中释放该名字**。

我们知道`StatefulSet` 中每个Pod有固定标识，而且不随着Pod的重新调度而改变。

在进行重新调度的时候，新调度创建的Pod会继承上一个旧Pod的一切有用资源，比如PV，唯一标识，网络标识等。

强制删除，直接从API服务器移除Pod对象，这个时候，`StatefulSet` 控制器有机会去创建一个**具有相同标识的替身 Pod**，并且去继承旧Pod的资源。

尚未完全删除Pod，如果创建了替身，那么此时和替身共享一个唯一标识，**违反 StatefulSet 固有的至多一个的规则**。

这是后果，主要的还是它的附带后果。

是什么呢？最绝的来了，**尚未完全删除的 Pod 仍然可以与 StatefulSet 的成员通信**，也就是说它仍然可以操作PV，可能导致PV数据流失。

![img](https://img-blog.csdnimg.cn/img_convert/5cb5135532aa3f00e16c46af3e6315e2.gif)



### 如果遇到Pod 长时间处于 '`Terminating`' 或者 '`Unknown`' 状态情况，有什么安全一些的处理手段吗？

**安全处理？**

既然知道了问题产生的原因，有什么安全一些的处理手段吗？

如果遇到Pod 长时间处于 '`Terminating`' 或者 '`Unknown`' 状态情况，再进行强制删除之前可以先考虑以下处理方式：

**第一种情况**，如果确认节点已经不可用了 (比如，永久断开网络、断电等)， 可以**主动删除掉点节点对象**，或者通过节点控制器来进行删除。

**第二种情况**，如果节点遇到网裂问题，请**尝试解决该问题或者等待其解决**。 当网裂愈合时，`kubelet` 将完成 Pod 的删除并从 API 服务器上释放其名字。

**第三种情况**，必须强制，无可选择。⚠️当你确定必须执行强制删除 `StatefulSet` 类型的 Pod 时，你要确保有问题的 Pod **不会再和 StatefulSet 管理的其他 Pod 通信**并且可以**安全地释放其名字**以便创建替代 Pod。

![img](https://img-blog.csdnimg.cn/img_convert/f7ad05479c60791d24254a4199d6bd08.gif)



Kubernetes 推荐学习书

> Kubernetes权威指南PDF
> 链接: https://pan.baidu.com/s/11huLHJkCeIPZqSyLEoUEmQ 提取码:sa88

k8s系列所有问题更新记录：[GitHub](https://gitee.com/jiongmefeishi/JMFS-Interview-Notebook-Kubernetes)

