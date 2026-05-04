package com.kash.di;

import com.kash.data.local.KashDatabase;
import com.kash.data.local.dao.WalletDao;
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
public final class DatabaseModule_ProvideWalletDaoFactory implements Factory<WalletDao> {
  private final Provider<KashDatabase> dbProvider;

  public DatabaseModule_ProvideWalletDaoFactory(Provider<KashDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public WalletDao get() {
    return provideWalletDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideWalletDaoFactory create(Provider<KashDatabase> dbProvider) {
    return new DatabaseModule_ProvideWalletDaoFactory(dbProvider);
  }

  public static WalletDao provideWalletDao(KashDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideWalletDao(db));
  }
}
