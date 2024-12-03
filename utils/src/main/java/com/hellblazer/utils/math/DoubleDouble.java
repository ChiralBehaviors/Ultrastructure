package com.hellblazer.utils.math;

import java.io.Serializable;

/**
 * Immutable, extended-precision floating-point numbers which maintain 106 bits
 * (approximately 30 decimal digits) of precision.
 * <p>
 * A DoubleDouble uses a representation containing two double-precision values.
 * A number x is represented as a pair of doubles, x.hi and x.lo, such that the
 * number represented by x is x.hi + x.lo, where
 * 
 * <pre>
 *    |x.lo| <= 0.5*ulp(x.hi)
 * </pre>
 * 
 * and ulp(y) means "unit in the last place of y". The basic arithmetic
 * operations are implemented using convenient properties of IEEE-754
 * floating-point arithmetic.
 * <p>
 * The range of values which can be represented is the same as in IEEE-754. The
 * precision of the representable numbers is twice as great as IEEE-754 double
 * precision.
 * <p>
 * The correctness of the arithmetic algorithms relies on operations being
 * performed with standard IEEE-754 double precision and rounding. This is the
 * Java standard arithmetic model, but for performance reasons Java
 * implementations are not constrained to using this standard by default. Some
 * processors (notably the Intel Pentium architecure) perform floating point
 * operations in (non-IEEE-754-standard) extended-precision. A JVM
 * implementation may choose to use the non-standard extended-precision as its
 * default arithmetic mode. To prevent this from happening, this code uses the
 * Java <tt>strictfp</tt> modifier, which forces all operations to take place in
 * the standard IEEE-754 rounding model.
 * <p>
 * The API provides a value-oriented interface. DoubleDouble values are
 * immutable; operations on them return new objects carrying the result of the
 * operation. This provides a much simpler semantics for writing DoubleDouble
 * expressions, and Java memory management is efficient enough that this imposes
 * very little performance penalty.
 * <p>
 * This implementation uses algorithms originally designed variously by Knuth,
 * Kahan, Dekker, and Linnainmaa. Douglas Priest developed the first C
 * implementation of these techniques. Other more recent C++ implementation are
 * due to Keith M. Briggs and David Bailey et al.
 * 
 * <h3>References</h3>
 * <ul>
 * <li>Priest, D., <i>Algorithms for Arbitrary Precision Floating Point
 * Arithmetic</i>, in P. Kornerup and D. Matula, Eds., Proc. 10th Symposium on
 * Computer Arithmetic, IEEE Computer Society Press, Los Alamitos, Calif., 1991.
 * <li>Yozo Hida, Xiaoye S. Li and David H. Bailey, <i>Quad-Double Arithmetic:
 * Algorithms, Implementation, and Application</i>, manuscript, Oct 2000;
 * Lawrence Berkeley National Laboratory Report BNL-46996.
 * <li>David Bailey, <i>High Precision Software Directory</i>;
 * <tt>http://crd.lbl.gov/~dhbailey/mpdist/index.html</tt>
 * </ul>
 * 
 * 
 * @author Martin Davis
 * 
 */
