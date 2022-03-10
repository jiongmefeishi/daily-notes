### 为什么k8s的 StatefulSets 需要VolumeClaimTemplate？

对于k8s集群中有状态的副本集都会用到**持久存储**。

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



了解完为什么缩容无法执行，那么再聊聊可能导致无法正常缩容原因都有哪些？

如果 `spec.replicas` 大于 1，Pod副本数量大于1 ，`Kubernetes` 无法直接判定 Pod 不健康的原因。

Pod 不健康可能是由于**永久性故障造成也可能是瞬态故障**。 

> 永久性故障

如果该 Pod 不健康是由于永久性故障导致，则在不纠正该故障的情况下进行缩容可能会导致 `StatefulSet` **成员 Pod 数量低于应正常运行的副本数**。这种状态也许会导致 StatefulSet 不可用。

> 瞬态故障

瞬态故障可能是**节点升级或维护而引起的节点重启**造成的。

如果由于瞬态故障而导致 Pod 不健康，并且 Pod **可能再次变为可用**，那么瞬态错误可能会干扰 你对 `StatefulSet` 的扩容/缩容操作。 

一些分布式数据库在同时有节点加入和离开时会遇到问题。

在这些情况下，最好是在**应用级别进行分析扩缩操作的状态**，并且只有在确保 `Stateful` 应用的集群是完全健康时才执行扩缩操作。



### 聊聊什么是StatefulSet的分区滚动更新吧？什么场景需要使用分区更新？

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

当 StatefulSet 的 `.spec.updateStrategy.type` 被设置为 `RollingUpdate` 时， StatefulSet 控制器会删除和重建 StatefulSet 中的每个 Pod。 它将按照与 Pod 终止相同的顺序（从最大序号到最小序号）进行，每次更新一个 Pod。

Kubernetes 控制面会等到被更新的 Pod 进入 `Running 和 Ready` 状态，然后再更新其前身Pod。 

如果你设置了 `.spec.minReadySeconds`（最短就绪秒数），控制面在 Pod 就绪后会额外等待一定的时间再执行下一步。

**接下来进入主题什么是分区滚动更新？**

分区滚动更新是滚动更新策略中的一个特殊场景，StatefulSet 控制一定范围内的Pod进行滚动更新，调度为新版本Pod运行，而范围外的Pod继续维持老版本运行。

通过声明 `.spec.updateStrategy.rollingUpdate.partition` 的方式，`RollingUpdate` 更新策略可以实现分区。 

如果声明了一个分区，当 StatefulSet 的 `.spec.template` 被更新时

```
所有序号大于等于该分区序号的 Pod 都会被更新。
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

此外，如果 StatefulSet 的 `.spec.updateStrategy.rollingUpdate.partition` 大于它的 `.spec.replicas`，对它的 `.spec.template` 的更新将不会传递到它的 Pod。 

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



### 线上某些特殊情况下可能需要强制删除 StatefulSet 的 Pod，操作不当会引发很严重的后果，该如何处理？

**正常情况下**

`StatefulSet` 的正常操作中，永远不需要强制删除 `StatefulSet` 管理的 Pod。 

`StatefulSet` 控制器会负责创建、 扩缩和删除 `StatefulSet` 管理的 Pods。

它尝试确保指定数量的从序数 `0 到 N-1 的 Pod` 处于活跃状态并准备就绪。

`StatefulSet` 遵循`At Most One`（最多一个）规则，确保在任何时候，集群中最多只有一个具有给定标识的 Pod。

**特殊情况下**

所谓特殊情况下必须进行强制删除，当某个节点不可达时，不会引发自动删除 Pod。在无法访问的节点上运行的 Pod 在超时 后会进入'`Terminating`' 或者 '`Unknown`' 状态。当用户尝试体面地删除无法访问的节点上的 Pod 时 Pod 也可能会进入这些状态。

如果你发现 StatefulSet 的某些 Pod 长时间处于 '`Terminating`' 或者 '`Unknown`' 状态

无法自己完成正常的调度，这个时候可能需要手动干预以强制的手段从 API 服务器中删除这些 Pod。

**操作不当会引发很严重的后果？**

 应谨慎进行**手动强制删除**操作，因为它可能会**违反 StatefulSet 固有的至多一个的规则**。

`StatefulSets` 用于运行**分布式和集群级**的应用，这些应用需要**稳定的网络标识和可靠的存储**。 

这些应用通常配置为具有**固定标识固定数量**的成员集合，每个Pod都是**唯一的，独立的**，你可以理解为每个人的身份证编号都是唯一的。

具有相同身份的多个成员（Pod）可能是灾难性的，**可能导致数据丢失** (例如：票选系统中的脑裂场景)。

**问题来了，为什么就会出现多个相同标识的Pod呢？**

原来，不同于Pod体面终止的是，在进行强制删除过程中，API 服务器**不会**等待来自 kubelet 对 Pod **已终止的确认消息**，它会立即从 API 服务器中释放该名字。我们知道`StatefulSet` 中每个Pod有固定标识，在进行重新调度的时候，新调度创建的Pod会继承上一个旧Pod的一切有用资源，比如PV，唯一标识，网络标识等。

强制删除，直接从API服务器移除Pod对象，这个时候，`StatefulSet` 控制器有机会去创建一个**具有相同标识的替身 Pod**，并且去继承旧Pod的资源。这就直接违背了`StatefulSet` 最多一个规则。

但是呢？最绝的来了，**尚未完全删除的 Pod 仍然可以与 StatefulSet 的成员通信**，也就是说它仍然可以操作PV，可能导致PV数据流失。

**安全处理？**

如果遇到Pod 长时间处于 '`Terminating`' 或者 '`Unknown`' 状态情况，再进行强制删除之前可以先考虑以下处理方式：

- 删除 Node 对象（要么你来主动删除，要么节点控制器来删除）
- 无响应节点上的 `kubelet` 开始响应，杀死 Pod 并从 API 服务器上移除 Pod 对象

如果确认节点已经不可用了 (比如，永久断开网络、断电等)， 则应删除 Node 对象。

如果节点遇到网裂问题，请尝试解决该问题或者等待其解决。 

当网裂愈合时，`kubelet` 将完成 Pod 的删除并从 API 服务器上释放其名字。

⚠️当你确定必须执行强制删除 `StatefulSet` 类型的 Pod 时，你要确保有问题的 Pod **不会再和 StatefulSet 管理的其他 Pod 通信**并且可以**安全地释放其名字**以便创建替代 Pod。







































