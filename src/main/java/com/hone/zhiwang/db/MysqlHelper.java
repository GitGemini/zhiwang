package com.hone.zhiwang.db;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MysqlHelper extends AbstractDBHelper {

    private java.sql.Connection conn = null;

    private Statement stmt = null;

    private ResultSet rs = null;

    @Override
    public void init() throws Exception {
        Class.forName(getName());
        conn = DriverManager.getConnection(getUrl(), getUser(), getPassword());
        stmt = (Statement) conn.createStatement();
    }

    private ResultSet executeQuery(String sql) {
        try {
            rs = stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    private int executeUpdate(String sql) {
        int rowCount = 0;
        try {
            rowCount = stmt.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowCount;

    }

    @Override
    public void saveSuccess(String url, String title) {
        String sql = "insert into success(url,title)values(" + "'" + url + "'" + "," + "'" + title + "'" + ")";
        executeUpdate(sql);
    }

    @Override
    public boolean existOfSuccess(String title) {
        String sql = "select count(id) from success where title = '" + title + "'";
        ResultSet rs = executeQuery(sql);
        try {
            if (rs.next() & rs.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    @Override
    public void saveFail(String url, String title, int times) {
        String sql = "insert into fail(url,title,times)values('" + url + "','" + title + "','" + times + "')";
        executeUpdate(sql);
    }

    @Override
    public void deleteFail(int id) {
        String sql = "delete from fail where id = " + id;
        executeUpdate(sql);
    }

    @Override
    public List<String> select() throws Exception {
        String sql = "select id, url, title, times from fail limit 1";
        ResultSet rs = executeQuery(sql);
        List<String> result = new ArrayList<>();
        if (rs.next()) {
            result.add(rs.getString(1));
            result.add(rs.getString(2));
            result.add(rs.getString(3));
            result.add(rs.getString(4));
        }
        rs.close();
        return result;
    }

    @Override
    public boolean existOfFail(String title) {
        String sql = "select count(id) from fail where title = '" + title + "'";
        ResultSet rs = executeQuery(sql);
        try {
            if (rs.next() & rs.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
}
