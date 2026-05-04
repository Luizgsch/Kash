package com.kash.domain.usecase;

import com.kash.domain.repository.InsightsRepository;
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
public final class GetProfitabilityInsightsUseCase_Factory implements Factory<GetProfitabilityInsightsUseCase> {
  private final Provider<InsightsRepository> repoProvider;

  public GetProfitabilityInsightsUseCase_Factory(Provider<InsightsRepository> repoProvider) {
    this.repoProvider = repoProvider;
  }

  @Override
  public GetProfitabilityInsightsUseCase get() {
    return newInstance(repoProvider.get());
  }

  public static GetProfitabilityInsightsUseCase_Factory create(
      Provider<InsightsRepository> repoProvider) {
    return new GetProfitabilityInsightsUseCase_Factory(repoProvider);
  }

  public static GetProfitabilityInsightsUseCase newInstance(InsightsRepository repo) {
    return new GetProfitabilityInsightsUseCase(repo);
  }
}
