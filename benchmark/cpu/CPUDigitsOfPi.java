package benchmark.cpu;

import benchmark.IBenchmark;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class CPUDigitsOfPi implements IBenchmark {
    private int digits;
    private BigDecimal piResult;
    private boolean cancelled;

    @Override
    public void init(Object... params) {
        if (params.length != 1 || !(params[0] instanceof Integer)) {
            throw new IllegalArgumentException("Expected one Integer parameter for number of digits.");
        }
        cancelled = false;
        digits = (Integer) params[0];
    }

    @Override
    public void run() {
        run(1); // default to Chudnovsky
    }

    @Override
    public void run(Object... options) {
        int algorithm = (options != null && options.length > 0 && options[0] instanceof Integer)
                ? (Integer) options[0]
                : 1;

        switch (algorithm) {
            case 0 -> piResult = computePiGaussLegendre(digits);
            case 1 -> piResult = computePiChudnovsky(digits);
            default -> throw new IllegalArgumentException("Unsupported algorithm index: " + algorithm);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("pi_output.txt"))) {
            writer.write("Pi (first " + digits + " digits):\n");
            writer.write(piResult.toString());
        } catch (IOException e) {
            throw new RuntimeException("Error writing Pi output to file", e);
        }
    }

    @Override
    public void clean() {
        piResult = null;
    }

    @Override
    public void cancel() {
        cancelled = true;
    }

    @Override
    public void warmup() {
        int warmupDigits = Math.min(500, digits);
        for (int i = 0; i < 3; i++) {
            computePiChudnovsky(warmupDigits);
        }
    }


    private BigDecimal computePiGaussLegendre(int digits) {
        int guard = 20;
        MathContext mc = new MathContext(digits + guard, RoundingMode.HALF_UP);
        BigDecimal one = BigDecimal.ONE;
        BigDecimal two = new BigDecimal("2");
        BigDecimal four = new BigDecimal("4");

        BigDecimal a = one;
        BigDecimal b = one.divide(sqrt(new BigDecimal("2"), mc), mc);
        BigDecimal t = new BigDecimal("0.25");
        BigDecimal p = one;

        BigDecimal piOld = BigDecimal.ZERO;
        BigDecimal pi = BigDecimal.ZERO;

        int maxIter = (int) Math.ceil(Math.log(digits) / Math.log(2)) + 5;

        for (int i = 0; i < maxIter && !cancelled; i++) {
            BigDecimal aNext = a.add(b).divide(two, mc);
            BigDecimal bNext = sqrt(a.multiply(b, mc), mc);
            BigDecimal aDiff = a.subtract(aNext, mc);
            t = t.subtract(p.multiply(aDiff.multiply(aDiff, mc), mc), mc);
            a = aNext;
            b = bNext;
            p = p.multiply(two);

            pi = a.add(b).pow(2, mc).divide(four.multiply(t, mc), mc);
            if (pi.subtract(piOld, mc).abs().compareTo(BigDecimal.ONE.scaleByPowerOfTen(-digits)) < 0) {
                break;
            }
            piOld = pi;
        }

        return pi.setScale(digits, RoundingMode.HALF_UP);
    }





    private BigDecimal computePiChudnovsky(int digits) {
        MathContext mc = new MathContext(digits + 5, RoundingMode.HALF_UP);
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal C = new BigDecimal("426880").multiply(sqrt(new BigDecimal("10005"), mc));
        BigDecimal K, M = BigDecimal.ONE, X = BigDecimal.ONE;
        BigDecimal L = new BigDecimal("13591409");

        for (int k = 0; k < digits / 14 + 1 && !cancelled; k++) {
            if (k > 0) {
                BigDecimal kBD = BigDecimal.valueOf(k);
                M = M.multiply(kBD.pow(3).subtract(BigDecimal.valueOf(16).multiply(kBD)));
                L = L.add(BigDecimal.valueOf(545140134));
                X = X.multiply(BigDecimal.valueOf(-262537412640768000L));
            }

            BigDecimal term = M.multiply(L).divide(X, mc);
            sum = sum.add(term);
        }

        return C.divide(sum, mc).setScale(digits, RoundingMode.HALF_UP);
    }

    private BigDecimal sqrt(BigDecimal x, MathContext mc) {
        BigDecimal guess = x.divide(BigDecimal.valueOf(2), mc);
        BigDecimal two = new BigDecimal("2");

        int maxIter = mc.getPrecision() + 5;
        for (int i = 0; i < maxIter; i++) {
            guess = guess.add(x.divide(guess, mc)).divide(two, mc);
        }

        return guess;
    }

}
