package com.angelatech.yeyelive.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.j256.ormlite.table.TableUtils;

import java.util.Arrays;

/**
 * 4.5 Upgrading Your Schema

 When you upgrade your application, you may have to add columns or make other changes to the data that was stored by previous versions of your application. If you are on Android then in your DatabaseHelper, there should be an onUpgrade() method that extends the following method from the OrmLiteSqliteOpenHelper.


 abstract void onUpgrade(SQLiteDatabase database,
 ConnectionSource connectionSource, int oldVersion, int newVersion)
 In that method you can use your DAO to perform any tweaks to the schema:


 Dao<Account, Integer> dao = getHelper().getAccountDao();
 // change the table to add a new column named "age"
 dao.executeRaw("ALTER TABLE `account` ADD COLUMN age INTEGER;");
 Here’s more information about SQLite’s ALTER TABLE. In SQLite, all you can do is rename a table name and add a new column. You can’t rename or remove a column or change the constraints. Remember that SQLite is typeless so changing the type of a column doesn’t matter.

 Most likely, you should make your schema changes conditional to the version you are upgrading from:


 if (oldVersion < 2) {
 // we added the age column in version 2
 dao.executeRaw("ALTER TABLE `account` ADD COLUMN age INTEGER;");
 }
 if (oldVersion < 3) {
 // we added the weight column in version 3
 dao.executeRaw("ALTER TABLE `account` ADD COLUMN weight INTEGER;");
 }
 You can also modify data in the tables using something like the following:


 dao.executeRaw(
 "ALTER TABLE `account` ADD COLUMN hasDog BOOLEAN DEFAULT 0;");
 dao.updateRaw("UPDATE `account` SET hasDog = 1 WHERE dogCount > 0;");
 */

public class DatabaseUpdateHelper {
    /**
     * 数据库表操作类型
     */
    public enum OPERATION_TYPE {
        /**
         * 表新增字段
         */
        ADD,
        /**
         * 表删除字段
         */
        DELETE
    }

    /**
     * 升级表，增加字段
     *
     * @param database
     * @param clazz
     */
    public static <T> void upgradeTable(SQLiteDatabase database, ConnectionSource connectionSource, Class<T> clazz, OPERATION_TYPE type) {
        String tableName = DatabaseTableConfig.extractTableName(clazz);
        database.beginTransaction();
        try {
            //Rename table
            String tempTableName = tableName + "_temp";
            String sql = "ALTER TABLE " + tableName + " RENAME TO " + tempTableName;
            database.execSQL(sql);
            //Create table
            try {
                sql = TableUtils.getCreateTableStatements(connectionSource, clazz).get(0);
                database.execSQL(sql);
            } catch (Exception e) {
                e.printStackTrace();
                TableUtils.createTable(connectionSource, clazz);
            }

            //Load data
            String columns;
            if (type == OPERATION_TYPE.ADD) {
                columns = Arrays.toString(getColumnNames(database, tempTableName)).replace("[", "").replace("]", "");
            }
            else if (type == OPERATION_TYPE.DELETE) {
                columns = Arrays.toString(getColumnNames(database, tableName)).replace("[", "").replace("]", "");
            }
            else {
                throw new IllegalArgumentException("OPERATION_TYPE error");
            }
            sql = "INSERT INTO " + tableName +
                    " (" + columns + ") " +
                    " SELECT " + columns + " FROM " + tempTableName;
            database.execSQL(sql);

            //Drop temp table
            sql = "DROP TABLE IF EXISTS " + tempTableName;
            database.execSQL(sql);

            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
        }
    }

    /**
     * 获取表的列名
     *
     * @param database
     * @param tableName
     * @return
     */
    private static String[] getColumnNames(SQLiteDatabase database, String tableName) {
        String[] columnNames = null;
        Cursor cursor = null;
        try {
            cursor = database.rawQuery("PRAGMA table_info(" + tableName + ")", null);
            if (cursor != null) {
                int columnIndex = cursor.getColumnIndex("name");
                if (columnIndex == -1) {
                    return null;
                }
                int index = 0;
                columnNames = new String[cursor.getCount()];
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    columnNames[index] = cursor.getString(columnIndex);
                    index++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return columnNames;
    }

}