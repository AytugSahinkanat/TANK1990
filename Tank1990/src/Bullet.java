import java.awt.*;
import java.io.Serializable;

public class Bullet implements Serializable {
    private int x, y;
    private Direction dir;
    private int spd = 6;
    private boolean isPlr;
    private boolean isPwr;
    private boolean dead = false;
    private int w = 4, h = 4;
    
    private static final int MAP_W = 624;
    private static final int MAP_H = 624;
    
    public Bullet(int x, int y, Direction dir, boolean isPlr, boolean isPwr) {
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.isPlr = isPlr;
        this.isPwr = isPwr;
    }
    
    public void update() {
        x += dir.dx * spd;
        y += dir.dy * spd;
        
        if (x < 0 || x > MAP_W || y < 0 || y > MAP_H) {
            dead = true;
        }
    }
    
    public void draw(Graphics2D g) {
        g.setColor(isPlr ? Color.YELLOW : Color.WHITE);
        g.fillOval(x, y, w, h);
        
        if (isPwr) {
            g.setColor(Color.RED);
            g.drawOval(x - 1, y - 1, w + 2, h + 2);
        }
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, w, h);
    }
    
    public boolean isPlayerBullet() { return isPlr; }
    public boolean isPowerful() { return isPwr; }
    public boolean isDead() { return dead; }
    public void setDead(boolean dead) { this.dead = dead; }
    public int getX() { return x; }
    public int getY() { return y; }
    public Direction getDirection() { return dir; }
}