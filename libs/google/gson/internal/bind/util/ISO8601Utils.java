package me.syncwrld.booter.libs.google.gson.internal.bind.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class ISO8601Utils {
  private static final String UTC_ID = "UTC";
  
  private static final TimeZone TIMEZONE_UTC = TimeZone.getTimeZone("UTC");
  
  public static String format(Date date) {
    return format(date, false, TIMEZONE_UTC);
  }
  
  public static String format(Date date, boolean millis) {
    return format(date, millis, TIMEZONE_UTC);
  }
  
  public static String format(Date date, boolean millis, TimeZone tz) {
    Calendar calendar = new GregorianCalendar(tz, Locale.US);
    calendar.setTime(date);
    int capacity = "yyyy-MM-ddThh:mm:ss".length();
    capacity += millis ? ".sss".length() : 0;
    capacity += (tz.getRawOffset() == 0) ? "Z".length() : "+hh:mm".length();
    StringBuilder formatted = new StringBuilder(capacity);
    padInt(formatted, calendar.get(1), "yyyy".length());
    formatted.append('-');
    padInt(formatted, calendar.get(2) + 1, "MM".length());
    formatted.append('-');
    padInt(formatted, calendar.get(5), "dd".length());
    formatted.append('T');
    padInt(formatted, calendar.get(11), "hh".length());
    formatted.append(':');
    padInt(formatted, calendar.get(12), "mm".length());
    formatted.append(':');
    padInt(formatted, calendar.get(13), "ss".length());
    if (millis) {
      formatted.append('.');
      padInt(formatted, calendar.get(14), "sss".length());
    } 
    int offset = tz.getOffset(calendar.getTimeInMillis());
    if (offset != 0) {
      int hours = Math.abs(offset / 60000 / 60);
      int minutes = Math.abs(offset / 60000 % 60);
      formatted.append((offset < 0) ? 45 : 43);
      padInt(formatted, hours, "hh".length());
      formatted.append(':');
      padInt(formatted, minutes, "mm".length());
    } else {
      formatted.append('Z');
    } 
    return formatted.toString();
  }
  
  public static Date parse(String date, ParsePosition pos) throws ParseException {
    // Byte code:
    //   0: aconst_null
    //   1: astore_2
    //   2: aload_1
    //   3: invokevirtual getIndex : ()I
    //   6: istore_3
    //   7: aload_0
    //   8: iload_3
    //   9: iinc #3, 4
    //   12: iload_3
    //   13: invokestatic parseInt : (Ljava/lang/String;II)I
    //   16: istore #4
    //   18: aload_0
    //   19: iload_3
    //   20: bipush #45
    //   22: invokestatic checkOffset : (Ljava/lang/String;IC)Z
    //   25: ifeq -> 31
    //   28: iinc #3, 1
    //   31: aload_0
    //   32: iload_3
    //   33: iinc #3, 2
    //   36: iload_3
    //   37: invokestatic parseInt : (Ljava/lang/String;II)I
    //   40: istore #5
    //   42: aload_0
    //   43: iload_3
    //   44: bipush #45
    //   46: invokestatic checkOffset : (Ljava/lang/String;IC)Z
    //   49: ifeq -> 55
    //   52: iinc #3, 1
    //   55: aload_0
    //   56: iload_3
    //   57: iinc #3, 2
    //   60: iload_3
    //   61: invokestatic parseInt : (Ljava/lang/String;II)I
    //   64: istore #6
    //   66: iconst_0
    //   67: istore #7
    //   69: iconst_0
    //   70: istore #8
    //   72: iconst_0
    //   73: istore #9
    //   75: iconst_0
    //   76: istore #10
    //   78: aload_0
    //   79: iload_3
    //   80: bipush #84
    //   82: invokestatic checkOffset : (Ljava/lang/String;IC)Z
    //   85: istore #11
    //   87: iload #11
    //   89: ifne -> 134
    //   92: aload_0
    //   93: invokevirtual length : ()I
    //   96: iload_3
    //   97: if_icmpgt -> 134
    //   100: new java/util/GregorianCalendar
    //   103: dup
    //   104: iload #4
    //   106: iload #5
    //   108: iconst_1
    //   109: isub
    //   110: iload #6
    //   112: invokespecial <init> : (III)V
    //   115: astore #12
    //   117: aload #12
    //   119: iconst_0
    //   120: invokevirtual setLenient : (Z)V
    //   123: aload_1
    //   124: iload_3
    //   125: invokevirtual setIndex : (I)V
    //   128: aload #12
    //   130: invokevirtual getTime : ()Ljava/util/Date;
    //   133: areturn
    //   134: iload #11
    //   136: ifeq -> 355
    //   139: aload_0
    //   140: iinc #3, 1
    //   143: iload_3
    //   144: iinc #3, 2
    //   147: iload_3
    //   148: invokestatic parseInt : (Ljava/lang/String;II)I
    //   151: istore #7
    //   153: aload_0
    //   154: iload_3
    //   155: bipush #58
    //   157: invokestatic checkOffset : (Ljava/lang/String;IC)Z
    //   160: ifeq -> 166
    //   163: iinc #3, 1
    //   166: aload_0
    //   167: iload_3
    //   168: iinc #3, 2
    //   171: iload_3
    //   172: invokestatic parseInt : (Ljava/lang/String;II)I
    //   175: istore #8
    //   177: aload_0
    //   178: iload_3
    //   179: bipush #58
    //   181: invokestatic checkOffset : (Ljava/lang/String;IC)Z
    //   184: ifeq -> 190
    //   187: iinc #3, 1
    //   190: aload_0
    //   191: invokevirtual length : ()I
    //   194: iload_3
    //   195: if_icmple -> 355
    //   198: aload_0
    //   199: iload_3
    //   200: invokevirtual charAt : (I)C
    //   203: istore #12
    //   205: iload #12
    //   207: bipush #90
    //   209: if_icmpeq -> 355
    //   212: iload #12
    //   214: bipush #43
    //   216: if_icmpeq -> 355
    //   219: iload #12
    //   221: bipush #45
    //   223: if_icmpeq -> 355
    //   226: aload_0
    //   227: iload_3
    //   228: iinc #3, 2
    //   231: iload_3
    //   232: invokestatic parseInt : (Ljava/lang/String;II)I
    //   235: istore #9
    //   237: iload #9
    //   239: bipush #59
    //   241: if_icmple -> 255
    //   244: iload #9
    //   246: bipush #63
    //   248: if_icmpge -> 255
    //   251: bipush #59
    //   253: istore #9
    //   255: aload_0
    //   256: iload_3
    //   257: bipush #46
    //   259: invokestatic checkOffset : (Ljava/lang/String;IC)Z
    //   262: ifeq -> 355
    //   265: iinc #3, 1
    //   268: aload_0
    //   269: iload_3
    //   270: iconst_1
    //   271: iadd
    //   272: invokestatic indexOfNonDigit : (Ljava/lang/String;I)I
    //   275: istore #13
    //   277: iload #13
    //   279: iload_3
    //   280: iconst_3
    //   281: iadd
    //   282: invokestatic min : (II)I
    //   285: istore #14
    //   287: aload_0
    //   288: iload_3
    //   289: iload #14
    //   291: invokestatic parseInt : (Ljava/lang/String;II)I
    //   294: istore #15
    //   296: iload #14
    //   298: iload_3
    //   299: isub
    //   300: lookupswitch default -> 348, 1 -> 338, 2 -> 328
    //   328: iload #15
    //   330: bipush #10
    //   332: imul
    //   333: istore #10
    //   335: goto -> 352
    //   338: iload #15
    //   340: bipush #100
    //   342: imul
    //   343: istore #10
    //   345: goto -> 352
    //   348: iload #15
    //   350: istore #10
    //   352: iload #13
    //   354: istore_3
    //   355: aload_0
    //   356: invokevirtual length : ()I
    //   359: iload_3
    //   360: if_icmpgt -> 373
    //   363: new java/lang/IllegalArgumentException
    //   366: dup
    //   367: ldc 'No time zone indicator'
    //   369: invokespecial <init> : (Ljava/lang/String;)V
    //   372: athrow
    //   373: aconst_null
    //   374: astore #12
    //   376: aload_0
    //   377: iload_3
    //   378: invokevirtual charAt : (I)C
    //   381: istore #13
    //   383: iload #13
    //   385: bipush #90
    //   387: if_icmpne -> 401
    //   390: getstatic me/syncwrld/booter/libs/google/gson/internal/bind/util/ISO8601Utils.TIMEZONE_UTC : Ljava/util/TimeZone;
    //   393: astore #12
    //   395: iinc #3, 1
    //   398: goto -> 638
    //   401: iload #13
    //   403: bipush #43
    //   405: if_icmpeq -> 415
    //   408: iload #13
    //   410: bipush #45
    //   412: if_icmpne -> 605
    //   415: aload_0
    //   416: iload_3
    //   417: invokevirtual substring : (I)Ljava/lang/String;
    //   420: astore #14
    //   422: aload #14
    //   424: invokevirtual length : ()I
    //   427: iconst_5
    //   428: if_icmplt -> 436
    //   431: aload #14
    //   433: goto -> 456
    //   436: new java/lang/StringBuilder
    //   439: dup
    //   440: invokespecial <init> : ()V
    //   443: aload #14
    //   445: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   448: ldc '00'
    //   450: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   453: invokevirtual toString : ()Ljava/lang/String;
    //   456: astore #14
    //   458: iload_3
    //   459: aload #14
    //   461: invokevirtual length : ()I
    //   464: iadd
    //   465: istore_3
    //   466: ldc '+0000'
    //   468: aload #14
    //   470: invokevirtual equals : (Ljava/lang/Object;)Z
    //   473: ifne -> 486
    //   476: ldc '+00:00'
    //   478: aload #14
    //   480: invokevirtual equals : (Ljava/lang/Object;)Z
    //   483: ifeq -> 494
    //   486: getstatic me/syncwrld/booter/libs/google/gson/internal/bind/util/ISO8601Utils.TIMEZONE_UTC : Ljava/util/TimeZone;
    //   489: astore #12
    //   491: goto -> 602
    //   494: new java/lang/StringBuilder
    //   497: dup
    //   498: invokespecial <init> : ()V
    //   501: ldc 'GMT'
    //   503: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   506: aload #14
    //   508: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   511: invokevirtual toString : ()Ljava/lang/String;
    //   514: astore #15
    //   516: aload #15
    //   518: invokestatic getTimeZone : (Ljava/lang/String;)Ljava/util/TimeZone;
    //   521: astore #12
    //   523: aload #12
    //   525: invokevirtual getID : ()Ljava/lang/String;
    //   528: astore #16
    //   530: aload #16
    //   532: aload #15
    //   534: invokevirtual equals : (Ljava/lang/Object;)Z
    //   537: ifne -> 602
    //   540: aload #16
    //   542: ldc ':'
    //   544: ldc ''
    //   546: invokevirtual replace : (Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
    //   549: astore #17
    //   551: aload #17
    //   553: aload #15
    //   555: invokevirtual equals : (Ljava/lang/Object;)Z
    //   558: ifne -> 602
    //   561: new java/lang/IndexOutOfBoundsException
    //   564: dup
    //   565: new java/lang/StringBuilder
    //   568: dup
    //   569: invokespecial <init> : ()V
    //   572: ldc 'Mismatching time zone indicator: '
    //   574: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   577: aload #15
    //   579: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   582: ldc ' given, resolves to '
    //   584: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   587: aload #12
    //   589: invokevirtual getID : ()Ljava/lang/String;
    //   592: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   595: invokevirtual toString : ()Ljava/lang/String;
    //   598: invokespecial <init> : (Ljava/lang/String;)V
    //   601: athrow
    //   602: goto -> 638
    //   605: new java/lang/IndexOutOfBoundsException
    //   608: dup
    //   609: new java/lang/StringBuilder
    //   612: dup
    //   613: invokespecial <init> : ()V
    //   616: ldc 'Invalid time zone indicator ''
    //   618: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   621: iload #13
    //   623: invokevirtual append : (C)Ljava/lang/StringBuilder;
    //   626: ldc '''
    //   628: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   631: invokevirtual toString : ()Ljava/lang/String;
    //   634: invokespecial <init> : (Ljava/lang/String;)V
    //   637: athrow
    //   638: new java/util/GregorianCalendar
    //   641: dup
    //   642: aload #12
    //   644: invokespecial <init> : (Ljava/util/TimeZone;)V
    //   647: astore #14
    //   649: aload #14
    //   651: iconst_0
    //   652: invokevirtual setLenient : (Z)V
    //   655: aload #14
    //   657: iconst_1
    //   658: iload #4
    //   660: invokevirtual set : (II)V
    //   663: aload #14
    //   665: iconst_2
    //   666: iload #5
    //   668: iconst_1
    //   669: isub
    //   670: invokevirtual set : (II)V
    //   673: aload #14
    //   675: iconst_5
    //   676: iload #6
    //   678: invokevirtual set : (II)V
    //   681: aload #14
    //   683: bipush #11
    //   685: iload #7
    //   687: invokevirtual set : (II)V
    //   690: aload #14
    //   692: bipush #12
    //   694: iload #8
    //   696: invokevirtual set : (II)V
    //   699: aload #14
    //   701: bipush #13
    //   703: iload #9
    //   705: invokevirtual set : (II)V
    //   708: aload #14
    //   710: bipush #14
    //   712: iload #10
    //   714: invokevirtual set : (II)V
    //   717: aload_1
    //   718: iload_3
    //   719: invokevirtual setIndex : (I)V
    //   722: aload #14
    //   724: invokevirtual getTime : ()Ljava/util/Date;
    //   727: areturn
    //   728: astore_3
    //   729: aload_3
    //   730: astore_2
    //   731: goto -> 743
    //   734: astore_3
    //   735: aload_3
    //   736: astore_2
    //   737: goto -> 743
    //   740: astore_3
    //   741: aload_3
    //   742: astore_2
    //   743: aload_0
    //   744: ifnonnull -> 751
    //   747: aconst_null
    //   748: goto -> 775
    //   751: new java/lang/StringBuilder
    //   754: dup
    //   755: invokespecial <init> : ()V
    //   758: bipush #34
    //   760: invokevirtual append : (C)Ljava/lang/StringBuilder;
    //   763: aload_0
    //   764: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   767: bipush #34
    //   769: invokevirtual append : (C)Ljava/lang/StringBuilder;
    //   772: invokevirtual toString : ()Ljava/lang/String;
    //   775: astore_3
    //   776: aload_2
    //   777: invokevirtual getMessage : ()Ljava/lang/String;
    //   780: astore #4
    //   782: aload #4
    //   784: ifnull -> 795
    //   787: aload #4
    //   789: invokevirtual isEmpty : ()Z
    //   792: ifeq -> 827
    //   795: new java/lang/StringBuilder
    //   798: dup
    //   799: invokespecial <init> : ()V
    //   802: ldc '('
    //   804: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   807: aload_2
    //   808: invokevirtual getClass : ()Ljava/lang/Class;
    //   811: invokevirtual getName : ()Ljava/lang/String;
    //   814: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   817: ldc ')'
    //   819: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   822: invokevirtual toString : ()Ljava/lang/String;
    //   825: astore #4
    //   827: new java/text/ParseException
    //   830: dup
    //   831: new java/lang/StringBuilder
    //   834: dup
    //   835: invokespecial <init> : ()V
    //   838: ldc 'Failed to parse date ['
    //   840: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   843: aload_3
    //   844: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   847: ldc_w ']: '
    //   850: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   853: aload #4
    //   855: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   858: invokevirtual toString : ()Ljava/lang/String;
    //   861: aload_1
    //   862: invokevirtual getIndex : ()I
    //   865: invokespecial <init> : (Ljava/lang/String;I)V
    //   868: astore #5
    //   870: aload #5
    //   872: aload_2
    //   873: invokevirtual initCause : (Ljava/lang/Throwable;)Ljava/lang/Throwable;
    //   876: pop
    //   877: aload #5
    //   879: athrow
    // Line number table:
    //   Java source line number -> byte code offset
    //   #128	-> 0
    //   #130	-> 2
    //   #133	-> 7
    //   #134	-> 18
    //   #135	-> 28
    //   #139	-> 31
    //   #140	-> 42
    //   #141	-> 52
    //   #145	-> 55
    //   #147	-> 66
    //   #148	-> 69
    //   #149	-> 72
    //   #150	-> 75
    //   #153	-> 78
    //   #155	-> 87
    //   #156	-> 100
    //   #157	-> 117
    //   #159	-> 123
    //   #160	-> 128
    //   #163	-> 134
    //   #166	-> 139
    //   #167	-> 153
    //   #168	-> 163
    //   #171	-> 166
    //   #172	-> 177
    //   #173	-> 187
    //   #176	-> 190
    //   #177	-> 198
    //   #178	-> 205
    //   #179	-> 226
    //   #180	-> 237
    //   #182	-> 255
    //   #183	-> 265
    //   #184	-> 268
    //   #185	-> 277
    //   #186	-> 287
    //   #188	-> 296
    //   #190	-> 328
    //   #191	-> 335
    //   #193	-> 338
    //   #194	-> 345
    //   #196	-> 348
    //   #198	-> 352
    //   #205	-> 355
    //   #206	-> 363
    //   #209	-> 373
    //   #210	-> 376
    //   #212	-> 383
    //   #213	-> 390
    //   #214	-> 395
    //   #215	-> 401
    //   #216	-> 415
    //   #219	-> 422
    //   #221	-> 458
    //   #223	-> 466
    //   #224	-> 486
    //   #230	-> 494
    //   #233	-> 516
    //   #235	-> 523
    //   #236	-> 530
    //   #242	-> 540
    //   #243	-> 551
    //   #244	-> 561
    //   #245	-> 589
    //   #249	-> 602
    //   #250	-> 605
    //   #253	-> 638
    //   #254	-> 649
    //   #255	-> 655
    //   #256	-> 663
    //   #257	-> 673
    //   #258	-> 681
    //   #259	-> 690
    //   #260	-> 699
    //   #261	-> 708
    //   #263	-> 717
    //   #264	-> 722
    //   #267	-> 728
    //   #268	-> 729
    //   #273	-> 731
    //   #269	-> 734
    //   #270	-> 735
    //   #273	-> 737
    //   #271	-> 740
    //   #272	-> 741
    //   #274	-> 743
    //   #275	-> 776
    //   #276	-> 782
    //   #277	-> 795
    //   #279	-> 827
    //   #280	-> 870
    //   #281	-> 877
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   117	17	12	calendar	Ljava/util/Calendar;
    //   277	78	13	endOffset	I
    //   287	68	14	parseEndOffset	I
    //   296	59	15	fraction	I
    //   205	150	12	c	C
    //   551	51	17	cleaned	Ljava/lang/String;
    //   516	86	15	timezoneId	Ljava/lang/String;
    //   530	72	16	act	Ljava/lang/String;
    //   422	180	14	timezoneOffset	Ljava/lang/String;
    //   7	721	3	offset	I
    //   18	710	4	year	I
    //   42	686	5	month	I
    //   66	662	6	day	I
    //   69	659	7	hour	I
    //   72	656	8	minutes	I
    //   75	653	9	seconds	I
    //   78	650	10	milliseconds	I
    //   87	641	11	hasT	Z
    //   376	352	12	timezone	Ljava/util/TimeZone;
    //   383	345	13	timezoneIndicator	C
    //   649	79	14	calendar	Ljava/util/Calendar;
    //   729	2	3	e	Ljava/lang/IndexOutOfBoundsException;
    //   735	2	3	e	Ljava/lang/NumberFormatException;
    //   741	2	3	e	Ljava/lang/IllegalArgumentException;
    //   0	880	0	date	Ljava/lang/String;
    //   0	880	1	pos	Ljava/text/ParsePosition;
    //   2	878	2	fail	Ljava/lang/Exception;
    //   776	104	3	input	Ljava/lang/String;
    //   782	98	4	msg	Ljava/lang/String;
    //   870	10	5	ex	Ljava/text/ParseException;
    // Exception table:
    //   from	to	target	type
    //   2	133	728	java/lang/IndexOutOfBoundsException
    //   2	133	734	java/lang/NumberFormatException
    //   2	133	740	java/lang/IllegalArgumentException
    //   134	727	728	java/lang/IndexOutOfBoundsException
    //   134	727	734	java/lang/NumberFormatException
    //   134	727	740	java/lang/IllegalArgumentException
  }
  
  private static boolean checkOffset(String value, int offset, char expected) {
    return (offset < value.length() && value.charAt(offset) == expected);
  }
  
  private static int parseInt(String value, int beginIndex, int endIndex) throws NumberFormatException {
    if (beginIndex < 0 || endIndex > value.length() || beginIndex > endIndex)
      throw new NumberFormatException(value); 
    int i = beginIndex;
    int result = 0;
    if (i < endIndex) {
      int digit = Character.digit(value.charAt(i++), 10);
      if (digit < 0)
        throw new NumberFormatException("Invalid number: " + value.substring(beginIndex, endIndex)); 
      result = -digit;
    } 
    while (i < endIndex) {
      int digit = Character.digit(value.charAt(i++), 10);
      if (digit < 0)
        throw new NumberFormatException("Invalid number: " + value.substring(beginIndex, endIndex)); 
      result *= 10;
      result -= digit;
    } 
    return -result;
  }
  
  private static void padInt(StringBuilder buffer, int value, int length) {
    String strValue = Integer.toString(value);
    for (int i = length - strValue.length(); i > 0; i--)
      buffer.append('0'); 
    buffer.append(strValue);
  }
  
  private static int indexOfNonDigit(String string, int offset) {
    for (int i = offset; i < string.length(); i++) {
      char c = string.charAt(i);
      if (c < '0' || c > '9')
        return i; 
    } 
    return string.length();
  }
}
