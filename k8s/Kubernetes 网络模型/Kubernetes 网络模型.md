说说你对Kubernetes 网络模型的理解？设计这种网络模型有什么好处？

Kubernetes设计了一种干净的、向后兼容的网络模型，即 `IP-per-pod` 模型。怎么说？

首先，Kubernetes 强制规定了一种简单粗暴的网络模型设计基础原则：**每个 Pod 都拥有一个独立的 IP 地址。**

> 需要注意的是：在K8S集群中，IP地址分配是以Pod对象为单位，而非容器，同一Pod内的所有容器共享同一网络名称空间。

这意味着什么？

```
k8s 假定所有 Pod 都在一个可以直接连通的、扁平的网络空间中
```

也就是说，无论容器运行在集群中的哪个节点，所有容器之间都能通过一个扁平的网络平面进行通信。不管它们是否运行在同一个 Node节点 (宿主机) 上，都要求它们可以直接通过对方的 IP 进行访问。

> 从端口分配、命名、服务发现、负载均衡、应用配置和迁移的角度来看在这个模型，所有的 `Pod` 都可以被视作虚拟机或者物理主机。





Kubernetes 网络模型 

每一个 Pod 都有它自己的IP地址， 这就意味着你不需要显式地在 `Pod` 之间创建链接， 你几乎不需要处理容器端口到主机端口之间的映射。 这将形成一个干净的、向后兼容的模型；在这个模型里，从端口分配、命名、服务发现、 [负载均衡](https://kubernetes.io/zh/docs/concepts/services-networking/ingress/#load-balancing)、应用配置和迁移的角度来看， `Pod` 可以被视作虚拟机或者物理主机。

Kubernetes 强制要求所有网络设施都满足以下基本要求（从而排除了有意隔离网络的策略）：

- [节点](https://kubernetes.io/zh/docs/concepts/architecture/nodes/)上的 Pod 可以不通过 NAT 和其他任何节点上的 Pod 通信
- 节点上的代理（比如：系统守护进程、kubelet）可以和节点上的所有 Pod 通信

备注：对于支持在主机网络中运行 `Pod` 的平台（比如：Linux）：

- 运行在节点主机网络里的 Pod 可以不通过 NAT 和所有节点上的 Pod 通信

这个模型不仅不复杂，而且还和 Kubernetes 的实现从虚拟机向容器平滑迁移的初衷相符， 如果你的任务开始是在虚拟机中运行的，你的虚拟机有一个 IP， 可以和项目中其他虚拟机通信。这里的模型是基本相同的。

Kubernetes 的 IP 地址存在于 `Pod` 范围内 - 容器共享它们的网络命名空间 - 包括它们的 IP 地址和 MAC 地址。 这就意味着 `Pod` 内的容器都可以通过 `localhost` 到达对方端口。 这也意味着 `Pod` 内的容器需要相互协调端口的使用，但是这和虚拟机中的进程似乎没有什么不同， 这也被称为“一个 Pod 一个 IP”模型。

如何实现以上需求是所使用的特定容器运行时的细节。

也可以在 `Node` 本身请求端口，并用这类端口转发到你的 `Pod`（称之为主机端口）， 但这是一个很特殊的操作。转发方式如何实现也是容器运行时的细节。 `Pod` 自己并不知道这些主机端口的存在。







2、Kubernetes网络模型

 我们知道的是，在K8S上的网络通信包含以下几类：

- 容器间的通信：同一个Pod内的多个容器间的通信，它们之间通过lo网卡进行通信。

- Pod之间的通信：通过Pod IP地址进行通信。

- Pod和Service之间的通信：Pod IP地址和Service IP进行通信，两者并不属于同一网络，实现方式是通过IPVS或iptables规则转发。

- Service和集群外部客户端的通信，实现方式：Ingress、NodePort、Loadbalance

  K8S网络的实现不是集群内部自己实现，而是依赖于第三方网络插件----CNI（Container Network Interface）

  flannel、calico、canel等是目前比较流行的第三方网络插件。

  这三种的网络插件需要实现Pod网络方案的方式通常有以下几种：

 虚拟网桥、多路复用（MacVLAN）、硬件交换（SR-IOV）

 无论是上面的哪种方式在容器当中实现，都需要大量的操作步骤，而K8S支持CNI插件进行编排网络，以实现Pod和集群网络管理功能的自动化。每次Pod被初始化或删除，kubelet都会调用默认的CNI插件去创建一个虚拟设备接口附加到相关的底层网络，为Pod去配置IP地址、路由信息并映射到Pod对象的网络名称空间。

 在配置Pod网络时，kubelet会在默认的/etc/cni/net.d/目录中去查找CNI JSON配置文件，然后通过type属性到/opt/cni/bin中查找相关的插件二进制文件，如下面的"portmap"。然后CNI插件调用IPAM插件（IP地址管理插件）来配置每个接口的IP地址：













Kubernetes 网络解决四方面的问题？

服务拓扑设置定向流量，拓扑键匹配规则是什么？

拓扑键有什么约束？

服务拓扑设置定向流量有哪些常见场景？举例说明？









https://kubernetes.io/zh/docs/concepts/services-networking/

https://kubernetes.io/zh/docs/concepts/services-networking/service-topology/

https://www.jianshu.com/p/478c56287c5c

http://dockone.io/article/3211

https://www.cnblogs.com/linuxk/p/10517055.html#kubernetes%E7%9A%84%E7%BD%91%E7%BB%9C%E6%A8%A1%E5%9E%8B%E5%92%8C%E7%BD%91%E7%BB%9C%E7%AD%96%E7%95%A5

