package com.pholser.annogami.fixtures;

public class Samples {
  private Samples() {
    throw new AssertionError();
  }

  @A("methodA") @B
  @Tags({@Tag("x"), @Tag("y")}) @Tag("z")
  public void mixed(@A("paramA") String p) {
  }

  public static class OnlyB {
    @B public void n() {
    }
  }

  public static class Fields {
    @A("fieldA") public String f1;
    @B public String f2;
  }

  public static class Parents {
    public static class Base {
      @A("baseMethod") public void m(String p) {
      }
    }

    public static class Derived extends Base {
      @Override public void m(@A("paramA") String p) {
      }
    }
  }

  public record Rec(@A("rcA") String x) {
  }

  public record RecB(@B String x) {
  }
}
