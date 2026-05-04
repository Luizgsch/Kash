package com.kash.presentation.profile;

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
public final class ProfileViewModel_Factory implements Factory<ProfileViewModel> {
  private final Provider<KashApiService> apiProvider;

  public ProfileViewModel_Factory(Provider<KashApiService> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public ProfileViewModel get() {
    return newInstance(apiProvider.get());
  }

  public static ProfileViewModel_Factory create(Provider<KashApiService> apiProvider) {
    return new ProfileViewModel_Factory(apiProvider);
  }

  public static ProfileViewModel newInstance(KashApiService api) {
    return new ProfileViewModel(api);
  }
}
