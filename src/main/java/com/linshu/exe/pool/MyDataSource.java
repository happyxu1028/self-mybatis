package com.linshu.exe.pool;

import com.linshu.exe.mapping.Environment;

import java.sql.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.logging.Logger;

public class MyDataSource implements DataSourceInterface{

    private final static Logger logger = Logger.getLogger(MyDataSource.class.getName());
    private static Vector<Connection> freeConnections = new Vector<Connection>();
    private static Map<String, ConnectionAndStartTime> busyConnectionsMap = Collections.synchronizedMap(new HashMap<String, ConnectionAndStartTime>());


    private String driverClass;
    private String url;
    private String username;
    private String password;

    private int minConns = 5;
    private int maxConns = 20;
    private static int isUsed = 0;
    private int timeout = 1000;

    // 构建定时器：自动关闭超时的连接.

    /**
     * 获取连接
     */
    public static int Try_Time = 0;

    // 只有这个构造方法
    public MyDataSource(Environment environment) {
        this.driverClass = environment.getDriver();
        this.url = environment.getUrl();
        this.username = environment.getUsername();
        this.password = environment.getPassword();
        initConnection();
    }

    private Connection createNewConnection() {

        try {
            Connection conn = null;
            conn = DriverManager.getConnection(url, username, password);
            if (LogUtil.isInfo()) {
                logger.info("创建了一个新的链接");
            }

            if (conn != null) {
                return conn;
            }
        } catch (SQLException e) {
            if (LogUtil.isInfo()) {
                logger.info("获取数据库连接失败" + e);
            }

        }
        // 使用连接数有可能数据库已经达到最大的连接
        return null;
    }

    /**
     * 释放连接入连接池
     */
    public synchronized void freeConnection(Connection conn) throws SQLException {
        if (conn != null && !conn.isClosed()) {
            freeConnections.add(conn);
            busyConnectionsMap.remove(conn.toString().trim());
            if (isUsed >= 1) {
                isUsed--;
            }
            notifyAll();
            if (LogUtil.isInfo()) {
                logger.info("释放连接!");
            }

        }

    }

    @Override
    public synchronized Connection getConnection() {
        if (LogUtil.isInfo()) {
            System.out.println("[系统报告]:已用 " + isUsed + " 个连接，空闲连接个数 " + freeConnections.size());
        }
        // ==========第一种情况
        if (freeConnections.size() >= 1) {
            if (LogUtil.isInfo) {
                System.out.println("[it has free connections]");
            }

            Connection conn = freeConnections.firstElement();
            try {
                if (conn.isClosed() || conn == null) {
                    // 新的连接代替无效连接
                    conn = createNewConnection();
                }
            } catch (SQLException e) {
                conn = createNewConnection();
            }
            freeConnections.removeElementAt(0);
            isUsed++;
            // 记住内存地址
            busyConnectionsMap.put(conn.toString().trim(), new ConnectionAndStartTime(conn, System.currentTimeMillis()));
            return conn;
        }

        if (freeConnections.size() <= 0) {
            if (LogUtil.isInfo()) {
                System.out.println("[now it is getting connection from db]");
            }

            // ==========第二种情况.1
            if (isUsed < maxConns) {
                Connection conn = createNewConnection();
                if (conn != null) {
                    isUsed++;
                    busyConnectionsMap.put(conn.toString().trim(), new ConnectionAndStartTime(conn, System.currentTimeMillis()));
                    return conn;
                } else {
                    // 再次自身调用自己:可能已经有空的连接存在
                    return getConnection();
                }

            }
            // ==========第二种情况.2
            if (isUsed >= maxConns) {
                if (LogUtil.isInfo) {
                    System.out.println("it has no more connections that is allowed for use");
                }

                Try_Time++;
                if (LogUtil.isInfo) {
                    System.out.println("***[第" + Try_Time + "尝试从新获取连接]***");
                }

                if (Try_Time > 10) {
                    // throw new RuntimeException("***[从新获取数据库连接的失败次数过多]***");
                    // 多次不能获得连接则返回null
                    if (LogUtil.isInfo()) {
                        System.out.println("重复尝试获取数据库连接10次...???等待解决问题");
                    }
                    return null;
                }
                // 连接池已满
                long startTime = System.currentTimeMillis();
                try {
                    wait(timeout);
                } catch (InterruptedException e) {
                    // e.printStackTrace();
                }
                if (System.currentTimeMillis() - startTime > timeout) {
                    if (LogUtil.isInfo()) {
                        logger.info("***[没有可获取的链接，正在重试...]***");
                    }

                    // 再次自身调用自己
                    Connection conn = getConnection();
                    if (conn != null) {
                        busyConnectionsMap.put(conn.toString(), new ConnectionAndStartTime(conn, System.currentTimeMillis()));
                        return conn;
                    } else {
                        // 再次自身调用自己
                        return getConnection();
                    }
                }
            }

        }
        return null;

    }

