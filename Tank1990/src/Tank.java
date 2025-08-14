import java.awt.*;
import java.io.Serializable;

public abstract class Tank implements Serializable, Runnable {
    protected int x, y;
    protected int w = 32, h = 32;
    protected Direction dir = Direction.UP;
    protected int spd = 2;
    protected long lastShot = 0;
    protected int shotCD = 500;
    protected boolean alive = true;
    protected transient Thread thread;
    protected boolean moving = false;
    
    private static final int MAP_W = 624;
    private static final int MAP_H = 624;
    
    public Tank(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void startThread() {
        thread = new Thread(this);
        thread.start();
    }
    
    public void stopThread() {
        alive = false;
        if (thread != null) {
            thread.interrupt();
        }
    }
    
    @Override
    public void run() {
        while (alive) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
    
    public void move(GameMap map) {
        try {
            int nextX = x + dir.dx * spd;
            int nextY = y + dir.dy * spd;
            
            if (nextX < 0) nextX = 0;
            if (nextX + w > MAP_W) nextX = MAP_W - w;
            if (nextY < 0) nextY = 0;
            if (nextY + h > MAP_H) nextY = MAP_H - h;
            
            Rectangle nextB = new Rectangle(nextX, nextY, w, h);
            if (map != null && !map.canMove(nextB)) {
                return;
            }
            
            if (map != null && map.isIceTile(x + w/2, y + h/2)) {
                int iceX = x + dir.dx * spd * 2;
                int iceY = y + dir.dy * spd * 2;
                
                if (iceX < 0) iceX = 0;
                if (iceX + w > MAP_W) iceX = MAP_W - w;
                if (iceY < 0) iceY = 0;
                if (iceY + h > MAP_H) iceY = MAP_H - h;
                
                Rectangle iceB = new Rectangle(iceX, iceY, w, h);
                if (map.canMove(iceB)) {
                    x = iceX;
                    y = iceY;
                    return;
                }
            }
            
            x = nextX;
            y = nextY;
        } catch (Exception e) {
            System.err.println("Tank move error: " + e.getMessage());
        }
    }
    
    public boolean canMoveTo(int nextX, int nextY, GameMap map, GameState gs) {
        if (nextX < 0 || nextX + w > MAP_W || 
            nextY < 0 || nextY + h > MAP_H) {
            return false;
        }
        
        Rectangle nextB = new Rectangle(nextX, nextY, w, h);
        
        if (map != null && !map.canMove(nextB)) {
            return false;
        }
        
        if (gs != null) {
            PlayerTank p1 = gs.getP1();
            if (p1 != null && p1 != this && p1.isAlive()) {
                if (nextB.intersects(p1.getBounds())) {
                    return false;
                }
            }
            
            PlayerTank p2 = gs.getP2();
            if (p2 != null && p2 != this && p2.isAlive()) {
                if (nextB.intersects(p2.getBounds())) {
                    return false;
                }
            }
            
            if (gs.getEnemies() != null) {
                for (EnemyTank e : gs.getEnemies()) {
                    if (e != null && e != this && e.isAlive()) {
                        if (nextB.intersects(e.getBounds())) {
                            return false;
                        }
                    }
                }
            }
        }
        
        return true;
    }
    
    public Bullet shoot() {
        try {
            long now = System.currentTimeMillis();
            if (now - lastShot < shotCD) {
                return null;
            }
            
            lastShot = now;
            return createBullet();
        } catch (Exception e) {
            System.err.println("Tank shoot error: " + e.getMessage());
            return null;
        }
    }
    
    protected abstract Bullet createBullet();
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, w, h);
    }
    
    public abstract void draw(Graphics2D g);
    
    public int getX() { return x; }
    public int getY() { return y; }
    public Direction getDir() { return dir; }
    public void setDir(Direction dir) { this.dir = dir; }
    public boolean isAlive() { return alive; }
    public void setAlive(boolean alive) { this.alive = alive; }
    public void setPos(int x, int y) { this.x = x; this.y = y; }
}