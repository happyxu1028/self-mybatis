package com.linshu.exe.pool;

import java.sql.Connection;

/**
 * 
 * 记录连接使用的时间
 * 
 */
class ConnectionAndStartTime {
    private Connection conn;

    private long startTime;

    public ConnectionAndStartTime(Connection conn, long startTime) {
        super();
        this.conn = conn;
        this.startTime = startTime;
    }

    public Connection getConn() {
        return conn;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
