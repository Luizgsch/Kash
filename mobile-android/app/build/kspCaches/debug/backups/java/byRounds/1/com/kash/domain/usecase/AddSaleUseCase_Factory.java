package com.kash.domain.usecase;

import com.kash.domain.repository.ProductRepository;
import com.kash.domain.repository.TransactionRepository;
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
public final class AddSaleUseCase_Factory implements Factory<AddSaleUseCase> {
  private final Provider<TransactionRepository> transactionRepoProvider;

  private final Provider<ProductRepository> productRepoProvider;

  public AddSaleUseCase_Factory(Provider<TransactionRepository> transactionRepoProvider,
      Provider<ProductRepository> productRepoProvider) {
    this.transactionRepoProvider = transactionRepoProvider;
    this.productRepoProvider = productRepoProvider;
  }

  @Override
  public AddSaleUseCase get() {
    return newInstance(transactionRepoProvider.get(), productRepoProvider.get());
  }

  public static AddSaleUseCase_Factory create(
      Provider<TransactionRepository> transactionRepoProvider,
      Provider<ProductRepository> productRepoProvider) {
    return new AddSaleUseCase_Factory(transactionRepoProvider, productRepoProvider);
  }

  public static AddSaleUseCase newInstance(TransactionRepository transactionRepo,
      ProductRepository productRepo) {
    return new AddSaleUseCase(transactionRepo, productRepo);
  }
}
