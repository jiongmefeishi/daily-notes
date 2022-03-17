### 跟k8s工作负载Deployments的缘起缘灭

```
考点之简单介绍一下什么是Deployments吧？
```

```
考点之怎么查看 Deployment 上线状态？
```

```
考点之集群中能不能设置多个Deployments控制器具有重叠的标签选择器？
```

```
考点之可以自定义Pod-template-hash 标签嘛？如果可以，有什么好处？如果不可以，有什么危害？
```

```
考点之什么场景下会触发Deployments上线动作？
```

```
考点之Deployments在更新时会关闭所有Pod嘛？如果不是，默认关闭最大比例是多少？
```

```
考点之你能不能简单描述一下Deployments更新时RS和Pod是如何滚动更新的？
```

```
考点之如何判定Deployment上线过程是否出现停滞？有哪些原因会造成停滞？如何解决配额不足的问题？
```

```
考点之保存修订历史会消耗 etcd 中的资源，并占用 `kubectl get rs` 的输出，如果给修订历史限制值设置为0是不是就能有效解决这个问题？
```

![](https://img-blog.csdnimg.cn/img_convert/cfdf2eea8a08a679ec0aa6805ef5299a.gif)

### 囧么肥事-胡说八道

![](https://img-blog.csdnimg.cn/b8c36781b3044850b2b936a58f5a69b2.png)

![](https://img-blog.csdnimg.cn/8c3db9f4ab7f434fb0b2a9ca4c299bd4.png)



### 考点之简单介绍一下什么是Deployments吧？

`Deployments`是k8s内置的工作负载之一，主要作用是帮助我们管理**无状态Pod**。

一个 `Deployment` 为 Pods 和 `ReplicaSets` 提供了声明式的更新能力，我们只需要负责描述 `Deployment` 中的RS和Pod需要达到的目标状态，那么DM就会以一种受控速率去帮助我们更改RS和Pod的实际状态， 使其变为我们期望出现的状态。

![](https://img-blog.csdnimg.cn/img_convert/9c4c68b5f29fdecdcfbc0d9c36bfb419.png)



`Deployment` 很适合用来管理你的集群上的**无状态应用**，`Deployment` 认为所有的 `Pod` 都是相互等价的，在需要的时候都是可以替换的。

```
Deployment: "小Pod 们，都给我听话"
DM: "你们都不是唯一的"
DM: "不听话，闹事的Pod"
DM: "随时可以让你走人"
DM: "大把的Pod可以替换你们"

Pods: "是是是，我们一定听话"
```

Deployment 是一个实干主义者，你如果好好工作，不闹事，那么一切OK，但是如果你有小心思，敢闹事，那它随时可以赶你走人，随时随地可以招新人。



### 考点之怎么查看 Deployment 上线状态？

Deployment 的生命周期中会有许多状态。上线新的 ReplicaSet 期间可能处于`Progressing`（进行中），可能是 `Complete`（已完成），也可能是`Failed`（失败）进入阻塞停滞无法继续进行。

利用`kubectl rollout status` 命令可以监视 `Deployment` 的进度。

假设创建了一个Nginx的DM，查看DM进度。

```
kubectl rollout status deployment/nginx-deployment
```

**哪些场景会让Deployment 进入这三种状态呢？**

> 为了方便，后续DM均代表Deployment 

#### 进行中（Progressing）

当`Deployment` 执行下面的任务期间，`Kubernetes` 将其标记为**进行中（Progressing）**：

```
- DM创建新的 `ReplicaSet`
- DM正在为最新的 `ReplicaSet` 执行扩容操作
- DM 正在为旧的 `ReplicaSet`执行缩容操作
- 新的 Pods 已经就绪或者可用（就绪至少持续了 `MinReadySeconds` 秒）
```



#### 完成（Complete）

当 `Deployment` 具有以下特征时，`Kubernetes` 将其标记为 **完成（Complete）**：

```
- 与 DM 关联的所有副本都已更新到指定的最新版本，这意味着之前请求的所有更新都已完成。
- 与 DM 关联的所有副本都可用。
- 未运行 DM 的旧副本。
```



#### 失败的（Failed）

当`Deployment` 在尝试部署其最新的 `ReplicaSet` 受挫时，会一直处于未完成状态。 造成此情况可能因素如下：

```
- 配额（Quota）不足
- 就绪探测（Readiness Probe）失败
- 镜像拉取错误
- 权限不足
- 限制范围（Limit Ranges）问题
- 应用程序运行时的配置错误
```



### 考点之集群中能不能设置多个Deployments控制器具有重叠的标签选择器？

首先答案肯定是不能的。

如果这样做，后果是什么呢？**如果有多个控制器的标签选择器发生重叠，则控制器之间会因冲突而无法正常工作。**

另一篇已经讨论类似问题：[线上预警k8s集群循环创建、删除Pod副本，一直无法稳定指定目标副本数量?如果排除了是Pod内部发生了故障，从RS角度你猜测可能是什么原因？](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484188&idx=1&sn=b8b06c4abe2f5d56556235f867ec10d0&chksm=cf31eacaf84663dc0dd330e4b5d183c06a2c92f8a0e42c8c76b30d67e1ba90381f5bc7c5f780#rd)

上一篇主要说明的是多个ReplicaSets 配置了相同的标签选择符，使用相同的标签选择器创建多个`ReplicaSet`，则多个RS无法识别哪个Pod是自己创建的，都会认为是归属于自己管理的Pod。这样做的后果就是会造成Pod被竞争接管的情况，导致Pod副本数量一直无法稳定。

我们知道 `Deployment` 为 `Pods` 和 `ReplicaSets` 提供了声明式的更新能力，主要管控的是RS和Pods。

Kubernetes 不会阻止你去给设置重叠的标签选择器，但是既然RS和Pods会出现因为竞争克制引发的**管理冲突**情况，那么身为他们俩的管理者DM肯定是不能独善其身，一定会受到影响的。

> 那么为了不出现**管理冲突**，我们应该怎么做呢？

必须在 Deployment 中指定适当的标签选择器和 Pod 模板标签，同时标签或者标签选择器不要与其他控制器（包括其他 `Deployment` 和 `StatefulSet`）重叠。

![](https://img-blog.csdnimg.cn/img_convert/034d130cae629b4e869684bc377c9aea.gif)

### 考点之可以自定义Pod-template-hash 标签嘛？如果可以，有什么好处？如果不可以，有什么危害？

> k8s官方说明： 不要更改此标签

k8s官方直接明确的告诉我们，不要自定义`Pod-template-hash` 标签，**那么为什么呢？凭什么就不能自定义？**

`Deployment` 控制器会将自己创建或者管理的每一个`ReplicaSet` 身上都**标注Pod-template-hash** 标签。

唯一的目的就是利用这个标签**确保 Deployment 的子 ReplicaSets 不重叠**。

注意`Deployment`中 `ReplicaSet` 的名称始终**被格式化**为`[Deployment名称]-[随机字符串]`。

其中随机字符串是使用 **pod-template-hash** 作为种子随机生成的。

通过对 ReplicaSet 的 `PodTemplate` 进行哈希处理，所生成的哈希值被添加到 **ReplicaSet** 的标签选择器、Pod 模板标签，以及RS中的每个Pod身上。

**疑问来了，自定义有什么危害呢？**

上面说了，这个标签主要是作为名称随机，确保不重叠，随机到每一个Pod和RS上，可以避免出现多个`Deployments`控制器具有重叠的标签选择器。也就是上面说的那个竞争排斥问题。



### 考点之什么场景下会触发Deployments上线动作？

仅当 Deployment Pod 模板（即 `.spec.template`）发生改变时，例如**模板的标签或容器镜像被更新**， 才会触发 Deployment 上线。

其他更新（如对 Deployment 执行扩缩容的操作）不会触发上线动作。



### 考点之Deployments在更新时会关闭所有Pod嘛？如果不是，默认关闭最大比例是多少？

Deployment 可确保在更新时仅关闭一定数量的 Pod。

默认情况下，它确保至少所需 Pods 75% 处于运行状态（`maxUnavailable`最大不可用比例为 25%）。

```
如果有100个Pod，在更新时，最多关闭25个Pod

DM 保证至少会有75个Pod能正常提供服务
```

Deployment 还确保所创建 Pod 数量只可能比期望 Pods 数高一点点。 

默认情况下，它可确保启动的 Pod 个数比期望个数**最多多出 25%**（最大峰值 25%）。

```
DM 更新会出现两种操作
1、销毁老版本Pod
2、创建新版本Pod

无论是销毁还是创建
默认峰值都是25%

销毁时，最多同时销毁25%Pod
保证有75%的Pod可以继续提供服务

创建时，最多运行比预期副本数多出25%

也就是说如果预期存活Pod副本是100个
那么最多允许同时在运行125个旧版副本+新版副本
```



### 考点之你能不能简单描述一下Deployments更新时RS和Pod是如何滚动更新的？

如果不去更改默认的最大不可用比例和最大运行峰值比例，那么DM更新时，会创建新版本RS，并将其进行扩容，控制到Pod副本数量**满足最大运行峰值比例**。

**达到比例后，DM会停止新版RS扩容，不会再创建新版Pod，直到DM杀死足够多的旧版Pod**



接下来对旧版本RS进行缩容操作，控制去除Pod副本数量**满足最大不可用比例**。

**同样，达到比例后，DM会停止旧版RS删除，不会再继续删除旧版Pod，直到DM创建到足够多的新版Pod**

此为一轮更新，DM不断的进行滚动更新上述操作，直到旧版RS，旧版Pod副本数为0，新版副本数稳定，停止滚动更新。



### 考点之如何判定Deployment上线过程是否出现停滞？有哪些原因会造成停滞？如何解决配额不足的问题？

`Deployment` 可能会在尝试部署最新的 `ReplicaSet` 时出现故障，一直处于未完成的停滞状态。

造成此情况**一些可能因素**如下：

```
- 配额（Quota）不足
- 就绪探测（Readiness Probe）失败
- 镜像拉取错误
- 权限不足
- 限制范围（Limit Ranges）问题
- 应用程序运行时的配置错误
```

**如何判定Deployment上线过程是否出现停滞？**

检测此状况的一种方法是在 `Deployment` 规约中**指定截止时间**参数`.spec.progressDeadlineSeconds`。

一旦超过 Deployment 进度限期，`Kubernetes` 将更新DM状态和进度状况的原因：

```
Conditions:
  Type            Status  Reason
  ----            ------  ------
  Available       True    MinimumReplicasAvailable
  Progressing     False   ProgressDeadlineExceeded
  ReplicaFailure  True    FailedCreate
```

通过 `Deployment` 状态，就能知道是否出现停滞。你可以使用 `kubectl rollout status` 检查 Deployment 是否未能取得进展。 如果 `Deployment` 已超过进度限期，`kubectl rollout status` 返回非零退出代码。

判断停滞，这时候我们可以在**上线过程中间安全地暂停 Deployment ，对其进行上线修复**。

假设排查出停滞原因是配额不足，直接在命名空间中增**加配额 来解决配额不足**的问题。

配额条件满足，Deployment 控制器完成了 `Deployment` 上线操作， Deployment 状态会更新为成功状况（`Status=True` and `Reason=NewReplicaSetAvailable`）



### 考点之保存修订历史会消耗 etcd 中的资源，并占用 `kubectl get rs` 的输出，如果给修订历史限制值设置为0是不是就能有效解决这个问题？

`.spec.revisionHistoryLimit` 是一个可选字段，用来设定为回滚操作所备份保留的旧 `ReplicaSet` 数量。

这些旧 `ReplicaSet` 会消耗 etcd 中的资源，并占用 `kubectl get rs` 的输出。 

每个 `Deployment` 修订版本的配置都存储在其 `ReplicaSets` 中；

因此，一旦删除了旧的 `ReplicaSet`， **将失去回滚到 `Deployment` 的对应修订版本的能力**。 

默认情况下，系统保留 10 个旧 `ReplicaSet`，但其理想值取决于新 `Deployment` 的频率和稳定性。

如果给修订历史限制值设置为0，将导致 `Deployment` 的所有历史记录被清空。没有了历史备份，因此 `Deployment` 将无法回滚，无法撤消新的 Deployment 上线。

总结：虽然可以减少`etcd`的资源消耗，但是**不利于k8s集群实现故障容错、高可用**。为了节约一些资源，而放弃容错，高可用性质，只能说，非常非常非常，不值得。



![](https://img-blog.csdnimg.cn/img_convert/e8fbfc8088f64a0a7c6dcce068eaf439.gif)





> 获取更多干货（MySQL、K8S），欢迎关注微信公众号：囧么肥事

Kubernetes 推荐学习书

> Kubernetes权威指南PDF
> 链接: https://pan.baidu.com/s/11huLHJkCeIPZqSyLEoUEmQ 提取码:sa88

-----

 **《Kubernetes-企业级容器应用托管》-持续胡说八道**

第一段：推荐阅读：[【云原生新时代弄潮儿k8s凭什么在容器化方面独树一帜？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484066&idx=1&sn=441fcae466eb5b5fba2fa29f007d7c07&chksm=cf31eb74f8466262ccc258fe1d21fbd8d65e73221c211b704d216d5116a15ffcc4f4cacf5b31#rd)

第二段：推荐阅读：[【趁着同事玩游戏偷偷认识k8s一家子补补课】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484077&idx=1&sn=2ba024c0e121f7ac83e7264bdf7b4dff&chksm=cf31eb7bf846626d02c59837a2f903ed848d8e0f117c80af16b364e858005c57849f0bb82e47#rd)

第三段：推荐阅读：[【Kubernetes家族容器小管家Pod在线答疑❓】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484110&idx=1&sn=cae2e84fb16b9fe5d8a7727c20009b3b&chksm=cf31eb18f846620e3dd1b7b8b9008fd5960363bc6bd3de679225ea5e45f9a48e93d210ccd572#rd)

第四段：推荐阅读：[【同事提出个我从未想过的问题，为什么Kubernetes要"多此一举"推出静态Pod概念？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484122&idx=1&sn=4f913c1e30808622e80a386aa6b4bef8&chksm=cf31eb0cf846621a4cf5ba605ec6fe4141b244dd2b8c49311accba15909f426277d643b6aceb#rd)

第五段：推荐阅读：[【探针配置失误，线上容器应用异常死锁后，kubernetes集群未及时响应自愈重启容器？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484133&idx=1&sn=116c23255e688ca1b86197689bcc8b72&chksm=cf31eb33f8466225400e6bfaac74d5d26de91b85e8f475ecbebedfb8ae08ebd9dde91aec1177#rd)

第六段：推荐阅读：[【kubernetes集群之Pod说能不能让我体面的消亡呀？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484143&idx=1&sn=5e764d67105c34bbaa4c851482dbe5cc&chksm=cf31eb39f846622f8c0aa21afd5d33d3928073de71058d59f974c5498bf84da2681cf76582a8#rd)

第七段：推荐阅读：[【k8s家族Pod辅助小能手Init容器认知答疑？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484153&idx=1&sn=2d6f43036cf2e4cea5fa2aebc4b67ebf&chksm=cf31eb2ff846623904c34e84943576ccf1714d73e042bdc9a4ce584050caf3fc0a85ff5c8908#rd)

第八段：推荐阅读：[【k8s初面考点ReplicaSet副本集极限9连击你懂了吗？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484188&idx=1&sn=b8b06c4abe2f5d56556235f867ec10d0&chksm=cf31eacaf84663dc0dd330e4b5d183c06a2c92f8a0e42c8c76b30d67e1ba90381f5bc7c5f780#rd)

第九段：推荐阅读：[【生产环境想要对某个Pod排错、数据恢复、故障复盘有什么办法？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484206&idx=1&sn=631183744568cd7756cb3d747595c479&chksm=cf31eaf8f84663eed04cfdd2c04dd8f1522a3c5bf43cbf4a24dd2e3a335fc4189f1269627e8f#rd)

第十段：推荐阅读：[【跟k8s工作负载Deployments的缘起缘灭】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484214&idx=1&sn=7a0a593abbcd34347351bcc3ecd6785a&chksm=cf31eae0f84663f61600e14108ebd7b0db326d26d1f78ca7686e685c4de4728af80b54de3b4c#rd)

后续未更新？推荐休闲阅读：[【囧么肥事】](https://mp.weixin.qq.com/mp/appmsgalbum?__biz=Mzg3NjU0NDE4NQ==&action=getalbum&album_id=2218140423993212933#wechat_redirect)

