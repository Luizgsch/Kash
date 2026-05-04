package com.kash.presentation.transaction;

import com.kash.data.remote.api.KashApiService;
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
public final class TransactionsViewModel_Factory implements Factory<TransactionsViewModel> {
  private final Provider<KashApiService> apiProvider;

  public TransactionsViewModel_Factory(Provider<KashApiService> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public TransactionsViewModel get() {
    return newInstance(apiProvider.get());
  }

  public static TransactionsViewModel_Factory create(Provider<KashApiService> apiProvider) {
    return new TransactionsViewModel_Factory(apiProvider);
  }

  public static TransactionsViewModel newInstance(KashApiService api) {
    return new TransactionsViewModel(api);
  }
}
