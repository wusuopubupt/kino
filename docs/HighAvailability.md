###服务高可用方案

#### 1. KeepAlived
Keepalived是一个基于**VRRP**协议来实现的服务高可用方案，可以利用其来避免IP单点故障，类似的工具还有heartbeat、corosync、pacemaker。但是它一般不会单独出现，而是与其它负载均衡技术（如lvs、haproxy、nginx）一起工作来达到集群的高可用

##### 以KeepAlived + Nginx实现服务高可用为例：

配置：

* /etc/keepalived/check_nginx.sh


```shell
#!/bin/bash
# 该脚本检测ngnix的运行状态，并在nginx进程不存在时尝试重新启动ngnix，如果启动失败则停止keepalived，准备让其它机器接管。
counter=$(ps -C nginx --no-heading|wc -l)
if [ "${counter}" = "0" ]; then
    /usr/local/bin/nginx
    sleep 2
    counter=$(ps -C nginx --no-heading|wc -l)
    if [ "${counter}" = "0" ]; then
        /etc/init.d/keepalived stop
    fi
fi
```

* keepalived.conf:

``` shell
! Configuration File for keepalived
global_defs {
    notification_email {
        zhangsan@example.com
        lisi@example.com
    }
    notification_email_from from@example.com
    smtp_server mail.example.com
    smtp_connect_timeout 30
    router_id LVS_DEVEL
}
vrrp_script chk_nginx {
    script "/etc/keepalived/check_nginx.sh"  # 健康检查脚本，自定义
    interval 2
    weight -5 # 检测失败（脚本返回非0）则优先级 -5
    fall 3    # 检测连续3次失败才算确定是真失败
    rise 2    # 
}
vrrp_instance VI_1 {
    state MASTER  # 实例状态，目前是Master
    interface eth0
    mcast_src_ip 172.29.88.224
    virtual_router_id 51
    priority 101  # 优先级，最高的是Master，如果检查到服务失败，则降低优先级，让其他备机重新选主,VIP漂移到新主
    advert_int 2
    authentication {
        auth_type PASS
        auth_pass 1111
    }
    #虚拟IP地址，他随着state的变化而增加删除，当state为master的时候就添加，当state为backup的时候删除，这里主要是有优先级来决定的，和state设置的值没有多大关系，这里可以设置多个IP地址
    virtual_ipaddress {
        172.29.88.222
    }
    # 对上面定义的vrrp_script的引用
    track_script {
       chk_nginx
    }
}

```


#### 2. 