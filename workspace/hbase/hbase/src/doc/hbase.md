# Mac 在docker容器内配置hadoop集群

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
   成功!
   
   需要注意这里 实际上配置的就是第一台hadoop01 端口号必不可少因为后续hdfs yarn启动需要配置对应的端口号访问 如果这里不加端口映射主机无法访问。
   
   -p:表示端口映射，这很重要，可以方便本机在外部访问web网页 需要设置容器和本机的相关端口映射。
   -i:表示运行的容器。
   -t：表示容器启动后会进入其命令行。加入这两个参数后，容器创建就能登录进去。即分配一个伪终端。
   -d: 在run后面加上-d参数,则会创建一个守护式容器在后台运行（这样创建容器后不会自动登录容器，如果只加-i -t两个参数，创建后就会自动进去容器）。
   –name :为创建的容器命名。
   –privileged：为true时赋予容器内root用户真正的root权限，否则root为普通用户,默认为flase。
   /usr/sbin/init: 使容器启动后可以使用systemctl方法。
   ```

4. 进入docker

   ```shell
   docker exec -it hadoop01 /bin/bash
   (base) ➜  doc git:(main) ✗ docker exec -it hadoop01 /bin/bash
   [root@ffe5923642b2 /]# ll
   total 56
   -rw-r--r--   1 root root 12114 Nov 13  2020 anaconda-post.log
   lrwxrwxrwx   1 root root     7 Nov 13  2020 bin -> usr/bin
   drwxr-xr-x   9 root root  3360 Sep  8 04:09 dev
   drwxr-xr-x   1 root root  4096 Sep  8 04:09 etc
   drwxr-xr-x   1 root root  4096 Sep  8 04:36 home
   lrwxrwxrwx   1 root root     7 Nov 13  2020 lib -> usr/lib
   lrwxrwxrwx   1 root root     9 Nov 13  2020 lib64 -> usr/lib64
   drwxr-xr-x   2 root root  4096 Apr 11  2018 media
   drwxr-xr-x   2 root root  4096 Apr 11  2018 mnt
   drwxr-xr-x   2 root root  4096 Apr 11  2018 opt
   dr-xr-xr-x 176 root root     0 Sep  8 04:09 proc
   dr-xr-x---   2 root root  4096 Nov 13  2020 root
   drwxr-xr-x   3 root root    60 Sep  8 04:09 run
   lrwxrwxrwx   1 root root     8 Nov 13  2020 sbin -> usr/sbin
   drwxr-xr-x   2 root root  4096 Apr 11  2018 srv
   dr-xr-xr-x  11 root root     0 Sep  8 04:09 sys
   drwxrwxrwt   7 root root  4096 Nov 13  2020 tmp
   drwxr-xr-x  13 root root  4096 Nov 13  2020 usr
   drwxr-xr-x  18 root root  4096 Nov 13  2020 var
   [root@ffe5923642b2 home]# mkdir download
   [root@ffe5923642b2 home]# ll
   total 856296
   drwxr-xr-x 2 root root       4096 Sep  8 04:40 download
   ```

   

5. 在主机copy 下载的文件到 docker

   ```shell
   docker cp /Users/qqkk/Downloads/hadoop-3.3.6.tar.gz hadoop01:/home/download/
   docker cp /Users/qqkk/Downloads/jdk-8u421-linux-x64.tar.gz hadoop01:/home/download/
   [root@ffe5923642b2 download]# ll
   total 856292
   -rw-r--r-- 1 501 games 730107476 Sep  8 04:29 hadoop-3.3.6.tar.gz
   -rw-r--r-- 1 501 games 146729827 Sep  8 04:33 jdk-8u421-linux-x64.tar.gz
   ```

   

6. 传好后开始解压 指定解压路径为/usr/local

   ```
   tar -zxvf jdk-8u421-linux-x64.tar.gz -C /usr/local/
   [root@ffe5923642b2 local]# ln -s jdk1.8.0_421/ jdk
   [root@ffe5923642b2 local]# ll
   total 44
   drwxr-xr-x 2 root root 4096 Apr 11  2018 bin
   drwxr-xr-x 2 root root 4096 Apr 11  2018 etc
   drwxr-xr-x 2 root root 4096 Apr 11  2018 games
   drwxr-xr-x 2 root root 4096 Apr 11  2018 include
   lrwxrwxrwx 1 root root   13 Sep  8 04:44 jdk -> jdk1.8.0_421/
   drwxr-xr-x 8 root root 4096 Sep  8 04:43 jdk1.8.0_421
   drwxr-xr-x 2 root root 4096 Apr 11  2018 lib
   drwxr-xr-x 2 root root 4096 Apr 11  2018 lib64
   drwxr-xr-x 2 root root 4096 Apr 11  2018 libexec
   drwxr-xr-x 2 root root 4096 Apr 11  2018 sbin
   drwxr-xr-x 5 root root 4096 Nov 13  2020 share
   drwxr-xr-x 2 root root 4096 Apr 11  2018 src
   ```

接着修改bashrc环境变量文件 在里面配置环境变量这样每次启动环境变量就能自动配置好。

```shell
vim /etc/bashrc 进入修改
#在末尾添加以下内容
#jdk environment
export JAVA_HOME=/usr/local/jdk
export PATH=$JAVA_HOME/bin:$JAVA_HOME/jre/bin:$PATH
source /etc/bashrc 修改完成后运行此命令生效

```

完成上述这些步骤就可以保存一下 把Hadoop01容器导出为镜像 方便配置另外两台hadoop02 hadoop03

```shell
#导出镜像
docker commit -m "first hadoop machine" -a "claem" hadoop01 新镜像名:tag名
#查看镜像列表
docker images
#创建相同容器
docker run -itd --name hadoop02 -p 2202:22 -p 50090:50090 --privileged=true 新镜像名:tag名 /usr/sbin/init
docker run -itd --name hadoop03 -p 2203:22 --privileged=true 新镜像名:tag名 /usr/sbin/init

```
