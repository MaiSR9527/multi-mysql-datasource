文章首发：[MySQL集群实战(一主多从，两主多从到SpringBoot多数据源实战)](http://www.maishuren.top/posts/mysql/2021/03/202103281450.html)

# MySQL主从复制概述

在实际生产中，数据的重要性不言而喻
	
如果我们的数据库只有一台服务器，那么很容易产生单点故障的问题，比如这台服务器访问压力过大而没有响应或者奔溃，那么服务就不可用了，再比如这台服务器的硬盘坏了，那么整个数据库的数据就全部丢失了，这是重大的安全事故.
	
为了避免服务的不可用以及保障数据的安全可靠性，我们至少需要部署两台或两台以上服务器来存储数据库数据，也就是我们需要将数据复制多份部署在多台不同的服务器上，即使有一台服务器出现故障了，其他服务器依然可以继续提供服务.
	
MySQL提供了主从复制功能以提高服务的可用性与数据的安全可靠性.

主从复制是指服务器分为主服务器和从服务器，主服务器负责读和写，从服务器只负责读，主从复制也叫 master/slave，master是主，slave是从，但是并没有强制，也就是说从也可以写，主也可以读，只不过一般我们不这么做。
	
主从复制可以实现对数据库备份和读写分离

## MySQL的主从复制架构

**一主多从架构**

![](https://cdn.jsdelivr.net/gh/MaiSR9527/blog-pic/mysql/mysql-ms.png)

**双主双从结构**

![](https://cdn.jsdelivr.net/gh/MaiSR9527/blog-pic/mysql/project.png)

## MySQL主从复制原理

* 当主库服务器上的数据发生改变时，则将其改变写入二进制事件日志文件中

* 从库服务器会在一定时间间隔内对主库服务器上的二进制日志进行探测，探测其是否发生过改变，如果探测到主库服务器的二进制事件日志发生了改变，则开始一个 I/O Thread 请求 master 二进制事件日志

* 同时主库服务器为每个 I/O Thread 启动一个dump Thread，用于向其发送二进制事件日志

* 从库服务器将接收到的二进制事件日志保存至自己本地的中继日志文件(relaylog)中

* 从库服务器将启动 SQL Thread 从中继日志中读取二进制日志，在本地重放，使得其数据和主服务器保持一致；

* 最后 I/O Thread 和 SQL Thread 将进入睡眠状态，等待下一次被唤醒

![](https://cdn.jsdelivr.net/gh/MaiSR9527/blog-pic/ShardingSphere/replica.png)

**主从复制的过程会有很小的延迟，基本没有影响**

# MySQL多实例搭建

MySQL的多实例就是指安装了MySQL之后，在一台Linux机器上启动多个MySQL实例(主要用于学习)。

## 多实例配置

* 安装mysql，下载mysql的安装包之后，解压。

* 创建mysql用户和用户组

  ```
  groupadd mysql
  useradd -r -g mysql mysql
  chown -R mysql.mysql /usr/local/mysql5.7
  ```

* 创建数据目录用于存放mysql多实例数据。

  ```
  mkdir -p /usr/local/mysql5.7/data/3307
  mkdir -p /usr/local/mysql5.7/data/3308
  mkdir -p /usr/local/mysql5.7/data/3309
  mkdir -p /usr/local/mysql5.7/data/3310
  ```

* 在mysql的解压目录下的bin目录下执行命令:

  ````
  ./mysqld --initialize --console --basedir=/usr/local/mysql5.7 --datadir=/usr/local/mysql5.7/data/3307 --user=mysql
  
  ./mysqld --initialize --console --basedir=/usr/local/mysql5.7 --datadir=/usr/local/mysql5.7/data/3308 --user=mysql
  
  ./mysqld --initialize --console --basedir=/usr/local/mysql5.7 --datadir=/usr/local/mysql5.7/data/3309 --user=mysql
  
  ./mysqld --initialize --console --basedir=/usr/local/mysql5.7 --datadir=/usr/local/mysql5.7/data/33010 --user=mysql
  ````
  
  **注意：--initialize 会随机生成root用户的密码。--console控制台输出初始化日志。这就可以看到生成的root密码。--user指定运行MySQL实例的用户**
  
* 在各自的数据目录下创建配置文件

  ```
  #================3307端口================
  [client]
  port        = 3307
  socket      = /usr/local/mysql5.7/data/3307/mysql.sock
  default-character-set=utf8
  
  [mysqld]
  port    = 3307
  socket  = /usr/local/mysql5.7/data/3307/mysql.sock
  datadir = /usr/local/mysql5.7/data/3307
  log-error = /usr/local/mysql5.7/data/3307/error.log
  pid-file = /usr/local/mysql5.7/data/3307/mysql.pid
  
  character-set-server=utf8
  lower_case_table_names=1
  autocommit = 1
  
  #================3308端口================
  [client]
  port        = 3308
  socket      = /usr/local/mysql5.7/data/3308/mysql.sock
  default-character-set=utf8
  
  [mysqld]
  port    = 3308
  socket  = /usr/local/mysql5.7/data/3308/mysql.sock
  datadir = /usr/local/mysql5.7/data/3308
  log-error = /usr/local/mysql5.7/data/3308/error.log
  pid-file = /usr/local/mysql5.7/data/3308/mysql.pid
  
  character-set-server=utf8
  lower_case_table_names=1
  autocommit = 1
  
  
  #================3309端口================
  [client]
  port        = 3309
  socket      = /usr/local/mysql5.7/data/3309/mysql.sock
  default-character-set=utf8
  
  [mysqld]
  port    = 3309
  socket  = /usr/local/mysql5.7/data/3309/mysql.sock
  datadir = /usr/local/mysql5.7/data/3309
  log-error = /usr/local/mysql5.7/data/3309/error.log
  pid-file = /usr/local/mysql5.7/data/3309/mysql.pid
  
  character-set-server=utf8
  lower_case_table_names=1
  autocommit = 1
  
  
  #================3310端口================
  [client]
  port        = 3310
  socket      = /usr/local/mysql5.7/data/3310/mysql.sock
  default-character-set=utf8
  
  [mysqld]
  port    = 3310
  socket  = /usr/local/mysql5.7/data/3310/mysql.sock
  datadir = /usr/local/mysql5.7/data/3310
  log-error = /usr/local/mysql5.7/data/3310/error.log
  pid-file = /usr/local/mysql5.7/data/3310/mysql.pid
  
  character-set-server=utf8
  lower_case_table_names=1
  autocommit = 1
  ```


## 启动多实例

在MySQL的安装目录下bin目录执行

  ```shell
./mysqld_safe --defaults-file=/usr/local/mysql5.7/data/3307/my.cnf &
  
./mysqld_safe --defaults-file=/usr/local/mysql5.7/data/3308/my.cnf &
  
./mysqld_safe --defaults-file=/usr/local/mysql5.7/data/3309/my.cnf &
  
./mysqld_safe --defaults-file=/usr/local/mysql5.7/data/3310/my.cnf &
  ```

**登录实例**

```shell
./mysql -uroot -p -P3307 -h127.0.0.1
./mysql -uroot -p -P3308 -h127.0.0.1
./mysql -uroot -p -P3309 -h127.0.0.1
./mysql -uroot -p -P3310 -h127.0.0.1
```

**修改密码**

`alter user 'root'@'localhost' identified by '123456;'`

**开启远程访问许可**

`grant all privileges on *.* to root@'%' identified by '123456';`   

其中\*.\* 的第一个*表示所有数据库名，第二个*表示所有的数据库表

root@'%' 中的root表示用户名

%表示所有ip地址，%也可以指定具体的ip地址，比如root@localhost，root@192.168.10.129。

**刷新权限**

`flush privileges;`

## 多实例关闭

```shell
./mysqladmin -uroot -p -P3307 -h127.0.0.1 shutdown
./mysqladmin -uroot -p -P3308 -h127.0.0.1 shutdown
./mysqladmin -uroot -p -P3309 -h127.0.0.1 shutdown
./mysqladmin -uroot -p -P3310 -h127.0.0.1 shutdown
```

# MySQL一主多从架构搭建

## 配置文件

**在MySQL主服务器(3307)配置文件my.cnf里面加入**

log-bin=mysql-bin      #表示启用二进制日志

server-id=3307         #表示server编号，编号要唯一

**在MySQL从服务器(3308)配置文件my.cnf里面加入**

server-id=3308    

**在MySQL从服务器(3309)配置文件my.cnf里面加入**

server-id=3309    

**在MySQL从服务器(3310)配置文件my.cnf里面加入**

server-id=3310 

## 主库配置

1. 登录，`./mysql -uroot -p -P3307 -h127.0.0.1`

2. 创建复制数据的账户并且授权：`grant replication slave on *.* to 'copy'@'%' identified by '123456';`

3. 查看主库状态

   mysql主库状态默认值：`File：mysql-bin.000001`。`Position：154`。因为执行过授权语句，所以偏移量会变化。所以需要充值一下master的偏移量。

   ![](https://cdn.jsdelivr.net/gh/MaiSR9527/blog-pic/mysql/mysql002.png)


## 从库配置

1. 查看从库状态：`show slave status \G;`结果会是：Empty set (0.00 sec)。从库的初始状态。如果不是需要重置：`reset slave;`

2. 设置监听的主库并开始执行复制

   ```shell
   change master to master_host='192.168.79.150',master_user='copy',master_port=3307,master_password='123456',master_log_file='mysql-bin.000001',master_log_pos=154;
   
   start slave;
   ```

   **master_host** ：Master的IP地址

   **master_port**：Master的端口号，指的是容器的端口号

   **master_user**：用于数据同步的用户

   **master_password**：用于同步的用户的密码

   **master_log_file**：指定 Slave 从哪个日志文件开始复制数据，即上文中提到的 File 字段的值

   **master_log_pos**：从哪个 Position 开始读，即上文中提到的 Position 字段的值

3. 查看从库状态：`show slave status;`

   `Slave_IO_Running`和`Slave_SQL_Running`的值为yes说明slave开启成功

   ```
   mysql> show slave status \G;
   *************************** 1. row ***************************
                  Slave_IO_State: Waiting for master to send event
                     Master_Host: 192.168.79.150
                     Master_User: copy
                     Master_Port: 3307
                   Connect_Retry: 60
                 Master_Log_File: mysql-bin.000001
             Read_Master_Log_Pos: 154
                  Relay_Log_File: dev1-relay-bin.000002
                   Relay_Log_Pos: 320
           Relay_Master_Log_File: mysql-bin.000001
                Slave_IO_Running: Yes
               Slave_SQL_Running: Yes
                 Replicate_Do_DB:
             Replicate_Ignore_DB:
              Replicate_Do_Table:
          Replicate_Ignore_Table:
         Replicate_Wild_Do_Table:
     Replicate_Wild_Ignore_Table:
                      Last_Errno: 0
                      Last_Error:
                    Skip_Counter: 0
             Exec_Master_Log_Pos: 154
                 Relay_Log_Space: 526
                 Until_Condition: None
                  Until_Log_File:
                   Until_Log_Pos: 0
              Master_SSL_Allowed: No
              Master_SSL_CA_File:
              Master_SSL_CA_Path:
                 Master_SSL_Cert:
               Master_SSL_Cipher:
                  Master_SSL_Key:
           Seconds_Behind_Master: 0
   Master_SSL_Verify_Server_Cert: No
                   Last_IO_Errno: 0
                   Last_IO_Error:
                  Last_SQL_Errno: 0
                  Last_SQL_Error:
     Replicate_Ignore_Server_Ids:
                Master_Server_Id: 3307
                     Master_UUID: be3a5680-8fdd-11eb-897a-000c297b3e36
                Master_Info_File: /usr/local/mysql5.7/data/3309/master.info
                       SQL_Delay: 0
             SQL_Remaining_Delay: NULL
         Slave_SQL_Running_State: Slave has read all relay log; waiting for more updates
              Master_Retry_Count: 86400
                     Master_Bind:
         Last_IO_Error_Timestamp:
        Last_SQL_Error_Timestamp:
                  Master_SSL_Crl:
              Master_SSL_Crlpath:
              Retrieved_Gtid_Set:
               Executed_Gtid_Set:
                   Auto_Position: 0
            Replicate_Rewrite_DB:
                    Channel_Name:
              Master_TLS_Version:
   1 row in set (0.00 sec)
   ```

## 测试

1. 连接上主库，在主库创建一个数据库，再在此库里面创建一个表并写入一下数据。

2. 为了方便可以使用Navicat之类的软件进行操作

3. 查看在主库创建的表数据库和表及其数据是否存在。数据存在主从模式搭建成功

4. 查看一下主库的binlong：`show binlog events in 'mysql-bin.000001'\G;`

   ```
   mysql> show binlog events in 'mysql-bin.000001'\G;
   *************************** 1. row ***************************
      Log_name: mysql-bin.000001
           Pos: 4
    Event_type: Format_desc
     Server_id: 3307
   End_log_pos: 123
          Info: Server ver: 5.7.24-log, Binlog ver: 4
   *************************** 2. row ***************************
      Log_name: mysql-bin.000001
           Pos: 123
    Event_type: Previous_gtids
     Server_id: 3307
   End_log_pos: 154
          Info:
   *************************** 3. row ***************************
      Log_name: mysql-bin.000001
           Pos: 154
    Event_type: Anonymous_Gtid
     Server_id: 3307
   End_log_pos: 219
          Info: SET @@SESSION.GTID_NEXT= 'ANONYMOUS'
   *************************** 4. row ***************************
      Log_name: mysql-bin.000001
           Pos: 219
    Event_type: Query
     Server_id: 3307
   End_log_pos: 368
          Info: CREATE DATABASE `shop` CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci'
   *************************** 5. row ***************************
      Log_name: mysql-bin.000001
           Pos: 368
    Event_type: Anonymous_Gtid
     Server_id: 3307
   End_log_pos: 433
          Info: SET @@SESSION.GTID_NEXT= 'ANONYMOUS'
   *************************** 6. row ***************************
      Log_name: mysql-bin.000001
           Pos: 433
    Event_type: Query
     Server_id: 3307
   End_log_pos: 628
          Info: use `shop`; CREATE TABLE `shop`.`order`  (
     `id` int NOT NULL AUTO_INCREMENT,
     `name` varchar(255) NULL,
     PRIMARY KEY (`id`)
   )
   *************************** 7. row ***************************
      Log_name: mysql-bin.000001
           Pos: 628
    Event_type: Anonymous_Gtid
     Server_id: 3307
   End_log_pos: 693
          Info: SET @@SESSION.GTID_NEXT= 'ANONYMOUS'
   *************************** 8. row ***************************
      Log_name: mysql-bin.000001
           Pos: 693
    Event_type: Query
     Server_id: 3307
   End_log_pos: 765
          Info: BEGIN
   *************************** 9. row ***************************
      Log_name: mysql-bin.000001
           Pos: 765
    Event_type: Table_map
     Server_id: 3307
   End_log_pos: 816
          Info: table_id: 109 (shop.order)
   *************************** 10. row ***************************
      Log_name: mysql-bin.000001
           Pos: 816
    Event_type: Write_rows
     Server_id: 3307
   End_log_pos: 862
          Info: table_id: 109 flags: STMT_END_F
   *************************** 11. row ***************************
      Log_name: mysql-bin.000001
           Pos: 862
    Event_type: Xid
     Server_id: 3307
   End_log_pos: 893
          Info: COMMIT /* xid=94 */
   *************************** 12. row ***************************
      Log_name: mysql-bin.000001
           Pos: 893
    Event_type: Anonymous_Gtid
     Server_id: 3307
   End_log_pos: 958
          Info: SET @@SESSION.GTID_NEXT= 'ANONYMOUS'
   *************************** 13. row ***************************
      Log_name: mysql-bin.000001
           Pos: 958
    Event_type: Query
     Server_id: 3307
   End_log_pos: 1030
          Info: BEGIN
   *************************** 14. row ***************************
      Log_name: mysql-bin.000001
           Pos: 1030
    Event_type: Table_map
     Server_id: 3307
   End_log_pos: 1081
          Info: table_id: 109 (shop.order)
   *************************** 15. row ***************************
      Log_name: mysql-bin.000001
           Pos: 1081
    Event_type: Write_rows
     Server_id: 3307
   End_log_pos: 1126
          Info: table_id: 109 flags: STMT_END_F
   *************************** 16. row ***************************
      Log_name: mysql-bin.000001
           Pos: 1126
    Event_type: Xid
     Server_id: 3307
   End_log_pos: 1157
          Info: COMMIT /* xid=97 */
   16 rows in set (0.00 sec)
   ```

5. 查看slave的状态

   ```
   mysql> mysql> show slave status \G;
   *************************** 1. row ***************************
                  Slave_IO_State: Waiting for master to send event
                     Master_Host: 192.168.79.150
                     Master_User: copy
                     Master_Port: 3307
                   Connect_Retry: 60
                 Master_Log_File: mysql-bin.000001
             Read_Master_Log_Pos: 1157
                  Relay_Log_File: dev1-relay-bin.000002
                   Relay_Log_Pos: 1323
           Relay_Master_Log_File: mysql-bin.000001
                Slave_IO_Running: Yes
               Slave_SQL_Running: Yes
                 Replicate_Do_DB:
             Replicate_Ignore_DB:
              Replicate_Do_Table:
          Replicate_Ignore_Table:
         Replicate_Wild_Do_Table:
     Replicate_Wild_Ignore_Table:
                      Last_Errno: 0
                      Last_Error:
                    Skip_Counter: 0
             Exec_Master_Log_Pos: 1157
                 Relay_Log_Space: 1529
                 Until_Condition: None
                  Until_Log_File:
                   Until_Log_Pos: 0
              Master_SSL_Allowed: No
              Master_SSL_CA_File:
              Master_SSL_CA_Path:
                 Master_SSL_Cert:
               Master_SSL_Cipher:
                  Master_SSL_Key:
           Seconds_Behind_Master: 0
   Master_SSL_Verify_Server_Cert: No
                   Last_IO_Errno: 0
                   Last_IO_Error:
                  Last_SQL_Errno: 0
                  Last_SQL_Error:
     Replicate_Ignore_Server_Ids:
                Master_Server_Id: 3307
                     Master_UUID: be3a5680-8fdd-11eb-897a-000c297b3e36
                Master_Info_File: /usr/local/mysql5.7/data/3308/master.info
                       SQL_Delay: 0
             SQL_Remaining_Delay: NULL
         Slave_SQL_Running_State: Slave has read all relay log; waiting for more updates
              Master_Retry_Count: 86400
                     Master_Bind:
         Last_IO_Error_Timestamp:
        Last_SQL_Error_Timestamp:
                  Master_SSL_Crl:
              Master_SSL_Crlpath:
              Retrieved_Gtid_Set:
               Executed_Gtid_Set:
                   Auto_Position: 0
            Replicate_Rewrite_DB:
                    Channel_Name:
              Master_TLS_Version:
   1 row in set (0.00 sec)
   ```

## 注意事项

在主库写入数据，数据会同步到从库中，但是当在从库写入一条数据时并不会同步到其他从库和主库中。当一个表的数据的主键是自增的。当主库插入数据时，单独在从库写入的数据和主库同步过来的数据的主键发生冲突时，该从库的数据不会显示主库同步过来的数据。此时从库的状态中：

```shell
Read_Master_Log_Pos: 1157  # 这一项依旧会随着主库的写入而变化，即同步主库的数据。
Relay_Log_Pos: 1323        # 这一项会停止不动，这是中继日志的操作。数据会无法回放
Slave_IO_Running: Yes
Slave_SQL_Running: NO      # 这一项会变成No。
```

查看从库的状态。Last_Error字段就会报错。

![](https://cdn.jsdelivr.net/gh/MaiSR9527/blog-pic/mysql/mysql003.jpg)

解决方法：

1. 删掉从库上与主库冲突的数据。然后停止`stop slave;`，再重启`start slave;`。这时候就会重新生成中继文件，重新同步数据。
2. 删除掉从库上的整个表或数据库，重新添加主库。不过这样的操作会复制很多的数据，毕竟整个表和数据库都被删除了。

# MySQL多主多从架构搭建

上面一主多从的架构，如果主库的服务器宕机了，写操作就会完成不了，读操作时可以的(因为一般MySQL的主从架构就是用于读写分离的)。这样一个主库的架构就会出现单点故障了。

![](https://cdn.jsdelivr.net/gh/MaiSR9527/blog-pic/mysql/mysql-multi-ms.png)





**3307端口的MySQL为主库，3309端口的MySQL为其从库**

**3308端口的MySQL为主库，3310端口的MySQL为其从库**

**3307端口的MySQL和3309端口的MySQL护卫主从**

## 配置文件的配置

**在第一台主服务器3307端口的MySQL的my.cnf文件增加如下配置**

```
auto_increment_increment=2

auto_increment_offset=1

log-slave-updates

sync_binlog=1
```



**在第二台主服务器3308端口的MySQL的my.cnf文件增加如下配置**    

```
auto_increment_increment=2

auto_increment_offset=2

log-slave-updates

sync_binlog=1
```

## 配置项说明

1. auto_increment_increment

   控制主键自增的自增步长，用于防止Master与Master之间复制出现重复自增字段值，通常auto_increment_increment=n，有多少台主服务器，n 就设置为多少
   
2. auto_increment_offset＝1

   设置自增起始值，这里设置为1，这样Master的auto_increment字段产生的数值是：1, 3, 5, 7, …等奇数ID

   auto_increment_offset的设置，不同的master设置不应该一样，否则就容易引起主键冲突，比如master1的offset=1，则master2的offset=2，master3的offset=3

3. log-slave-updates

   在双主模式中，log-slave-updates 配置项一定要配置，否则在master1（3307端口）上进行了更新数据，在master2（3308端口）和slave1（3309端口）上会更新，但是在slave2（3310端口）上不会更新

4. sync_binlog

   表示每几次事务提交，MySQL把binlog缓存刷进日志文件中，默认是0，最安全的是设置为1。

   sync_binlog=0，当事务提交之后，MySQL不做fsync之类的磁盘同步指令刷新binlog_cache中的信息到磁盘，而让Filesystem自行决定什么时候来做同步，或者cache满了之后才同步到磁盘

   sync_binlog=n，当每进行n次事务提交之后，MySQL将进行一次fsync之类的磁盘同步指令来将binlog_cache中的数据强制写入磁盘

**注意事项**

从库只开启log-bin功能，不添加log-slave-updates参数，从库从主库复制的数据不会写入log-bin日志文件里。开启log-slave-updates参数后，从库从主库复制的数据会写入log-bin日志文件里。这也是该参数的功能，直接向从库写入数据时，是会写入log-bin日志的。
	
在自动生成主键的时候，会在已生成主键的基础上按照规则生成，即比存在的值大。

## 主从库的配置

1. 启动分别启动MySQL

   ```shell
   ./mysqld_safe --defaults-file=/usr/local/mysql5.7/data/3307/my.cnf &
     
   ./mysqld_safe --defaults-file=/usr/local/mysql5.7/data/3308/my.cnf &
     
   ./mysqld_safe --defaults-file=/usr/local/mysql5.7/data/3309/my.cnf &
     
   ./mysqld_safe --defaults-file=/usr/local/mysql5.7/data/3310/my.cnf &
   ```

2. 在3308端口的MySQL里执行：`grant replication slave on *.* to 'copy'@'%' identified by '123456';`由于3307端口的MySQL已经执行过。

3. 重置Master：`reset master;`不重置也可以，再从库监听主库的时候，制定好biglog的名字就好。重置后都是：`mysql-bin.000001`。

4. 在3308端口的MySQL执行，因为3308端口、3309端口和3310端口在上面设置过主从配置。

   ```
   stop slave;
   reset salve;
   ```

5. 从库监听主库

   ```shell
   #==============在3307端口和3310端口的MySQL里执行===============
   change master to master_host='192.168.79.150',master_user='copy',master_port=3308,master_password='123456',master_log_file='mysql-bin.000001',master_log_pos=154;
   
   start slave;
   
   #==============在3308端口和3309端口的MySQL里执行===============
   change master to master_host='192.168.79.150',master_user='copy',master_port=3307,master_password='123456',master_log_file='mysql-bin.000001',master_log_pos=154;
   
   start slave;
   ```

   ## 测试

   跟上一节的一主多从的测试一样。

# 代码实战：多数据源配置

当我们的MySQL集群搭建好了之后，那就要在代码里面使用了。一般我们在写代码时，数据源都是只设置了一个。现在这么多个数据源，又要怎么使用呢！
	
有两种情况：1、做读写分离。2、只操作主库，从库只是当作备份。

## 方案一：MyBatis的Mapper包隔离

### 核心原理

基于Mapper包的隔离，每个Mapper包操作不同的数据库，每个Mapper包对应一个数据库。

### 代码实现

**pom.xml：**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.msr</groupId>
    <artifactId>spring-mybatis-mutil-datasource</artifactId>
    <version>1.0</version>

    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
    </properties>

    <dependencies>
        <!--Spring相关的依赖-->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jdbc</artifactId>
            <version>5.1.4.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-tx</artifactId>
            <version>5.1.4.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>5.1.4.RELEASE</version>
        </dependency>

        <!--Mybatis框架依赖-->
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.4.6</version>
        </dependency>
        <!--Mybatis与Spring整合依赖-->
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis-spring</artifactId>
            <version>1.3.2</version>
        </dependency>
        <!--MySQL数据库连接驱动 版本不要过高-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.43</version>
        </dependency>
        <!--JDBC 数据库连接池-->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid</artifactId>
            <version>1.1.1</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.mybatis.generator</groupId>
                <artifactId>mybatis-generator-maven-plugin</artifactId>
                <version>1.3.6</version>
                <configuration>
                    <!--配置文件的位置-->
                    <configurationFile>GeneratorMapper.xml</configurationFile>
                    <verbose>true</verbose>
                    <overwrite>true</overwrite>
                </configuration>
            </plugin>
        </plugins>
        <resources>
            <resource>
                <directory>src/main/java</directory>
                <includes>
                    <include>**/*.xml</include>
                </includes>
            </resource>

        </resources>
    </build>
</project>
```

**配置文件：**

datasource.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!--配置主库的数据源 -->
    <bean id="masterDruidDataSource" class="com.alibaba.druid.pool.DruidDataSource">
        <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://192.168.79.150:3307/shop"/>
        <property name="username" value="root"/>
        <property name="password" value="123456"/>
    </bean>
    <!--配置主库的连接工厂 -->
    <bean id="masterSqlSessionFactoryBean" class="org.mybatis.spring.SqlSessionFactoryBean">
        <property name="dataSource" ref="masterDruidDataSource"/>
    </bean>
    <!--配置主库的包扫描 -->
    <bean id="masterMapperScannerConfigurer" class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <property name="sqlSessionFactoryBeanName" value="masterSqlSessionFactoryBean"/>
        <property name="basePackage" value="org.msr.masterslave.master.mapper"/>
    </bean>


    <!--配置从库的数据源 -->
    <bean id="slaveDruidDataSource" class="com.alibaba.druid.pool.DruidDataSource">
        <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://192.168.79.150:3309/shop"/>
        <property name="username" value="root"/>
        <property name="password" value="123456"/>
    </bean>
    <!--配置从库的连接工厂 -->
    <bean id="slaveSqlSessionFactoryBean" class="org.mybatis.spring.SqlSessionFactoryBean">
        <property name="dataSource" ref="slaveDruidDataSource"/>
    </bean>
    <!--配置从库的包扫描 -->
    <bean id="slaveMapperScannerConfigurer" class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <property name="sqlSessionFactoryBeanName" value="slaveSqlSessionFactoryBean"/>
        <property name="basePackage" value="org.msr.masterslave.slave.mapper"/>
    </bean>
</beans>
```

application.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context.xsd
        ">
    <import resource="classpath:datasource.xml"/>
    <context:component-scan base-package="org.msr"/>
</beans>
```

**mapper：**

master库的mapper

```java
package org.msr.masterslave.master.mapper;

import org.msr.masterslave.model.UserInfo;


public interface MasterUserInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserInfo record);

    int insertSelective(UserInfo record);

    int updateByPrimaryKeySelective(UserInfo record);

    int updateByPrimaryKey(UserInfo record);
}
```

MasterUserInfoMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="org.msr.masterslave.master.mapper.MasterOrderMapper">
    <resultMap id="BaseResultMap" type="org.msr.masterslave.model.UserInfo">
        <id column="id" jdbcType="INTEGER" property="id"/>
        <result column="name" jdbcType="VARCHAR" property="name"/>
    </resultMap>
    <sql id="Base_Column_List">
        id
        , name
    </sql>
    <delete id="deleteByPrimaryKey" parameterType="java.lang.Integer">
        delete
        from userInfo
        where id = #{id,jdbcType=INTEGER}
    </delete>
    <insert id="insert" parameterType="org.msr.masterslave.model.UserInfo">
        insert into userInfo (id, name)
        values (#{id,jdbcType=INTEGER}, #{name,jdbcType=VARCHAR})
    </insert>
    <insert id="insertSelective" parameterType="org.msr.masterslave.model.UserInfo">
        insert into userInfo
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <if test="id != null">
                id,
            </if>
            <if test="name != null">
                name,
            </if>
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
            <if test="id != null">
                #{id,jdbcType=INTEGER},
            </if>
            <if test="name != null">
                #{name,jdbcType=VARCHAR},
            </if>
        </trim>
    </insert>
    <update id="updateByPrimaryKeySelective" parameterType="org.msr.masterslave.model.UserInfo">
        update userInfo
        <set>
            <if test="name != null">
                name = #{name,jdbcType=VARCHAR},
            </if>
        </set>
        where id = #{id,jdbcType=INTEGER}
    </update>
    <update id="updateByPrimaryKey" parameterType="org.msr.masterslave.model.UserInfo">
        update userInfo
        set name = #{name,jdbcType=VARCHAR}
        where id = #{id,jdbcType=INTEGER}
    </update>
</mapper>
```

slave库的mapper

```java
package org.msr.masterslave.slave.mapper;

import org.msr.masterslave.model.UserInfo;

public interface SlavUserInfoMapper {
    UserInfo selectByPrimaryKey(Integer id);
}
```

SlaveUserInfoMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="org.msr.masterslave.slave.mapper.SlaveOrderMapper">
    <resultMap id="BaseResultMap" type="org.msr.masterslave.model.UserInfo">
        <id column="id" jdbcType="INTEGER" property="id"/>
        <result column="name" jdbcType="VARCHAR" property="name"/>
    </resultMap>
    <sql id="Base_Column_List">
        id
        , name
    </sql>
    <select id="selectByPrimaryKey" parameterType="java.lang.Integer" resultMap="BaseResultMap">
        select
        <include refid="Base_Column_List"/>
        from userInfo
        where id = #{id,jdbcType=INTEGER}
    </select>
</mapper>
```

**model代码：**

```java
package org.msr.masterslave.model;

import java.io.Serializable;

public class UserInfo implements Serializable {
    private Integer id;

    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
```

**service代码：**

```java
package org.msr.masterslave.service;

import org.msr.masterslave.master.mapper.MasterOrderMapper;
import org.msr.masterslave.model.UserInfo;
import org.msr.masterslave.slave.mapper.SlaveOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author MaiShuRen
 * @site http://www.maishuren.top
 * @since 2021-03-28 23:17
 **/
@Service("orderService")
public class OrderService {
    @Autowired
    private MasterUserInfoMapper masterUserInfoMapper;

    @Autowired
    private SlaveUserInfoMapper slaveUserInfoMapper;

    public UserInfo read(Integer id) {
        return slaveOrderMapper.selectByPrimaryKey(id);
    }

    public int write(UserInfo userInfo){
        return masterOrderMapper.insert(userInfo);
    }
}
```

**测试：**

```java
package org.msr.masterslave;

import org.msr.masterslave.model.UserInfo;
import org.msr.masterslave.service.OrderService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author MaiShuRen
 * @site http://www.maishuren.top
 * @since 2021-03-28 23:32
 **/
public class Main {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        // 插入数据
        UserInfo userInfo = new UserInfo();
        userInfo.setName("hahaha");
        int write = orderService.write(userInfo);
        System.out.println(write);
        // 根据id查询
        UserInfoService userInfoService = applicationContext.getBean(UserInfoService.class);
        UserInfo read = userInfoService.read(1);
        System.out.println(read.toString());
        
    }
}
```

### 缺点

1、有很多的重复代码例如Spring中的配置以及多套Mapper映射中的代码，每一个数据库基本上都应该拥有独立的配置

2、切换主从进行读写操作时需要程序认为手动选择对应的类，这样就有可能会形成向从库写数据向主库读数据

3、如果有很多个从节点，那么我们没有办法进行负载均衡

4、如果某个节点崩溃我们不能切换到另外一台节点中，不能故障转移

## 方案二：动态数据源(基于ThreadLocal)

### 核心原理

自定义数据源并继承`AbstractRoutingDataSource`实现其`protected Object determineCurrentLookupKey()`方法。使用`ThreadLocal`的线程隔离动态设置数据源的key来达到数据源切换。

### 代码实现

**pom.xml同方案一**

**一套的mapper：**

UserInfoMapper

```java
package org.msr.masterslave.mapper;

import org.msr.masterslave.model.UserInfo;

public interface UserInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserInfo record);

    int insertSelective(UserInfo record);

    UserInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserInfo record);

    int updateByPrimaryKey(UserInfo record);
}
```

UserInfoMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="org.msr.masterslave.mapper.UserInfoMapper">
    <resultMap id="BaseResultMap" type="org.msr.masterslave.model.UserInfo">
        <id column="id" jdbcType="INTEGER" property="id"/>
        <result column="name" jdbcType="VARCHAR" property="name"/>
    </resultMap>
    <sql id="Base_Column_List">
        id
        , name
    </sql>
    <select id="selectByPrimaryKey" parameterType="java.lang.Integer" resultMap="BaseResultMap">
        select
        <include refid="Base_Column_List"/>
        from userInfo
        where id = #{id,jdbcType=INTEGER}
    </select>
    <delete id="deleteByPrimaryKey" parameterType="java.lang.Integer">
        delete
        from userInfo
        where id = #{id,jdbcType=INTEGER}
    </delete>
    <insert id="insert" parameterType="org.msr.masterslave.model.UserInfo">
        insert into userInfo (id, name)
        values (#{id,jdbcType=INTEGER}, #{name,jdbcType=VARCHAR})
    </insert>
    <insert id="insertSelective" parameterType="org.msr.masterslave.model.UserInfo">
        insert into userInfo
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <if test="id != null">
                id,
            </if>
            <if test="name != null">
                name,
            </if>
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
            <if test="id != null">
                #{id,jdbcType=INTEGER},
            </if>
            <if test="name != null">
                #{name,jdbcType=VARCHAR},
            </if>
        </trim>
    </insert>
    <update id="updateByPrimaryKeySelective" parameterType="org.msr.masterslave.model.UserInfo">
        update userInfo
        <set>
            <if test="name != null">
                name = #{name,jdbcType=VARCHAR},
            </if>
        </set>
        where id = #{id,jdbcType=INTEGER}
    </update>
    <update id="updateByPrimaryKey" parameterType="org.msr.masterslave.model.UserInfo">
        update userInfo
        set name = #{name,jdbcType=VARCHAR}
        where id = #{id,jdbcType=INTEGER}
    </update>
</mapper>
```

**model代码：UserInfo同方案一**

**service代码：**

UserInfoService

```java
package org.msr.masterslave.service;

import org.msr.masterslave.config.MyDataSource;
import org.msr.masterslave.mapper.UserInfoMapper;
import org.msr.masterslave.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author MaiShuRen
 * @site http://www.maishuren.top
 * @since 2021-03-28 23:17
 **/
@Service("orderService")
public class UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    public UserInfo read(Integer id) {
        MyDataSource.setDataSource("slave");
        return userInfoMapper.selectByPrimaryKey(id);
    }

    public int write(UserInfo userInfo) {
        MyDataSource.setDataSource("master");
        return userInfoMapper.insert(userInfo);
    }
}
```

**测试：**

```java
package org.msr.masterslave;

import org.msr.masterslave.model.UserInfo;
import org.msr.masterslave.service.UserInfoService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author MaiShuRen
 * @site http://www.maishuren.top
 * @since 2021-03-28 23:32
 **/
public class Main {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        UserInfoService userInfoService = applicationContext.getBean(UserInfoService.class);
        // 读数据
        UserInfo read = userInfoService.read(3);
        System.out.println(read.toString());
        // 写数据
        UserInfo userInfo = new UserInfo();
        userInfo.setName("hahaha");
        int write = userInfoService.write(userInfo);
        System.out.println(write);
    }
}
```

### 代码图解执行原理

![](https://cdn.jsdelivr.net/gh/MaiSR9527/blog-pic/mysql/mysql004.png)

### 缺点

动态多数据源，这种方案我们需要自定义一个动态数据源的类，可以在程序运行过程中动态切换数据源

1、有很多的重复代码例如Spring中的配置多个数据源的配置Bean

2、切换主从进行读写操作时需要程序认为手动设置ThreadLocal中数据，这样就有可能会形成向从库写数据向主库读数据

3、如果有很多个从节点，那么我们没有办法进行负载均衡

4、如果某个节点崩溃我们不能切换到另外一台节点中，不能故障转移

## 方案三：使用Springboot

其实使用Springboot也只是对上面两个方案进行SpringBoot的改写而已。也可以说是上面的两个方案的Springboot实现。

### 改造方案一

#### 核心原理

也是使用mapper的包隔离，写法是使用SpringBoot(约定大于配置)的注解进行配置

#### 代码实现

pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.3.9.RELEASE</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>org.msr.masterslave</groupId>
	<artifactId>springboot-multi-datasource-1</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>springboot-multi-datasource-1</name>
	<description>Demo project for Spring Boot</description>
	<properties>
		<java.version>1.8</java.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter</artifactId>
		</dependency>

		<!-- 加载mybatis整合springboot -->
		<dependency>
			<groupId>org.mybatis.spring.boot</groupId>
			<artifactId>mybatis-spring-boot-starter</artifactId>
			<!--在springboot的父工程中没有指定版本，我们需要手动指定-->
			<version>1.3.2</version>
		</dependency>
		<!-- MySQL的jdbc驱动包 -->
		<dependency>
			<groupId>mysql</groupId>
			<!--在springboot的父工程中指定了版本，我们就不需要手动指定了-->
			<artifactId>mysql-connector-java</artifactId>
		</dependency>
		<!--JDBC 数据库连接池-->
		<dependency>
			<groupId>com.alibaba</groupId>
			<artifactId>druid</artifactId>
			<version>1.1.1</version>
		</dependency>
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-configuration-processor</artifactId>
			<optional>true</optional>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>
	<resources>
		<resource>
			<directory>src/main/java</directory>
			<includes>
				<include>**/*.xml</include>
			</includes>
		</resource>
	</resources>
</project>
```

model、service、mapper的代码和方案一一致。对于xml的配置使用配置类来完成

配置文件：application.properties

```properties
#3307数据库的连接配置信息
master.datasource.url=jdbc:mysql://192.168.79.150:3307/test?useUnicode=true&characterEncoding=utf8&useSSL=false
master.datasource.driver=com.mysql.cj.jdbc.Driver
master.datasource.username=root
master.datasource.password=123456

#3309数据库的连接配置信息
slave.datasource.url=jdbc:mysql://192.168.79.150:3309/test?useUnicode=true&characterEncoding=utf8&useSSL=false
slave.datasource.driver=com.mysql.cj.jdbc.Driver
slave.datasource.username=root
slave.datasource.password=123456
```

MasterDataSourceConfig

```java
package org.msr.masterslave.config;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Getter;
import lombok.Setter;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author MaiShuRen
 * @site http://www.maishuren.top
 * @since 2021-03-28 23:32
 */
@Getter
@Setter
@Configuration
@MapperScan(basePackages = {"org.msr.masterslave.master.mapper"}, sqlSessionFactoryRef = "masterSqlSessionFactoryBean")
public class MasterDataSourceConfig {

    @Value("master.datasource.username")
    private String masterUsername;
    @Value("master.datasource.password")
    private String masterPassword;
    @Value("master.datasource.driver")
    private String masterDriver;
    @Value("master.datasource.url")
    private String masterUrl;

    /**
     * 配置master数据源
     *
     * @return
     */
    @Bean
    public DruidDataSource masterDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(masterUrl);
        druidDataSource.setUsername(masterUsername);
        druidDataSource.setPassword(masterPassword);
        druidDataSource.setDriverClassName(masterDriver);
        return druidDataSource;
    }

    @Bean
    public SqlSessionFactoryBean masterSqlSessionFactoryBean(DruidDataSource masterDataSource) {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(masterDataSource);
        return sqlSessionFactoryBean;
    }
}
```

SlaveDataSourceConfig

```java
package org.msr.masterslave.config;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Getter;
import lombok.Setter;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author MaiShuRen
 * @site http://www.maishuren.top
 * @since 2021-03-28 23:32
 */
@Getter
@Setter
@Configuration
@MapperScan(basePackages = {"org.msr.masterslave.slave.mapper"}, sqlSessionFactoryRef = "slaveSqlSessionFactoryBean")
public class SlaveDataSourceConfig {

    @Value("slave.datasource.username")
    private String slaveUsername;
    @Value("slave.datasource.password")
    private String slavePassword;
    @Value("slave.datasource.driver")
    private String slaveDriver;
    @Value("slave.datasource.url")
    private String slaveUrl;

    /**
     * 配置slave数据源
     *
     * @return
     */
    @Bean
    public DruidDataSource slaveDruidDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(slaveDriver);
        dataSource.setUrl(slaveUrl);
        dataSource.setUsername(slaveUsername);
        dataSource.setPassword(slavePassword);
        return dataSource;
    }

    @Bean
    public SqlSessionFactoryBean slaveSqlSessionFactoryBean(DruidDataSource slaveDruidDataSource) {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(slaveDruidDataSource);
        return sqlSessionFactoryBean;
    }
}
```

测试

```java
package org.msr.masterslave;

import org.msr.masterslave.model.UserInfo;
import org.msr.masterslave.service.UserInfoService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MultiDatasource1Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(MultiDatasource1Application.class, args);
        UserInfoService userInfoService = applicationContext.getBean(UserInfoService.class);

        UserInfo userInfo = new UserInfo();
        userInfo.setName("springboot");
        int write = userInfoService.write(userInfo);
        System.out.println(write);

        UserInfo read = userInfoService.read(1);
        System.out.println(read.toString());
    }

}
```

#### 缺点

同方案一

### 改造方案二

#### 核心原理

同方案二

#### 代码实现

pom.xml的maven依赖和配置文件application.properties同上一节、model、service、mapper的代码同方案和一致。

**动态数据源Springboot实现**

MyDataSource

```java
package org.msr.masterslave.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * author: MaiShuRen
 * site: http://www.maishuren.top
 * since: 2021-03-29 23:01
 **/
