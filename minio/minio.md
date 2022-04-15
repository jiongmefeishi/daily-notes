minio

什么是minio 纠删码？

修改用户名和密码

![image-20220412103141053](/Users/tao/Library/Application Support/typora-user-images/image-20220412103141053.png)



#### 运行minio

```
mkdir -p ~/minio/data

docker run \
  -p 9000:9000 \
  -p 9001:9001 \
  --name minio1 \
  -v ~/minio/data:/data \
  -e "MINIO_ROOT_USER=asp" \
  -e "MINIO_ROOT_PASSWORD=Asp123456" \
  quay.io/minio/minio server /data --console-address ":9001"
```

启用纠删码部署

```
mkdir -p ~/minio/data
cd ~/minio/data
mkdir data1 data2 data3 data4 data5 data6

docker run -d \
  -p 9000:9000 \
  -p 9001:9001 \
  --name minio2 \
  -v ~/minio/data/data1:/data1 \
  -v ~/minio/data/data2:/data2 \
  -v ~/minio/data/data3:/data3 \
  -v ~/minio/data/data4:/data4 \
  -v ~/minio/data/data5:/data5 \
  -v ~/minio/data/data6:/data6 \
  -e "MINIO_ROOT_USER=asp" \
  -e "MINIO_ROOT_PASSWORD=Asp123456" \
  quay.io/minio/minio server /data{1...6} --console-address ":9001"
```



![image-20220413092807953](/Users/tao/Library/Application Support/typora-user-images/image-20220413092807953.png)
