package com.kash.data.repository;

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
public final class AuthRepositoryImpl_Factory implements Factory<AuthRepositoryImpl> {
  private final Provider<KashApiService> apiProvider;

  private final Provider<UserPreferences> prefsProvider;

  public AuthRepositoryImpl_Factory(Provider<KashApiService> apiProvider,
      Provider<UserPreferences> prefsProvider) {
    this.apiProvider = apiProvider;
    this.prefsProvider = prefsProvider;
  }

  @Override
  public AuthRepositoryImpl get() {
    return newInstance(apiProvider.get(), prefsProvider.get());
  }

  public static AuthRepositoryImpl_Factory create(Provider<KashApiService> apiProvider,
      Provider<UserPreferences> prefsProvider) {
    return new AuthRepositoryImpl_Factory(apiProvider, prefsProvider);
  }

  public static AuthRepositoryImpl newInstance(KashApiService api, UserPreferences prefs) {
    return new AuthRepositoryImpl(api, prefs);
  }
}
