#ThreadLocal
 
### 1. ThreadLocal是什么?

是ThreadLocalVariable(线程局部变量), 提供线程绑定对象机制，并通过线程绑定概念简化参数传递，使每一个线程都可以独立地改变自己的副本，而不会和其它线程的副本冲突。
 
### 2. ThreadLocal与其它同步机制的比较

**相同点：**
 
ThreadLocal和其它所有的同步机制都是为了解决多线程中的对同一变量的访问冲突。
 
**不同点：**
 
**同步机制**通过对象的锁机制保证同一时间只有一个线程访问变量。这时该变量是多个线程共享的，使用同步机制要求程序慎密地分析什么时候对变量进行读写，什么时候需要锁定某个对象，什么时候释放对象锁等繁杂的问题，程序设计和编写难度相对较大。

**ThreadLocal**从另一个角度来解决多线程的并发访问，ThreadLocal会为每一个线程维护一个和该线程绑定的变量的副本，从而隔离了多个线程的数据，每一个线程都拥有自己的变量副本，从而也就没有必要对该变量进行同步了。最明显的，ThreadLoacl变量的活动范围为某线程，并且我的理解是该线程“专有的，独自霸占”，对该变量的所有操作均有该线程完成！ThreadLocal提供了线程安全的共享对象，在编写多线程代码时，可以把不安全的整个变量封装进ThreadLocal，或者把该对象的特定于线程的状态封装进ThreadLocal。

举例说明：
 
```
同步就是一个公司只有一辆车，员工甲使用的时候其他人只能等待，只有员工甲用完后，其他人才可以使用
ThreadLocal就是公司为每一个员工配一辆车，每个员工使用自己的车，员工之间用车互不影响，互相不受制约
```
 

 
### 3. ThreadLocal使用示例
 
我们现在软件经常会分层，比如MVC，类似这种横向分层，而ThreadLocal会提供一种方式，方便的在同一个线程范围内，提供一个存储空间，供我们使用，实现纵向的存储结构，便于我们在同一个线程范围内，随时取得我们在另外一个层面存放的数据。
 
比如：在业务逻辑层需要调用多个Dao层的方法，我们要保证事务（jdbc事务）就要确保他们使用的是同一个数据库连接.那么如何确保使用同一个数据库连接呢？
 
第一种方案，从业务层创建数据库连接，然后一直将连接以参数形式传递到Dao

第二种方案，使用ThreadLocal，每一个线程拥有自己的变量副本，从业务逻辑层创建connection，然后到Dao层获取这个数据库连接

**方案二代码示例：**

```java
public class ConnectionManager {  
  
    // 线程局部变量
    private static ThreadLocal<Connection> connectionHolder = new ThreadLocal<Connection>();  
    /** 
     * 得到Connection 
     */  
    public static Connection getConnection(){  
             //get() 返回此线程局部变量的当前线程副本中的值，如果这是线程第一次调用该方法，则创建并初始化此副本。  
        Connection conn=connectionHolder.get();  
        //如果在当前线程中没有绑定相应的connection  
        if(conn == null){         
            try {  
                JdbcConfig jdbcConfig = XmlConfigReader.getInstance().getJdbcConfig();  
                Class.forName(jdbcConfig.getDriverName());  
                conn = DriverManager.getConnection(jdbcConfig.getUrl(), jdbcConfig.getUserName(), jdbcConfig.getPassword());  
            } catch (ClassNotFoundException e) {  
                e.printStackTrace();  
                throw new ApplicationException("系统错误，请联系系统管理员");  
            } catch (SQLException e) {  
                e.printStackTrace();  
                throw new ApplicationException("系统错误，请联系系统管理员");  
            }  
        }     
        return conn;  
    }  
      
    public static void closeConnection(){  
        Connection conn=connectionHolder.get();  
        if(conn !=null){  
            try{  
                conn.close();  
                //从ThreadLocal中清除Connection  
                connectionHolder.remove();  
            }catch(SQLException e){  
                e.printStackTrace();  
            }  
        }  
    }      
} 
``` 

业务逻辑层：

```java
public void addFlowCard(FlowCard flowCard) throws ApplicationException {  
        Connection conn=null;  
        try{  
            //取得Connection  
             conn=ConnectionManager.getConnection();  
              
           //开始事务  
           ConnectionManager.beginTransaction(conn);  
              
            //生成流向单单号  
            String flowCardVouNo=flowCardDao.generateVouNo();  
          
            //添加流向单主信息  
            flowCardDao.addFlowCardMaster(flowCardVouNo, flowCard);  
            //添加流向单明细信息  
            flowCardDao.addFlowCardDetail(flowCardVouNo, flowCard.getFlowCardDetailList());  
            //flowCardDao.addFlowCardDetail(flowCardVouNo, flowCard.getFlowCardDetailList());  
            //提交事务  
            ConnectionManager.commitTransaction(conn);  
              
        }catch(DaoException e){  
            //回滚事务  
            ConnectionManager.rollbackTransaction(conn);  
            throw new ApplicationException("添加流向单失败");  
        }finally{  
            //关闭Connection并从threadLocal中清除  
            ConnectionManager.closeConnection();  
        }  
    } 
``` 

### 4. 总结
概括起来说，对于多线程资源共享的问题，同步机制采用了“以时间换空间”的方式，而ThreadLocal采用了“以空间换时间”的方式。前者仅提供一份变量，让不同的线程排队访问，而后者为每一个线程都提供了一份变量，因此可以同时访问而互不影响。
 
如果你需要进行多个线程之间进行通信，则使用同步机制；

如果需要隔离多个线程之间的共享冲突，可以使用ThreadLocal
 
