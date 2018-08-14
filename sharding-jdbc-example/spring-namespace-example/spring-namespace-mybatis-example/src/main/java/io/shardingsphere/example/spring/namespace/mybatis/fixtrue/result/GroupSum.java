package io.shardingsphere.example.spring.namespace.mybatis.fixtrue.result;

public class GroupSum {
    private int userId;
    private long total;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    @Override public String toString() {
        return "GroupSum{" + "userId=" + userId + ", total=" + total + '}';
    }
}
