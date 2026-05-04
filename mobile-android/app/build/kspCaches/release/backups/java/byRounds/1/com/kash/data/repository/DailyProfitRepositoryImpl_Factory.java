package com.kash.data.repository;

import com.kash.data.local.dao.ProductTransactionDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class DailyProfitRepositoryImpl_Factory implements Factory<DailyProfitRepositoryImpl> {
  private final Provider<ProductTransactionDao> daoProvider;

  public DailyProfitRepositoryImpl_Factory(Provider<ProductTransactionDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public DailyProfitRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static DailyProfitRepositoryImpl_Factory create(
      Provider<ProductTransactionDao> daoProvider) {
    return new DailyProfitRepositoryImpl_Factory(daoProvider);
  }

  public static DailyProfitRepositoryImpl newInstance(ProductTransactionDao dao) {
    return new DailyProfitRepositoryImpl(dao);
  }
}
