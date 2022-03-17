先抛出几个问题。

1. 为什么在kubernetes我们不直接使用一个单独的容器（container），而是用pod来封装一个或多个容器呢？
2. 为什么我们要运行多个容器呢？
3. 我们能将我们所有的应用程序都放到一个容器里面运行么？









#

https://www.zhihu.com/question/486993957/answer/2122103097

https://zhuanlan.zhihu.com/p/463749017

https://zhuanlan.zhihu.com/p/440922591







登录到 master 节点上查看`/etc/kubernetes/manifests`目录：

现在明白了吧，这种方式也为我们将集群的一些组件容器化提供了可能，因为这些 Pod 都不会受到 apiserver 的控制，不然我们这里`kube-apiserver`怎么自己去控制自己呢？万一不小心把这个 Pod 删掉了呢？所以只能有`kubelet`自己来进行控制，这就是我们所说的静态 Pod。







**Containers aren't Containers**

许多人已经知道这一点，Linux“容器”实际上并不存在。

在Linux中没有所谓的“容器”，众所周知，容器是使用Linux内核的两个特性---**namesapce和cgroups**来执行的正常进程。

**namespace**允许您为进程提供一个“view”，该视图隐藏了这些名称空间之外的所有内容，从而为进程提供了自己的运行环境，这使得进程不能看到或干涉其他进程。

**namesapce 包括，下面的内容只能用英文才能准确的表达其含义**

- Hostname
- Process IDs
- File System
- Network interfaces
- Inter-Process Communication (IPC)

虽然我在上面说过，在名称空间中运行的进程不能干扰其他进程，但事实并非如此。进程可以使用它所运行的物理机器上的所有资源，从而使其他进程无法获得资源。为了限制这种情况，Linux有一个叫做cgroups的特性。进程可以像命名空间一样在cgroup中运行，但是cgroup限制了进程可以使用的资源。这些资源包括CPU、RAM、块I/O、网络I/O等。CPU通常受毫核(1000分之一个核)的限制，内存受RAM字节的限制。进程本身可以运行。





命名空间和cgroups也用于进程组。在一个名称空间中可以运行多个进程，这样它们就可以看到彼此，并相互交流。或者你可以在一个cgroup中运行它们。这样，这些过程将被限制在一个特定的范围内。

**Combinations of Combinations**

当你正常使用Docker运行一个容器时，Docker会为每个容器创建命名空间和cgroups，这样它们就会一一映射。这就是开发人员通常对容器的看法。

![img](https://pic1.zhimg.com/80/v2-f69acedf74d0a3ee31e6b09620378644_1440w.jpg)





现在我们的nginx容器可以在本地主机上直接代理请求到该容器。如果你访问http://localhost:8080/，你应该能够看到ghost通过nginx代理运行。这些命令创建了一组在一组名称空间中运行的容器。这些名称空间允许Docker容器之间的相互发现和通信。







**Pods are Containers (sort of)**

既然我们已经看到了可以将名称空间和cgroups与多个进程组合在一起，那么我们可以看到Kubernetes Pods就是这样的。Pods允许您指定要运行的容器，Kubernetes以正确的方式自动设置名称空间和cgroups。它比那要复杂一点，因为Kubernetes不使用Docker网络(它使用CNI)。





