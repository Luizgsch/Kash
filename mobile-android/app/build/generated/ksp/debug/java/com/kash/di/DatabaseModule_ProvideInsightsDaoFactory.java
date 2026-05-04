package com.kash.di;

import com.kash.data.local.KashDatabase;
import com.kash.data.local.dao.InsightsDao;
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
public final class DatabaseModule_ProvideInsightsDaoFactory implements Factory<InsightsDao> {
  private final Provider<KashDatabase> dbProvider;

  public DatabaseModule_ProvideInsightsDaoFactory(Provider<KashDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public InsightsDao get() {
    return provideInsightsDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideInsightsDaoFactory create(Provider<KashDatabase> dbProvider) {
    return new DatabaseModule_ProvideInsightsDaoFactory(dbProvider);
  }

  public static InsightsDao provideInsightsDao(KashDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideInsightsDao(db));
  }
}
