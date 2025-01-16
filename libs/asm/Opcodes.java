package me.syncwrld.booter.libs.asm;

public interface Opcodes {
  public static final int ASM4 = 262144;
  
  public static final int ASM5 = 327680;
  
  public static final int ASM6 = 393216;
  
  public static final int ASM7 = 458752;
  
  public static final int ASM8 = 524288;
  
  public static final int ASM9 = 589824;
  
  @Deprecated
  public static final int ASM10_EXPERIMENTAL = 17432576;
  
  public static final int SOURCE_DEPRECATED = 256;
  
  public static final int SOURCE_MASK = 256;
  
  public static final int V1_1 = 196653;
  
  public static final int V1_2 = 46;
  
  public static final int V1_3 = 47;
  
  public static final int V1_4 = 48;
  
  public static final int V1_5 = 49;
  
  public static final int V1_6 = 50;
  
  public static final int V1_7 = 51;
  
  public static final int V1_8 = 52;
  
  public static final int V9 = 53;
  
  public static final int V10 = 54;
  
  public static final int V11 = 55;
  
  public static final int V12 = 56;
  
  public static final int V13 = 57;
  
  public static final int V14 = 58;
  
  public static final int V15 = 59;
  
  public static final int V16 = 60;
  
  public static final int V17 = 61;
  
  public static final int V_PREVIEW = -65536;
  
  public static final int ACC_PUBLIC = 1;
  
  public static final int ACC_PRIVATE = 2;
  
  public static final int ACC_PROTECTED = 4;
  
  public static final int ACC_STATIC = 8;
  
  public static final int ACC_FINAL = 16;
  
  public static final int ACC_SUPER = 32;
  
  public static final int ACC_SYNCHRONIZED = 32;
  
  public static final int ACC_OPEN = 32;
  
  public static final int ACC_TRANSITIVE = 32;
  
  public static final int ACC_VOLATILE = 64;
  
  public static final int ACC_BRIDGE = 64;
  
  public static final int ACC_STATIC_PHASE = 64;
  
  public static final int ACC_VARARGS = 128;
  
  public static final int ACC_TRANSIENT = 128;
  
  public static final int ACC_NATIVE = 256;
  
  public static final int ACC_INTERFACE = 512;
  
  public static final int ACC_ABSTRACT = 1024;
  
  public static final int ACC_STRICT = 2048;
  
  public static final int ACC_SYNTHETIC = 4096;
  
  public static final int ACC_ANNOTATION = 8192;
  
  public static final int ACC_ENUM = 16384;
  
  public static final int ACC_MANDATED = 32768;
  
  public static final int ACC_MODULE = 32768;
  
  public static final int ACC_RECORD = 65536;
  
  public static final int ACC_DEPRECATED = 131072;
  
  public static final int T_BOOLEAN = 4;
  
  public static final int T_CHAR = 5;
  
  public static final int T_FLOAT = 6;
  
  public static final int T_DOUBLE = 7;
  
  public static final int T_BYTE = 8;
  
  public static final int T_SHORT = 9;
  
  public static final int T_INT = 10;
  
  public static final int T_LONG = 11;
  
  public static final int H_GETFIELD = 1;
  
  public static final int H_GETSTATIC = 2;
  
  public static final int H_PUTFIELD = 3;
  
  public static final int H_PUTSTATIC = 4;
  
  public static final int H_INVOKEVIRTUAL = 5;
  
  public static final int H_INVOKESTATIC = 6;
  
  public static final int H_INVOKESPECIAL = 7;
  
  public static final int H_NEWINVOKESPECIAL = 8;
  
  public static final int H_INVOKEINTERFACE = 9;
  
  public static final int F_NEW = -1;
  
  public static final int F_FULL = 0;
  
  public static final int F_APPEND = 1;
  
  public static final int F_CHOP = 2;
  
  public static final int F_SAME = 3;
  
  public static final int F_SAME1 = 4;
  
  public static final Integer TOP = Integer.valueOf(0);
  
  public static final Integer INTEGER = Integer.valueOf(1);
  
  public static final Integer FLOAT = Integer.valueOf(2);
  
  public static final Integer DOUBLE = Integer.valueOf(3);
  
  public static final Integer LONG = Integer.valueOf(4);
  
  public static final Integer NULL = Integer.valueOf(5);
  
  public static final Integer UNINITIALIZED_THIS = Integer.valueOf(6);
  
  public static final int NOP = 0;
  
  public static final int ACONST_NULL = 1;
  
  public static final int ICONST_M1 = 2;
  
  public static final int ICONST_0 = 3;
  
  public static final int ICONST_1 = 4;
  
  public static final int ICONST_2 = 5;
  
  public static final int ICONST_3 = 6;
  
  public static final int ICONST_4 = 7;
  
  public static final int ICONST_5 = 8;
  
  public static final int LCONST_0 = 9;
  
  public static final int LCONST_1 = 10;
  
  public static final int FCONST_0 = 11;
  
  public static final int FCONST_1 = 12;
  
  public static final int FCONST_2 = 13;
  
  public static final int DCONST_0 = 14;
  
  public static final int DCONST_1 = 15;
  
  public static final int BIPUSH = 16;
  
  public static final int SIPUSH = 17;
  
  public static final int LDC = 18;
  
  public static final int ILOAD = 21;
  
  public static final int LLOAD = 22;
  
  public static final int FLOAD = 23;
  
  public static final int DLOAD = 24;
  
  public static final int ALOAD = 25;
  
  public static final int IALOAD = 46;
  
  public static final int LALOAD = 47;
  
  public static final int FALOAD = 48;
  
