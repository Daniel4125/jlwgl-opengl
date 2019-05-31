package engine;

import game.Renderer;

public class GameEngine implements Runnable {

    public static final int TARGET_FPS = 75;
    public static final int TARGET_UPS = 30;
    private final Window window;
    private final Thread gameLoopThread;
    private final Timer timer;
    private final IGameLogic gameLogic;

    public GameEngine(String windowTitle, int width, int height,
                     boolean vsSync, IGameLogic gameLogic) throws Exception {
        this.gameLoopThread =  new Thread(this, "GAME_LOOP_THREAD");
        this.window = new Window(windowTitle, width, height, vsSync);
        this.gameLogic = gameLogic;
        this.timer = new Timer();
    }

    public void start() {
        String osName = System.getProperty("os.name");
        if ( osName.contains("Mac") ) {
            this.gameLoopThread.run();
        } else {
            this.gameLoopThread.start();
        }
    }

    @Override
    public void run() {
        try {
            init();
            gameLoop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    protected void init() throws Exception {
        this.window.init();
        this.timer.init();
        this.gameLogic.init();
    }

    protected void gameLoop() {
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f / TARGET_UPS;

        boolean running = true;
        while (running && !window.windowShouldClose()) {
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;

            input();

            while (accumulator >= interval) {
                update(interval);
                accumulator -= interval;
            }

            render();

            if (!window.isvSync()) {
                sync();
            }
        }
    }

    private void sync() {
        float loopSlot = 1f / TARGET_FPS;
        double endTime = this.timer.getLastLoopTime() + loopSlot;
        while (this.timer.getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ie) {
                System.out.println(ie);
            }
        }
    }

    protected void input() {
        this.gameLogic.input(this.window);
    }

    protected void update(float interval) {
        this.gameLogic.update(interval);
    }

    protected void render() {
        this.gameLogic.render(this.window);
        this.window.update();
    }

    protected void cleanup() {
        this.gameLogic.cleanup();
    }


}
