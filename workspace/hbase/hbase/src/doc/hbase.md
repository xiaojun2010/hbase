1. 拉取镜像：

   ```shell
   docker pull centos:7.9.2009
   ```
2. 运行镜像生成容器：

   ```
   docker run -itd --name hadoop01 -p 2201:22 -p 8088:8088 -p 9000:9000 -p 50070:50070 --privileged=true centos:7.9.2009 /usr/sbin/init

   ```
3. 运行后查看 docker ps

   ```
   (base) ➜  ~ docker ps
   CONTAINER ID   IMAGE             COMMAND            CREATED          STATUS          PORTS                                                                                            NAMES
   ffe5923642b2   centos:7.9.2009   "/usr/sbin/init"   24 seconds ago   Up 23 seconds   0.0.0.0:8088->8088/tcp, 0.0.0.0:9000->9000/tcp, 0.0.0.0:50070->50070/tcp, 0.0.0.0:2201->22/tcp   hadoop01
   ```
4.