public strictfp class DoubleDouble implements Serializable,
        Comparable<DoubleDouble>, Cloneable {
    /**
	 * 
	 */
    private static final long         serialVersionUID      = 1L;

    /**
     * The value nearest to the constant Pi.
     */
    public static final DoubleDouble  PI                    = new DoubleDouble(
                                                                               3.141592653589793116e+00,
                                                                               1.224646799147353207e-16);

    /**
     * The value nearest to the constant 2 * Pi.
     */
    public static final DoubleDouble  TWO_PI                = new DoubleDouble(
                                                                               6.283185307179586232e+00,
                                                                               2.449293598294706414e-16);

    /**
     * The value nearest to the constant Pi / 2.
     */
    public static final DoubleDouble  PI_2                  = new DoubleDouble(
                                                                               1.570796326794896558e+00,
                                                                               6.123233995736766036e-17);

    /**
     * The value nearest to the constant e (the natural logarithm base).
     */
    public static final DoubleDouble  E                     = new DoubleDouble(
                                                                               2.718281828459045091e+00,
                                                                               1.445646891729250158e-16);

    /**
     * A value representing the result of an operation which does not return a
     * valid number.
     */
    public static final DoubleDouble  NaN                   = new DoubleDouble(
                                                                               Double.NaN,
                                                                               Double.NaN);

    /**
     * The smallest representable relative difference between two {link @
     * DoubleDouble} values
     */
    public static final double        EPS                   = 1.23259516440783e-32;                      /* = 2^-106 */

    /**
     * The value to split a double-precision value on during multiplication
     */
    private static final double       SPLIT                 = 134217729.0D;                              // 2^27+1, for IEEE double

    private static final int          MAX_PRINT_DIGITS      = 32;

    private static final DoubleDouble TEN                   = new DoubleDouble(
                                                                               10.0);

    private static final DoubleDouble ONE                   = new DoubleDouble(
                                                                               1.0);

    private static final String       SCI_NOT_EXPONENT_CHAR = "E";

    private static final String       SCI_NOT_ZERO          = "0.0E0";

    /**
     * Converts a string representation of a real number into a DoubleDouble
     * value. The format accepted is similar to the standard Java real number
     * syntax. It is defined by the following regular expression:
     * 
     * <pre>
     * [<tt>+</tt>|<tt>-</tt>] {<i>digit</i>} [ <tt>.</tt> {<i>digit</i>} ] [ ( <tt>e</tt> | <tt>E</tt> ) [<tt>+</tt>|<tt>-</tt>
     * ] {<i>digit</i>}+
     * 
     * <pre>
     * 
     * @param str the string to parse
     * @return the value of the parsed number
     * @throws NumberFormatException if <tt>str</tt>
     * is not a valid representation of a number
     */
    public static DoubleDouble parse(String str) throws NumberFormatException {
        int i = 0;
        int strlen = str.length();

        // skip leading whitespace
        while (Character.isWhitespace(str.charAt(i))) {
            i++;
        }

        // check for sign
        boolean isNegative = false;
        if (i < strlen) {
            char signCh = str.charAt(i);
            if (signCh == '-' || signCh == '+') {
                i++;
                if (signCh == '-') {
                    isNegative = true;
                }
            }
        }

        // scan all digits and accumulate into an integral value
        // Keep track of the location of the decimal point (if any) to allow
        // scaling later
        DoubleDouble val = new DoubleDouble();

        int numDigits = 0;
        int numBeforeDec = 0;
        int exp = 0;
        while (true) {
            if (i >= strlen) {
                break;
            }
            char ch = str.charAt(i);
            i++;
            if (Character.isDigit(ch)) {
                double d = ch - '0';
                val.selfMultiply(TEN);
                // MD: need to optimize this
                val.selfAdd(new DoubleDouble(d));
                numDigits++;
                continue;
            }
            if (ch == '.') {
                numBeforeDec = numDigits;
                continue;
            }
            if (ch == 'e' || ch == 'E') {
                String expStr = str.substring(i);
                // this should catch any format problems with the exponent
                try {
                    exp = Integer.parseInt(expStr);
                } catch (NumberFormatException ex) {
                    throw new NumberFormatException("Invalid exponent "
                                                    + expStr + " in string "
                                                    + str);
                }
                break;
            }
            throw new NumberFormatException("Unexpected character '" + ch
                                            + "' at position " + i
                                            + " in string " + str);
        }
        DoubleDouble val2 = val;

        // scale the number correctly
        int numDecPlaces = numDigits - numBeforeDec - exp;
        if (numDecPlaces == 0) {
            val2 = val;
        } else if (numDecPlaces > 0) {
            DoubleDouble scale = TEN.pow(numDecPlaces);
            val2 = val.divide(scale);
        } else if (numDecPlaces < 0) {
            DoubleDouble scale = TEN.pow(-numDecPlaces);
            val2 = val.multiply(scale);
        }
        // apply leading sign, if any
        if (isNegative) {
            return val2.negate();
        }
        return val2;

    }

    /**
     * Converts the <tt>double</tt> argument to a DoubleDouble number.
     * 
     * @param x
     *            a numeric value
     * @return the extended precision version of the value
     */
    public static DoubleDouble valueOf(double x) {
        return new DoubleDouble(x);
    }

    /**
     * Converts the string argument to a DoubleDouble number.
     * 
     * @param str
     *            a string containing a representation of a numeric value
     * @return the extended precision version of the value
     * @throws NumberFormatException
     *             if <tt>s</tt> is not a valid representation of a number
     */
    public static DoubleDouble valueOf(String str) throws NumberFormatException {
        return parse(str);
    }

    /**
     * Determines the decimal magnitude of a number. The magnitude is the
     * exponent of the greatest power of 10 which is less than or equal to the
     * number.
     * 
     * @param x
     *            the number to find the magnitude of
     * @return the decimal magnitude of x
     */
    private static int magnitude(double x) {
        double xAbs = Math.abs(x);
        double xLog10 = Math.log(xAbs) / Math.log(10);
        int xMag = (int) Math.floor(xLog10);
        /**
         * Since log computation is inexact, there may be an off-by-one error in
         * the computed magnitude. Following tests that magnitude is correct,
         * and adjusts it if not
         */
        double xApprox = Math.pow(10, xMag);
        if (xApprox * 10 <= xAbs) {
            xMag += 1;
        }

        return xMag;
    }

    /**
     * Creates a string of a given length containing the given character
     * 
     * @param ch
     *            the character to be repeated
     * @param len
     *            the len of the desired string
     * @return the string
     */
    private static String stringOfChar(char ch, int len) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < len; i++) {
            buf.append(ch);
        }
        return buf.toString();
    }

    /**
     * The high-order component of the double-double precision value.
     */
    private double hi = 0.0;

    /**
     * The low-order component of the double-double precision value.
     */
    private double lo = 0.0;

    /**
     * Creates a new DoubleDouble with value 0.0.
     */
    public DoubleDouble() {
        init(0.0);
    }

    /*
     * double getHighComponent() { return hi; }
     * 
     * double getLowComponent() { return lo; }
     */

    // Testing only - should not be public
    /*
     * public void RENORM() { double s = hi + lo; double err = lo - (s - hi); hi
     * = s; lo = err; }
     */

    /**
     * Creates a new DoubleDouble with value x.
     * 
     * @param x
     *            the value to initialize
     */
    public DoubleDouble(double x) {
        init(x);
    }

    /**
     * Creates a new DoubleDouble with value (hi, lo).
     * 
     * @param hi
     *            the high-order component
     * @param lo
     *            the high-order component
     */
    public DoubleDouble(double hi, double lo) {
        init(hi, lo);
    }

    /*
     * // experimental private DoubleDouble selfAdd(double yhi, double ylo) {
     * double H, h, T, t, S, s, e, f; S = hi + yhi; T = lo + ylo; e = S - hi; f
     * = T - lo; s = S-e; t = T-f; s = (yhi-e)+(hi-s); t = (ylo-f)+(lo-t); e =
     * s+T; H = S+e; h = e+(S-H); e = t+h;
     * 
     * double zhi = H + e; double zlo = e + (H - zhi); hi = zhi; lo = zlo;
     * 
     * return this; }
     */

    /**
     * Creates a new DoubleDouble with value equal to the argument.
     * 
     * @param dd
     *            the value to initialize
     */
    public DoubleDouble(DoubleDouble dd) {
        init(dd);
    }

    /*
     * public DoubleDouble selfSubtract(DoubleDouble y) { if (isNaN()) return
     * this; return selfAdd(-y.hi, -y.lo); }
     */

    /**
     * Creates a new DoubleDouble with value equal to the argument.
     * 
     * @param str
     *            the value to initialize by
     * @throws NumberFormatException
     *             if <tt>str</tt> is not a valid representation of a number
     */
    public DoubleDouble(String str) throws NumberFormatException {
        this(parse(str));
    }

    /**
     * Returns the absolute value of this value. Special cases:
     * <ul>
     * <li>If this value is NaN, it is returned.
     * </ul>
     * 
     * @return the absolute value of this value
     */
    public DoubleDouble abs() {
        if (isNaN()) {
            return NaN;
        }
        if (isNegative()) {
            return negate();
        }
        return new DoubleDouble(this);
    }

    /**
     * Returns a DoubleDouble whose value is <tt>(this + y)</tt>.
     * 
     * @param y
     *            the addend
     * @return <tt>(this + y)</tt>
     */
    public DoubleDouble add(DoubleDouble y) {
        if (isNaN()) {
            return this;
        }
        return new DoubleDouble(this).selfAdd(y);
    }

    /**
     * Returns the smallest (closest to negative infinity) value that is not
     * less than the argument and is equal to a mathematical integer. Special
     * cases:
     * <ul>
     * <li>If this value is NaN, returns NaN.
     * </ul>
     * 
     * @return the smallest (closest to negative infinity) value that is not
     *         less than the argument and is equal to a mathematical integer.
     */
    public DoubleDouble ceil() {
        if (isNaN()) {
            return NaN;
        }
        double fhi = Math.ceil(hi);
        double flo = 0.0;
        // Hi is already integral. Ceil the low word
        if (fhi == hi) {
            flo = Math.ceil(lo);
            // do we need to renormalize here?
        }
        return new DoubleDouble(fhi, flo);
    }

    /*
     * 
     * // experimental public DoubleDouble selfDivide(DoubleDouble y) { double
     * hc, tc, hy, ty, C, c, U, u; C = hi/y.hi; c = SPLIT*C; hc =c-C; u =
     * SPLIT*y.hi; hc = c-hc; tc = C-hc; hy = u-y.hi; U = C * y.hi; hy = u-hy;
     * ty = y.hi-hy; u = (((hc*hy-U)+hc*ty)+tc*hy)+tc*ty; c =
     * ((((hi-U)-u)+lo)-C*y.lo)/y.hi; u = C+c;
     * 
     * hi = u; lo = (C-u)+c; return this; }
     */

    /**
     * Creates and returns a copy of this value.
     * 
     * @return a copy of this value
     */
    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            // should never reach here
            return null;
        }
    }

    /**
     * Compares two DoubleDouble objects numerically.
     * 
     * @return -1,0 or 1 depending on whether this value is less than, equal to
     *         or greater than the value of <tt>o</tt>
     */
    @Override
    public int compareTo(DoubleDouble other) {
        if (hi < other.hi) {
            return -1;
        }
        if (hi > other.hi) {
            return 1;
        }
        if (lo < other.lo) {
            return -1;
        }
        if (lo > other.lo) {
            return 1;
        }
        return 0;
    }

    /**
     * Returns a DoubleDouble whose value is <tt>(this / y)</tt>.
     * 
     * @param y
     *            the divisor
     * @return <tt>(this / y)</tt>
     */
    public DoubleDouble divide(DoubleDouble y) {
        double hc, tc, hy, ty, C, c, U, u;
        C = hi / y.hi;
        c = SPLIT * C;
        hc = c - C;
        u = SPLIT * y.hi;
        hc = c - hc;
        tc = C - hc;
        hy = u - y.hi;
        U = C * y.hi;
        hy = u - hy;
        ty = y.hi - hy;
        u = hc * hy - U + hc * ty + tc * hy + tc * ty;
        c = (hi - U - u + lo - C * y.lo) / y.hi;
        u = C + c;

        double zhi = u;
        double zlo = C - u + c;
        return new DoubleDouble(zhi, zlo);
    }

    /**
     * Converts this value to the nearest double-precision number.
     * 
     * @return the nearest double-precision number to this value
     */
    public double doubleValue() {
        return hi + lo;
    }

    /**
     * Dumps the components of this number to a string.
     * 
     * @return a string showing the components of the number
     */
    public String dump() {
        return "DD<" + hi + ", " + lo + ">";
    }

    /**
     * Tests whether this value is equal to another <tt>DoubleDouble</tt> value.
     * 
     * @param y
     *            a DoubleDouble value
     * @return true if this value = y
     */
    public boolean equals(DoubleDouble y) {
        return hi == y.hi && lo == y.lo;
    }

    /**
     * Returns the largest (closest to positive infinity) value that is not
     * greater than the argument and is equal to a mathematical integer. Special
     * cases:
     * <ul>
     * <li>If this value is NaN, returns NaN.
     * </ul>
     * 
     * @return the largest (closest to positive infinity) value that is not
     *         greater than the argument and is equal to a mathematical integer.
     */
    public DoubleDouble floor() {
        if (isNaN()) {
            return NaN;
        }
        double fhi = Math.floor(hi);
        double flo = 0.0;
        // Hi is already integral. Floor the low word
        if (fhi == hi) {
            flo = Math.floor(lo);
        }
        // do we need to renormalize here?
        return new DoubleDouble(fhi, flo);
    }

    /**
     * Tests whether this value is greater than or equals to another
     * <tt>DoubleDouble</tt> value.
     * 
     * @param y
     *            a DoubleDouble value
     * @return true if this value >= y
     */
    public boolean ge(DoubleDouble y) {
        return hi > y.hi || hi == y.hi && lo >= y.lo;
    }

    /**
     * Tests whether this value is greater than another <tt>DoubleDouble</tt>
     * value.
     * 
     * @param y
     *            a DoubleDouble value
     * @return true if this value > y
     */
    public boolean gt(DoubleDouble y) {
        return hi > y.hi || hi == y.hi && lo > y.lo;
    }

    /**
     * Converts this value to the nearest integer.
     * 
     * @return the nearest integer to this value
     */
    public int intValue() {
        return (int) hi;
    }

    /*------------------------------------------------------------
     *   Conversion Functions
     *------------------------------------------------------------
     */

    /**
     * Tests whether this value is NaN.
     * 
     * @return true if this value is NaN
     */
    public boolean isNaN() {
        return Double.isNaN(hi);
    }

    /**
     * Tests whether this value is less than 0.
     * 
     * @return true if this value is less than 0
     */
    public boolean isNegative() {
        return hi < 0.0 || hi == 0.0 && lo < 0.0;
    }

    /*------------------------------------------------------------
     *   Predicates
     *------------------------------------------------------------
     */

    /**
     * Tests whether this value is greater than 0.
     * 
     * @return true if this value is greater than 0
     */
    public boolean isPositive() {
        return hi > 0.0 || hi == 0.0 && lo > 0.0;
    }

    /**
     * Tests whether this value is equal to 0.
     * 
     * @return true if this value is equal to 0
     */
    public boolean isZero() {
        return hi == 0.0 && lo == 0.0;
    }

    /**
     * Tests whether this value is less than or equal to another
     * <tt>DoubleDouble</tt> value.
     * 
     * @param y
     *            a DoubleDouble value
     * @return true if this value <= y
     */
    public boolean le(DoubleDouble y) {
        return hi < y.hi || hi == y.hi && lo <= y.lo;
    }

    /**
     * Tests whether this value is less than another <tt>DoubleDouble</tt>
     * value.
     * 
     * @param y
     *            a DoubleDouble value
     * @return true if this value < y
     */
    public boolean lt(DoubleDouble y) {
        return hi < y.hi || hi == y.hi && lo < y.lo;
    }

    /**
     * Returns a DoubleDouble whose value is <tt>(this * y)</tt>.
     * 
     * @param y
     *            the multiplicand
     * @return <tt>(this * y)</tt>
     */
    public DoubleDouble multiply(DoubleDouble y) {
        if (isNaN()) {
            return this;
        }
        if (y.isNaN()) {
            return y;
        }
        return new DoubleDouble(this).selfMultiply(y);
    }

    /**
     * Returns a DoubleDouble whose value is <tt>-this</tt>.
     * 
     * @return <tt>-this</tt>
     */
    public DoubleDouble negate() {
        if (isNaN()) {
            return this;
        }
        return new DoubleDouble(-hi, -lo);
    }

    /**
     * Computes the value of this number raised to an integral power. Follows
     * semantics of Java Math.pow as closely as possible.
     * 
     * @param exp
     *            the integer exponent
     * @return x raised to the integral power exp
     */
    public DoubleDouble pow(int exp) {
        if (exp == 0.0) {
            return valueOf(1.0);
        }

        DoubleDouble r = new DoubleDouble(this);
        DoubleDouble s = valueOf(1.0);
        int n = Math.abs(exp);

        if (n > 1) {
            /* Use binary exponentiation */
            while (n > 0) {
                if (n % 2 == 1) {
                    s.selfMultiply(r);
                }
                n /= 2;
                if (n > 0) {
                    r = r.sqr();
                }
            }
        } else {
            s = r;
        }

        /* Compute the reciprocal if n is negative. */
        if (exp < 0) {
            return s.reciprocal();
        }
        return s;
    }

    /**
     * Returns a DoubleDouble whose value is <tt>1 / this</tt>.
     * 
     * @return the reciprocal of this value
     */
    public DoubleDouble reciprocal() {
        double hc, tc, hy, ty, C, c, U, u;
        C = 1.0 / hi;
        c = SPLIT * C;
        hc = c - C;
        u = SPLIT * hi;
        hc = c - hc;
        tc = C - hc;
        hy = u - hi;
        U = C * hi;
        hy = u - hy;
        ty = hi - hy;
        u = hc * hy - U + hc * ty + tc * hy + tc * ty;
        c = (1.0 - U - u - C * lo) / hi;

        double zhi = C + c;
        double zlo = C - zhi + c;
        return new DoubleDouble(zhi, zlo);
    }

    /**
     * Rounds this value to the nearest integer. The value is rounded to an
     * integer by adding 1/2 and taking the floor of the result. Special cases:
     * <ul>
     * <li>If this value is NaN, returns NaN.
     * </ul>
     * 
     * @return this value rounded to the nearest integer
     */
    public DoubleDouble rint() {
        if (isNaN()) {
            return this;
        }
        // may not be 100% correct
        DoubleDouble plus5 = add(new DoubleDouble(0.5));
        return plus5.floor();
    }

    /**
     * Returns an integer indicating the sign of this value.
     * <ul>
     * <li>if this value is > 0, returns 1
     * <li>if this value is < 0, returns -1
     * <li>if this value is = 0, returns 0
     * <li>if this value is NaN, returns 0
     * </ul>
     * 
     * @return an integer indicating the sign of this value
     */
    public int signum() {
        if (isPositive()) {
            return 1;
        }
        if (isNegative()) {
            return -1;
        }
        return 0;
    }

    /*------------------------------------------------------------
     *   Output
     *------------------------------------------------------------
     */

    /**
     * Computes the square of this value.
     * 
     * @return the square of this value.
     */
    public DoubleDouble sqr() {
        return multiply(this);
    }

    /**
     * Computes the positive square root of this value. If the number is NaN or
     * negative, NaN is returned.
     * 
     * @return the positive square root of this number. If the argument is NaN
     *         or less than zero, the result is NaN.
     */
    public DoubleDouble sqrt() {
        /*
         * Strategy: Use Karp's trick: if x is an approximation to sqrt(a), then
         * 
         * sqrt(a) = a*x + [a - (a*x)^2] * x / 2 (approx)
         * 
         * The approximation is accurate to twice the accuracy of x. Also, the
         * multiplication (a*x) and [-]*x can be done with only half the
         * precision.
         */

        if (isZero()) {
            return new DoubleDouble(0.0);
        }

        if (isNegative()) {
            return NaN;
        }

        double x = 1.0 / Math.sqrt(hi);
        double ax = hi * x;

        DoubleDouble axdd = new DoubleDouble(ax);
        DoubleDouble diffSq = subtract(axdd.sqr());
        double d2 = diffSq.hi * (x * 0.5);

        return axdd.add(new DoubleDouble(d2));
    }

    /**
     * Returns a DoubleDouble whose value is <tt>(this - y)</tt>.
     * 
     * @param y
     *            the subtrahend
     * @return <tt>(this - y)</tt>
     */
    public DoubleDouble subtract(DoubleDouble y) {
        if (isNaN()) {
            return this;
        }
        return add(y.negate());
    }

    /**
     * Returns the string representation of this value in scientific notation.
     * 
     * @return the string representation in scientific notation
     */
    public String toSciNotation() {
        // special case zero, to allow as
        if (isZero()) {
            return SCI_NOT_ZERO;
        }

        String specialStr = getSpecialNumberString();
        if (specialStr != null) {
            return specialStr;
        }

        int[] magnitude = new int[1];
        String digits = extractSignificantDigits(false, magnitude);
        String expStr = SCI_NOT_EXPONENT_CHAR + magnitude[0];

        // should never have leading zeroes
        // MD - is this correct? Or should we simply strip them if they are
        // present?
        if (digits.charAt(0) == '0') {
            throw new IllegalStateException("Found leading zero: " + digits);
        }

        // add decimal point
        String trailingDigits = "";
        if (digits.length() > 1) {
            trailingDigits = digits.substring(1);
        }
        String digitsWithDecimal = digits.charAt(0) + "." + trailingDigits;

        if (isNegative()) {
            return "-" + digitsWithDecimal + expStr;
        }
        return digitsWithDecimal + expStr;
    }

    /**
     * Returns the string representation of this value in standard notation.
     * 
     * @return the string representation in standard notation
     */
    public String toStandardNotation() {
        String specialStr = getSpecialNumberString();
        if (specialStr != null) {
            return specialStr;
        }

        int[] magnitude = new int[1];
        String sigDigits = extractSignificantDigits(true, magnitude);
        int decimalPointPos = magnitude[0] + 1;

        String num = sigDigits;
        // add a leading 0 if the decimal point is the first char
        if (sigDigits.charAt(0) == '.') {
            num = "0" + sigDigits;
        } else if (decimalPointPos < 0) {
            num = "0." + stringOfChar('0', -decimalPointPos) + sigDigits;
        } else if (sigDigits.indexOf('.') == -1) {
            // no point inserted - sig digits must be smaller than magnitude of
            // number
            // add zeroes to end to make number the correct size
            int numZeroes = decimalPointPos - sigDigits.length();
            String zeroes = stringOfChar('0', numZeroes);
            num = sigDigits + zeroes + ".0";
        }

        if (isNegative()) {
            return "-" + num;
        }
        return num;
    }

    /**
     * Returns a string representation of this number, in either standard or
     * scientific notation. If the magnitude of the number is in the range [
     * 10<sup>-3</sup>, 10<sup>8</sup> ] standard notation will be used.
     * Otherwise, scientific notation will be used.
     * 
     * @return a string representation of this number
     */
    @Override
    public String toString() {
        int mag = magnitude(hi);
        if (mag >= -3 && mag <= 20) {
            return toStandardNotation();
        }
        return toSciNotation();
    }

    /**
     * Returns the integer which is largest in absolute value and not further
     * from zero than this value. Special cases:
     * <ul>
     * <li>If this value is NaN, returns NaN.
     * </ul>
     * 
     * @return the integer which is largest in absolute value and not further
     *         from zero than this value
     */
    public DoubleDouble trunc() {
        if (isNaN()) {
            return NaN;
        }
        if (isPositive()) {
            return floor();
        } else {
            return ceil();
        }
    }

    /**
     * Extracts the significant digits in the decimal representation of the
     * argument. A decimal point may be optionally inserted in the string of
     * digits (as long as its position lies within the extracted digits - if
     * not, the caller must prepend or append the appropriate zeroes and decimal
     * point).
     * 
     * @param y
     *            the number to extract ( >= 0)
     * @param decimalPointPos
     *            the position in which to insert a decimal point
     * @return the string containing the significant digits and possibly a
     *         decimal point
     */
    private String extractSignificantDigits(boolean insertDecimalPoint,
                                            int[] magnitude) {
        DoubleDouble y = abs();
        // compute *correct* magnitude of y
        int mag = magnitude(y.hi);
        DoubleDouble scale = TEN.pow(mag);
        y = y.divide(scale);

        // fix magnitude if off by one
        if (y.gt(TEN)) {
            y = y.divide(TEN);
            mag += 1;
        } else if (y.lt(ONE)) {
            y = y.multiply(TEN);
            mag -= 1;
        }

        int decimalPointPos = mag + 1;
        StringBuffer buf = new StringBuffer();
        int numDigits = MAX_PRINT_DIGITS - 1;
        for (int i = 0; i <= numDigits; i++) {
            if (insertDecimalPoint && i == decimalPointPos) {
                buf.append('.');
            }
            int digit = (int) y.hi;
            // System.out.println("printDump: [" + i + "] digit: " + digit +
            // "  y: " + y.dump() + "  buf: " + buf);

            /**
             * This should never happen, due to heuristic checks on remainder
             * below
             */
            if (digit < 0 || digit > 9) {
                // System.out.println("digit > 10 : " + digit);
                // throw new
                // IllegalStateException("Internal errror: found digit = " +
                // digit);
            }
            /**
             * If a negative remainder is encountered, simply terminate the
             * extraction. This is robust, but maybe slightly inaccurate. My
             * current hypothesis is that negative remainders only occur for
             * very small lo components, so the inaccuracy is tolerable
             */
            if (digit < 0) {
                break;
                // throw new
                // IllegalStateException("Internal errror: found digit = " +
                // digit);
            }
            boolean rebiasBy10 = false;
            char digitChar = 0;
            if (digit > 9) {
                // set flag to re-bias after next 10-shift
                rebiasBy10 = true;
                // output digit will end up being '9'
                digitChar = '9';
            } else {
                digitChar = (char) ('0' + digit);
            }
            buf.append(digitChar);
            y = y.subtract(DoubleDouble.valueOf(digit)).multiply(TEN);
            if (rebiasBy10) {
                y.selfAdd(TEN);
            }

            boolean continueExtractingDigits = true;
            /**
             * Heuristic check: if the remaining portion of y is non-positive,
             * assume that output is complete
             */
            // if (y.hi <= 0.0)
            // if (y.hi < 0.0)
            // continueExtractingDigits = false;
            /**
             * Check if remaining digits will be 0, and if so don't output them.
             * Do this by comparing the magnitude of the remainder with the
             * expected precision.
             */
            int remMag = magnitude(y.hi);
            if (remMag < 0 && Math.abs(remMag) >= numDigits - i) {
                continueExtractingDigits = false;
            }
            if (!continueExtractingDigits) {
                break;
            }
        }
        magnitude[0] = mag;
        return buf.toString();
    }

    /**
     * Returns the string for this value if it has a known representation. (E.g.
     * NaN or 0.0)
     * 
     * @return the string for this special number
     * @return null if the number is not a special number
     */
    private String getSpecialNumberString() {
        if (isZero()) {
            return "0.0";
        }
        if (isNaN()) {
            return "NaN ";
        }
        return null;
    }

    private void init(double x) {
        init(x, 0.0);
    }

    private void init(double hi, double lo) {
        this.hi = hi;
        this.lo = lo;
    }

    private void init(DoubleDouble dd) {
        init(dd.hi, dd.lo);
    }

    /**
     * Adds the argument to the value of <tt>this</tt>. To prevent altering
     * constants, this method <b>must only</b> be used on values known to be
     * newly created.
     * 
     * @param y
     *            the addend
     * @return <tt>this</tt>, with its value incremented by <tt>y</tt>
     */
    private DoubleDouble selfAdd(DoubleDouble y) {
        double H, h, T, t, S, s, e, f;
        S = hi + y.hi;
        T = lo + y.lo;
        e = S - hi;
        f = T - lo;
        s = S - e;
        t = T - f;
        s = y.hi - e + (hi - s);
        t = y.lo - f + (lo - t);
        e = s + T;
        H = S + e;
        h = e + (S - H);
        e = t + h;

        double zhi = H + e;
        double zlo = e + (H - zhi);
        hi = zhi;
        lo = zlo;

        return this;
    }

    /*------------------------------------------------------------
     *   Input
     *------------------------------------------------------------
     */

    /**
     * Multiplies this by the argument, returning this. To prevent altering
     * constants, this method <b>must only</b> be used on values known to be
     * newly created.
     * 
     * @param y
     *            a DoubleDouble value to multiply by
     * @return this
     */
    private DoubleDouble selfMultiply(DoubleDouble y) {
        double hx, tx, hy, ty, C, c;
        C = SPLIT * hi;
        hx = C - hi;
        c = SPLIT * y.hi;
        hx = C - hx;
        tx = hi - hx;
        hy = c - y.hi;
        C = hi * y.hi;
        hy = c - hy;
        ty = y.hi - hy;
        c = hx * hy - C + hx * ty + tx * hy + tx * ty + (hi * y.lo + lo * y.hi);
        double zhi = C + c;
        hx = C - zhi;
        double zlo = c + hx;
        hi = zhi;
        lo = zlo;
        return this;
    }
}