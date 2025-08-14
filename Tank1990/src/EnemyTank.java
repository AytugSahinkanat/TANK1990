import java.awt.*;
import java.util.Random;

public class EnemyTank extends Tank {
    public enum TankType {
        BASIC(1, 5, 800, 100, new Color(200, 200, 200)),
        FAST(1, 7, 600, 200, new Color(100, 200, 150)),
        POWER(1, 6, 300, 300, new Color(200, 100, 100)),
        ARMOR(4, 6, 500, 400, new Color(255, 255, 200));
        
        public final int hp;
        public final int spd;
        public final int shotCD;
        public final int score;
        public final Color clr;
        
        TankType(int hp, int spd, int shotCD, int score, Color clr) {
            this.hp = hp;
            this.spd = spd;
            this.shotCD = shotCD;
            this.score = score;
            this.clr = clr;
        }
    }
 

public void setGameState(GameState gs) {
    this.gs = gs;
}
    private static final int MAP_W = 624;
    private static final int MAP_H = 624;
    
    private TankType type;
    private int hp;
    private boolean isRed;
    private boolean droppedPU = false;
    private Random rnd = new Random();
    private long lastDirChg = 0;
    private boolean frz = false;
    private long frzEnd = 0;
    private boolean shld = false;
    private long shldEnd = 0;
    private int stars = 0;
    private boolean hasGun = false;
    private transient GameState gs;
    
    private long startTime = System.currentTimeMillis();
    private int startX, startY;
    
    public EnemyTank(int x, int y, TankType type, boolean isRed, GameState gs) {
        super(x, y);
        this.type = type;
        this.hp = type.hp;
        this.spd = type.spd;
        this.shotCD = type.shotCD;
        this.isRed = isRed;
        this.gs = gs;
        
        this.startX = x;
        this.startY = y;
        this.startTime = System.currentTimeMillis();
        
        Direction[] dirs = Direction.values();
        this.dir = dirs[rnd.nextInt(dirs.length)];
    }
    
