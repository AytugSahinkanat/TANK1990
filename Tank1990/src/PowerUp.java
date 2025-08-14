import java.awt.*;
import java.io.Serializable;

public class PowerUp implements Serializable {
    public enum PUType {
        GRENADE(Color.RED),
        HELMET(Color.CYAN),   
        SHOVEL(Color.ORANGE), 
        STAR(Color.YELLOW),   
        TANK(Color.GREEN),     
        TIMER(Color.BLUE);   
        
        public final Color clr;
        
        PUType(Color clr) {
            this.clr = clr;
        }
    }
    
    private int x, y;
    private PUType type;
    private long spawnT;
    private int w = 32, h = 32;
    private boolean exp = false;
    private boolean blink = false;
    
    public PowerUp(int x, int y, PUType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.spawnT = System.currentTimeMillis();
    }
    
    public void update() {
        long elapsed = System.currentTimeMillis() - spawnT;
        
        if (elapsed > 10000) {
            blink = true;
        }
        
        if (elapsed > 15000) {
            exp = true;
        }
    }
    
    public void draw(Graphics2D g) {
        long time = System.currentTimeMillis();
        
        if (blink && (time / 100) % 2 == 0) {
            return;
        }
        
        if ((time / 200) % 2 == 0 || blink) {
            g.setColor(type.clr);
            g.fillRect(x, y, w, h);
            
            g.setColor(Color.BLACK);
            g.fillRect(x + 2, y + 2, w - 4, h - 4);
            
            g.setColor(type.clr.brighter());
            g.fillRect(x + 4, y + 4, w - 8, h - 8);
            
            g.setColor(Color.WHITE);
            g.drawRect(x + 1, y + 1, w - 2, h - 2);
            
            drawIcon(g);
        }
    }
    
    private void drawIcon(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        
        switch (type) {
            case GRENADE:
                drawGren(g, x + w/2, y + h/2);
                return;
            case HELMET:
                drawHelm(g, x + w/2, y + h/2);
                return;
            case SHOVEL:
                drawShov(g, x + w/2, y + h/2);
                return;
            case STAR:
                drawStar(g, x + w/2, y + h/2, 10);
                return;
            case TANK:
                drawTank(g, x + w/2, y + h/2);
                return;
            case TIMER:
                drawTime(g, x + w/2, y + h/2);
                return;
        }
    }
    
    private void drawGren(Graphics2D g, int cx, int cy) {
        g.setColor(Color.BLACK);
        g.fillOval(cx - 6, cy - 4, 12, 12);
        g.fillRect(cx - 2, cy - 8, 4, 6);
        g.setColor(Color.DARK_GRAY);
        g.drawArc(cx - 1, cy - 10, 2, 4, 0, 180);
    }
    
    private void drawHelm(Graphics2D g, int cx, int cy) {
        g.setColor(Color.DARK_GRAY);
        g.fillArc(cx - 8, cy - 8, 16, 16, 0, 180);
        g.fillRect(cx - 8, cy, 16, 4);
        g.setColor(Color.GRAY);
        g.fillArc(cx - 6, cy - 6, 12, 12, 0, 180);
    }
    
    private void drawShov(Graphics2D g, int cx, int cy) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(cx - 1, cy - 8, 2, 12);
        g.setColor(Color.GRAY);
        g.fillRect(cx - 4, cy - 10, 8, 4);
        g.fillPolygon(new int[]{cx - 4, cx + 4, cx}, 
                      new int[]{cy - 10, cy - 10, cy - 14}, 3);
    }
    
    private void drawStar(Graphics2D g, int cx, int cy, int sz) {
        g.setColor(Color.BLACK);
        int[] xPts = new int[10];
        int[] yPts = new int[10];
        
        for (int i = 0; i < 10; i++) {
            double angle = Math.PI * i / 5;
            int r = (i % 2 == 0) ? sz : sz / 2;
            xPts[i] = cx + (int)(r * Math.sin(angle));
            yPts[i] = cy - (int)(r * Math.cos(angle));
        }
        
        g.fillPolygon(xPts, yPts, 10);
    }
    
    private void drawTank(Graphics2D g, int cx, int cy) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(cx - 6, cy - 3, 12, 6);
        g.fillRect(cx - 2, cy - 6, 4, 4);
        g.setColor(Color.BLACK);
        g.fillOval(cx - 6, cy + 2, 3, 3);
        g.fillOval(cx + 3, cy + 2, 3, 3);
    }
    
    private void drawTime(Graphics2D g, int cx, int cy) {
        g.setColor(Color.BLACK);
        g.drawOval(cx - 8, cy - 8, 16, 16);
        g.fillOval(cx - 6, cy - 6, 12, 12);
        g.setColor(Color.WHITE);
        g.drawLine(cx, cy, cx + 4, cy - 4);
        g.drawLine(cx, cy, cx - 2, cy - 5);
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x + 4, y + 4, w - 8, h - 8);
    }
    
    public PUType getType() { return type; }
    public boolean isExpired() { return exp; }
    public int getX() { return x; }
    public int getY() { return y; }
}