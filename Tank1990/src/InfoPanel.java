import javax.swing.*;
import java.awt.*;

public class InfoPanel extends JPanel {
    private GameState gs;
    private static final Color BG_CLR = new Color(180, 180, 180);
    private static final Color TXT_CLR = Color.BLACK;
    private static final Font INFO_FNT = new Font("Monospaced", Font.BOLD, 16);
    private static final Font SML_FNT = new Font("Monospaced", Font.BOLD, 12);
    
    private static final Color PLR_CLR = new Color(255, 200, 0);
    private static final Color EN_CLR = new Color(100, 100, 100);
    
    private Timer timer;
    
    public InfoPanel(GameState gs) {
        this.gs = gs;
        setPreferredSize(new Dimension(100, 624));
        setBackground(BG_CLR);
        setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        setDoubleBuffered(true);
        
        timer = new Timer(33, e -> repaint());
        timer.start();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (gs == null) return;
        
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        
        int x = 10;
        int y = 20;
        
        drawEnemyInfo(g2, x, y);
        
        y = 300;
        drawPlrInfo(g2, x, y);
        
        y = 500;
        drawStgInfo(g2, x, y);
    }
    
    private void drawEnemyInfo(Graphics2D g, int x, int y) {
        int rem = gs.getRemEnemies();
        
        for (int i = 0; i < 20; i++) {
            int row = i / 2;
            int col = i % 2;
            int tX = x + col * 40;
            int tY = y + row * 25;
            
            if (i < rem) {
                drawMiniTank(g, tX, tY, EN_CLR);
            }
        }
    }
    
    private void drawPlrInfo(Graphics2D g, int x, int y) {
        g.setColor(TXT_CLR);
        g.setFont(INFO_FNT);
        g.drawString("IP", x + 20, y);
        
        y += 30;
        
        for (int i = 0; i < gs.getLives(); i++) {
            drawMiniTank(g, x + 10 + i * 25, y, PLR_CLR);
        }
        
        if (gs.is2P()) {
            y += 50;
            g.setColor(TXT_CLR);
            g.setFont(INFO_FNT);
            g.drawString("IIP", x + 15, y);
            
            y += 30;
            for (int i = 0; i < gs.getLives(); i++) {
                drawMiniTank(g, x + 10 + i * 25, y, PLR_CLR);
            }
        }
    }
    
    private void drawStgInfo(Graphics2D g, int x, int y) {
        g.setColor(Color.BLACK);
        g.fillRect(x + 35, y, 3, 30);
        
        g.setColor(Color.BLACK);
        int[] xPts = {x + 38, x + 55, x + 38};
        int[] yPts = {y, y + 8, y + 16};
        g.fillPolygon(xPts, yPts, 3);
        
        y += 50;
        g.setColor(TXT_CLR);
        g.setFont(INFO_FNT);
        String stgTxt = String.valueOf(gs.getLvl());
        g.drawString(stgTxt, x + 35, y);
    }
    
    private void drawMiniTank(Graphics2D g, int x, int y, Color clr) {
        g.setColor(clr);
        g.fillRect(x, y + 2, 16, 12);
        
        g.setColor(clr.darker());
        g.fillRect(x + 2, y + 4, 12, 8);
        
        g.setColor(Color.BLACK);
        g.fillRect(x + 7, y, 2, 4);
        
        g.fillRect(x - 1, y + 3, 2, 10);
        g.fillRect(x + 15, y + 3, 2, 10);
    }
    
    public void updateGS(GameState newGS) {
        this.gs = newGS;
        repaint();
    }
}