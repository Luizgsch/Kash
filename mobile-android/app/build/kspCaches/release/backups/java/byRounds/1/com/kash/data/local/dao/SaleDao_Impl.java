package com.kash.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.kash.data.local.converter.Converters;
import com.kash.data.local.entity.SaleEntity;
import com.kash.data.local.entity.SyncStatus;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class SaleDao_Impl implements SaleDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SaleEntity> __insertionAdapterOfSaleEntity;

  private final Converters __converters = new Converters();

  private final SharedSQLiteStatement __preparedStmtOfUpdateSyncStatus;

  public SaleDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSaleEntity = new EntityInsertionAdapter<SaleEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `sales` (`id`,`productId`,`transactionId`,`quantity`,`salePriceCentsEach`,`costPriceCentsEach`,`walletId`,`organizationId`,`syncStatus`,`createdAt`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SaleEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getProductId());
        statement.bindString(3, entity.getTransactionId());
        statement.bindLong(4, entity.getQuantity());
        statement.bindLong(5, entity.getSalePriceCentsEach());
        statement.bindLong(6, entity.getCostPriceCentsEach());
        statement.bindString(7, entity.getWalletId());
        statement.bindString(8, entity.getOrganizationId());
        final String _tmp = __converters.fromSyncStatus(entity.getSyncStatus());
        statement.bindString(9, _tmp);
        statement.bindLong(10, entity.getCreatedAt());
      }
    };
    this.__preparedStmtOfUpdateSyncStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE sales SET syncStatus = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final SaleEntity s, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSaleEntity.insert(s);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateSyncStatus(final String id, final SyncStatus status,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateSyncStatus.acquire();
        int _argIndex = 1;
        final String _tmp = __converters.fromSyncStatus(status);
        _stmt.bindString(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateSyncStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<SaleEntity>> watchByWallet(final String walletId) {
    final String _sql = "SELECT * FROM sales WHERE walletId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, walletId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sales"}, new Callable<List<SaleEntity>>() {
      @Override
      @NonNull
      public List<SaleEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfProductId = CursorUtil.getColumnIndexOrThrow(_cursor, "productId");
          final int _cursorIndexOfTransactionId = CursorUtil.getColumnIndexOrThrow(_cursor, "transactionId");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfSalePriceCentsEach = CursorUtil.getColumnIndexOrThrow(_cursor, "salePriceCentsEach");
          final int _cursorIndexOfCostPriceCentsEach = CursorUtil.getColumnIndexOrThrow(_cursor, "costPriceCentsEach");
          final int _cursorIndexOfWalletId = CursorUtil.getColumnIndexOrThrow(_cursor, "walletId");
          final int _cursorIndexOfOrganizationId = CursorUtil.getColumnIndexOrThrow(_cursor, "organizationId");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<SaleEntity> _result = new ArrayList<SaleEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SaleEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpProductId;
            _tmpProductId = _cursor.getString(_cursorIndexOfProductId);
            final String _tmpTransactionId;
            _tmpTransactionId = _cursor.getString(_cursorIndexOfTransactionId);
            final int _tmpQuantity;
            _tmpQuantity = _cursor.getInt(_cursorIndexOfQuantity);
            final long _tmpSalePriceCentsEach;
            _tmpSalePriceCentsEach = _cursor.getLong(_cursorIndexOfSalePriceCentsEach);
            final long _tmpCostPriceCentsEach;
            _tmpCostPriceCentsEach = _cursor.getLong(_cursorIndexOfCostPriceCentsEach);
            final String _tmpWalletId;
            _tmpWalletId = _cursor.getString(_cursorIndexOfWalletId);
            final String _tmpOrganizationId;
            _tmpOrganizationId = _cursor.getString(_cursorIndexOfOrganizationId);
            final SyncStatus _tmpSyncStatus;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfSyncStatus);
            _tmpSyncStatus = __converters.toSyncStatus(_tmp);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new SaleEntity(_tmpId,_tmpProductId,_tmpTransactionId,_tmpQuantity,_tmpSalePriceCentsEach,_tmpCostPriceCentsEach,_tmpWalletId,_tmpOrganizationId,_tmpSyncStatus,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getPending(final Continuation<? super List<SaleEntity>> $completion) {
    final String _sql = "SELECT * FROM sales WHERE syncStatus = 'PENDING'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SaleEntity>>() {
      @Override
      @NonNull
      public List<SaleEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfProductId = CursorUtil.getColumnIndexOrThrow(_cursor, "productId");
          final int _cursorIndexOfTransactionId = CursorUtil.getColumnIndexOrThrow(_cursor, "transactionId");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfSalePriceCentsEach = CursorUtil.getColumnIndexOrThrow(_cursor, "salePriceCentsEach");
          final int _cursorIndexOfCostPriceCentsEach = CursorUtil.getColumnIndexOrThrow(_cursor, "costPriceCentsEach");
          final int _cursorIndexOfWalletId = CursorUtil.getColumnIndexOrThrow(_cursor, "walletId");
          final int _cursorIndexOfOrganizationId = CursorUtil.getColumnIndexOrThrow(_cursor, "organizationId");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<SaleEntity> _result = new ArrayList<SaleEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SaleEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpProductId;
            _tmpProductId = _cursor.getString(_cursorIndexOfProductId);
            final String _tmpTransactionId;
            _tmpTransactionId = _cursor.getString(_cursorIndexOfTransactionId);
            final int _tmpQuantity;
            _tmpQuantity = _cursor.getInt(_cursorIndexOfQuantity);
            final long _tmpSalePriceCentsEach;
            _tmpSalePriceCentsEach = _cursor.getLong(_cursorIndexOfSalePriceCentsEach);
            final long _tmpCostPriceCentsEach;
            _tmpCostPriceCentsEach = _cursor.getLong(_cursorIndexOfCostPriceCentsEach);
            final String _tmpWalletId;
            _tmpWalletId = _cursor.getString(_cursorIndexOfWalletId);
            final String _tmpOrganizationId;
            _tmpOrganizationId = _cursor.getString(_cursorIndexOfOrganizationId);
            final SyncStatus _tmpSyncStatus;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfSyncStatus);
            _tmpSyncStatus = __converters.toSyncStatus(_tmp);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new SaleEntity(_tmpId,_tmpProductId,_tmpTransactionId,_tmpQuantity,_tmpSalePriceCentsEach,_tmpCostPriceCentsEach,_tmpWalletId,_tmpOrganizationId,_tmpSyncStatus,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
