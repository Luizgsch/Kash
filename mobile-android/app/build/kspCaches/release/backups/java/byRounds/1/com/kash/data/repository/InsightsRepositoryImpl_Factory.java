package com.kash.data.repository;

import com.kash.data.local.dao.InsightsDao;
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
public final class InsightsRepositoryImpl_Factory implements Factory<InsightsRepositoryImpl> {
  private final Provider<InsightsDao> daoProvider;

  public InsightsRepositoryImpl_Factory(Provider<InsightsDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public InsightsRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static InsightsRepositoryImpl_Factory create(Provider<InsightsDao> daoProvider) {
    return new InsightsRepositoryImpl_Factory(daoProvider);
  }

  public static InsightsRepositoryImpl newInstance(InsightsDao dao) {
    return new InsightsRepositoryImpl(dao);
  }
}
