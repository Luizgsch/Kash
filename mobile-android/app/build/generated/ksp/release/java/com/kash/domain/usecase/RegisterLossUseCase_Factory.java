package com.kash.domain.usecase;

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
public final class RegisterLossUseCase_Factory implements Factory<RegisterLossUseCase> {
  private final Provider<ProductRepository> productRepoProvider;

  public RegisterLossUseCase_Factory(Provider<ProductRepository> productRepoProvider) {
    this.productRepoProvider = productRepoProvider;
  }

  @Override
  public RegisterLossUseCase get() {
    return newInstance(productRepoProvider.get());
  }

  public static RegisterLossUseCase_Factory create(
      Provider<ProductRepository> productRepoProvider) {
    return new RegisterLossUseCase_Factory(productRepoProvider);
  }

  public static RegisterLossUseCase newInstance(ProductRepository productRepo) {
    return new RegisterLossUseCase(productRepo);
  }
}
