package com.kash.di;

import com.kash.data.local.KashDatabase;
import com.kash.data.local.dao.SaleDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideSaleDaoFactory implements Factory<SaleDao> {
  private final Provider<KashDatabase> dbProvider;

  public DatabaseModule_ProvideSaleDaoFactory(Provider<KashDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public SaleDao get() {
    return provideSaleDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideSaleDaoFactory create(Provider<KashDatabase> dbProvider) {
    return new DatabaseModule_ProvideSaleDaoFactory(dbProvider);
  }

  public static SaleDao provideSaleDao(KashDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideSaleDao(db));
  }
}
