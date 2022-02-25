### 探针配置失误，线上容器应用异常死锁后，kubernetes集群未及时响应自愈重启容器？



探针配置失误，线上容器应用异常死锁后，`kubernetes`集群未及时响应自愈重启容器？

线上多个服务应用陷入了死循环，大量服务访问不通，陷入死循环的应用长时间搁置，并没有进行自愈。

`k8s`应用容器没有检测到应用陷入了故障，容器未及时重启？

![img](https://img-blog.csdnimg.cn/img_convert/7f728855f6b30c317e309b5156c291ce.gif)

### 囧么肥事-胡说八道

![img](https://img-blog.csdnimg.cn/img_convert/634a3b6aaee7a5402940278b4b68be9d.png)

![img](https://img-blog.csdnimg.cn/img_convert/386476ef412f0bc5ca2f08bc063218e7.png)

![img](https://img-blog.csdnimg.cn/img_convert/bdcfa671b1d38fbcbd6ae669dd29b388.png)


### 弄清楚为什么要使用容器探针？

`kubernetes` 集群的好处是可以监测应用容器健康状态，在必要时候进行故障自愈。Pod管家一旦调度到某个节点，该节点上的`Kubelet`就会运行Pod的容器。

如果应用程序中有一个导致它每隔一段时间就会崩溃的bug,`Kubernetes`会自动重启应用程序，所以即使应用程序本身没有做任何特殊的事，在Kubernetes中运行也能自动获得自我修复的能力。



默认情况下，kubelet根据容器运行状态作为健康依据，不能监控容器中应用程序状态，例如程序假死。这就会导致无法提供服务，丢失流量。因此引入健康检查机制确保容器健康存活。



Pod通过两类探针来检查容器的健康状态。分别是`LivenessProbe`（存活探针）和 `ReadinessProbe`（就绪探针）。

还有一种启动探针监控应用启动状态：`StartupProbe`（启动探针）

- `livenessProbe`：指示容器是否正在运行。如果存活态探针失败，则 `kubelet` 会杀死容器， 并且容器将根据其[重启策略](https://kubernetes.io/zh/docs/concepts/workloads/pods/pod-lifecycle/#restart-policy)决定未来。如果容器不提供存活探针， 则默认状态为 `Success`。
- `readinessProbe`：指示容器是否准备好为请求提供服务。如果就绪态探针失败， 端点控制器将从与 Pod 匹配的所有服务的端点列表中删除该 Pod 的 IP 地址。 初始延迟之前的就绪态的状态值默认为 `Failure`。 如果容器不提供就绪态探针，则默认状态为 `Success`。

- `startupProbe`: 指示容器中的应用是否已经启动。如果提供了启动探针，则所有其他探针都会被 禁用，直到此探针成功为止。如果启动探针失败，kubelet 将杀死容器，而容器依其重启策略进行重启。 如果容器没有提供启动探针，则默认状态为 Success。

### 特殊场景如何选择正确的探针？

`kubelet` **使用存活探针来知道什么时候要重启容器**。 

例如，存活探针可以捕捉到死锁（应用程序在运行，但是无法继续执行后面的步骤）。 这样的情况下重启容器有助于让应用程序在有问题的情况下更可用。

`kubelet` **使用就绪探针判断容器什么时候准备好了并可以开始接受请求流量**。

 当一个 Pod 内的**所有容器都准备好了**，才能把这个 Pod 看作就绪了。 这种信号的一个用途就是控制哪个 Pod 作为 `Service` 的后端。 在 Pod 还没有准备好的时候，会从 Service 的负载均衡器中被剔除的。

`kubelet` **使用启动探针监测应用程序容器什么时候启动了**。 

如果配置了这类探针，就可以控制容器在启动成功后再进行**存活性和就绪检查**， 确保这些存活、就绪探针不会影响应用程序的启动。 这可以用于对慢启动容器进行存活性检测，避免它们在启动运行之前就被杀掉。



### 何时该使用存活态探针? 

如果容器中的进程能够在遇到问题或不健康的情况下自行崩溃，则不一定需要存活态探针; `kubelet` 将根据 Pod 的`restartPolicy` 自动执行修复操作。

如果你希望容器在探测失败时被杀死并重新启动，那么请指定一个存活态探针， 并指定`restartPolicy` 为 "`Always`" 或 "`OnFailure`"。

### 何时该使用就绪态探针? 

> 如果要仅在探测成功时才开始向 Pod 发送请求流量，请指定就绪态探针。 

在这种情况下，就绪态探针可能与存活态探针相同，但是规约中的就绪态探针的存在意味着 Pod 将在启动阶段不接收任何数据，并且只有在探针探测成功后才开始接收数据。

> 如果你希望容器能够自行进入维护状态，也可以指定一个就绪态探针

检查某个特定于就绪态的**不同于存活态探测的端点**。

> 如果你的应用程序对后端服务有严格的依赖性，你可以同时实现存活态和就绪态探针。

当应用程序本身是健康的，存活态探针检测通过后，就绪态探针会额外检查每个所需的后端服务是否可用。

这可以帮助你避免将流量导向只能返回错误信息的 Pod。

> 如果你的容器需要在启动期间加载大型数据、配置文件或执行迁移，你可以使用 启动探针。 

然而，如果你想区分已经失败的应用和仍在处理其启动数据的应用，你可能更倾向于使用就绪探针。

**说明：**

请注意，如果你只是想在 Pod 被删除时能够排空请求，则不一定需要使用就绪态探针； 在删除 Pod 时，Pod 会自动将自身置于未就绪状态，无论就绪态探针是否存在。 等待 Pod 中的容器停止期间，Pod 会一直处于未就绪状态。

### 何时该使用启动探针？ 

对于所包含的容器需要较长时间才能启动就绪的 Pod 而言，启动探针是有用的。 你不再需要配置一个较长的存活态探测时间间隔，只需要设置另一个独立的配置选定， 对启动期间的容器执行探测，从而允许使用远远超出存活态时间间隔所允许的时长。

如果你的容器启动时间通常超出 `initialDelaySeconds + failureThreshold × periodSeconds` 总值，你应该设置一个启动探测，对存活态探针所使用的同一端点执行检查。 `periodSeconds` 的默认值是 10 秒。你应该将其 `failureThreshold` 设置得足够高， 以便容器有充足的时间完成启动，并且避免更改存活态探针所使用的默认值。 这一设置有助于减少死锁状况的发生。

**例如使用启动探针保护慢启动容器**

有时候，会有一些现有的应用程序在启动时需要**较多的初始化时间**。

要不影响对引起探针死锁的快速响应，这种情况下，设置存活探针参数是要技巧的。 

技巧就是使用一个命令来**设置启动探针**，针对`HTTP` 或者 `TCP` 检测，可以通过设置 `failureThreshold * periodSeconds` 参数来保证有足够长的时间应对糟糕情况下的启动时间。

### 探针执行的三种方式？

`Probe` 是由 `kubelet`对容器执行的定期诊断。 要执行诊断，`kubelet` 有三种类型的处理程序：

- `ExecAction`： 在容器内执行指定命令。如果命令退出时返回码为 0 则认为诊断成功。
- `TCPSocketAction`： 对容器的 IP 地址上的指定端口执行 TCP 检查。如果端口打开，则诊断被认为是成功的。
- `HTTPGetAction`： 对容器的 IP 地址上指定端口和路径执行 HTTP Get 请求。如果响应的状态码大于等于 200 且小于 400，则诊断被认为是成功的。

每次探测都将获得以下三种结果之一：

- `Success`（成功）：容器通过了诊断。
- `Failure`（失败）：容器未通过诊断。
- `Unknown`（未知）：诊断失败，因此不会采取任何行动。

![img](https://img-blog.csdnimg.cn/img_convert/b2ab81c5321cab41f01b30f4f1c213f2.gif)



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
