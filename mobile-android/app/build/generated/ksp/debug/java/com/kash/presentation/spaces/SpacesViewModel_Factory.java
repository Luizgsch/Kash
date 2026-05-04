package com.kash.presentation.spaces;

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
public final class SpacesViewModel_Factory implements Factory<SpacesViewModel> {
  private final Provider<KashApiService> apiProvider;

  public SpacesViewModel_Factory(Provider<KashApiService> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public SpacesViewModel get() {
    return newInstance(apiProvider.get());
  }

  public static SpacesViewModel_Factory create(Provider<KashApiService> apiProvider) {
    return new SpacesViewModel_Factory(apiProvider);
  }

  public static SpacesViewModel newInstance(KashApiService api) {
    return new SpacesViewModel(api);
  }
}
