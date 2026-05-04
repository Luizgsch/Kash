package com.kash.di;

import com.kash.data.local.KashDatabase;
import com.kash.data.local.dao.ProductTransactionDao;
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
public final class DatabaseModule_ProvideProductTransactionDaoFactory implements Factory<ProductTransactionDao> {
  private final Provider<KashDatabase> dbProvider;

  public DatabaseModule_ProvideProductTransactionDaoFactory(Provider<KashDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public ProductTransactionDao get() {
    return provideProductTransactionDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideProductTransactionDaoFactory create(
      Provider<KashDatabase> dbProvider) {
    return new DatabaseModule_ProvideProductTransactionDaoFactory(dbProvider);
  }

  public static ProductTransactionDao provideProductTransactionDao(KashDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideProductTransactionDao(db));
  }
}
