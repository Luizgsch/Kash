package com.kash.data.sync;

import android.content.Context;
import androidx.work.WorkerParameters;
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

  private final Provider<KashApiService> apiProvider;

  public SyncWorker_Factory(Provider<TransactionDao> transactionDaoProvider,
      Provider<KashApiService> apiProvider) {
    this.transactionDaoProvider = transactionDaoProvider;
    this.apiProvider = apiProvider;
  }

  public SyncWorker get(Context ctx, WorkerParameters params) {
    return newInstance(ctx, params, transactionDaoProvider.get(), apiProvider.get());
  }

  public static SyncWorker_Factory create(Provider<TransactionDao> transactionDaoProvider,
      Provider<KashApiService> apiProvider) {
    return new SyncWorker_Factory(transactionDaoProvider, apiProvider);
  }

  public static SyncWorker newInstance(Context ctx, WorkerParameters params,
      TransactionDao transactionDao, KashApiService api) {
    return new SyncWorker(ctx, params, transactionDao, api);
  }
}