  public static final int DALOAD = 49;
  
  public static final int AALOAD = 50;
  
  public static final int BALOAD = 51;
  
  public static final int CALOAD = 52;
  
  public static final int SALOAD = 53;
  
  public static final int ISTORE = 54;
  
  public static final int LSTORE = 55;
  
  public static final int FSTORE = 56;
  
  public static final int DSTORE = 57;
  
  public static final int ASTORE = 58;
  
  public static final int IASTORE = 79;
  
  public static final int LASTORE = 80;
  
  public static final int FASTORE = 81;
  
  public static final int DASTORE = 82;
  
  public static final int AASTORE = 83;
  
  public static final int BASTORE = 84;
  
  public static final int CASTORE = 85;
  
  public static final int SASTORE = 86;
  
  public static final int POP = 87;
  
  public static final int POP2 = 88;
  
  public static final int DUP = 89;
  
  public static final int DUP_X1 = 90;
  
  public static final int DUP_X2 = 91;
  
  public static final int DUP2 = 92;
  
  public static final int DUP2_X1 = 93;
  
  public static final int DUP2_X2 = 94;
  
  public static final int SWAP = 95;
  
  public static final int IADD = 96;
  
  public static final int LADD = 97;
  
  public static final int FADD = 98;
  
  public static final int DADD = 99;
  
  public static final int ISUB = 100;
  
  public static final int LSUB = 101;
  
  public static final int FSUB = 102;
  
  public static final int DSUB = 103;
  
  public static final int IMUL = 104;
  
  public static final int LMUL = 105;
  
  public static final int FMUL = 106;
  
  public static final int DMUL = 107;
  
  public static final int IDIV = 108;
  
  public static final int LDIV = 109;
  
  public static final int FDIV = 110;
  
  public static final int DDIV = 111;
  
  public static final int IREM = 112;
  
  public static final int LREM = 113;
  
  public static final int FREM = 114;
  
  public static final int DREM = 115;
  
  public static final int INEG = 116;
  
  public static final int LNEG = 117;
  
  public static final int FNEG = 118;
  
  public static final int DNEG = 119;
  
  public static final int ISHL = 120;
  
  public static final int LSHL = 121;
  
  public static final int ISHR = 122;
  
  public static final int LSHR = 123;
  
  public static final int IUSHR = 124;
  
  public static final int LUSHR = 125;
  
  public static final int IAND = 126;
  
  public static final int LAND = 127;
  
  public static final int IOR = 128;
  
  public static final int LOR = 129;
  
  public static final int IXOR = 130;
  
  public static final int LXOR = 131;
  
  public static final int IINC = 132;
  
  public static final int I2L = 133;
  
  public static final int I2F = 134;
  
  public static final int I2D = 135;
  
  public static final int L2I = 136;
  
  public static final int L2F = 137;
  
  public static final int L2D = 138;
  
  public static final int F2I = 139;
  
  public static final int F2L = 140;
  
  public static final int F2D = 141;
  
  public static final int D2I = 142;
  
  public static final int D2L = 143;
  
  public static final int D2F = 144;
  
  public static final int I2B = 145;
  
  public static final int I2C = 146;
  
  public static final int I2S = 147;
  
  public static final int LCMP = 148;
  
  public static final int FCMPL = 149;
  
  public static final int FCMPG = 150;
  
  public static final int DCMPL = 151;
  
  public static final int DCMPG = 152;
  
  public static final int IFEQ = 153;
  
  public static final int IFNE = 154;
  
  public static final int IFLT = 155;
  
  public static final int IFGE = 156;
  
  public static final int IFGT = 157;
  
  public static final int IFLE = 158;
  
  public static final int IF_ICMPEQ = 159;
  
  public static final int IF_ICMPNE = 160;
  
  public static final int IF_ICMPLT = 161;
  
  public static final int IF_ICMPGE = 162;
  
  public static final int IF_ICMPGT = 163;
  
  public static final int IF_ICMPLE = 164;
  
  public static final int IF_ACMPEQ = 165;
  
  public static final int IF_ACMPNE = 166;
  
  public static final int GOTO = 167;
  
  public static final int JSR = 168;
  
  public static final int RET = 169;
  
  public static final int TABLESWITCH = 170;
  
  public static final int LOOKUPSWITCH = 171;
  
  public static final int IRETURN = 172;
  
  public static final int LRETURN = 173;
  
  public static final int FRETURN = 174;
  
  public static final int DRETURN = 175;
  
  public static final int ARETURN = 176;
  
  public static final int RETURN = 177;
  
  public static final int GETSTATIC = 178;
  
  public static final int PUTSTATIC = 179;
  
  public static final int GETFIELD = 180;
  
  public static final int PUTFIELD = 181;
  
  public static final int INVOKEVIRTUAL = 182;
  
  public static final int INVOKESPECIAL = 183;
  
  public static final int INVOKESTATIC = 184;
  
  public static final int INVOKEINTERFACE = 185;
  
  public static final int INVOKEDYNAMIC = 186;
  
  public static final int NEW = 187;
  
  public static final int NEWARRAY = 188;
  
  public static final int ANEWARRAY = 189;
  
  public static final int ARRAYLENGTH = 190;
  
  public static final int ATHROW = 191;
  
  public static final int CHECKCAST = 192;
  
  public static final int INSTANCEOF = 193;
  
  public static final int MONITORENTER = 194;
  
  public static final int MONITOREXIT = 195;
  
  public static final int MULTIANEWARRAY = 197;
  
  public static final int IFNULL = 198;
  
  public static final int IFNONNULL = 199;
}
