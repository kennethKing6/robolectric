package org.robolectric.internal.bytecode;

import java.io.InputStream;
import java.net.URL;

public interface ResourceProvider {

  URL getResource(String resName);

  InputStream getResourceAsStream(String resName);
}
