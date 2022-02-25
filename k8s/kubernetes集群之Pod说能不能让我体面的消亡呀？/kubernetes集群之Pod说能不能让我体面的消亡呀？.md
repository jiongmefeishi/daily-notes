### kubernetes集群之Pod说能不能让我体面的消亡呀？

由于 Pod 所代表的是在集群中节点上运行的进程，当不再需要这些进程时允许其体面地终止。



1、如果 preStop 回调所需要的时间长于默认的体面终止限期会发生什么？

2、 Pod 的体面终止限期是默认值是多少？

3、超出终止宽限期限时，kubelet 会触发强制关闭过程，这个过程是怎么样的？

4、强制删除 StatefulSet 的 Pod，会出现什么问题？为什么强制删除 StatefulSet 的 Pod可能会违背至多一个Pod原则？

![](https://img-blog.csdnimg.cn/img_convert/742c662cb9cec8287bb24fd310a2a6e7.gif)

### 囧么肥事-胡说八道

![](https://img-blog.csdnimg.cn/img_convert/b14c12777d918445542cb95127bba0a6.png)

![](https://img-blog.csdnimg.cn/img_convert/8ebffa2bb617ed93964e5695e8f1751a.png)

![](https://img-blog.csdnimg.cn/img_convert/04e00c8b0d14362c1eecb3de7573d1bb.png)



### 1、Pod 的体面终止限期是默认值是多少？

默认情况下，所有的删除操作都会附有 **30 秒钟**的宽限期限。

 `kubectl delete` 命令支持 `--grace-period=<seconds>` 选项，允许你**重载默认值**， 设定自己希望的期限值。

将宽限期限**强制**设置为 `0` 意味着立即从 API 服务器删除 Pod。

如果 Pod 仍然运行于某节点上，强制删除操作会触发 `kubelet` 立即执行清理操作。

> **说明：** 你必须在设置 `--grace-period=0` 的同时额外设置 `--force` 参数才能发起强制删除请求。

### 2、Pod体面终止过程是怎么样的？

Pod正常终止，容器运行时会发送一个 TERM 信号到每个容器中的主进程。`kubelet` 开始本地的 Pod 关闭过程，API 服务器中的 Pod 对象被更新，记录涵盖体面终止限期在内 Pod 的最终死期30秒，超出所计算时间点则认为 Pod 已死（dead），之后 Pod 就会被从 API 服务器上移除。

**拆分理解**

1. 发起删除一个Pod命令后系统默认给30s的宽限期，API系统标志这个Pod对象为`Terminating`（终止中）状态
2. kublectl发现Pod状态为`Terminating`则尝试执行`preStop`生命周期勾子，并可多给2s的宽限期
3. 同时控制面将Pod中svc的`endpoint`中移除
4. 宽限期到则发送`TERM`信号，API 服务器删除 Pod 的 API 对象，同时告诉`kubelet`删除Pod资源对象
5. Pod还不关闭再发送`SIGKILL`强制关闭，`kubelet` 也会清理隐藏的 `pause` 容器

![](https://img-blog.csdnimg.cn/img_convert/10730e9d5452df58f60202341208ae79.gif)

### 2、超出终止宽限期限时，kubelet 会触发强制关闭过程，这个过程是怎么样的？

直接执行强制删除操作时，API 服务器不再等待来自 `kubelet` 的、关于 Pod 已经在原来运行的节点上终止执行的确认消息。

API 服务器直接删除 Pod 对象，无论强制删除是否成功杀死了 Pod，都会立即从 API 服务器中释放该名字。

 不过在节点侧，被设置为立即终止的 Pod 仍然会在被强行杀死之前获得一点点的宽限时间。

> 注意：这里的强制删除主要说的是从API服务器移除Pod对象
>
> ReplicaSets和其他工作负载资源不再将 Pod 视为合法的、能够提供服务的副本
>
> 节点端依然是可以允许Pod体面消亡

### 3、 如果 preStop 回调所需要的时间长于默认的体面终止限期会发生什么？

如果 Pod 中的容器之一定义了 `preStop` 回调勾子， `kubelet` 开始在容器内运行该回调逻辑。如果超出体面终止限期时，`preStop` 回调逻辑 仍在运行，`kubelet` 会请求给予该 Pod 的**宽限期一次性增加 2 秒钟**。

### 4、强制删除 StatefulSet 的 Pod，会出现什么问题？为什么强制删除 StatefulSet 的 Pod可能会违背至多一个Pod原则？

强制删除**不会**等待来自 `kubelet` 对 Pod 已终止的确认消息。 **无论强制删除是否成功杀死了 Pod，它都会立即从 API 服务器中释放该名字**。 

这将让 StatefulSet 控制器**可以创建一个具有相同标识的替身 Pod**；因而可能导致正在运行 Pod 的重复。

`StatefulSets` 可用于运行分布式和集群级的应用，这些应用需要稳定的网络标识和可靠的存储。 这些应用通常配置为具有固定标识固定数量的成员集合。

具有**相同身份的多个成员**可能是灾难性的，并且可能**导致数据丢失** (例如：**票选系统中的脑裂场景**)。

![](https://img-blog.csdnimg.cn/img_convert/6cb10ff2371e9709c00f1a73cc65da62.gif)





----



 **《Kubernetes-企业级容器应用托管》-持续胡说八道**

第一段：推荐阅读：[【云原生新时代弄潮儿k8s凭什么在容器化方面独树一帜？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484066&idx=1&sn=441fcae466eb5b5fba2fa29f007d7c07&chksm=cf31eb74f8466262ccc258fe1d21fbd8d65e73221c211b704d216d5116a15ffcc4f4cacf5b31#rd)

第二段：推荐阅读：[【趁着同事玩游戏偷偷认识k8s一家子补补课】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484077&idx=1&sn=2ba024c0e121f7ac83e7264bdf7b4dff&chksm=cf31eb7bf846626d02c59837a2f903ed848d8e0f117c80af16b364e858005c57849f0bb82e47#rd)

第三段：推荐阅读：[【Kubernetes家族容器小管家Pod在线答疑❓】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484110&idx=1&sn=cae2e84fb16b9fe5d8a7727c20009b3b&chksm=cf31eb18f846620e3dd1b7b8b9008fd5960363bc6bd3de679225ea5e45f9a48e93d210ccd572#rd)

第四段：推荐阅读：[【同事提出个我从未想过的问题，为什么Kubernetes要"多此一举"推出静态Pod概念？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484122&idx=1&sn=4f913c1e30808622e80a386aa6b4bef8&chksm=cf31eb0cf846621a4cf5ba605ec6fe4141b244dd2b8c49311accba15909f426277d643b6aceb#rd)

第五段：推荐阅读：[【探针配置失误，线上容器应用异常死锁后，kubernetes集群未及时响应自愈重启容器？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484133&idx=1&sn=116c23255e688ca1b86197689bcc8b72&chksm=cf31eb33f8466225400e6bfaac74d5d26de91b85e8f475ecbebedfb8ae08ebd9dde91aec1177#rd)

第六段：推荐阅读：[【kubernetes集群之Pod说能不能让我体面的消亡呀？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484143&idx=1&sn=5e764d67105c34bbaa4c851482dbe5cc&chksm=cf31eb39f846622f8c0aa21afd5d33d3928073de71058d59f974c5498bf84da2681cf76582a8#rd)

第七段：推荐阅读：[【k8s家族Pod辅助小能手Init容器认知答疑？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484153&idx=1&sn=2d6f43036cf2e4cea5fa2aebc4b67ebf&chksm=cf31eb2ff846623904c34e84943576ccf1714d73e042bdc9a4ce584050caf3fc0a85ff5c8908#rd)

第八段：待更新？推荐休闲阅读：[【囧么肥事】](



