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
import com.kash.data.local.entity.ProductTransactionEntity;
import com.kash.data.local.entity.ProductTransactionType;
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
public final class ProductTransactionDao_Impl implements ProductTransactionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ProductTransactionEntity> __insertionAdapterOfProductTransactionEntity;

  private final Converters __converters = new Converters();

  private final SharedSQLiteStatement __preparedStmtOfMarkSynced;

  public ProductTransactionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfProductTransactionEntity = new EntityInsertionAdapter<ProductTransactionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `product_transactions` (`id`,`productId`,`productName`,`type`,`quantity`,`unitPriceCents`,`unitCostCents`,`reason`,`walletId`,`organizationId`,`synced`,`createdAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ProductTransactionEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getProductId());
        statement.bindString(3, entity.getProductName());
        final String _tmp = __converters.fromProductTxType(entity.getType());
        statement.bindString(4, _tmp);
        statement.bindLong(5, entity.getQuantity());
        statement.bindLong(6, entity.getUnitPriceCents());
        statement.bindLong(7, entity.getUnitCostCents());
        statement.bindString(8, entity.getReason());
        statement.bindString(9, entity.getWalletId());
        statement.bindString(10, entity.getOrganizationId());
        final int _tmp_1 = entity.getSynced() ? 1 : 0;
        statement.bindLong(11, _tmp_1);
        statement.bindLong(12, entity.getCreatedAt());
      }
    };
    this.__preparedStmtOfMarkSynced = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE product_transactions SET synced = 1 WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final ProductTransactionEntity tx,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfProductTransactionEntity.insert(tx);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object markSynced(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkSynced.acquire();
        int _argIndex = 1;
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
          __preparedStmtOfMarkSynced.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<DailyProfitResult> watchDailyProfit(final String walletId, final long startMs,
      final long endMs) {
    final String _sql = "\n"
            + "        SELECT\n"
            + "            COALESCE(SUM(CASE WHEN type = 'SALE' THEN quantity * unitPriceCents ELSE 0 END), 0) AS totalRevenueCents,\n"
            + "            COALESCE(SUM(CASE WHEN type = 'SALE' THEN quantity * unitCostCents  ELSE 0 END), 0) AS totalCOGSCents,\n"
            + "            COALESCE(SUM(CASE WHEN type = 'LOSS' THEN quantity * unitCostCents  ELSE 0 END), 0) AS totalLossCents\n"
            + "        FROM product_transactions\n"
            + "        WHERE walletId  = ?\n"
            + "          AND createdAt >= ?\n"
            + "          AND createdAt <  ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, walletId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, startMs);
    _argIndex = 3;
    _statement.bindLong(_argIndex, endMs);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"product_transactions"}, new Callable<DailyProfitResult>() {
      @Override
      @NonNull
      public DailyProfitResult call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfTotalRevenueCents = 0;
          final int _cursorIndexOfTotalCOGSCents = 1;
          final int _cursorIndexOfTotalLossCents = 2;
          final DailyProfitResult _result;
          if (_cursor.moveToFirst()) {
            final long _tmpTotalRevenueCents;
            _tmpTotalRevenueCents = _cursor.getLong(_cursorIndexOfTotalRevenueCents);
            final long _tmpTotalCOGSCents;
            _tmpTotalCOGSCents = _cursor.getLong(_cursorIndexOfTotalCOGSCents);
            final long _tmpTotalLossCents;
            _tmpTotalLossCents = _cursor.getLong(_cursorIndexOfTotalLossCents);
            _result = new DailyProfitResult(_tmpTotalRevenueCents,_tmpTotalCOGSCents,_tmpTotalLossCents);
          } else {
            _result = null;
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
  public Flow<List<ProductTransactionEntity>> watchRecent(final String walletId, final int limit) {
    final String _sql = "\n"
            + "        SELECT * FROM product_transactions\n"
            + "        WHERE walletId = ?\n"
            + "        ORDER BY createdAt DESC\n"
            + "        LIMIT ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, walletId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"product_transactions"}, new Callable<List<ProductTransactionEntity>>() {
      @Override
      @NonNull
      public List<ProductTransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfProductId = CursorUtil.getColumnIndexOrThrow(_cursor, "productId");
          final int _cursorIndexOfProductName = CursorUtil.getColumnIndexOrThrow(_cursor, "productName");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfUnitPriceCents = CursorUtil.getColumnIndexOrThrow(_cursor, "unitPriceCents");
          final int _cursorIndexOfUnitCostCents = CursorUtil.getColumnIndexOrThrow(_cursor, "unitCostCents");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfWalletId = CursorUtil.getColumnIndexOrThrow(_cursor, "walletId");
          final int _cursorIndexOfOrganizationId = CursorUtil.getColumnIndexOrThrow(_cursor, "organizationId");
          final int _cursorIndexOfSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "synced");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ProductTransactionEntity> _result = new ArrayList<ProductTransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ProductTransactionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpProductId;
            _tmpProductId = _cursor.getString(_cursorIndexOfProductId);
            final String _tmpProductName;
            _tmpProductName = _cursor.getString(_cursorIndexOfProductName);
            final ProductTransactionType _tmpType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfType);
            _tmpType = __converters.toProductTxType(_tmp);
            final int _tmpQuantity;
            _tmpQuantity = _cursor.getInt(_cursorIndexOfQuantity);
            final long _tmpUnitPriceCents;
            _tmpUnitPriceCents = _cursor.getLong(_cursorIndexOfUnitPriceCents);
            final long _tmpUnitCostCents;
            _tmpUnitCostCents = _cursor.getLong(_cursorIndexOfUnitCostCents);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpWalletId;
            _tmpWalletId = _cursor.getString(_cursorIndexOfWalletId);
            final String _tmpOrganizationId;
            _tmpOrganizationId = _cursor.getString(_cursorIndexOfOrganizationId);
            final boolean _tmpSynced;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfSynced);
            _tmpSynced = _tmp_1 != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ProductTransactionEntity(_tmpId,_tmpProductId,_tmpProductName,_tmpType,_tmpQuantity,_tmpUnitPriceCents,_tmpUnitCostCents,_tmpReason,_tmpWalletId,_tmpOrganizationId,_tmpSynced,_tmpCreatedAt);
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
  public Flow<List<ProductTransactionEntity>> watchByPeriodAndType(final String walletId,
      final long startMs, final long endMs, final String typeFilter) {
    final String _sql = "\n"
            + "        SELECT * FROM product_transactions\n"
            + "        WHERE walletId = ?\n"
            + "          AND createdAt >= ?\n"
            + "          AND createdAt <  ?\n"
            + "          AND (? IS NULL OR type = ?)\n"
            + "        ORDER BY createdAt DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 5);
    int _argIndex = 1;
    _statement.bindString(_argIndex, walletId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, startMs);
    _argIndex = 3;
    _statement.bindLong(_argIndex, endMs);
    _argIndex = 4;
    if (typeFilter == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, typeFilter);
    }
    _argIndex = 5;
    if (typeFilter == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, typeFilter);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"product_transactions"}, new Callable<List<ProductTransactionEntity>>() {
      @Override
      @NonNull
      public List<ProductTransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfProductId = CursorUtil.getColumnIndexOrThrow(_cursor, "productId");
          final int _cursorIndexOfProductName = CursorUtil.getColumnIndexOrThrow(_cursor, "productName");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfUnitPriceCents = CursorUtil.getColumnIndexOrThrow(_cursor, "unitPriceCents");
          final int _cursorIndexOfUnitCostCents = CursorUtil.getColumnIndexOrThrow(_cursor, "unitCostCents");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfWalletId = CursorUtil.getColumnIndexOrThrow(_cursor, "walletId");
          final int _cursorIndexOfOrganizationId = CursorUtil.getColumnIndexOrThrow(_cursor, "organizationId");
          final int _cursorIndexOfSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "synced");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ProductTransactionEntity> _result = new ArrayList<ProductTransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ProductTransactionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpProductId;
            _tmpProductId = _cursor.getString(_cursorIndexOfProductId);
            final String _tmpProductName;
            _tmpProductName = _cursor.getString(_cursorIndexOfProductName);
            final ProductTransactionType _tmpType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfType);
            _tmpType = __converters.toProductTxType(_tmp);
            final int _tmpQuantity;
            _tmpQuantity = _cursor.getInt(_cursorIndexOfQuantity);
            final long _tmpUnitPriceCents;
            _tmpUnitPriceCents = _cursor.getLong(_cursorIndexOfUnitPriceCents);
            final long _tmpUnitCostCents;
            _tmpUnitCostCents = _cursor.getLong(_cursorIndexOfUnitCostCents);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpWalletId;
            _tmpWalletId = _cursor.getString(_cursorIndexOfWalletId);
            final String _tmpOrganizationId;
            _tmpOrganizationId = _cursor.getString(_cursorIndexOfOrganizationId);
            final boolean _tmpSynced;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfSynced);
            _tmpSynced = _tmp_1 != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ProductTransactionEntity(_tmpId,_tmpProductId,_tmpProductName,_tmpType,_tmpQuantity,_tmpUnitPriceCents,_tmpUnitCostCents,_tmpReason,_tmpWalletId,_tmpOrganizationId,_tmpSynced,_tmpCreatedAt);
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
  public Flow<List<ProductTransactionEntity>> watchLosses(final String walletId, final int limit) {
    final String _sql = "\n"
            + "        SELECT * FROM product_transactions\n"
            + "        WHERE walletId = ? AND type = 'LOSS'\n"
            + "        ORDER BY createdAt DESC\n"
            + "        LIMIT ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, walletId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"product_transactions"}, new Callable<List<ProductTransactionEntity>>() {
      @Override
      @NonNull
      public List<ProductTransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfProductId = CursorUtil.getColumnIndexOrThrow(_cursor, "productId");
          final int _cursorIndexOfProductName = CursorUtil.getColumnIndexOrThrow(_cursor, "productName");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfUnitPriceCents = CursorUtil.getColumnIndexOrThrow(_cursor, "unitPriceCents");
          final int _cursorIndexOfUnitCostCents = CursorUtil.getColumnIndexOrThrow(_cursor, "unitCostCents");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfWalletId = CursorUtil.getColumnIndexOrThrow(_cursor, "walletId");
          final int _cursorIndexOfOrganizationId = CursorUtil.getColumnIndexOrThrow(_cursor, "organizationId");
          final int _cursorIndexOfSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "synced");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ProductTransactionEntity> _result = new ArrayList<ProductTransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ProductTransactionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpProductId;
            _tmpProductId = _cursor.getString(_cursorIndexOfProductId);
            final String _tmpProductName;
            _tmpProductName = _cursor.getString(_cursorIndexOfProductName);
            final ProductTransactionType _tmpType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfType);
            _tmpType = __converters.toProductTxType(_tmp);
            final int _tmpQuantity;
            _tmpQuantity = _cursor.getInt(_cursorIndexOfQuantity);
            final long _tmpUnitPriceCents;
            _tmpUnitPriceCents = _cursor.getLong(_cursorIndexOfUnitPriceCents);
            final long _tmpUnitCostCents;
            _tmpUnitCostCents = _cursor.getLong(_cursorIndexOfUnitCostCents);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpWalletId;
            _tmpWalletId = _cursor.getString(_cursorIndexOfWalletId);
            final String _tmpOrganizationId;
            _tmpOrganizationId = _cursor.getString(_cursorIndexOfOrganizationId);
            final boolean _tmpSynced;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfSynced);
            _tmpSynced = _tmp_1 != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ProductTransactionEntity(_tmpId,_tmpProductId,_tmpProductName,_tmpType,_tmpQuantity,_tmpUnitPriceCents,_tmpUnitCostCents,_tmpReason,_tmpWalletId,_tmpOrganizationId,_tmpSynced,_tmpCreatedAt);
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
  public Object getUnsynced(
      final Continuation<? super List<ProductTransactionEntity>> $completion) {
    final String _sql = "SELECT * FROM product_transactions WHERE synced = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ProductTransactionEntity>>() {
      @Override
      @NonNull
      public List<ProductTransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfProductId = CursorUtil.getColumnIndexOrThrow(_cursor, "productId");
          final int _cursorIndexOfProductName = CursorUtil.getColumnIndexOrThrow(_cursor, "productName");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfUnitPriceCents = CursorUtil.getColumnIndexOrThrow(_cursor, "unitPriceCents");
          final int _cursorIndexOfUnitCostCents = CursorUtil.getColumnIndexOrThrow(_cursor, "unitCostCents");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfWalletId = CursorUtil.getColumnIndexOrThrow(_cursor, "walletId");
          final int _cursorIndexOfOrganizationId = CursorUtil.getColumnIndexOrThrow(_cursor, "organizationId");
          final int _cursorIndexOfSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "synced");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ProductTransactionEntity> _result = new ArrayList<ProductTransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ProductTransactionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpProductId;
            _tmpProductId = _cursor.getString(_cursorIndexOfProductId);
            final String _tmpProductName;
            _tmpProductName = _cursor.getString(_cursorIndexOfProductName);
            final ProductTransactionType _tmpType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfType);
            _tmpType = __converters.toProductTxType(_tmp);
            final int _tmpQuantity;
            _tmpQuantity = _cursor.getInt(_cursorIndexOfQuantity);
            final long _tmpUnitPriceCents;
            _tmpUnitPriceCents = _cursor.getLong(_cursorIndexOfUnitPriceCents);
            final long _tmpUnitCostCents;
            _tmpUnitCostCents = _cursor.getLong(_cursorIndexOfUnitCostCents);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpWalletId;
            _tmpWalletId = _cursor.getString(_cursorIndexOfWalletId);
            final String _tmpOrganizationId;
            _tmpOrganizationId = _cursor.getString(_cursorIndexOfOrganizationId);
            final boolean _tmpSynced;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfSynced);
            _tmpSynced = _tmp_1 != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ProductTransactionEntity(_tmpId,_tmpProductId,_tmpProductName,_tmpType,_tmpQuantity,_tmpUnitPriceCents,_tmpUnitCostCents,_tmpReason,_tmpWalletId,_tmpOrganizationId,_tmpSynced,_tmpCreatedAt);
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
