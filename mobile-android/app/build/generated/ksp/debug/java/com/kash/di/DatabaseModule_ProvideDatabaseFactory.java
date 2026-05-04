package com.kash.di;

import android.content.Context;
import com.kash.data.local.KashDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class DatabaseModule_ProvideDatabaseFactory implements Factory<KashDatabase> {
  private final Provider<Context> ctxProvider;

  public DatabaseModule_ProvideDatabaseFactory(Provider<Context> ctxProvider) {
    this.ctxProvider = ctxProvider;
  }

  @Override
  public KashDatabase get() {
    return provideDatabase(ctxProvider.get());
  }

  public static DatabaseModule_ProvideDatabaseFactory create(Provider<Context> ctxProvider) {
    return new DatabaseModule_ProvideDatabaseFactory(ctxProvider);
  }

  public static KashDatabase provideDatabase(Context ctx) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideDatabase(ctx));
  }
}
