package com.kash.presentation.dashboard;

import com.kash.data.local.UserPreferences;
import com.kash.domain.repository.DailyProfitRepository;
import com.kash.domain.repository.ProductRepository;
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
  private final Provider<DailyProfitRepository> profitRepoProvider;

  private final Provider<ProductRepository> productRepoProvider;

  private final Provider<UserPreferences> prefsProvider;

  public DashboardViewModel_Factory(Provider<DailyProfitRepository> profitRepoProvider,
      Provider<ProductRepository> productRepoProvider, Provider<UserPreferences> prefsProvider) {
    this.profitRepoProvider = profitRepoProvider;
    this.productRepoProvider = productRepoProvider;
    this.prefsProvider = prefsProvider;
  }

  @Override
  public DashboardViewModel get() {
    return newInstance(profitRepoProvider.get(), productRepoProvider.get(), prefsProvider.get());
  }

  public static DashboardViewModel_Factory create(
      Provider<DailyProfitRepository> profitRepoProvider,
      Provider<ProductRepository> productRepoProvider, Provider<UserPreferences> prefsProvider) {
    return new DashboardViewModel_Factory(profitRepoProvider, productRepoProvider, prefsProvider);
  }

  public static DashboardViewModel newInstance(DailyProfitRepository profitRepo,
      ProductRepository productRepo, UserPreferences prefs) {
    return new DashboardViewModel(profitRepo, productRepo, prefs);
  }
}