    private synchronized void initConnection() {
        try {
            Class.forName(driverClass); // 加载驱动
            for (int i = 0; i < minConns; i++) {
                Connection conn = createNewConnection();
                if (conn != null) {
                    freeConnections.add(conn);
                } else {
                    throw new RuntimeException("获取的数据库连接为null");
                }

            }
            if (LogUtil.isInfo()) {
                logger.info("初始化数据库" + minConns + "个连接放入连接池\n");
            }

        } catch (ClassNotFoundException e) {
            if (LogUtil.isInfo()) {
                logger.info("驱动无法加载，请检查驱动是否存在，driver: " + driverClass + e + "\n");
            }
        }
    }

    public synchronized void releaseAll() {
        Enumeration<Connection> enums = freeConnections.elements();
        while (enums.hasMoreElements()) {
            try {
                enums.nextElement().close();
            } catch (SQLException e) {
                if (LogUtil.isInfo()) {
                    logger.info("关闭链接失败" + e);
                }

            }
        }
        freeConnections.removeAllElements();
        busyConnectionsMap.clear();
        if (LogUtil.isInfo()) {
            logger.info("释放了所有的连接");
        }

    }


//    /**
//     * 计时统计
//     */
//    private static Timer timer = new Timer();
//    private static long timerCount = 0;
//    private static int timeOut = 30;
//
//    static {
//        // 另起一个线程
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                timer.schedule(new TimerTask() {
//
//                    @Override
//                    public void run() {
//                        if (LogUtil.isDebug()) {
//                            logger.info("----------[清除超时的线程进行清除...----------");
//                        }
//                        if (LogUtil.isInfo()) {
//                            System.out.println("----------[清除超时的线程进行清除...----------");
//                        }
//
//                        timerCount++;
//                        if (timerCount >= 100000000) {
//                            timerCount = 0;
//                        }
//                        if (LogUtil.isDebug()) {
//                            System.out.println("第" + timerCount + "进行定时清除超时的数据库连接");
//                        }
//                        if (LogUtil.isDebug()) {
//                            System.out.println("----------[清除超时的线程进行清除...----------");
//                        }
//                        Set<String> set = busyConnectionsMap.keySet();
//                        Iterator<String> iterator = set.iterator();
//                        String connectionAndTimeKeyArray = "";
//                        int index = 0;
//                        while (iterator.hasNext()) {
//                            String connectionClassString = iterator.next();
//                            ConnectionAndStartTime connectionAndTime = busyConnectionsMap.get(connectionClassString);
//                            if (System.currentTimeMillis() - connectionAndTime.getStartTime() > timeOut * 1000) {// 大于2分钟
//                                if (index == 0) {
//                                    connectionAndTimeKeyArray += connectionClassString;
//                                } else {
//                                    connectionAndTimeKeyArray += "," + connectionClassString;
//                                }
//                                index++;
//                            }
//
//                        }
//                        // 清除
//                        if (connectionAndTimeKeyArray != null && connectionAndTimeKeyArray != "") {
//                            String[] connectionClassStringArray = connectionAndTimeKeyArray.split(",");
//                            for (int i = 0; i < connectionClassStringArray.length; i++) {
//                                if (busyConnectionsMap.get(connectionClassStringArray[i]) != null) {
//                                    System.out.println("connectionClassStringArray[i]" + connectionClassStringArray[i]);
//                                    busyConnectionsMap.remove(connectionClassStringArray[i]);
//                                    if (LogUtil.isDebug()) {
//                                        System.out.println("清除超时的Connection:" + connectionClassStringArray[i]);
//                                    }
//                                    isUsed--;
//                                }
//
//                            }
//                        }
//                        if (LogUtil.isDebug()) {
//                            System.out.println("当前数据库可用连接" + freeConnections.size());
//                            System.out.println("----------[清除超时的线程进行清除...----------");
//                            System.out.println("----------[清除超时的线程成功]----------");
//                        }
//
//                    }
//                    // 30秒后执行定时操作:每个10秒检查是否超时
//                }, 30 * 1000, 10 * 1000);
//
//            }
//        }).start();
//        if (LogUtil.isInfo()) {
//            System.out.println("超时处理Connection线程启动");
//        }
//        if (LogUtil.isInfo()) {
//
//        }
//
//    }
}