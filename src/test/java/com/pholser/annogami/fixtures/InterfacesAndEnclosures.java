package com.pholser.annogami.fixtures;

public class InterfacesAndEnclosures {
  private InterfacesAndEnclosures() {
    throw new AssertionError();
  }

  @A("ifaceTypeA")
  public interface ITypeA {
  }

  @A("ifaceTypeB")
  public interface ITypeB {
  }

  public static class ImplementsAandB implements ITypeA, ITypeB {
    public void m() {
    }
  }

  public interface IMethodAnn {
    @A("ifaceMethodA")
    void m();
  }

  public static class OverridesMethodButUnannotated implements IMethodAnn {
    @Override
    public void m() {
    }
  }

  @A("i1")
  public interface I1 {
  }

  @A("i2")
  public interface I2 {
  }

  public static class ImplementsI1ThenI2 implements I1, I2 {
  }

  @A("outer")
  public static class Outer {
    public class InnerNoOwnA {
      public void m() {
      }
    }
  }

  public static class Outer2 {
    @A("middle")
    public class Middle {
      public class Leaf {
        public void m() {
        }
      }
    }
  }

  @A("grandOuter")
  public static class GrandOuter {
    public class Middle2 {
      public class Leaf2 {
        public void m() {
        }
      }
    }
  }
}
