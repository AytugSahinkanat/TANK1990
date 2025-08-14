import java.awt.*;

public class PlayerTank extends Tank {
    private boolean shld = false;
    private long shldEnd = 0;
    private int stars = 0;
    private boolean hasGun = false;
    private boolean frz = false;
    private long frzEnd = 0;
    private int plrNum;
    private transient GameState gs;
    
    public PlayerTank(int x, int y, int plrNum) {
        super(x, y);
        this.spd = 3;
        this.shotCD = 300;
        this.plrNum = plrNum;
    }
    
    @Override
    public void run() {
        while (alive) {
            try {
                long now = System.currentTimeMillis();
                if (shld && now > shldEnd) {
                    shld = false;
                }
                if (frz && now > frzEnd) {
                    frz = false;
                }
                
                Thread.sleep(50);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
    
    public void respawn(int newX, int newY) {
        int[][] spawnPositions = {
            {newX - 48, newY}, {newX, newY}, {newX + 48, newY},
            {newX - 48, newY - 48}, {newX, newY - 48}, {newX + 48, newY - 48}
        };
        
        boolean spawned = false;
        
        for (int[] pos : spawnPositions) {
            int testX = pos[0];
            int testY = pos[1];
            
            if (testX < 0) testX = 0;
            if (testX > 592) testX = 592;
            if (testY < 0) testY = 0;
            if (testY > 592) testY = 592;
            
            Rectangle testRect = new Rectangle(testX, testY, 32, 32);
            
            if (isSafeSpawnPosition(testX, testY)) {
                this.x = testX;
                this.y = testY;
                spawned = true;
                break;
            }
        }
        
        if (!spawned) {
            this.x = newX;
            this.y = newY;
        }
        
        this.alive = true;
        this.stars = 0;
        this.hasGun = false;
        shield(3000);
    }
    
    private boolean isSafeSpawnPosition(int testX, int testY) {
        if (gs == null) {
            return true;
        }
        
        Rectangle testRect = new Rectangle(testX, testY, w, h);
        
        GameMap map = gs.getMap();
        if (map != null && !map.canMove(testRect)) {
            return false;
        }
        
        PlayerTank otherPlayer = (plrNum == 1) ? gs.getP2() : gs.getP1();
        if (otherPlayer != null && otherPlayer.isAlive()) {
            if (testRect.intersects(otherPlayer.getBounds())) {
                return false;
            }
        }
        
        if (gs.getEnemies() != null) {
            for (EnemyTank enemy : gs.getEnemies()) {
                if (enemy != null && enemy.isAlive()) {
                    Rectangle enemyRect = new Rectangle(enemy.getX(), enemy.getY(), 32, 32);
                    Rectangle safeRect = new Rectangle(testX - 16, testY - 16, 64, 64);
                    if (safeRect.intersects(enemyRect)) {
                        return false;
                    }
                }
            }
        }
        
        return true;
    }
    
    public void move(GameMap map, GameState gs) {
        if (!frz) {
            int nextX = x + dir.dx * spd;
            int nextY = y + dir.dy * spd;
            
            if (canMoveTo(nextX, nextY, map, gs)) {
                x = nextX;
                y = nextY;
            }
        }
    }
    
    @Override
    public void move(GameMap map) {
        if (!frz) {
            super.move(map);
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
        
        if (stars == 1) {
            shotCD = 250;
        } else if (stars == 2) {
            shotCD = 200;
        } else if (stars >= 3) {
            shotCD = 150;
            hasGun = true;
        }
        
        return new Bullet(bX, bY, dir, true, pwr);
    }
    
    public void hit() {
        if (!shld) {
            alive = false;
        }
    }
    
    public void shield(int dur) {
        shld = true;
        shldEnd = System.currentTimeMillis() + dur;
    }
    
    public void freeze(int dur) {
        frz = true;
        frzEnd = System.currentTimeMillis() + dur;
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
    
    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void setGS(GameState gs) {
        this.gs = gs;
    }
    
    @Override
    public void draw(Graphics2D g) {
        Color tankClr = plrNum == 1 ? Color.YELLOW : Color.GREEN;
        
        boolean hidden = isHidden(null);
        if (hidden) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        }
        
        g.setColor(tankClr);
        g.fillRect(x + 2, y + 2, w - 4, h - 4);
        
        g.setColor(tankClr.darker());
        g.fillRect(x + 6, y + 6, w - 12, h - 12);
        
        g.setColor(Color.DARK_GRAY);
        switch (dir) {
            case UP:
                g.fillRect(x + w/2 - 2, y - 4, 4, 8);
                break;
            case DOWN:
                g.fillRect(x + w/2 - 2, y + h - 4, 4, 8);
                break;
            case LEFT:
                g.fillRect(x - 4, y + h/2 - 2, 8, 4);
                break;
            case RIGHT:
                g.fillRect(x + w - 4, y + h/2 - 2, 8, 4);
                break;
        }
        
        if (shld) {
            g.setColor(new Color(0, 255, 255, 100));
            g.fillOval(x - 4, y - 4, w + 8, h + 8);
        }
        
        if (frz) {
            g.setColor(new Color(100, 100, 255, 150));
            g.fillRect(x, y, w, h);
        }
        
        if (stars > 0) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 10));
            g.drawString("*" + stars, x + 2, y - 2);
        }
    }
    
    public boolean isHidden(GameMap map) {
        if (map == null) return false;
        return map.isHideTile(x + w/2, y + h/2);
    }
    
    public boolean isShielded() { return shld; }
    public int getStars() { return stars; }
    public boolean hasGun() { return hasGun; }
    public boolean isFrozen() { return frz; }
}