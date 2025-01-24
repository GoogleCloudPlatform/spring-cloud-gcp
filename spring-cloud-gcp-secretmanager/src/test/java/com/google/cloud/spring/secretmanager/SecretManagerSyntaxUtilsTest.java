package com.google.cloud.spring.secretmanager;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.Marker;

public class SecretManagerSyntaxUtilsTest {

  @Test
  public void testDeprecatedSyntaxWarns() {
    FakeLogger logger = new FakeLogger();
    SecretManagerSyntaxUtils.warnIfUsingDeprecatedSyntax(logger, "sm@my-secret");
    assertThat(logger.getWarnCounter()).isEqualTo(0);
    SecretManagerSyntaxUtils.warnIfUsingDeprecatedSyntax(logger, "sm://my-secret");
    assertThat(logger.getWarnCounter()).isEqualTo(1);
  }

  public class FakeLogger implements Logger {

    private int warnCounter;

    public int getWarnCounter() {
      return warnCounter;
    }

    @Override
    public void warn(String s) {
      warnCounter++;
    }

    @Override
    public void warn(String s, Object o) {
      warnCounter++;
    }

    @Override
    public void warn(String s, Object... objects) {
      warnCounter++;
    }

    @Override
    public void warn(String s, Object o, Object o1) {
      warnCounter++;
    }

    @Override
    public void warn(String s, Throwable throwable) {
      warnCounter++;
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
      return true;
    }

    @Override
    public void warn(Marker marker, String s) {
      warnCounter++;
    }

    @Override
    public void warn(Marker marker, String s, Object o) {
      warnCounter++;
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1) {
      warnCounter++;
    }

    @Override
    public void warn(Marker marker, String s, Object... objects) {
      warnCounter++;
    }

    @Override
    public void warn(Marker marker, String s, Throwable throwable) {
      warnCounter++;
    }

    @Override
    public String getName() {
      return "";
    }

    @Override
    public boolean isTraceEnabled() {
      return false;
    }

    @Override
    public void trace(String s) {

    }

    @Override
    public void trace(String s, Object o) {

    }

    @Override
    public void trace(String s, Object o, Object o1) {

    }

    @Override
    public void trace(String s, Object... objects) {

    }

    @Override
    public void trace(String s, Throwable throwable) {

    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
      return false;
    }

    @Override
    public void trace(Marker marker, String s) {

    }

    @Override
    public void trace(Marker marker, String s, Object o) {

    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1) {

    }

    @Override
    public void trace(Marker marker, String s, Object... objects) {

    }

    @Override
    public void trace(Marker marker, String s, Throwable throwable) {

    }

    @Override
    public boolean isDebugEnabled() {
      return false;
    }

    @Override
    public void debug(String s) {

    }

    @Override
    public void debug(String s, Object o) {

    }

    @Override
    public void debug(String s, Object o, Object o1) {

    }

    @Override
    public void debug(String s, Object... objects) {

    }

    @Override
    public void debug(String s, Throwable throwable) {

    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
      return false;
    }

    @Override
    public void debug(Marker marker, String s) {

    }

    @Override
    public void debug(Marker marker, String s, Object o) {

    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1) {

    }

    @Override
    public void debug(Marker marker, String s, Object... objects) {

    }

    @Override
    public void debug(Marker marker, String s, Throwable throwable) {

    }

    @Override
    public boolean isInfoEnabled() {
      return false;
    }

    @Override
    public void info(String s) {

    }

    @Override
    public void info(String s, Object o) {

    }

    @Override
    public void info(String s, Object o, Object o1) {

    }

    @Override
    public void info(String s, Object... objects) {

    }

    @Override
    public void info(String s, Throwable throwable) {

    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
      return false;
    }

    @Override
    public void info(Marker marker, String s) {

    }

    @Override
    public void info(Marker marker, String s, Object o) {

    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1) {

    }

    @Override
    public void info(Marker marker, String s, Object... objects) {

    }

    @Override
    public void info(Marker marker, String s, Throwable throwable) {

    }

    @Override
    public boolean isWarnEnabled() {
      return true;
    }

    @Override
    public boolean isErrorEnabled() {
      return false;
    }

    @Override
    public void error(String s) {

    }

    @Override
    public void error(String s, Object o) {

    }

    @Override
    public void error(String s, Object o, Object o1) {

    }

    @Override
    public void error(String s, Object... objects) {

    }

    @Override
    public void error(String s, Throwable throwable) {

    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
      return false;
    }

    @Override
    public void error(Marker marker, String s) {

    }

    @Override
    public void error(Marker marker, String s, Object o) {

    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1) {

    }

    @Override
    public void error(Marker marker, String s, Object... objects) {

    }

    @Override
    public void error(Marker marker, String s, Throwable throwable) {

    }
  }
}
