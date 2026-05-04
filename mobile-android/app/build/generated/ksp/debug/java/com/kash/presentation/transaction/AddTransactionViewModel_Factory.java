package com.kash.presentation.transaction;

import com.kash.data.local.UserPreferences;
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
public final class AddTransactionViewModel_Factory implements Factory<AddTransactionViewModel> {
  private final Provider<KashApiService> apiProvider;

  private final Provider<UserPreferences> prefsProvider;

  public AddTransactionViewModel_Factory(Provider<KashApiService> apiProvider,
      Provider<UserPreferences> prefsProvider) {
    this.apiProvider = apiProvider;
    this.prefsProvider = prefsProvider;
  }

  @Override
  public AddTransactionViewModel get() {
    return newInstance(apiProvider.get(), prefsProvider.get());
  }

  public static AddTransactionViewModel_Factory create(Provider<KashApiService> apiProvider,
      Provider<UserPreferences> prefsProvider) {
    return new AddTransactionViewModel_Factory(apiProvider, prefsProvider);
  }

  public static AddTransactionViewModel newInstance(KashApiService api, UserPreferences prefs) {
    return new AddTransactionViewModel(api, prefs);
  }
}
