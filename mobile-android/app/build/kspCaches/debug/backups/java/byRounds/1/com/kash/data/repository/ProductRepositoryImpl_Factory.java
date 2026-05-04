package com.kash.data.repository;

import com.kash.data.local.dao.LossDao;
import com.kash.data.local.dao.ProductDao;
import com.kash.data.local.dao.SaleDao;
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
public final class ProductRepositoryImpl_Factory implements Factory<ProductRepositoryImpl> {
  private final Provider<ProductDao> productDaoProvider;

  private final Provider<SaleDao> saleDaoProvider;

  private final Provider<LossDao> lossDaoProvider;

  public ProductRepositoryImpl_Factory(Provider<ProductDao> productDaoProvider,
      Provider<SaleDao> saleDaoProvider, Provider<LossDao> lossDaoProvider) {
    this.productDaoProvider = productDaoProvider;
    this.saleDaoProvider = saleDaoProvider;
    this.lossDaoProvider = lossDaoProvider;
  }

  @Override
  public ProductRepositoryImpl get() {
    return newInstance(productDaoProvider.get(), saleDaoProvider.get(), lossDaoProvider.get());
  }

  public static ProductRepositoryImpl_Factory create(Provider<ProductDao> productDaoProvider,
      Provider<SaleDao> saleDaoProvider, Provider<LossDao> lossDaoProvider) {
    return new ProductRepositoryImpl_Factory(productDaoProvider, saleDaoProvider, lossDaoProvider);
  }

  public static ProductRepositoryImpl newInstance(ProductDao productDao, SaleDao saleDao,
      LossDao lossDao) {
    return new ProductRepositoryImpl(productDao, saleDao, lossDao);
  }
}
