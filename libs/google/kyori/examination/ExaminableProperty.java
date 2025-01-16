package me.syncwrld.booter.libs.google.kyori.examination;

import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

public abstract class ExaminableProperty {
  private ExaminableProperty() {}
  
  public String toString() {
    return "ExaminableProperty{" + name() + "}";
  }
  
  @NotNull
  public static ExaminableProperty of(@NotNull final String name, @Nullable final Object value) {
    return new ExaminableProperty() {
        @NotNull
        public String name() {
          return name;
        }
        
        @NotNull
        public <R> R examine(@NotNull Examiner<? extends R> examiner) {
          return examiner.examine(value);
        }
      };
  }
  
  @NotNull
  public static ExaminableProperty of(@NotNull final String name, @Nullable final String value) {
    return new ExaminableProperty() {
        @NotNull
        public String name() {
          return name;
        }
        
        @NotNull
        public <R> R examine(@NotNull Examiner<? extends R> examiner) {
          return examiner.examine(value);
        }
      };
  }
  
  @NotNull
  public static ExaminableProperty of(@NotNull final String name, final boolean value) {
    return new ExaminableProperty() {
        @NotNull
        public String name() {
          return name;
        }
        
        @NotNull
        public <R> R examine(@NotNull Examiner<? extends R> examiner) {
          return examiner.examine(value);
        }
      };
  }
  
  @NotNull
  public static ExaminableProperty of(@NotNull final String name, final boolean[] value) {
    return new ExaminableProperty() {
        @NotNull
        public String name() {
          return name;
        }
        
        @NotNull
        public <R> R examine(@NotNull Examiner<? extends R> examiner) {
          return examiner.examine(value);
        }
      };
  }
  
  @NotNull
  public static ExaminableProperty of(@NotNull final String name, final byte value) {
    return new ExaminableProperty() {
        @NotNull
        public String name() {
          return name;
        }
        
        @NotNull
        public <R> R examine(@NotNull Examiner<? extends R> examiner) {
          return examiner.examine(value);
        }
      };
  }
  
  @NotNull
  public static ExaminableProperty of(@NotNull final String name, final byte[] value) {
    return new ExaminableProperty() {
        @NotNull
        public String name() {
          return name;
        }
        
        @NotNull
        public <R> R examine(@NotNull Examiner<? extends R> examiner) {
          return examiner.examine(value);
        }
      };
  }
  
  @NotNull
  public static ExaminableProperty of(@NotNull final String name, final char value) {
    return new ExaminableProperty() {
        @NotNull
        public String name() {
          return name;
        }
        
        @NotNull
        public <R> R examine(@NotNull Examiner<? extends R> examiner) {
          return examiner.examine(value);
        }
      };
  }
  
  @NotNull
  public static ExaminableProperty of(@NotNull final String name, final char[] value) {
    return new ExaminableProperty() {
        @NotNull
        public String name() {
          return name;
        }
        
        @NotNull
        public <R> R examine(@NotNull Examiner<? extends R> examiner) {
          return examiner.examine(value);
        }
      };
  }
  
  @NotNull
  public static ExaminableProperty of(@NotNull final String name, final double value) {
    return new ExaminableProperty() {
        @NotNull
        public String name() {
          return name;
        }
        
        @NotNull
        public <R> R examine(@NotNull Examiner<? extends R> examiner) {
          return examiner.examine(value);
        }
      };
  }
  
  @NotNull
  public static ExaminableProperty of(@NotNull final String name, final double[] value) {
    return new ExaminableProperty() {
        @NotNull
        public String name() {
          return name;
        }
        
        @NotNull
        public <R> R examine(@NotNull Examiner<? extends R> examiner) {
          return examiner.examine(value);
        }
      };
  }
  
  @NotNull
  public static ExaminableProperty of(@NotNull final String name, final float value) {
    return new ExaminableProperty() {
        @NotNull
        public String name() {
          return name;
        }
        
        @NotNull
        public <R> R examine(@NotNull Examiner<? extends R> examiner) {
          return examiner.examine(value);
        }
      };
  }
  
  @NotNull
  public static ExaminableProperty of(@NotNull final String name, final float[] value) {
    return new ExaminableProperty() {
        @NotNull
        public String name() {
          return name;
        }
        
        @NotNull
        public <R> R examine(@NotNull Examiner<? extends R> examiner) {
          return examiner.examine(value);
        }
      };
  }
  
  @NotNull
  public static ExaminableProperty of(@NotNull final String name, final int value) {
    return new ExaminableProperty() {
        @NotNull
        public String name() {
          return name;
        }
        
        @NotNull
        public <R> R examine(@NotNull Examiner<? extends R> examiner) {
          return examiner.examine(value);
        }
      };
  }
  
  @NotNull
  public static ExaminableProperty of(@NotNull final String name, final int[] value) {
    return new ExaminableProperty() {
        @NotNull
        public String name() {
          return name;
        }
        
        @NotNull
        public <R> R examine(@NotNull Examiner<? extends R> examiner) {
          return examiner.examine(value);
        }
      };
  }
  
  @NotNull
  public static ExaminableProperty of(@NotNull final String name, final long value) {
    return new ExaminableProperty() {
        @NotNull
        public String name() {
          return name;
        }
        
        @NotNull
        public <R> R examine(@NotNull Examiner<? extends R> examiner) {
          return examiner.examine(value);
        }
      };
  }
  
  @NotNull
  public static ExaminableProperty of(@NotNull final String name, final long[] value) {
    return new ExaminableProperty() {
        @NotNull
        public String name() {
          return name;
        }
        
        @NotNull
        public <R> R examine(@NotNull Examiner<? extends R> examiner) {
          return examiner.examine(value);
        }
      };
  }
  
  @NotNull
  public static ExaminableProperty of(@NotNull final String name, final short value) {
    return new ExaminableProperty() {
        @NotNull
        public String name() {
          return name;
        }
        
        @NotNull
        public <R> R examine(@NotNull Examiner<? extends R> examiner) {
          return examiner.examine(value);
        }
      };
  }
  
  @NotNull
  public static ExaminableProperty of(@NotNull final String name, final short[] value) {
    return new ExaminableProperty() {
        @NotNull
        public String name() {
          return name;
        }
        
        @NotNull
        public <R> R examine(@NotNull Examiner<? extends R> examiner) {
          return examiner.examine(value);
        }
      };
  }
  
  @NotNull
  public abstract String name();
  
  @NotNull
  public abstract <R> R examine(@NotNull Examiner<? extends R> paramExaminer);
}
