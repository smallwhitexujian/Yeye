package com.angelatech.yeyelive1.db.dao;

import com.angelatech.yeyelive1.db.DatabaseHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class CommonDao<T> {

    private Dao<T, Integer> mDao;
    private DatabaseHelper mHelper;

    public CommonDao(DatabaseHelper helper, Class<?> clazz) {
        try {
            mHelper = helper;
            mDao = mHelper.getDao(clazz);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 增加一条记录
     */
    public int add(T entity) {
        try {
            return mDao.create(entity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 删除一条记录
     */
    public void delete(T entity) {
        try {
            mDao.delete(entity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除所有记录 当前表
     */
    public void deleteAll(Class<?> dataClass) {
        try {
            TableUtils.clearTable(mDao.getConnectionSource(), dataClass);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * 更新一条记录
     */
    public void update(T entity) {
        try {
            mDao.update(entity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * 多条件修改某个字段的值
     *
     * @param tabName
     * @param keyValue
     * @param eqs
     */
    public void updateName(String tabName, Object keyValue, Map<String, Object> eqs) {
        try {
            UpdateBuilder builder = mDao.updateBuilder();
            Where where = builder.where();
            builder.updateColumnValue(tabName, keyValue);
            int size = eqs.size();
            int index = 0;
            for (Map.Entry<String, Object> eq : eqs.entrySet()) {
                String key = eq.getKey();
                Object value = eq.getValue();
                if (key != null && !"".equals(key) && value != null) {
                    if (index++ != size - 1) {
                        where = where.eq(key, value).and();
                    } else {
                        where = where.eq(key, value);
                    }
                } else {
                    continue;
                }
            }
            builder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询一条记录
     */
    public T queryById(int id) {
        T data = null;
        try {
            data = mDao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    /***
     * 查询第一条记录
     */
    public T queryForFirst() throws SQLException {
        List<T> datas = queryAll();
        if (datas != null && !datas.isEmpty()) {
            return datas.get(0);
        }
        return null;
    }

    /**
     * 查询所有记录
     *
     * @return
     */
    public List<T> queryAll() {
        List<T> datas = new ArrayList<T>();
        try {
            datas = mDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return datas;
    }

    /***
     * 根据条件查询所有数据
     */
    public List<T> queryByCondition(String orderByKey, boolean ascending, Map<String, Object> eqs) throws SQLException {
        QueryBuilder builder = mDao.queryBuilder();
        Where where = builder.orderBy(orderByKey, ascending).where();
        int size = eqs.size();
        int index = 0;
        for (Map.Entry<String, Object> eq : eqs.entrySet()) {
            String key = eq.getKey();
            Object value = eq.getValue();
            if (key != null && !"".equals(key) && value != null) {
                if (index++ != size - 1) {
                    where = where.eq(key, value).and();
                } else {
                    where = where.eq(key, value);
                }
            } else {
                continue;
            }
        }
        return builder.query();
    }

    /**
     * 根据条件查询一条数据
     */
    public T queryByConditionSingle(String orderByKey, boolean ascending, Map<String, Object> eqs) throws SQLException {
        QueryBuilder builder = mDao.queryBuilder();
        Where where = builder.orderBy(orderByKey, ascending).where();
        int size = eqs.size();
        int index = 0;
        for (Map.Entry<String, Object> eq : eqs.entrySet()) {
            String key = eq.getKey();
            Object value = eq.getValue();
            if (key != null && !"".equals(key) && value != null) {
                if (index++ != size - 1) {
                    where = where.eq(key, value).and();
                } else {
                    where = where.eq(key, value);
                }
            } else {
                continue;
            }
        }
        List<T> results = builder.query();
        if (results == null || results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    /**
     * 分页
     ***/
    public List<T> queryByConditionLimit(String orderByKey, boolean ascending, Map<String, Object> eqs, long startRow, long maxRows) throws SQLException {
        QueryBuilder builder = mDao.queryBuilder();
        Where where = builder.orderBy(orderByKey, ascending).where();
        int size = eqs.size();
        int index = 0;
        for (Map.Entry<String, Object> eq : eqs.entrySet()) {
            String key = eq.getKey();
            Object value = eq.getValue();
            if (key != null && !"".equals(key) && value != null) {
                if (index++ != size - 1) {
                    where = where.eq(key, value).and();
                } else {
                    where = where.eq(key, value);
                }
            } else {
                continue;
            }
        }
        builder.offset(startRow);
        builder.limit(maxRows);
        List<T> results = builder.query();
        if (results == null || results.isEmpty()) {
            return null;
        }
        return results;
    }

    /**
     * 查询所有分页
     *
     * @param orderByKey
     * @param ascending
     * @param startRow
     * @param maxRows
     * @return
     * @throws SQLException
     */
    public List<T> queryAllLimit(String orderByKey, boolean ascending, long startRow, long maxRows) throws SQLException {
        QueryBuilder builder = mDao.queryBuilder();
        builder.orderBy(orderByKey, ascending).where();
        builder.offset(startRow);
        builder.limit(maxRows);
        List<T> results = builder.query();
        if (results == null || results.isEmpty()) {
            return null;
        }
        return results;
    }

    /**
     * 根据条件更新相应的字段
     */
    public void update(Map<String, Object> eqs, Map<String, Object> updates) throws SQLException {
        UpdateBuilder updateBuilder = mDao.updateBuilder();
        Where where = updateBuilder.where();
        int size = eqs.size();
        int index = 0;
        for (Map.Entry<String, Object> eq : eqs.entrySet()) {
            String key = eq.getKey();
            Object value = eq.getValue();
            if (key != null && !"".equals(key) && value != null) {
                if (index++ != size - 1) {
                    where = where.eq(key, value).and();
                } else {
                    where = where.eq(key, value);
                }
            } else {
                continue;
            }
        }
        for (Map.Entry<String, Object> update : updates.entrySet()) {
            String key = update.getKey();
            Object value = update.getValue();
            if (key != null && !"".equals(key) && value != null) {
                updateBuilder = updateBuilder.updateColumnValue(key, value);
            } else {
                continue;
            }
        }
        updateBuilder.update();
    }

    /**
     * 根据条件删除
     */
    public void delete(Map<String, Object> eqs) throws SQLException {
        DeleteBuilder deleteBuilder = mDao.deleteBuilder();
        Where where = deleteBuilder.where();
        int size = eqs.size();
        int index = 0;
        for (Map.Entry<String, Object> eq : eqs.entrySet()) {
            String key = eq.getKey();
            Object value = eq.getValue();
            if (key != null && !"".equals(key) && value != null) {
                if (index++ != size - 1) {
                    where = where.eq(key, value).and();
                } else {
                    where = where.eq(key, value);
                }
            } else {
                continue;
            }
        }
        deleteBuilder.delete();
    }


}
