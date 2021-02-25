package com.abook23.tv.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.abook23.tv.ben.AppConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "APP_CONFIG".
*/
public class AppConfigDao extends AbstractDao<AppConfig, Long> {

    public static final String TABLENAME = "APP_CONFIG";

    /**
     * Properties of entity AppConfig.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Name = new Property(0, String.class, "name", false, "NAME");
        public final static Property Id = new Property(1, long.class, "id", true, "_id");
        public final static Property Type = new Property(2, long.class, "type", false, "TYPE");
        public final static Property Tid = new Property(3, int.class, "tid", false, "TID");
    }


    public AppConfigDao(DaoConfig config) {
        super(config);
    }
    
    public AppConfigDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"APP_CONFIG\" (" + //
                "\"NAME\" TEXT," + // 0: name
                "\"_id\" INTEGER PRIMARY KEY NOT NULL ," + // 1: id
                "\"TYPE\" INTEGER NOT NULL ," + // 2: type
                "\"TID\" INTEGER NOT NULL );"); // 3: tid
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"APP_CONFIG\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, AppConfig entity) {
        stmt.clearBindings();
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(1, name);
        }
        stmt.bindLong(2, entity.getId());
        stmt.bindLong(3, entity.getType());
        stmt.bindLong(4, entity.getTid());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, AppConfig entity) {
        stmt.clearBindings();
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(1, name);
        }
        stmt.bindLong(2, entity.getId());
        stmt.bindLong(3, entity.getType());
        stmt.bindLong(4, entity.getTid());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 1);
    }    

    @Override
    public AppConfig readEntity(Cursor cursor, int offset) {
        AppConfig entity = new AppConfig( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // name
            cursor.getLong(offset + 1), // id
            cursor.getLong(offset + 2), // type
            cursor.getInt(offset + 3) // tid
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, AppConfig entity, int offset) {
        entity.setName(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setId(cursor.getLong(offset + 1));
        entity.setType(cursor.getLong(offset + 2));
        entity.setTid(cursor.getInt(offset + 3));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(AppConfig entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(AppConfig entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(AppConfig entity) {
        throw new UnsupportedOperationException("Unsupported for entities with a non-null key");
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}