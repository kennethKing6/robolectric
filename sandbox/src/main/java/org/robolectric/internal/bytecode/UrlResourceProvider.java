package org.robolectric.internal.bytecode;

import java.net.URL;
import java.net.URLClassLoader;

public class UrlResourceProvider extends URLClassLoader implements ResourceProvider {

  public UrlResourceProvider(URL... urls) {
    super(urls, null);
  }
}
