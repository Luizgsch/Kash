package com.kash.presentation.dashboard;

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
public final class DashboardViewModel_Factory implements Factory<DashboardViewModel> {
  private final Provider<KashApiService> apiProvider;

  public DashboardViewModel_Factory(Provider<KashApiService> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public DashboardViewModel get() {
    return newInstance(apiProvider.get());
  }

  public static DashboardViewModel_Factory create(Provider<KashApiService> apiProvider) {
    return new DashboardViewModel_Factory(apiProvider);
  }

  public static DashboardViewModel newInstance(KashApiService api) {
    return new DashboardViewModel(api);
  }
}
