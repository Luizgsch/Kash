package com.kash.presentation.transaction;

import com.kash.data.local.UserPreferences;
import com.kash.domain.repository.DailyProfitRepository;
import com.kash.domain.repository.ProductRepository;
import com.kash.domain.usecase.AddSaleUseCase;
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
public final class TransactionViewModel_Factory implements Factory<TransactionViewModel> {
  private final Provider<ProductRepository> productRepoProvider;

  private final Provider<DailyProfitRepository> dailyProfitRepoProvider;

  private final Provider<AddSaleUseCase> addSaleUseCaseProvider;

  private final Provider<UserPreferences> prefsProvider;

  public TransactionViewModel_Factory(Provider<ProductRepository> productRepoProvider,
      Provider<DailyProfitRepository> dailyProfitRepoProvider,
      Provider<AddSaleUseCase> addSaleUseCaseProvider, Provider<UserPreferences> prefsProvider) {
    this.productRepoProvider = productRepoProvider;
    this.dailyProfitRepoProvider = dailyProfitRepoProvider;
    this.addSaleUseCaseProvider = addSaleUseCaseProvider;
    this.prefsProvider = prefsProvider;
  }

  @Override
  public TransactionViewModel get() {
    return newInstance(productRepoProvider.get(), dailyProfitRepoProvider.get(), addSaleUseCaseProvider.get(), prefsProvider.get());
  }

  public static TransactionViewModel_Factory create(Provider<ProductRepository> productRepoProvider,
      Provider<DailyProfitRepository> dailyProfitRepoProvider,
      Provider<AddSaleUseCase> addSaleUseCaseProvider, Provider<UserPreferences> prefsProvider) {
    return new TransactionViewModel_Factory(productRepoProvider, dailyProfitRepoProvider, addSaleUseCaseProvider, prefsProvider);
  }

  public static TransactionViewModel newInstance(ProductRepository productRepo,
      DailyProfitRepository dailyProfitRepo, AddSaleUseCase addSaleUseCase, UserPreferences prefs) {
    return new TransactionViewModel(productRepo, dailyProfitRepo, addSaleUseCase, prefs);
  }
}
