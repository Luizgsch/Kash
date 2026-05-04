package com.kash.presentation.auth;

import com.kash.domain.usecase.LoginUseCase;
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
public final class LoginViewModel_Factory implements Factory<LoginViewModel> {
  private final Provider<LoginUseCase> loginProvider;

  public LoginViewModel_Factory(Provider<LoginUseCase> loginProvider) {
    this.loginProvider = loginProvider;
  }

  @Override
  public LoginViewModel get() {
    return newInstance(loginProvider.get());
  }

  public static LoginViewModel_Factory create(Provider<LoginUseCase> loginProvider) {
    return new LoginViewModel_Factory(loginProvider);
  }

  public static LoginViewModel newInstance(LoginUseCase login) {
    return new LoginViewModel(login);
  }
}
