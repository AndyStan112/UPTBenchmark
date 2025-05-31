package benchmark.cpu;

import benchmark.IBenchmark;

public class CPUFixedPoint implements IBenchmark {
    private int size;

    @Override
    public void init(Object... params) {
        this.size = (Integer) params[0];
    }

    @Override
    public void warmup() {

        integerArithmeticTest(1000);
        branchingTest(1000);
        arrayAccessAndAssignmentsTest(1000);
    }

    @Override
    @Deprecated
    public void run() {

        run((Object[]) null);
    }

    @Override
    public void run(Object... options) {
        integerArithmeticTest(size);
        branchingTest(size);
        arrayAccessAndAssignmentsTest(size);
    }

    @Override
    public void cancel() { }

    @Override
    public void clean() {}


    public String getResult() {
        return "Fixed-point tests complete";
    }


    private void integerArithmeticTest(int n) {
        int[] num = {0, 1, 2, 3};
        int[] res = new int[n];
        int j = 0, k = 1, l = 2;

        for (int i = 0; i < n; i++) {
            j = num[1] * (k - j) * (l - k);
            k = num[3] * k - (l - j) * k;
            l = (l - k) * (num[2] + j);
            
            
            


        int i1 = ((l - 2) % n + n) % n;
        res[i1] = j + k + l;


        int i2 = ((k - 2) % n + n) % n;
        res[i2] = j * k * l;
        }
    }


    private void branchingTest(int n) {
        int[] num = {0, 1, 2, 3};
        int j = 1;

        for (int i = 0; i < n; i++) {
            if (j == 1) {
                j = num[2];
            } else {
                j = num[3];
            }
            if (j > 2) {
                j = num[0];
            } else {
                j = num[1];
            }
            if (j < 1) {
                j = num[1];
            } else {
                j = num[0];
            }
        }
    }


    private void arrayAccessAndAssignmentsTest(int n) {
        int[] a = new int[n];
        int[] b = new int[n];
        int[] c = new int[n];


        for (int i = 0; i < n; i++) {
            a[i] = i;
            b[i] = n - 1 - i;
        }
  
        for (int i = 0; i < n; i++) {
            c[i] = a[b[i]];
        }
    }
}
