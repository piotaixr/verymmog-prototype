package com.verymmog.nioengine;

/**
 * Base implementation of the loginc for an engine having a lifecycle (boot/run/shutdown) using a Thread for the main loop
 */
public abstract class ThreadedEngine implements EngineInterface {

    /**
     * The thread executing  the main loop of the engine
     */
    private Thread thread;
    /**
     * Is the engine currently running?
     */
    private boolean running = false;
    /**
     * True if the engine is marked for termination, False otherwise.
     */
    private boolean terminated = false;

    @Override
    public void start() {
        if (thread == null) {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    execute();
                }
            }, "NioEngine");

            thread.start();
        }
    }

    /**
     * Bootstap for the engine
     */
    protected void boot() {
        running = true;

        System.out.println("NioEngine Started");
    }

    /**
     * Cleaning code for the rngine.
     */
    protected void shutdown() {
        running = false;
        thread = null;
    }

    @Override
    public void terminate() {
        terminated = true;
    }

    protected boolean isTerminated() {
        return terminated;
    }

    @Override
    public Thread getThread() {
        return thread;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    /**
     * Boots the engine, launch the mainloop and then, when terminated, shutdown it.
     */
    protected void execute() {
        boot();

        while (!isTerminated()) {
            mainLoop();
        }

        shutdown();
    }

    /**
     * The main loop which will be executed while the engine is not terminated.
     */
    protected abstract void mainLoop();
}
