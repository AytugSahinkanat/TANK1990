import java.awt.*;
import java.io.Serializable;

public class Eagle implements Serializable {
    private int x, y;
    private int w = 32, h = 32;
    private boolean dead = false;
    
    public Eagle(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void draw(Graphics2D g) {
        if (!dead) {
            // kartal çizimi
            g.setColor(Color.WHITE);
            g.fillRect(x + 4, y + 4, w - 8, h - 8);
            g.setColor(Color.BLACK);
            g.drawRect(x + 4, y + 4, w - 8, h - 8);
            
            // kartal simgesi
            g.setColor(Color.RED);
            g.fillOval(x + 10, y + 8, 12, 8);
            int[] xPts = {x + 16, x + 12, x + 20};
            int[] yPts = {y + 16, y + 24, y + 24};
            g.fillPolygon(xPts, yPts, 3);
        } else {
            // yıkılmış kartal
            g.setColor(Color.DARK_GRAY);
            g.fillRect(x + 4, y + 4, w - 8, h - 8);
        }
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, w, h);
    }
    
    public void destroy() { dead = true; }
    public boolean isDestroyed() { return dead; }
}