public class MyDataSource extends AbstractRoutingDataSource {
    public static final ThreadLocal<String> LOCAL = new ThreadLocal<>();
    @Override
    protected Object determineCurrentLookupKey() {
        return getDataSource();
    }

    public static void setDataSource(String dataSourceKey) {
        LOCAL.set(dataSourceKey);
    }

    public static String getDataSource() {
        return LOCAL.get();
    }
}
```

MyDataSourceConfig

```java
package org.msr.masterslave.config;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Getter;
import lombok.Setter;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author MaiShuRen
 * @site http://www.maishuren.top
 * @since 2021-03-29 23:01
 **/
@Getter
@Setter
@Configuration
@MapperScan(basePackages = {"org.msr.masterslave.mapper"}, sqlSessionFactoryRef = "sqlSessionFactoryBean")
public class MyDataSourceConfig {

    @Value("master.datasource.username")
    private String masterUsername;
    @Value("master.datasource.password")
    private String masterPassword;
    @Value("master.datasource.driver")
    private String masterDriver;
    @Value("master.datasource.url")
    private String masterUrl;

    @Value("slave.datasource.username")
    private String slaveUsername;
    @Value("slave.datasource.password")
    private String slavePassword;
    @Value("slave.datasource.driver")
    private String slaveDriver;
    @Value("slave.datasource.url")
    private String slaveUrl;

