package com.kash.presentation.insights;

import com.kash.data.local.UserPreferences;
import com.kash.domain.usecase.GetProfitabilityInsightsUseCase;
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
public final class InsightsViewModel_Factory implements Factory<InsightsViewModel> {
  private final Provider<GetProfitabilityInsightsUseCase> getInsightsProvider;

  private final Provider<UserPreferences> prefsProvider;

  public InsightsViewModel_Factory(Provider<GetProfitabilityInsightsUseCase> getInsightsProvider,
      Provider<UserPreferences> prefsProvider) {
    this.getInsightsProvider = getInsightsProvider;
    this.prefsProvider = prefsProvider;
  }

  @Override
  public InsightsViewModel get() {
    return newInstance(getInsightsProvider.get(), prefsProvider.get());
  }

  public static InsightsViewModel_Factory create(
      Provider<GetProfitabilityInsightsUseCase> getInsightsProvider,
      Provider<UserPreferences> prefsProvider) {
    return new InsightsViewModel_Factory(getInsightsProvider, prefsProvider);
  }

  public static InsightsViewModel newInstance(GetProfitabilityInsightsUseCase getInsights,
      UserPreferences prefs) {
    return new InsightsViewModel(getInsights, prefs);
  }
}
