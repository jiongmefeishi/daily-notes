### 同事提出个我从未想过的问题，为什么Kubernetes要"多此一举"推出静态Pod概念？

我们知道k8s中Pod可以说是一个合格的容器小管家，Pod 被设计成支持多个容器可以一起进行调度，容器之间可以**共享资源和依赖、彼此通信、协调何时以及何种方式运行或终止自身**。

不知道小伙伴有没有注意到我们小管家的**孪生兄弟静态Pod**?

**为什么k8s会推出静态Pod概念？**

![啦啦啦](https://img-blog.csdnimg.cn/img_convert/7b836187fbeecfa7d7c4b822f8a1ec8e.gif)

### 囧么肥事胡说八道开课啦

![101](https://img-blog.csdnimg.cn/img_convert/f72f50bd85a9880f62f7e50f9ce7861f.png)

![102](https://img-blog.csdnimg.cn/img_convert/cd65286cc7e56b502e7d706a5b321493.png)



### 静态 Pod 有什么特殊的地方呢？

正常情况下Pod是在Master上统一管理，指定，分配。所谓静态Pod就是不接受Master的管理，在指定的node上当 `kubelet` 启动时，会**自动启动**所有定义的静态Pod。

静态 Pod 直接由**特定节点上的 kubelet 进程**来管理，不通过 master 节点上的 `apiserver `。⽆法与我们常⽤的控制器 `Deployment` 或者 `DaemonSet` 进⾏关联，`kubelet` **直接监控每个Pod**，并在**故障失效时进行重启自愈**。

静态 Pod 始终绑定在某⼀个 `kubelet `，并且**始终运⾏在同⼀个节点**上。

### 既然发现API不能管理，为什么能“看见”运行的静态Pod？

kubelet会为每个它管理的静态Pod，调用`api-server`在 `Kubernetes` 的 `apiserver `上创建⼀个镜像 Pod（`Mirror Pod`）。因此我们可以在 `apiserver` 中查询到该 Pod，也能通过kubectl等方式进行访问，但是不能通过 apiserver 进⾏控制（例如不能删除）。

### 普通Pod失败自愈和静态Pod有什么区别？

常规Pod用**工作负载**资源来创建和管理多个 Pod。 **资源的控制器能够处理副本的管理、上线，并在 Pod 失效时提供自愈能力**。 

本身节点可以尝试**重启或者完全替换**操作，`kubernetes`**默认的自愈机制是当Pod退出时对Pod进行重启**。

如果重启失败，可以重新拉取Pod，实现替身替换：

例如，如果一个节点失败，控制器注意到该节点上的 Pod 已经停止工作， 就可以创建替换性的 **替身Pod**。调度器会将替身 Pod 调度到一个健康的节点执行。

下面是一些管理一个或者多个 Pod 的工作负载资源的示例：

- `Deployment`
- `StatefulSet`
- `DaemonSet`



静态Pod是指定在特定的节点上运行的Pod，**完全交给kubelet进行监督自愈**，重启也会在同一个指定的节点上进行重启。静态 Pod 始终绑定在某⼀个 `kubelet` ，并且始终运⾏在同⼀个节点上。

### 如果kubectl停止或者删除静态Pod会怎样？

如果尝试删除或者停止，静态Pod会进入**`Pending`**状态，并且很快会被`kubelet`重启。

**那如果我非要删除它呢？**

`kubelet` 启动时，由 `–Pod-manifest-path= or –manifest-url=` 参数指定的⽬录下定义的所有 Pod 都会自动创建。

删除只需要在**配置目录**下删除对应的 `yaml` 配置文件。

运行中的 kubelet 会定期扫描配置的目录，并且根据文件中出现或者消失的 Pod配置文件来创建或者删除 Pod。

### 静态Pod有什么作用？有哪些内置静态Pod？

静态 Pod 通常绑定到某个节点上的 `kubelet`。 其主要用途是**运行自托管的控制面**。 

因为使用静态Pod可以有效预防通过`kubectl`、或管理工具操作的误删除，可以利用它来部署一些核心组件应用，**保障应用服务总是运行稳定数量和提供稳定服务**。

在自托管场景中，使用 `kubelet` 来管理各个独立的控制面组件。例如：

- 调度组件`kube-scheduler`
- 秘书组件`kube-apiserver`
- 核心大脑组件 `kube-controller-manager`
- 数据仓组件 `etcd`

![喵喵喵](https://img-blog.csdnimg.cn/img_convert/e404c8e3e4b00d1f8fcb0967b94ee9f6.png)



----



 **《Kubernetes-企业级容器应用托管》-持续胡说八道**

第一段：推荐阅读：[【云原生新时代弄潮儿k8s凭什么在容器化方面独树一帜？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484066&idx=1&sn=441fcae466eb5b5fba2fa29f007d7c07&chksm=cf31eb74f8466262ccc258fe1d21fbd8d65e73221c211b704d216d5116a15ffcc4f4cacf5b31#rd)

第二段：推荐阅读：[【趁着同事玩游戏偷偷认识k8s一家子补补课】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484077&idx=1&sn=2ba024c0e121f7ac83e7264bdf7b4dff&chksm=cf31eb7bf846626d02c59837a2f903ed848d8e0f117c80af16b364e858005c57849f0bb82e47#rd)

第三段：推荐阅读：[【Kubernetes家族容器小管家Pod在线答疑❓】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484110&idx=1&sn=cae2e84fb16b9fe5d8a7727c20009b3b&chksm=cf31eb18f846620e3dd1b7b8b9008fd5960363bc6bd3de679225ea5e45f9a48e93d210ccd572#rd)

第四段：推荐阅读：[【同事提出个我从未想过的问题，为什么Kubernetes要"多此一举"推出静态Pod概念？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484122&idx=1&sn=4f913c1e30808622e80a386aa6b4bef8&chksm=cf31eb0cf846621a4cf5ba605ec6fe4141b244dd2b8c49311accba15909f426277d643b6aceb#rd)

第五段：推荐阅读：[【探针配置失误，线上容器应用异常死锁后，kubernetes集群未及时响应自愈重启容器？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484133&idx=1&sn=116c23255e688ca1b86197689bcc8b72&chksm=cf31eb33f8466225400e6bfaac74d5d26de91b85e8f475ecbebedfb8ae08ebd9dde91aec1177#rd)

第六段：推荐阅读：[【kubernetes集群之Pod说能不能让我体面的消亡呀？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484143&idx=1&sn=5e764d67105c34bbaa4c851482dbe5cc&chksm=cf31eb39f846622f8c0aa21afd5d33d3928073de71058d59f974c5498bf84da2681cf76582a8#rd)

第七段：推荐阅读：[【k8s家族Pod辅助小能手Init容器认知答疑？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484153&idx=1&sn=2d6f43036cf2e4cea5fa2aebc4b67ebf&chksm=cf31eb2ff846623904c34e84943576ccf1714d73e042bdc9a4ce584050caf3fc0a85ff5c8908#rd)

第八段：待更新？推荐休闲阅读：[【囧么肥事】](https://mp.weixin.qq.com/mp/appmsgalbum?__biz=Mzg3NjU0NDE4NQ==&action=getalbum&album_id=2218140423993212933#wechat_redirect)