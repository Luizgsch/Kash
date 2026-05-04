package com.kash.presentation;

import com.kash.domain.repository.AuthRepository;
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
public final class AppViewModel_Factory implements Factory<AppViewModel> {
  private final Provider<AuthRepository> authRepoProvider;

  public AppViewModel_Factory(Provider<AuthRepository> authRepoProvider) {
    this.authRepoProvider = authRepoProvider;
  }

  @Override
  public AppViewModel get() {
    return newInstance(authRepoProvider.get());
  }

  public static AppViewModel_Factory create(Provider<AuthRepository> authRepoProvider) {
    return new AppViewModel_Factory(authRepoProvider);
  }

  public static AppViewModel newInstance(AuthRepository authRepo) {
    return new AppViewModel(authRepo);
  }
}
