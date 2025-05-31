package timing;

public class Timer implements ITimer {
    private long resumeTime;
    private long totalElapsed;
    private boolean isRunning;

    @Override
    public void start() {
        totalElapsed = 0;
        resumeTime = System.nanoTime();
        isRunning = true;
    }

    @Override
    public long stop() {
        if (isRunning) {
            totalElapsed += System.nanoTime() - resumeTime;
            isRunning = false;
        }
        return totalElapsed;
    }

    @Override
    public void resume() {
        if (!isRunning) {
            resumeTime = System.nanoTime();
            isRunning = true;
        }
    }

    @Override
    public long pause() {
        if (isRunning) {
            long elapsedSinceResume = System.nanoTime() - resumeTime;
            totalElapsed += elapsedSinceResume;
            isRunning = false;
            return elapsedSinceResume;
        }
        return 0;
    }
}
