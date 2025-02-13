package com.heroku.sdk.deploy;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.heroku.sdk.deploy.utils.Main;
import com.heroku.sdk.deploy.utils.Properties;
import org.apache.commons.io.FileUtils;

public class DeployWar extends WarApp {

  public static final String DEFAULT_WEBAPP_RUNNER_VERSION = Properties.getProperty("webapp-runner.version");

  private static final String WEBAPP_RUNNER_URL_FORMAT="http://central.maven.org/maven2/com/github/jsimone/webapp-runner/%s/webapp-runner-%s.jar";

  public DeployWar(String name, File warFile, URL webappRunnerUrl, List<String> buildpacks) throws IOException {
    super(name, buildpacks);
    this.warFile = warFile;

    // Setup the proxy before making the request to Maven Central
    setProxy();

    this.webappRunnerJar = new File(getAppDir(), "webapp-runner.jar");
    FileUtils.copyURLToFile(webappRunnerUrl, webappRunnerJar);
  }

  @Override
  protected Map<String,String> defaultProcTypes() {
    Map<String,String> processTypes = new HashMap<String, String>();
    processTypes.put("web", "java $JAVA_OPTS -jar webapp-runner.jar ${WEBAPP_RUNNER_OPTS} --port $PORT ./" + relativize(warFile));

    return processTypes;
  }

  private void setProxy() {
    String httpProxy = System.getenv("HTTP_PROXY");
    String httpsProxy = System.getenv("HTTPS_PROXY");
    if (null != httpsProxy) {
      setProxyProperties("https", httpsProxy);
    } else if (null != httpProxy) {
      setProxyProperties("http", httpProxy);
    }
  }

  private void setProxyProperties(String prefix, String proxy) {
    try {
      URI proxyUri = new URI(proxy);
      System.setProperty(prefix + ".proxyHost", proxyUri.getHost());
      if (proxyUri.getPort() > 0) {
        System.setProperty(prefix + ".proxyPort", String.valueOf(proxyUri.getPort()));
      }
    } catch (URISyntaxException e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public void logInfo(String message) { System.out.println(message); }

  @Override
  public void logDebug(String message) {
    if (Main.isDebug()) {
      System.out.println(message);
    }
  }

  public static void deploy() throws Exception {
    final String warFile = System.getProperty("heroku.warFile", null);

    final String webappRunnerVersion = System.getProperty(
            "heroku.webappRunnerVersion", DEFAULT_WEBAPP_RUNNER_VERSION);
    final String webappRunnerUrl = System.getProperty(
            "heroku.webappRunnerUrl", String.format(WEBAPP_RUNNER_URL_FORMAT, webappRunnerVersion, webappRunnerVersion));

    if (warFile == null) {
      throw new IllegalArgumentException("Path to WAR file must be provided with heroku.warFile system property!");
    }

    Main.deploy(new Main.DeployFunction<String, List<String>, App>() {
      @Override
      public App apply(String appName, List<String> buildpacks) throws IOException {
        return new DeployWar(appName, new File(warFile), new URL(webappRunnerUrl), buildpacks);
      }
    });
  }

  public static void main(String[] args) {
    try {
      deploy();
    } catch (Exception e) {
      System.out.println(" ! ERROR: " + e.getMessage());
      if (Main.isDebug()) {
        e.printStackTrace();
      } else {
        System.out.println(" !        Re-run with HEROKU_DEBUG=1 for more info.");
      }
      System.exit(1);
    }
  }
}
