package com.heroku.sdk.deploy;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WarApp extends App {

  protected File warFile;

  protected File webappRunnerJar;

  public WarApp(String name) throws IOException {
    super(name);
  }

  public WarApp(String name, List<String> buildpacks) throws IOException {
    super(name, buildpacks);
  }

  public WarApp(String client, String name, File warFile, File webappRunnerJar, File rootDir, File targetDir) {
    super(client, name, rootDir, targetDir, new ArrayList<String>());
    this.warFile = warFile;
    this.webappRunnerJar = webappRunnerJar;
  }

  public void deploy(List<File> includedFiles, Map<String,String> configVars, String jdkVersion, String stack, String slugFileName) throws Exception {
    includedFiles.add(webappRunnerJar);
    includedFiles.add(warFile);
    super.deploy(includedFiles, configVars, jdkVersion, stack, defaultProcTypes(), slugFileName);
  }

  public void deploy(List<File> includedFiles, Map<String,String> configVars, URL jdkUrl, String stack, String slugFileName) throws Exception {
    includedFiles.add(webappRunnerJar);
    includedFiles.add(warFile);
    super.deploy(includedFiles, configVars, jdkUrl, stack, defaultProcTypes(), slugFileName);
  }

  public void deploySlug(List<File> includedFiles, Map<String,String> configVars, String jdkVersion, String stack, String slugFileName) throws Exception {
    includedFiles.add(webappRunnerJar);
    includedFiles.add(warFile);
    super.deploySlug(includedFiles, configVars, jdkVersion, stack, defaultProcTypes(), slugFileName);
  }

  public void deploySlug(List<File> includedFiles, Map<String,String> configVars, URL jdkUrl, String stack, String slugFileName) throws Exception {
    includedFiles.add(webappRunnerJar);
    includedFiles.add(warFile);
    super.deploySlug(includedFiles, configVars, jdkUrl, stack, defaultProcTypes(), slugFileName);
  }

  protected Map<String,String> defaultProcTypes() {
    Map<String,String> processTypes = new HashMap<String, String>();
    processTypes.put("web", "java $JAVA_OPTS -jar " + relativize(webappRunnerJar) + " $WEBAPP_RUNNER_OPTS --port $PORT " + relativize(warFile));

    return processTypes;
  }
}
