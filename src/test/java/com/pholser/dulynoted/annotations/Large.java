package com.pholser.dulynoted.annotations;

import java.lang.annotation.Retention;
import java.math.RoundingMode;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.math.RoundingMode.CEILING;
import static java.math.RoundingMode.FLOOR;
import static java.math.RoundingMode.HALF_UP;

@Retention(RUNTIME)
public @interface Large {
  boolean b() default true;

  byte by() default 1;

  char ch() default '2';

  double d() default 3;

  float f() default 4;

  int i() default 5;

  long ell() default 6;

  short sh() default 7;

  String s() default "8";

  RoundingMode mode() default FLOOR;

  boolean[] bs() default {};

  byte[] bys() default {9};

  char[] chs() default {10, 11};

  double[] ds() default {12, 13, 14};

  float[] fs() default {15, 16, 17, 18};

  int[] is() default {19, 20, 21, 22, 23};

  long[] longs() default {24, 25, 26, 27, 28, 29};

  short[] shs() default {30, 31, 32};

  String[] esses() default {"33", "34"};

  RoundingMode[] modes() default {CEILING, HALF_UP};
}
