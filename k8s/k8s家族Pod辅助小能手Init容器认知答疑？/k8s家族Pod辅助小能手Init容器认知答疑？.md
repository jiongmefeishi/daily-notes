### k8s家族Pod辅助小能手Init容器认知答疑？

k8s集群Init 容器是一种**特殊容器**，职责是在Pod的生命周期中作为应用容器的**前置启动容器**。

在很多应用场景中，在 Pod 内的**应用容器正式启动之前**之前需要进行预热操作，为正式启动应用容器铺垫先决条件，如**预加载一些基本配置、资源限制配额、还可以包括一些应用镜像中不存在的实用工具和安装脚本**

![](https://img-blog.csdnimg.cn/img_convert/ba05ea1908420db265cbbe8b4a6a57c7.gif)

### 囧么肥事-胡说八道

![](https://img-blog.csdnimg.cn/img_convert/010f7cd3159d6f8e2399e70a83799962.png)

![](https://img-blog.csdnimg.cn/img_convert/d3f3e29956fb50237438ca04f9da1b1e.png)

![](https://img-blog.csdnimg.cn/img_convert/683caf337b7e466eaf9ebc65f7df92f7.png)

### Init容器有什么特殊吗？与普通容器有何不同？

k8s集群Init 容器是一种**特殊容器**，职责是在Pod的生命周期中作为应用容器的**前置启动容器**。

在很多应用场景中，在 Pod 内的**应用容器正式启动之前**之前需要进行预热操作，为正式启动应用容器铺垫先决条件，如**预加载一些基本配置、资源限制配额、还可以包括一些应用镜像中不存在的实用工具和安装脚本**。例如

- 1、基于环境变量或配置模板生成配置文件

- 2、等待其他关联组件加载完成（如MySQL数据库服务，Nginx服务等）

- 3、下载相关依赖包，对系统预配置等

- 4、从远程数据库获取本地应用所需配置，或将自己数据库注册到某个中央数据库等

Init 容器与普通的容器非常像，Init 容器支持应用容器的全部字段和特性，包括资源限制、数据卷和安全设置。

但是也有自己独特的”性格“。

**第一点Init容器必须保证成功启动后才会启动下个容器**

如果 Pod 的 Init 容器失败，kubelet 会不断地重启该 Init 容器直到该容器成功为止，然后才会考虑去启动其他容器。对自己的要求比较严格，只许成功，不许失败！

**第二点 Kubernetes 其实禁止Init容器使用 `readinessProbe`**

因为 Init 容器不能定义不同于完成态（Completion）的就绪态（Readiness）

**第二点Init 容器对资源请求和限制的处理稍有不同**

在给定的 Init 容器执行顺序下，资源使用适用于如下规则：

- 所有 Init 容器上定义的任何特定资源的 `limit` 或 `request` 的最大值，作为 Pod ***有效初始** `request/limit`*。 如果任何资源没有指定资源限制，这被视为最高限制。
- Pod 对资源的有效 `limit/request`，取决于两种判断标准中的较大者
  - 所有应用容器对某个资源的 `limit/request` 之和
  - 对某个资源的有效初始 `limit/request`
- 基于有效 limit/request 完成调度，这意味着 Init 容器能够为**初始化过程预留资源**， 这些资源在 Pod 生命周期过程中并没有被使用。
- Pod 的 *有效 `QoS` 层* ，与 Init 容器和应用容器的一样。

配额和限制适用于有效 Pod 的请求和限制值。 **Pod 级别的 cgroups 是基于有效 Pod 的请求和限制值**，和调度器相同。

同时 Init 容器不支持 `lifecycle`、`livenessProbe`、`readinessProbe` 和 `startupProbe`， 因为它们必须在 Pod 就绪之前运行完成。

如果为一个 Pod 指定了多个 Init 容器，这些容器会**按顺序逐个运行**。 每个 Init 容器必须运行成功，下一个才能够运行。当所有的 Init 容器运行完成时， `Kubernetes` 才会为 Pod 初始化应用容器并像平常一样运行。



### 它的启动有什么不同，如果多个Init容器启动呢？失败呢？

在 Pod 启动过程中，每个 Init 容器会在网络和数据卷初始化之后按**顺序启动**。 kubelet 运行依据 Init 容器在 Pod 规约中的出现顺序依次运行之。

如果 Pod 的 Init 容器失败，`kubelet` 会不断地重启该 Init 容器直到该容器成功为止。 然而，如果 Pod 对应的 `restartPolicy` 值为 "`Never`"，并且 Pod 的 Init 容器失败， 则 `Kubernetes` 会将整个 Pod 状态设置为失败。

如果为一个 Pod 指定了多个 Init 容器，这些容器会按顺序逐个运行。 每个 Init 容器必须运行成功，下一个才能够运行。当所有的 Init 容器运行完成时， Kubernetes 才会为 Pod 初始化应用容器并像平常一样运行。

![](https://img-blog.csdnimg.cn/img_convert/1319512d788a30916a6b2be3329d7345.gif)



### 使用 Init 容器有什么优势？

因为 Init 容器具有与应用容器分离的单独镜像，其启动相关代码具有如下优势：

- Init 容器可以包含一些安装过程中应用容器中**不存在的实用工具或个性化代码**。 

  例如，没有必要仅为了在安装过程中使用类似 `sed、awk、python` 或 `dig` 这样的工具而去 `FROM` 一个镜像来生成一个新的镜像。

- Init 容器可以安全地运行这些工具，避免这些工具导致应用镜像的安全性降低。

- 应用镜像的创建者和部署者可以各自**独立工作**，而没有必要联合构建一个单独的应用镜像。

- Init 容器能以**不同于 Pod 内应用容器的文件系统视图运行**。因此，`Init` 容器可以访问 应用容器不能访问的`Secret` 的权限。

- 由于 Init 容器必须在应用容器启动之前运行完成，因此 Init 容器 提供了一种机制来阻塞或延迟应用容器的启动，直到满足了一组先决条件。 一旦前置条件满足，Pod 内的所有的应用容器会并行启动。

![](https://img-blog.csdnimg.cn/img_convert/28edb31dd02df41b723c90edd9d8a6ec.gif)


-----

 **《Kubernetes-企业级容器应用托管》-持续胡说八道**

第一段：推荐阅读：[【云原生新时代弄潮儿k8s凭什么在容器化方面独树一帜？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484066&idx=1&sn=441fcae466eb5b5fba2fa29f007d7c07&chksm=cf31eb74f8466262ccc258fe1d21fbd8d65e73221c211b704d216d5116a15ffcc4f4cacf5b31#rd)

第二段：推荐阅读：[【趁着同事玩游戏偷偷认识k8s一家子补补课】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484077&idx=1&sn=2ba024c0e121f7ac83e7264bdf7b4dff&chksm=cf31eb7bf846626d02c59837a2f903ed848d8e0f117c80af16b364e858005c57849f0bb82e47#rd)

第三段：推荐阅读：[【Kubernetes家族容器小管家Pod在线答疑❓】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484110&idx=1&sn=cae2e84fb16b9fe5d8a7727c20009b3b&chksm=cf31eb18f846620e3dd1b7b8b9008fd5960363bc6bd3de679225ea5e45f9a48e93d210ccd572#rd)

第四段：推荐阅读：[【同事提出个我从未想过的问题，为什么Kubernetes要"多此一举"推出静态Pod概念？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484122&idx=1&sn=4f913c1e30808622e80a386aa6b4bef8&chksm=cf31eb0cf846621a4cf5ba605ec6fe4141b244dd2b8c49311accba15909f426277d643b6aceb#rd)

第五段：推荐阅读：[【探针配置失误，线上容器应用异常死锁后，kubernetes集群未及时响应自愈重启容器？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484133&idx=1&sn=116c23255e688ca1b86197689bcc8b72&chksm=cf31eb33f8466225400e6bfaac74d5d26de91b85e8f475ecbebedfb8ae08ebd9dde91aec1177#rd)

第六段：推荐阅读：[【kubernetes集群之Pod说能不能让我体面的消亡呀？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484143&idx=1&sn=5e764d67105c34bbaa4c851482dbe5cc&chksm=cf31eb39f846622f8c0aa21afd5d33d3928073de71058d59f974c5498bf84da2681cf76582a8#rd)

第七段：推荐阅读：[【k8s家族Pod辅助小能手Init容器认知答疑？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484153&idx=1&sn=2d6f43036cf2e4cea5fa2aebc4b67ebf&chksm=cf31eb2ff846623904c34e84943576ccf1714d73e042bdc9a4ce584050caf3fc0a85ff5c8908#rd)

第八段：待更新？推荐休闲阅读：[【囧么肥事】](https://mp.weixin.qq.com/mp/appmsgalbum?__biz=Mzg3NjU0NDE4NQ==&action=getalbum&album_id=2218140423993212933#wechat_redirect)



