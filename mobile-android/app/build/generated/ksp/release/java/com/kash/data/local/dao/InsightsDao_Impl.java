package com.kash.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.DBUtil;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class InsightsDao_Impl implements InsightsDao {
  private final RoomDatabase __db;

  public InsightsDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
  }

  @Override
  public Flow<List<ProductProfitabilityResult>> watchProfitability(final String walletId,
      final String orgId, final long startMs, final long endMs) {
    final String _sql = "\n"
            + "        SELECT\n"
            + "            p.id                                                                    AS productId,\n"
            + "            p.name                                                                  AS productName,\n"
            + "            COALESCE(SUM(s.quantity), 0)                                            AS totalQuantitySold,\n"
            + "            COALESCE(SUM(s.salePriceCentsEach * s.quantity), 0)                     AS totalRevenueCents,\n"
            + "            COALESCE(SUM(s.costPriceCentsEach * s.quantity), 0)                     AS totalCostCents,\n"
            + "            COALESCE((SELECT SUM(l2.quantity) * p.costPriceCents\n"
            + "                      FROM losses l2\n"
            + "                      WHERE l2.productId = p.id\n"
            + "                        AND l2.walletId  = ?\n"
            + "                        AND l2.createdAt BETWEEN ? AND ?), 0)           AS totalLossCents,\n"
            + "            COALESCE(SUM(s.salePriceCentsEach * s.quantity), 0)\n"
            + "              - COALESCE(SUM(s.costPriceCentsEach * s.quantity), 0)\n"
            + "              - COALESCE((SELECT SUM(l2.quantity) * p.costPriceCents\n"
            + "                          FROM losses l2\n"
            + "                          WHERE l2.productId = p.id\n"
            + "                            AND l2.walletId  = ?\n"
            + "                            AND l2.createdAt BETWEEN ? AND ?), 0)       AS netProfitCents\n"
            + "        FROM products p\n"
            + "        LEFT JOIN sales s ON s.productId = p.id\n"
            + "                          AND s.walletId  = ?\n"
            + "                          AND s.createdAt BETWEEN ? AND ?\n"
            + "        WHERE p.walletId       = ?\n"
            + "          AND p.organizationId = ?\n"
            + "        GROUP BY p.id\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 11);
    int _argIndex = 1;
    _statement.bindString(_argIndex, walletId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, startMs);
    _argIndex = 3;
    _statement.bindLong(_argIndex, endMs);
    _argIndex = 4;
    _statement.bindString(_argIndex, walletId);
    _argIndex = 5;
    _statement.bindLong(_argIndex, startMs);
    _argIndex = 6;
    _statement.bindLong(_argIndex, endMs);
    _argIndex = 7;
    _statement.bindString(_argIndex, walletId);
    _argIndex = 8;
    _statement.bindLong(_argIndex, startMs);
    _argIndex = 9;
    _statement.bindLong(_argIndex, endMs);
    _argIndex = 10;
    _statement.bindString(_argIndex, walletId);
    _argIndex = 11;
    _statement.bindString(_argIndex, orgId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"losses", "products",
        "sales"}, new Callable<List<ProductProfitabilityResult>>() {
      @Override
      @NonNull
      public List<ProductProfitabilityResult> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfProductId = 0;
          final int _cursorIndexOfProductName = 1;
          final int _cursorIndexOfTotalQuantitySold = 2;
          final int _cursorIndexOfTotalRevenueCents = 3;
          final int _cursorIndexOfTotalCostCents = 4;
          final int _cursorIndexOfTotalLossCents = 5;
          final int _cursorIndexOfNetProfitCents = 6;
          final List<ProductProfitabilityResult> _result = new ArrayList<ProductProfitabilityResult>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ProductProfitabilityResult _item;
            final String _tmpProductId;
            _tmpProductId = _cursor.getString(_cursorIndexOfProductId);
            final String _tmpProductName;
            _tmpProductName = _cursor.getString(_cursorIndexOfProductName);
            final int _tmpTotalQuantitySold;
            _tmpTotalQuantitySold = _cursor.getInt(_cursorIndexOfTotalQuantitySold);
            final long _tmpTotalRevenueCents;
            _tmpTotalRevenueCents = _cursor.getLong(_cursorIndexOfTotalRevenueCents);
            final long _tmpTotalCostCents;
            _tmpTotalCostCents = _cursor.getLong(_cursorIndexOfTotalCostCents);
            final long _tmpTotalLossCents;
            _tmpTotalLossCents = _cursor.getLong(_cursorIndexOfTotalLossCents);
            final long _tmpNetProfitCents;
            _tmpNetProfitCents = _cursor.getLong(_cursorIndexOfNetProfitCents);
            _item = new ProductProfitabilityResult(_tmpProductId,_tmpProductName,_tmpTotalQuantitySold,_tmpTotalRevenueCents,_tmpTotalCostCents,_tmpTotalLossCents,_tmpNetProfitCents);
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
  public Flow<Long> watchPeriodLossCents(final String walletId, final long startMs,
      final long endMs) {
    final String _sql = "\n"
            + "        SELECT COALESCE(SUM(l.quantity * p.costPriceCents), 0)\n"
            + "        FROM losses l\n"
            + "        INNER JOIN products p ON p.id = l.productId\n"
            + "        WHERE l.walletId = ?\n"
            + "          AND l.createdAt BETWEEN ? AND ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, walletId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, startMs);
    _argIndex = 3;
    _statement.bindLong(_argIndex, endMs);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"losses",
        "products"}, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            final long _tmp;
            _tmp = _cursor.getLong(0);
            _result = _tmp;
          } else {
            _result = 0L;
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
