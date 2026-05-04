package com.kash.di;

import com.kash.data.local.KashDatabase;
import com.kash.data.local.dao.LossDao;
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
public final class DatabaseModule_ProvideLossDaoFactory implements Factory<LossDao> {
  private final Provider<KashDatabase> dbProvider;

  public DatabaseModule_ProvideLossDaoFactory(Provider<KashDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public LossDao get() {
    return provideLossDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideLossDaoFactory create(Provider<KashDatabase> dbProvider) {
    return new DatabaseModule_ProvideLossDaoFactory(dbProvider);
  }

  public static LossDao provideLossDao(KashDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideLossDao(db));
  }
}
