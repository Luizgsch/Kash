package com.kash.presentation.history;

import com.kash.data.local.UserPreferences;
import com.kash.domain.repository.DailyProfitRepository;
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
public final class HistoryViewModel_Factory implements Factory<HistoryViewModel> {
  private final Provider<DailyProfitRepository> profitRepoProvider;

  private final Provider<UserPreferences> prefsProvider;

  public HistoryViewModel_Factory(Provider<DailyProfitRepository> profitRepoProvider,
      Provider<UserPreferences> prefsProvider) {
    this.profitRepoProvider = profitRepoProvider;
    this.prefsProvider = prefsProvider;
  }

  @Override
  public HistoryViewModel get() {
    return newInstance(profitRepoProvider.get(), prefsProvider.get());
  }

  public static HistoryViewModel_Factory create(Provider<DailyProfitRepository> profitRepoProvider,
      Provider<UserPreferences> prefsProvider) {
    return new HistoryViewModel_Factory(profitRepoProvider, prefsProvider);
  }

  public static HistoryViewModel newInstance(DailyProfitRepository profitRepo,
      UserPreferences prefs) {
    return new HistoryViewModel(profitRepo, prefs);
  }
}
