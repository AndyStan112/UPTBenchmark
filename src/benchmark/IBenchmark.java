package benchmark;

interface IBenchmark{
    void run() ;
    void run(Object...params);
    void init(Object...params);
    void clean();
    void cancel();
}