    @Override
    public void run() {
        while (alive) {
            try {
                long now = System.currentTimeMillis();
                if (frz && now > frzEnd) {
                    frz = false;
                }
                if (shld && now > shldEnd) {
                    shld = false;
                }
                
                if (!frz && gs != null) {
                    moveAI(gs.getMap());
                    
                    if (rnd.nextInt(100) < 3) {
                        Bullet b = shoot();
                        if (b != null) {
                            gs.addBullet(b);
                        }
                    }
                }
                
                Thread.sleep(50);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
    
    private void moveAI(GameMap map) {
        try {
            long now = System.currentTimeMillis();
            
            if (lastDirChg == 0) {
                lastDirChg = now;
                Direction[] dirs = Direction.values();
                dir = dirs[rnd.nextInt(dirs.length)];
            }
            
            boolean shouldChangeDir = false;
            
            if (now - lastDirChg > 2000) {
                shouldChangeDir = true;
            } else if (rnd.nextInt(100) < 10) {
                shouldChangeDir = true;
            }
            
            int oldX = x;
            int oldY = y;
            
            if (map != null && gs != null) {
                int nextX = x + dir.dx * spd;
                int nextY = y + dir.dy * spd;
                
                if (canMoveTo(nextX, nextY, map, gs)) {
                    x = nextX;
                    y = nextY;
                } else {
                    shouldChangeDir = true;
                }
            }
            
            if (shouldChangeDir || (x == oldX && y == oldY)) {
                Direction[] dirs = Direction.values();
                Direction oldDir = dir;
                
                for (int i = 0; i < 4; i++) {
                    dir = dirs[rnd.nextInt(dirs.length)];
                    
                    if (dir != oldDir) {
                        int testX = x + dir.dx * spd;
                        int testY = y + dir.dy * spd;
                        
                        if (canMoveTo(testX, testY, map, gs)) {
                            break;
                        }
                    }
                }
                
                lastDirChg = now;
            }
            
            if (now - startTime < 2000 && x == startX && y == startY) {
                tryEmergencyMove(map, gs);
            }
            
        } catch (Exception e) {
            System.err.println("Tank AI move error: " + e.getMessage());
        }
    }
    
    private void tryEmergencyMove(GameMap map, GameState gs) {
        int[] offsets = {8, 16, 24, 32};
        Direction[] dirs = {Direction.DOWN, Direction.LEFT, Direction.RIGHT, Direction.UP};
        
        for (int offset : offsets) {
            for (Direction d : dirs) {
                int testX = x + d.dx * offset;
                int testY = y + d.dy * offset;
                
                if (canMoveTo(testX, testY, map, gs)) {
                    x = testX;
                    y = testY;
                    dir = d;
                    return;
                }
            }
        }
    }
    
    @Override
    protected Bullet createBullet() {
        if (frz) return null;
        
        int bX = x + w / 2 - 2;
        int bY = y + h / 2 - 2;
        
        switch (dir) {
            case UP: bY = y - 4; break;
            case DOWN: bY = y + h; break;
            case LEFT: bX = x - 4; break;
            case RIGHT: bX = x + w; break;
        }
        
        boolean pwr = stars > 0 || hasGun;
        return new Bullet(bX, bY, dir, false, pwr);
    }
    
    public void hit() {
        if (!shld) {
            hp--;
            if (hp <= 0) {
                alive = false;
                stopThread();
            }
        }
    }
    
    public void freeze(int dur) {
        frz = true;
        frzEnd = System.currentTimeMillis() + dur;
    }
    
    public void shield(int dur) {
        shld = true;
        shldEnd = System.currentTimeMillis() + dur;
    }
    
    public void addStar() {
        stars = Math.min(3, stars + 1);
        if (stars >= 3) {
            hasGun = true;
        }
    }
    
    public void giveGun() {
        hasGun = true;
    }
    
    public boolean drawHidden(Graphics2D g, GameMap map) {
        boolean hidden = isHidden(map);
        
        Color tankClr = isRed ? Color.RED : type.clr;
        
        if (type == TankType.ARMOR && hp < type.hp) {
            int alpha = 255 - (type.hp - hp) * 60;
            tankClr = new Color(tankClr.getRed(), tankClr.getGreen(), tankClr.getBlue(), Math.max(100, alpha));
        }
        
        if (hidden) {
            Composite old = g.getComposite();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            drawTank(g, tankClr);
            g.setComposite(old);
        } else {
            drawTank(g, tankClr);
        }
        
        return hidden;
    }
    
    private void drawTank(Graphics2D g, Color tankClr) {
        if (type == TankType.ARMOR) {
            switch (hp) {
                case 4: tankClr = new Color(255, 255, 200); break;
                case 3: tankClr = new Color(255, 255, 150); break;
                case 2: tankClr = new Color(200, 200, 100); break;
                case 1: tankClr = new Color(150, 150, 50); break;
            }
        }
        
        g.setColor(tankClr);
        g.fillRect(x + 4, y + 4, w - 8, h - 8);
        g.fillRect(x, y + 2, 4, h - 4);
        g.fillRect(x + w - 4, y + 2, 4, h - 4);
        
        g.setColor(Color.BLACK);
        for (int i = 0; i < 4; i++) {
            g.fillRect(x + 1, y + 4 + i * 6, 2, 2);
            g.fillRect(x + w - 3, y + 4 + i * 6, 2, 2);
        }
        
        switch (type) {
            case BASIC:
                g.setColor(tankClr.darker());
                g.fillRect(x + 8, y + 8, w - 16, h - 16);
                break;
            case FAST:
                g.setColor(tankClr.darker());
                g.drawLine(x + 6, y + 10, x + w - 6, y + 10);
                g.drawLine(x + 6, y + h - 10, x + w - 6, y + h - 10);
                break;
            case POWER:
                g.setColor(tankClr.darker());
                g.fillRect(x + 12, y + 8, 8, 16);
                g.fillRect(x + 8, y + 12, 16, 8);
                break;
            case ARMOR:
                g.setColor(tankClr.darker());
                g.drawRect(x + 6, y + 6, w - 12, h - 12);
                g.drawRect(x + 8, y + 8, w - 16, h - 16);
                break;
        }
        
        g.setColor(Color.BLACK);
        switch (dir) {
            case UP:
                g.fillRect(x + w/2 - 2, y - 4, 4, 8);
                g.fillRect(x + w/2 - 1, y - 6, 2, 2);
                break;
            case DOWN:
                g.fillRect(x + w/2 - 2, y + h - 4, 4, 8);
                g.fillRect(x + w/2 - 1, y + h + 4, 2, 2);
                break;
            case LEFT:
                g.fillRect(x - 4, y + h/2 - 2, 8, 4);
                g.fillRect(x - 6, y + h/2 - 1, 2, 2);
                break;
            case RIGHT:
                g.fillRect(x + w - 4, y + h/2 - 2, 8, 4);
                g.fillRect(x + w + 4, y + h/2 - 1, 2, 2);
                break;
        }
        
        if (isRed) {
            g.setColor(Color.RED);
            g.fillRect(x + w/2 - 3, y + h/2 - 3, 6, 6);
            g.setColor(Color.WHITE);
            g.fillRect(x + w/2 - 2, y + h/2 - 2, 4, 4);
        }
        
        if (shld) {
            g.setColor(new Color(255, 200, 0, 100));
            g.fillOval(x - 4, y - 4, w + 8, h + 8);
        }
        
        if (frz) {
            g.setColor(new Color(100, 100, 255, 150));
            g.fillRect(x, y, w, h);
        }
    }
    
    @Override
    public void draw(Graphics2D g) {
        drawHidden(g, null);
    }
    
    public boolean isHidden(GameMap map) {
        if (map == null) return false;
        return map.isHideTile(x + w/2, y + h/2);
    }
    
    public boolean isDead() { return !alive; }
    public boolean isRed() { return isRed; }
    public int getScore() { return type.score; }
    public boolean isFrozen() { return frz; }
    public TankType getType() { return type; }
    public boolean hasDroppedPU() { return droppedPU; }
    public void setDroppedPU(boolean dropped) { droppedPU = dropped; }
}