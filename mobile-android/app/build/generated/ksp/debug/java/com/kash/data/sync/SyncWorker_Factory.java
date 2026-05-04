package com.kash.data.sync;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.kash.data.local.dao.LossDao;
import com.kash.data.local.dao.ProductDao;
import com.kash.data.local.dao.SaleDao;
import com.kash.data.local.dao.TransactionDao;
import com.kash.data.remote.api.KashApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class SyncWorker_Factory {
  private final Provider<TransactionDao> transactionDaoProvider;

  private final Provider<ProductDao> productDaoProvider;

  private final Provider<SaleDao> saleDaoProvider;

  private final Provider<LossDao> lossDaoProvider;

  private final Provider<KashApiService> apiProvider;

  public SyncWorker_Factory(Provider<TransactionDao> transactionDaoProvider,
      Provider<ProductDao> productDaoProvider, Provider<SaleDao> saleDaoProvider,
      Provider<LossDao> lossDaoProvider, Provider<KashApiService> apiProvider) {
    this.transactionDaoProvider = transactionDaoProvider;
    this.productDaoProvider = productDaoProvider;
    this.saleDaoProvider = saleDaoProvider;
    this.lossDaoProvider = lossDaoProvider;
    this.apiProvider = apiProvider;
  }

  public SyncWorker get(Context ctx, WorkerParameters params) {
    return newInstance(ctx, params, transactionDaoProvider.get(), productDaoProvider.get(), saleDaoProvider.get(), lossDaoProvider.get(), apiProvider.get());
  }

  public static SyncWorker_Factory create(Provider<TransactionDao> transactionDaoProvider,
      Provider<ProductDao> productDaoProvider, Provider<SaleDao> saleDaoProvider,
      Provider<LossDao> lossDaoProvider, Provider<KashApiService> apiProvider) {
    return new SyncWorker_Factory(transactionDaoProvider, productDaoProvider, saleDaoProvider, lossDaoProvider, apiProvider);
  }

  public static SyncWorker newInstance(Context ctx, WorkerParameters params,
      TransactionDao transactionDao, ProductDao productDao, SaleDao saleDao, LossDao lossDao,
      KashApiService api) {
    return new SyncWorker(ctx, params, transactionDao, productDao, saleDao, lossDao, api);
  }
}
