package com.kash.presentation.product;

import com.kash.data.local.UserPreferences;
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
public final class ProductViewModel_Factory implements Factory<ProductViewModel> {
  private final Provider<ProductRepository> productRepoProvider;

  private final Provider<UserPreferences> prefsProvider;

  public ProductViewModel_Factory(Provider<ProductRepository> productRepoProvider,
      Provider<UserPreferences> prefsProvider) {
    this.productRepoProvider = productRepoProvider;
    this.prefsProvider = prefsProvider;
  }

  @Override
  public ProductViewModel get() {
    return newInstance(productRepoProvider.get(), prefsProvider.get());
  }

  public static ProductViewModel_Factory create(Provider<ProductRepository> productRepoProvider,
      Provider<UserPreferences> prefsProvider) {
    return new ProductViewModel_Factory(productRepoProvider, prefsProvider);
  }

  public static ProductViewModel newInstance(ProductRepository productRepo, UserPreferences prefs) {
    return new ProductViewModel(productRepo, prefs);
  }
}
