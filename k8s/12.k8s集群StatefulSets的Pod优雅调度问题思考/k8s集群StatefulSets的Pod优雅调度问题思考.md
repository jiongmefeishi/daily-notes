### k8s集群StatefulSets的Pod优雅调度问题思考？

```
考点之你能解释一下为什么k8s的 StatefulSets 需要VolumeClaimTemplate嘛？
```
```
考点之简单描述一下StatefulSets 对Pod的编排调度过程？
```
```
考点之针对线上StatefulSet 的Pod缩容故障无法正常缩容的情况，你能灰度分析一下嘛？
```
```
考点之聊聊什么是StatefulSet的分区滚动更新吧？什么场景需要使用分区更新？
```
```
考点之StatefulSet提供优雅稳定的存储，但是线上告警StatefulSet Pod重新调度后数据丢失？
```



![](https://img-blog.csdnimg.cn/img_convert/d660a4b491b226f527b7e86cacaeea8e.gif)

### 囧么肥事-胡说八道

![](https://img-blog.csdnimg.cn/img_convert/2703a664ae7c0bcb1909a80e5150a654.png)

![](https://img-blog.csdnimg.cn/img_convert/012a99abe16dba0987d1bb850ec8b4b1.png)

![](https://img-blog.csdnimg.cn/img_convert/7243248884e6177a1af7193e167c1d6a.png)

### 你能解释一下为什么k8s的 StatefulSets 需要VolumeClaimTemplate嘛？

对于k8s集群来说有状态的副本集都会用到**持久存储**。

`Deployment`中的`Pod template`里定义的存储卷，是**基于模板配置调度**，**所有副本集共用一个存储卷**，数据是相同的。

`StatefulSet`职责是管理**有状态应用**，所以它管理的每个`Pod`都要自已的**专有存储卷**，它的存储卷就不能再用Pod模板来创建。

所以 `StatefulSets` 需要一种新方式来为管辖的`Pod`分配存储卷。

就这样`VolumeClaimTemplate`来了，`k8s` 给 `StatefulSets` 设置了`VolumeClaimTemplate`，也就是**卷申请模板**。

**说了为什么需要它，那么VCT到底是什么呢？**

`VolumeClaimTemplate`：基于**静态或动态**地PV供给方式为Pod资源提供**专有且固定的存储**，它会为每个Pod都生成不同的`PVC`，并且绑定PV，实现每个Pod都有自己独立专用的存储卷。



### 简单描述一下StatefulSets 对Pod的编排调度过程？

StatefulSets 提供了**有序且优雅的部署和扩缩保证**。

SS是如何优雅部署和扩缩的呢？

对于包含 N 个 副本的 StatefulSet

```
当部署 Pod 时，它们是依次创建的，顺序为 `0..N-1`。

当删除 Pod 时，它们是逆序终止的，顺序为 `N-1..0`。

在将缩放操作应用到 Pod 之前，它前面的所有 Pod 必须是 Running 和 Ready 状态。

在 Pod 终止之前，所有的继任者必须完全关闭
```

创建或扩容过程，以Nginx举例

```
定义副本数replicas=3

SS会创建3个Pod
分配有序序号
ng-0, ng-1, ng-2

SS严格执行部署或调度顺序，按序部署
ng-0 开始部署...
ng-0 进入Running 和 Ready 状态

SS 检测 ng-0 部署状态
确定ng-0，符合Running 和 Ready 状态

ng-1 开始部署
ng-1 进入Running 和 Ready 状态

SS 检测 ng-0 和 ng-1 部署状态
确定ng-0 和 ng-1 都符合Running 和 Ready 状态
才会执行 ng-2 部署

假设此时 ng-0 发生故障
那么ng-2 会阻塞，等待 ng-0 重新部署完成

ng-2 开始部署
ng-2 进入Running 和 Ready 状态
```

类似，`StatefulSet` 进行缩容跟扩容整体规则是一样的，只不过缩容时，终止顺序和创建顺序**相反**。

按照 `ng-2, ng-1, ng-0` 的顺序进行缩容操作。`ng-2`没有完全停止和删除前，ng-1不会进行终止操作。

**注意：如果SS在缩容过程中，有些Pod发生了故障，那么终止会进入阻塞，等待发生故障的Pod重新调度，进入Running和Ready状态之后才会继续执行SS缩容。**



### 针对线上StatefulSet 的Pod缩容故障无法正常缩容的情况，你能灰度分析一下嘛？

> 为什么缩容无法正常执行？

`StatefulSet` 执行缩容操作，**需要保证管辖范围内的Pod处于健康状态**。如果某些Pod**发生故障，则缩容会陷入阻塞**，无法继续执行。

仅当 `StatefulSet` 等待到所有 Pod 都处于运`Running和 Ready` 状态后才可继续进行缩容操作。



**了解完为什么缩容无法执行，那么再聊聊可能导致无法正常缩容原因都有哪些？**

如果 `spec.replicas` 大于 1，Pod副本数量大于1 ，`Kubernetes` 无法直接判定 Pod 不健康的原因。

Pod 不健康可能是由于**永久性故障造成也可能是瞬态故障**。 

> 永久性故障

如果该 Pod 不健康是由于永久性故障导致，则在不纠正该故障的情况下进行缩容可能会导致 ``StatefulSet`` **成员 Pod 数量低于应正常运行的副本数**。这种状态也许会导致 StatefulSet 不可用。

> 瞬态故障

瞬态故障可能是**节点升级或维护而引起的节点重启**造成的。

如果由于瞬态故障而导致 Pod 不健康，一般情况下，Pod **最终会再次变为可用**，但是瞬态错误也可能会干扰 你对 `StatefulSet` 的扩容/缩容操作。 

一些分布式数据库在同时有节点加入和离开时会遇到问题。

在这些情况下，最好是在**应用级别进行分析扩缩操作的状态**，并且只有在确保 `Stateful` 应用的集群是完全健康时才执行扩缩操作。

![](https://img-blog.csdnimg.cn/img_convert/96bdbea803ccc15c47ec4b1c77527d7d.gif)

### 聊聊什么是StatefulSet的分区滚动更新吧？什么场景可以使用分区更新？什么情况分区更新会失效？

**先说一下StatefulSet的更新策略**

`StatefulSet` 的 `.spec.updateStrategy` 字段可以配置和禁用掉自动滚动更新 Pod 的容器、标签、资源请求或限制、以及注解。

`spec.updateStrategy` 有两个允许的值：`RollingUpdate`和`OnDelete`

`RollingUpdate` 更新策略

```
对 StatefulSet 中的 Pod 执行自动的滚动更新。这是默认的更新策略
```

`OnDelete`更新策略

```
StatefulSet 将不会自动更新 StatefulSet 中的 Pod

当StatefulSet 的 .spec.template 设置出现变动
用户必须手动删除 Pod 以便让控制器创建新的 Pod
```

**滚动更新**

当 `StatefulSet` 的 `.spec.updateStrategy.type` 被设置为 `RollingUpdate` 时， 属于默认滚动更新策略，这个时候如果template发生变化，StatefulSet 控制器会自动发起调度，进行删除和重建 `StatefulSet` 中的每个 Pod。 它将按照与 Pod 终止相同的顺序（从最大序号到最小序号）进行，每次更新一个 Pod。

Kubernetes 控制面会等到被更新的 Pod 进入 `Running 和 Ready` 状态，然后再更新其前身Pod。 

如果你设置了 `.spec.minReadySeconds`（最短就绪秒数），控制面在 Pod 就绪后会额外等待一定的时间再执行下一步。

**接下来进入主题什么是分区滚动更新？**

分区滚动更新是滚动更新策略中的一个特殊场景，StatefulSet 控制一定范围内的Pod进行滚动更新，调度为新版本Pod运行，而范围外的Pod继续维持老版本运行。

可以理解为，学校16个班级，校长通知说："今天最后5个班级留下来打扫卫生"

通过声明 `.spec.updateStrategy.rollingUpdate.partition` 的方式，`RollingUpdate` 更新策略可以实现分区。 

如果声明了一个分区，当 StatefulSet 的 `.spec.template` 被更新时

```
所有序号大于等于该分区序号的 Pod 都会被更新
```

```
所有序号小于该分区序号的 Pod 都不会被更新
```

分区更新，就是进行**分段处理**。

```
假设原来有5个Pod
ng-0
ng-1
ng-2
ng-3
ng-4

SS滚动更新
ng-4 更新
ng-3 更新
ng-2 更新
ng-1 更新
ng-0 更新

如果指定 partition=2
那么SS执行滚动更新时
ng-4 更新
ng-3 更新
ng-2 更新
ng-1 不更新
ng-0 不更新
```

需要注意的是，**分区范围外的Pod**，即使他们被删除或是重新调度，也会依据**之前的旧版本进行重建**，不会依赖当前最新版本重建。

此外，如果 StatefulSet 的 `.spec.updateStrategy.rollingUpdate.partition` 大于它的 `.spec.replicas`，对它的 `.spec.template` 的更新将不会传递到它的 Pod，此时所谓分区更新将失去意义。

**分区更新应用场景？**

在大多数情况下，你不需要使用分区，但如果你希望进行**阶段式更新、执行金丝雀或执行分阶段上线**，则分区更新会非常有用。



### StatefulSet提供优雅稳定的存储，但是线上告警StatefulSet Pod重新调度后数据丢失？

究竟是什么情况呢？

我们都知道k8s中当 `StatefulSet` 或者它管理的 Pod 被删除时并**不会删除关联的卷**，当重新调度完成后，新Pod应该会**挂载原PV**，继续使用上一个Pod的数据。

坏事来了，本应该继续使用原PV，皆大欢喜，可是线上告警发现PV 持久卷无法使用，导致数据丢失。咦，失联了？？？

k8s删除 `StatefulSet` 管理的 Pod 并不会删除关联的PV卷，这是为了确保你有机会重新调度Pod之后继续使用原PV卷，或者在删除卷之前从卷中复制数据，保证数据不会丢失。当一个 Pod 被调度（重新调度）到节点上时，它的 `volumeMounts` 会挂载与其 PVC相关联的 PV。

删除StatefulSet 和Pod虽然不会删除关联的PV卷，但是删除PVC就不一定了，问题就出现在这里，在 Pod 离开终止状态后删除 PVC ，**可能会触发删除背后的 PV 持久卷**，具体触发策略要取决配置的存储类和回收策略。

> 警告：⚠️ 永远不要假定在 PVC 删除后仍然能够访问卷
>
> 警告：⚠️ 删除 PVC 时要谨慎，因为这可能会导致数据丢失

![](https://img-blog.csdnimg.cn/img_convert/4d17e54f5bfc4009c5370acb2e7eb078.gif)

> 获取更多干货（MySQL、K8S），欢迎关注微信公众号：囧么肥事

-----

**Kubernetes 推荐学习书**

> Kubernetes权威指南PDF
> 链接:https://pan.baidu.com/s/11huLHJkCeIPZqSyLEoUEmQ 提取码:sa88



k8s系列所有问题更新记录：[GitHub](https://gitee.com/jiongmefeishi/JMFS-Interview-Notebook-Kubernetes)

