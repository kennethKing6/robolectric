package org.robolectric.internal;

import java.util.concurrent.ThreadFactory;
import javax.inject.Inject;
import javax.inject.Named;
import org.robolectric.ApkLoader;
import org.robolectric.android.internal.AndroidEnvironment;
import org.robolectric.internal.bytecode.Sandbox;
import org.robolectric.internal.bytecode.SandboxClassLoader;
import org.robolectric.pluginapi.Sdk;
import org.robolectric.util.inject.Injector;

/** Container simulating an Android device. */
@SuppressWarnings("NewApi")
public class AndroidSandbox extends Sandbox {
  private final Sdk sdk;
  private final Environment environment;

  @Inject
  public AndroidSandbox(
      @Named("runtimeSdk") Sdk runtimeSdk,
      @Named("compileSdk") Sdk compileSdk,
      ResourcesMode resourcesMode,
      ApkLoader apkLoader,
      EnvironmentSpec environmentSpec,
      SandboxClassLoader sandboxClassLoader) {
    super(sandboxClassLoader);

    ClassLoader robolectricClassLoader = getRobolectricClassLoader();

    Injector sandboxScope =
        new Injector.Builder(robolectricClassLoader)
            .bind(ApkLoader.class, apkLoader) // shared singleton
            .bind(Environment.class, bootstrappedClass(environmentSpec.getEnvironmentClass()))
            .bind(new Injector.Key<>(Sdk.class, "runtimeSdk"), runtimeSdk)
            .bind(new Injector.Key<>(Sdk.class, "compileSdk"), compileSdk)
            .bind(ResourcesMode.class, resourcesMode)
            .build();

    sdk = runtimeSdk;
    this.environment = runOnMainThread(() -> sandboxScope.getInstance(Environment.class));
  }

  @Override
  protected ThreadFactory mainThreadFactory() {
    return r -> {
      String name = "SDK " + sdk.getApiLevel();
      return new Thread(new ThreadGroup(name), r, name + " Main Thread");
    };
  }

  public Sdk getSdk() {
    return sdk;
  }

  public Environment getEnvironment() {
    return environment;
  }

  public static class EnvironmentSpec {

    private final Class<? extends AndroidEnvironment> environmentClass;

    @Inject
    public EnvironmentSpec() {
      environmentClass = AndroidEnvironment.class;
    }

    public EnvironmentSpec(Class<? extends AndroidEnvironment> environmentClass) {
      this.environmentClass = environmentClass;
    }

    public Class<? extends AndroidEnvironment> getEnvironmentClass() {
      return environmentClass;
    }
  }

  @Override
  public String toString() {
    return "AndroidSandbox[SDK " + sdk + "]";
  }
}
