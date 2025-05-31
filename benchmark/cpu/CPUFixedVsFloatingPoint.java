package benchmark.cpu;

import benchmark.IBenchmark;

public class CPUFixedVsFloatingPoint implements IBenchmark {


    private long result;
    private int size;

    @Override
    public void init(Object... params) {
        this.size = (Integer) params[0];
    }

    @Override
    public void warmup() {
        for (int i = 0; i < size; i++) {
            int fixed = i >> 8;          
            float fp  = (float) i / 256f; 
        }
    }

    @Override
    @Deprecated
    public void run() {

        run(NumberRepresentation.FIXED);
    }

    @Override
    public void run(Object... options) {
        result = 0L;
        NumberRepresentation rep = (NumberRepresentation) options[0];
        switch (rep) {
            case FIXED:
                for (int i = 0; i < size; i++) {
                    result += (i >> 8);
                }
                break;

            case FLOATING:
                double sum = 0.0;
                for (int i = 0; i < size; i++) {
                    sum += (double) i / 256.0;
                }
                result = (long) sum; 
                break;

            default:
                throw new IllegalArgumentException("Unknown representation: " + rep);
        }
    }

    @Override
    public void cancel() { }

    @Override
    public void clean() { }


    public String getResult() {
        return Long.toString(result);
    }
}
