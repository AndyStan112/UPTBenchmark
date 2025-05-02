package benchmark;

import java.util.Random;

public class QuickSortBenchmark implements IBenchmark {
    private int[] data;
    private boolean cancelled;

    @Override
    public void init(Object... params) {
        if (params.length != 1 || !(params[0] instanceof Integer)) {
            throw new IllegalArgumentException("Expected one Integer parameter for array size.");
        }
        int size = (Integer) params[0];
        data = new int[size];
        Random rand = new Random();

        for (int i = 0; i < size; i++) {
            data[i] = rand.nextInt();
        }

        cancelled = false;
    }

    @Override
    public void run() {
        run((Object[]) null);
    }

    @Override
    public void run(Object... params) {
        quickSort(data, 0, data.length - 1);
    }

    private void quickSort(int[] arr, int low, int high) {
        if (cancelled || low >= high) return;

        int pivot = arr[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (cancelled) return;
            if (arr[j] < pivot) {
                i++;
                swap(arr, i, j);
            }
        }

        swap(arr, i + 1, high);

        quickSort(arr, low, i);
        quickSort(arr, i + 2, high);
    }

    private void swap(int[] arr, int i, int j) {
        if (i == j) return;
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    @Override
    public void clean() {
        data = null;
    }

    @Override
    public void cancel() {
        cancelled = true;
    }
}