    @Bean
    public DruidDataSource masterDruidDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(masterDriver);
        druidDataSource.setUrl(masterUrl);
        druidDataSource.setUsername(masterUsername);
        druidDataSource.setPassword(masterPassword);
        return druidDataSource;
    }

    @Bean
    public DruidDataSource salveDruidDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(slaveDriver);
        druidDataSource.setUrl(slaveUrl);
        druidDataSource.setUsername(slaveUsername);
        druidDataSource.setPassword(slavePassword);
        return druidDataSource;
    }

    @Bean
    public MyDataSource myDataSource(DruidDataSource masterDruidDataSource, DruidDataSource salveDruidDataSource) {
        MyDataSource myDataSource = new MyDataSource();
        myDataSource.setDefaultTargetDataSource(masterDruidDataSource);
        Map dataSourceMap = new HashMap(16);
        dataSourceMap.put("master", masterDruidDataSource);
        dataSourceMap.put("slave", salveDruidDataSource);
        myDataSource.setTargetDataSources(dataSourceMap);
        return myDataSource;
    }

    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean(MyDataSource myDataSource) {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(myDataSource);
        return sqlSessionFactoryBean;
    }
}
```

**测试**

```java
package org.msr.masterslave;

import org.msr.masterslave.model.UserInfo;
import org.msr.masterslave.service.UserInfoService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SpringbootDynamicDatasourceApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(SpringbootDynamicDatasourceApplication.class, args);
        UserInfoService userInfoService = applicationContext.getBean(UserInfoService.class);
        UserInfo userInfo = new UserInfo();
        userInfo.setName("springboot");
        int write = userInfoService.write(userInfo);
        System.out.println(write);

        UserInfo read = userInfoService.read(1);
        System.out.println(read.toString());
    }

}
```

#### 缺点

也是和方案一样。

## 多数据数据源总结

看到了上面四种的实现，可以看出他们之间没有差别不是很大，从方案一的问题一直到最后都没有解决掉。 如果需要解决上面的问题需要我们自己写一套管理代码来动态的切换数据源进行读写分离，故障转移以及负载均衡。这样代码就复杂起来的。这时候就要用到数据库中间件了，例如Apache ShardingSphere和MyCat。我个人更加喜欢用ShardingSphere。
	
Apache ShardingSphere 是一套开源的分布式数据库解决方案组成的生态圈，它由 JDBC、Proxy 和 Sidecar（规划中）这 3 款既能够独立部署，又支持混合部署配合使用的产品组成。 它们均提供标准化的数据水平扩展、分布式事务和分布式治理等功能，可适用于如 Java 同构、异构语言、云原生等各种多样化的应用场景。

推荐阅读链接：[数据库中间件ShardingSphere的使用](http://www.maishuren.top/posts/middleware/2020/06/202006261803.html)
