/**
 * Copyright (c) 2013 Cangol
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.StrictMode;

class DaoImpl<T,ID> implements Dao<T, ID> {
	private CoreSQLiteOpenHelper mDatabaseHelper;
	private String mTableName;
	private Class<T> mClazz;
	
	public DaoImpl(CoreSQLiteOpenHelper databaseHelper,Class<T> clazz){
		this.mDatabaseHelper=databaseHelper;
		this.mClazz=clazz;
		DatabaseTable dbtable = clazz.getAnnotation(DatabaseTable.class);
		this.mTableName="".equals(dbtable.value())?clazz.getSimpleName():dbtable.value();
	}
	
	private Cursor query(SQLiteDatabase db,QueryBuilder queryBuilder){
		return db.query(queryBuilder.isDistinct(),
				queryBuilder.getTable(), 	
				null,
				queryBuilder.getSelection(), 
				queryBuilder.getSelectionArgs(),
				queryBuilder.getGroupBy(),
				queryBuilder.getHaving(),
				queryBuilder.getOrderBy(),
				queryBuilder.getLimit());
	}
		
	@Override
	public List<T> query(QueryBuilder queryBuilder){
        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
		ArrayList<T> list=new ArrayList<T>();
		try {
			SQLiteDatabase db=mDatabaseHelper.getReadableDatabase();
			Cursor cursor=query(db,queryBuilder);
			T obj=null;
			while(cursor.moveToNext()){
				obj=DatabaseUtils.cursorToObject(mClazz,cursor);
				list.add(obj);
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
        StrictMode.setThreadPolicy(oldPolicy);
		return list;
	}
	
	@Override
	public T queryForId(ID paramID) throws SQLException {
        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
		T obj=null;	
		try {
			SQLiteDatabase db=mDatabaseHelper.getReadableDatabase();
			QueryBuilder queryBuilder=new QueryBuilder(mClazz);
			queryBuilder.addQuery(DatabaseUtils.getIdColumnName(mClazz), paramID, "=");
			Cursor cursor=query(db,queryBuilder);
			if(cursor.getCount()>0){
				cursor.moveToFirst();
				obj=DatabaseUtils.cursorToObject(mClazz,cursor);
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
        StrictMode.setThreadPolicy(oldPolicy);
		return obj;
	}

	@Override
	public List<T> queryForAll() throws SQLException {
        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
		ArrayList<T> list=new ArrayList<T>();
		try {
			SQLiteDatabase db=mDatabaseHelper.getReadableDatabase();
			QueryBuilder queryBuilder=new QueryBuilder(mClazz);
			Cursor cursor=query(db,queryBuilder);
			T obj=null;
			while(cursor.moveToNext()){
				obj=DatabaseUtils.cursorToObject(mClazz,cursor);
				list.add(obj);
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
        StrictMode.setThreadPolicy(oldPolicy);
		return list;
	}

	@Override
	public int refresh(T paramT) throws SQLException {
        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
		int result=-1;
		try {
			SQLiteDatabase db=mDatabaseHelper.getReadableDatabase();
			QueryBuilder queryBuilder=new QueryBuilder(mClazz);
			queryBuilder.addQuery(DatabaseUtils.getIdColumnName(mClazz), DatabaseUtils.getIdValue(paramT), "=");
			Cursor cursor=query(db,queryBuilder);
			result=cursor.getCount();
			paramT=DatabaseUtils.cursorToObject(paramT,cursor);
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
        StrictMode.setThreadPolicy(oldPolicy);
		return result;
	}

	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
	public int create(T paramT) throws SQLException {
        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
		long result=-1;
		try {
			SQLiteDatabase db=mDatabaseHelper.getWritableDatabase();
			result=db.insert(mTableName,null, DatabaseUtils.getContentValues(paramT));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
        StrictMode.setThreadPolicy(oldPolicy);
		return (int)result;
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
	public int update(T paramT) throws SQLException {
        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
		SQLiteDatabase db=mDatabaseHelper.getWritableDatabase();
		int result=-1;
		try {
			result = db.update(mTableName, DatabaseUtils.getContentValues(paramT), DatabaseUtils.getIdColumnName(mClazz)+"=?",new String[]{""+DatabaseUtils.getIdValue(paramT)});
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
        StrictMode.setThreadPolicy(oldPolicy);
		return result;
	}	

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
	public int updateId(T paramT, ID paramID) throws SQLException {
        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();

		SQLiteDatabase db=mDatabaseHelper.getWritableDatabase();
		int result=-1;
		try {
			result = db.update(mTableName, DatabaseUtils.getContentValues(paramT), DatabaseUtils.getIdColumnName(mClazz)+"=?",new String[]{""+paramID});
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
        StrictMode.setThreadPolicy(oldPolicy);
		return result;
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
	public int delete(T paramT) throws SQLException {
        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
		SQLiteDatabase db=mDatabaseHelper.getWritableDatabase();
		int result=-1;
		try {
			result = db.delete(mTableName, DatabaseUtils.getIdColumnName(mClazz)+"=?",new String[]{""+DatabaseUtils.getIdValue(paramT)});
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
        StrictMode.setThreadPolicy(oldPolicy);
		return result;
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
	public int delete(Collection<T> paramCollection) throws SQLException {
        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
		SQLiteDatabase db=mDatabaseHelper.getWritableDatabase();
		int result=0;
		try {
			db.beginTransaction();
			for(T t:paramCollection){
				result =result+ db.delete(mTableName, DatabaseUtils.getIdColumnName(mClazz)+"=?",new String[]{""+DatabaseUtils.getIdValue(t)});
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
        StrictMode.setThreadPolicy(oldPolicy);
		return result;
	}

	@Override
	public int delete(DeleteBuilder deleteBuilder) throws SQLException {
		SQLiteDatabase db=mDatabaseHelper.getWritableDatabase();
		int result = db.delete(mTableName, deleteBuilder.getWhere(),deleteBuilder.getWhereArgs());
		return result;
	}
	
	@Override
	public int deleteById(ID paramID) throws SQLException {
		SQLiteDatabase db=mDatabaseHelper.getWritableDatabase();
		int result = db.delete(mTableName, DatabaseUtils.getIdColumnName(mClazz)+"=?",new String[]{""+paramID});
		return result;
	}

	@Override
	public int deleteByIds(Collection<ID> paramCollection) throws SQLException {
		SQLiteDatabase db=mDatabaseHelper.getWritableDatabase();
		int result = 0;
		try {
			db.beginTransaction();
			for(ID id:paramCollection){
				result=result+db.delete(mTableName, DatabaseUtils.getIdColumnName(mClazz)+"=?",new String[]{""+id});
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
		return result;
	}

	@Override
	public Class<T> getEntityClass() {
		return mClazz;
	}
}
