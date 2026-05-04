package com.kash.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.kash.data.local.dao.CategoryDao;
import com.kash.data.local.dao.CategoryDao_Impl;
import com.kash.data.local.dao.InsightsDao;
import com.kash.data.local.dao.InsightsDao_Impl;
import com.kash.data.local.dao.LossDao;
import com.kash.data.local.dao.LossDao_Impl;
import com.kash.data.local.dao.ProductDao;
import com.kash.data.local.dao.ProductDao_Impl;
import com.kash.data.local.dao.ProductTransactionDao;
import com.kash.data.local.dao.ProductTransactionDao_Impl;
import com.kash.data.local.dao.SaleDao;
import com.kash.data.local.dao.SaleDao_Impl;
import com.kash.data.local.dao.TransactionDao;
import com.kash.data.local.dao.TransactionDao_Impl;
import com.kash.data.local.dao.WalletDao;
import com.kash.data.local.dao.WalletDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class KashDatabase_Impl extends KashDatabase {
  private volatile WalletDao _walletDao;

  private volatile CategoryDao _categoryDao;

  private volatile TransactionDao _transactionDao;

  private volatile ProductDao _productDao;

  private volatile SaleDao _saleDao;

  private volatile LossDao _lossDao;

  private volatile InsightsDao _insightsDao;

  private volatile ProductTransactionDao _productTransactionDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `wallets` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `organizationId` TEXT NOT NULL, `syncStatus` TEXT NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_wallets_organizationId` ON `wallets` (`organizationId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `categories` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `walletId` TEXT NOT NULL, `syncStatus` TEXT NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`walletId`) REFERENCES `wallets`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_categories_walletId` ON `categories` (`walletId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `transactions` (`id` TEXT NOT NULL, `amountCents` INTEGER NOT NULL, `type` TEXT NOT NULL, `description` TEXT NOT NULL, `categoryId` TEXT, `walletId` TEXT NOT NULL, `organizationId` TEXT NOT NULL, `userId` TEXT NOT NULL, `syncStatus` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`walletId`) REFERENCES `wallets`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_walletId` ON `transactions` (`walletId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_syncStatus` ON `transactions` (`syncStatus`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `products` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `walletId` TEXT NOT NULL, `organizationId` TEXT NOT NULL, `categoryId` TEXT, `salePriceCents` INTEGER NOT NULL, `costPriceCents` INTEGER NOT NULL, `currentStock` INTEGER NOT NULL, `syncStatus` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`walletId`) REFERENCES `wallets`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_products_walletId` ON `products` (`walletId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `sales` (`id` TEXT NOT NULL, `productId` TEXT NOT NULL, `transactionId` TEXT NOT NULL, `quantity` INTEGER NOT NULL, `salePriceCentsEach` INTEGER NOT NULL, `costPriceCentsEach` INTEGER NOT NULL, `walletId` TEXT NOT NULL, `organizationId` TEXT NOT NULL, `syncStatus` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`productId`) REFERENCES `products`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT , FOREIGN KEY(`transactionId`) REFERENCES `transactions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_sales_productId` ON `sales` (`productId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_sales_transactionId` ON `sales` (`transactionId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `losses` (`id` TEXT NOT NULL, `productId` TEXT NOT NULL, `quantity` INTEGER NOT NULL, `reason` TEXT NOT NULL, `walletId` TEXT NOT NULL, `organizationId` TEXT NOT NULL, `syncStatus` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`productId`) REFERENCES `products`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_losses_productId` ON `losses` (`productId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `product_transactions` (`id` TEXT NOT NULL, `productId` TEXT NOT NULL, `productName` TEXT NOT NULL, `type` TEXT NOT NULL, `quantity` INTEGER NOT NULL, `unitPriceCents` INTEGER NOT NULL, `unitCostCents` INTEGER NOT NULL, `reason` TEXT NOT NULL, `walletId` TEXT NOT NULL, `organizationId` TEXT NOT NULL, `synced` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`productId`) REFERENCES `products`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_product_transactions_productId` ON `product_transactions` (`productId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_product_transactions_createdAt` ON `product_transactions` (`createdAt`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_product_transactions_walletId` ON `product_transactions` (`walletId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_product_transactions_synced` ON `product_transactions` (`synced`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '7aa6cd0cdbf107a3f6e8e9dc323f1454')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `wallets`");
        db.execSQL("DROP TABLE IF EXISTS `categories`");
        db.execSQL("DROP TABLE IF EXISTS `transactions`");
        db.execSQL("DROP TABLE IF EXISTS `products`");
        db.execSQL("DROP TABLE IF EXISTS `sales`");
        db.execSQL("DROP TABLE IF EXISTS `losses`");
        db.execSQL("DROP TABLE IF EXISTS `product_transactions`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsWallets = new HashMap<String, TableInfo.Column>(5);
        _columnsWallets.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWallets.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWallets.put("organizationId", new TableInfo.Column("organizationId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWallets.put("syncStatus", new TableInfo.Column("syncStatus", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWallets.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysWallets = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesWallets = new HashSet<TableInfo.Index>(1);
        _indicesWallets.add(new TableInfo.Index("index_wallets_organizationId", false, Arrays.asList("organizationId"), Arrays.asList("ASC")));
        final TableInfo _infoWallets = new TableInfo("wallets", _columnsWallets, _foreignKeysWallets, _indicesWallets);
        final TableInfo _existingWallets = TableInfo.read(db, "wallets");
        if (!_infoWallets.equals(_existingWallets)) {
          return new RoomOpenHelper.ValidationResult(false, "wallets(com.kash.data.local.entity.WalletEntity).\n"
                  + " Expected:\n" + _infoWallets + "\n"
                  + " Found:\n" + _existingWallets);
        }
        final HashMap<String, TableInfo.Column> _columnsCategories = new HashMap<String, TableInfo.Column>(5);
        _columnsCategories.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("walletId", new TableInfo.Column("walletId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("syncStatus", new TableInfo.Column("syncStatus", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCategories = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysCategories.add(new TableInfo.ForeignKey("wallets", "CASCADE", "NO ACTION", Arrays.asList("walletId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesCategories = new HashSet<TableInfo.Index>(1);
        _indicesCategories.add(new TableInfo.Index("index_categories_walletId", false, Arrays.asList("walletId"), Arrays.asList("ASC")));
        final TableInfo _infoCategories = new TableInfo("categories", _columnsCategories, _foreignKeysCategories, _indicesCategories);
        final TableInfo _existingCategories = TableInfo.read(db, "categories");
        if (!_infoCategories.equals(_existingCategories)) {
          return new RoomOpenHelper.ValidationResult(false, "categories(com.kash.data.local.entity.CategoryEntity).\n"
                  + " Expected:\n" + _infoCategories + "\n"
                  + " Found:\n" + _existingCategories);
        }
        final HashMap<String, TableInfo.Column> _columnsTransactions = new HashMap<String, TableInfo.Column>(11);
        _columnsTransactions.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("amountCents", new TableInfo.Column("amountCents", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("categoryId", new TableInfo.Column("categoryId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("walletId", new TableInfo.Column("walletId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("organizationId", new TableInfo.Column("organizationId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("userId", new TableInfo.Column("userId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("syncStatus", new TableInfo.Column("syncStatus", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTransactions = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysTransactions.add(new TableInfo.ForeignKey("wallets", "CASCADE", "NO ACTION", Arrays.asList("walletId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesTransactions = new HashSet<TableInfo.Index>(2);
        _indicesTransactions.add(new TableInfo.Index("index_transactions_walletId", false, Arrays.asList("walletId"), Arrays.asList("ASC")));
        _indicesTransactions.add(new TableInfo.Index("index_transactions_syncStatus", false, Arrays.asList("syncStatus"), Arrays.asList("ASC")));
        final TableInfo _infoTransactions = new TableInfo("transactions", _columnsTransactions, _foreignKeysTransactions, _indicesTransactions);
        final TableInfo _existingTransactions = TableInfo.read(db, "transactions");
        if (!_infoTransactions.equals(_existingTransactions)) {
          return new RoomOpenHelper.ValidationResult(false, "transactions(com.kash.data.local.entity.TransactionEntity).\n"
                  + " Expected:\n" + _infoTransactions + "\n"
                  + " Found:\n" + _existingTransactions);
        }
        final HashMap<String, TableInfo.Column> _columnsProducts = new HashMap<String, TableInfo.Column>(11);
        _columnsProducts.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("walletId", new TableInfo.Column("walletId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("organizationId", new TableInfo.Column("organizationId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("categoryId", new TableInfo.Column("categoryId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("salePriceCents", new TableInfo.Column("salePriceCents", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("costPriceCents", new TableInfo.Column("costPriceCents", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("currentStock", new TableInfo.Column("currentStock", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("syncStatus", new TableInfo.Column("syncStatus", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProducts.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysProducts = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysProducts.add(new TableInfo.ForeignKey("wallets", "CASCADE", "NO ACTION", Arrays.asList("walletId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesProducts = new HashSet<TableInfo.Index>(1);
        _indicesProducts.add(new TableInfo.Index("index_products_walletId", false, Arrays.asList("walletId"), Arrays.asList("ASC")));
        final TableInfo _infoProducts = new TableInfo("products", _columnsProducts, _foreignKeysProducts, _indicesProducts);
        final TableInfo _existingProducts = TableInfo.read(db, "products");
        if (!_infoProducts.equals(_existingProducts)) {
          return new RoomOpenHelper.ValidationResult(false, "products(com.kash.data.local.entity.ProductEntity).\n"
                  + " Expected:\n" + _infoProducts + "\n"
                  + " Found:\n" + _existingProducts);
        }
        final HashMap<String, TableInfo.Column> _columnsSales = new HashMap<String, TableInfo.Column>(10);
        _columnsSales.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSales.put("productId", new TableInfo.Column("productId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSales.put("transactionId", new TableInfo.Column("transactionId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSales.put("quantity", new TableInfo.Column("quantity", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSales.put("salePriceCentsEach", new TableInfo.Column("salePriceCentsEach", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSales.put("costPriceCentsEach", new TableInfo.Column("costPriceCentsEach", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSales.put("walletId", new TableInfo.Column("walletId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSales.put("organizationId", new TableInfo.Column("organizationId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSales.put("syncStatus", new TableInfo.Column("syncStatus", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSales.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSales = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysSales.add(new TableInfo.ForeignKey("products", "RESTRICT", "NO ACTION", Arrays.asList("productId"), Arrays.asList("id")));
        _foreignKeysSales.add(new TableInfo.ForeignKey("transactions", "CASCADE", "NO ACTION", Arrays.asList("transactionId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesSales = new HashSet<TableInfo.Index>(2);
        _indicesSales.add(new TableInfo.Index("index_sales_productId", false, Arrays.asList("productId"), Arrays.asList("ASC")));
        _indicesSales.add(new TableInfo.Index("index_sales_transactionId", false, Arrays.asList("transactionId"), Arrays.asList("ASC")));
        final TableInfo _infoSales = new TableInfo("sales", _columnsSales, _foreignKeysSales, _indicesSales);
        final TableInfo _existingSales = TableInfo.read(db, "sales");
        if (!_infoSales.equals(_existingSales)) {
          return new RoomOpenHelper.ValidationResult(false, "sales(com.kash.data.local.entity.SaleEntity).\n"
                  + " Expected:\n" + _infoSales + "\n"
                  + " Found:\n" + _existingSales);
        }
        final HashMap<String, TableInfo.Column> _columnsLosses = new HashMap<String, TableInfo.Column>(8);
        _columnsLosses.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLosses.put("productId", new TableInfo.Column("productId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLosses.put("quantity", new TableInfo.Column("quantity", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLosses.put("reason", new TableInfo.Column("reason", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLosses.put("walletId", new TableInfo.Column("walletId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLosses.put("organizationId", new TableInfo.Column("organizationId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLosses.put("syncStatus", new TableInfo.Column("syncStatus", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLosses.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysLosses = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysLosses.add(new TableInfo.ForeignKey("products", "CASCADE", "NO ACTION", Arrays.asList("productId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesLosses = new HashSet<TableInfo.Index>(1);
        _indicesLosses.add(new TableInfo.Index("index_losses_productId", false, Arrays.asList("productId"), Arrays.asList("ASC")));
        final TableInfo _infoLosses = new TableInfo("losses", _columnsLosses, _foreignKeysLosses, _indicesLosses);
        final TableInfo _existingLosses = TableInfo.read(db, "losses");
        if (!_infoLosses.equals(_existingLosses)) {
          return new RoomOpenHelper.ValidationResult(false, "losses(com.kash.data.local.entity.LossEntity).\n"
                  + " Expected:\n" + _infoLosses + "\n"
                  + " Found:\n" + _existingLosses);
        }
        final HashMap<String, TableInfo.Column> _columnsProductTransactions = new HashMap<String, TableInfo.Column>(12);
        _columnsProductTransactions.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProductTransactions.put("productId", new TableInfo.Column("productId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProductTransactions.put("productName", new TableInfo.Column("productName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProductTransactions.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProductTransactions.put("quantity", new TableInfo.Column("quantity", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProductTransactions.put("unitPriceCents", new TableInfo.Column("unitPriceCents", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProductTransactions.put("unitCostCents", new TableInfo.Column("unitCostCents", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProductTransactions.put("reason", new TableInfo.Column("reason", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProductTransactions.put("walletId", new TableInfo.Column("walletId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProductTransactions.put("organizationId", new TableInfo.Column("organizationId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProductTransactions.put("synced", new TableInfo.Column("synced", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProductTransactions.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysProductTransactions = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysProductTransactions.add(new TableInfo.ForeignKey("products", "RESTRICT", "NO ACTION", Arrays.asList("productId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesProductTransactions = new HashSet<TableInfo.Index>(4);
        _indicesProductTransactions.add(new TableInfo.Index("index_product_transactions_productId", false, Arrays.asList("productId"), Arrays.asList("ASC")));
        _indicesProductTransactions.add(new TableInfo.Index("index_product_transactions_createdAt", false, Arrays.asList("createdAt"), Arrays.asList("ASC")));
        _indicesProductTransactions.add(new TableInfo.Index("index_product_transactions_walletId", false, Arrays.asList("walletId"), Arrays.asList("ASC")));
        _indicesProductTransactions.add(new TableInfo.Index("index_product_transactions_synced", false, Arrays.asList("synced"), Arrays.asList("ASC")));
        final TableInfo _infoProductTransactions = new TableInfo("product_transactions", _columnsProductTransactions, _foreignKeysProductTransactions, _indicesProductTransactions);
        final TableInfo _existingProductTransactions = TableInfo.read(db, "product_transactions");
        if (!_infoProductTransactions.equals(_existingProductTransactions)) {
          return new RoomOpenHelper.ValidationResult(false, "product_transactions(com.kash.data.local.entity.ProductTransactionEntity).\n"
                  + " Expected:\n" + _infoProductTransactions + "\n"
                  + " Found:\n" + _existingProductTransactions);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "7aa6cd0cdbf107a3f6e8e9dc323f1454", "189f1cbaacbe55455e2f7c1552603b11");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "wallets","categories","transactions","products","sales","losses","product_transactions");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `wallets`");
      _db.execSQL("DELETE FROM `categories`");
      _db.execSQL("DELETE FROM `transactions`");
      _db.execSQL("DELETE FROM `sales`");
      _db.execSQL("DELETE FROM `products`");
      _db.execSQL("DELETE FROM `losses`");
      _db.execSQL("DELETE FROM `product_transactions`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(WalletDao.class, WalletDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CategoryDao.class, CategoryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TransactionDao.class, TransactionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ProductDao.class, ProductDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SaleDao.class, SaleDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(LossDao.class, LossDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(InsightsDao.class, InsightsDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ProductTransactionDao.class, ProductTransactionDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public WalletDao walletDao() {
    if (_walletDao != null) {
      return _walletDao;
    } else {
      synchronized(this) {
        if(_walletDao == null) {
          _walletDao = new WalletDao_Impl(this);
        }
        return _walletDao;
      }
    }
  }

  @Override
  public CategoryDao categoryDao() {
    if (_categoryDao != null) {
      return _categoryDao;
    } else {
      synchronized(this) {
        if(_categoryDao == null) {
          _categoryDao = new CategoryDao_Impl(this);
        }
        return _categoryDao;
      }
    }
  }

  @Override
  public TransactionDao transactionDao() {
    if (_transactionDao != null) {
      return _transactionDao;
    } else {
      synchronized(this) {
        if(_transactionDao == null) {
          _transactionDao = new TransactionDao_Impl(this);
        }
        return _transactionDao;
      }
    }
  }

  @Override
  public ProductDao productDao() {
    if (_productDao != null) {
      return _productDao;
    } else {
      synchronized(this) {
        if(_productDao == null) {
          _productDao = new ProductDao_Impl(this);
        }
        return _productDao;
      }
    }
  }

  @Override
  public SaleDao saleDao() {
    if (_saleDao != null) {
      return _saleDao;
    } else {
      synchronized(this) {
        if(_saleDao == null) {
          _saleDao = new SaleDao_Impl(this);
        }
        return _saleDao;
      }
    }
  }

  @Override
  public LossDao lossDao() {
    if (_lossDao != null) {
      return _lossDao;
    } else {
      synchronized(this) {
        if(_lossDao == null) {
          _lossDao = new LossDao_Impl(this);
        }
        return _lossDao;
      }
    }
  }

  @Override
  public InsightsDao insightsDao() {
    if (_insightsDao != null) {
      return _insightsDao;
    } else {
      synchronized(this) {
        if(_insightsDao == null) {
          _insightsDao = new InsightsDao_Impl(this);
        }
        return _insightsDao;
      }
    }
  }

  @Override
  public ProductTransactionDao productTransactionDao() {
    if (_productTransactionDao != null) {
      return _productTransactionDao;
    } else {
      synchronized(this) {
        if(_productTransactionDao == null) {
          _productTransactionDao = new ProductTransactionDao_Impl(this);
        }
        return _productTransactionDao;
      }
    }
  }
}
