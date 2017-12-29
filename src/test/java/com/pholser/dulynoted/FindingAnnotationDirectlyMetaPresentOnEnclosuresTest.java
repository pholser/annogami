package com.pholser.dulynoted;

import com.pholser.dulynoted.annotations.A;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FindingAnnotationDirectlyMetaPresentOnEnclosuresTest {
    @Disabled("turn back on after addressing class scans")
    @Test void packageLevel() throws Exception {
        A found =
            new DirectMetaPresenceEnclosing(
                getClass().getDeclaredMethod("packageLevel"))
                .find(A.class)
                .orElseThrow(() -> new AssertionError("Missing annotation"));

        assertEquals(8, found.value());
    }

    @A(100) private interface I1 {
        int I = 0;

        void a();

        @A(101) interface I1Level1 {
            int J = 0;

            void b();
        }
    }

    @A(102) private interface I2 extends I1 {
        int I = 0;

        void c();

        @A(103) class I2Nest1 {
            int x;

            void yermom() {
            }
        }

        interface I2Level1 extends I1.I1Level1 {
            int J = 0;

            void d();
        }
    }

    @A(104) private static final class Level1 implements I2 {
        private int i;

        Level1() {
        }

        void foo() {
        }

        @Override public void a() {
        }

        @Override public void c() {
        }

        @A(105) private static final class Level2 {
            private int j;

            Level2() {
            }

            void bar() {
            }

            private static final class Level3 implements I1 {
                private int k;

                @A(106) Level3() {
                }

                void baz() {
                }

                @A(107) @Override public void a() {
                }
            }
        }
    }
}
