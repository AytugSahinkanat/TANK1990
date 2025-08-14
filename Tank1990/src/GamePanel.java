import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private static final int GAME_W = 624;
    private static final int GAME_H = 624;
    
    private GameState gs;
    private Timer timer;
    private TankGame parent;
    
    private Set<Integer> keys = new HashSet<>();
    
    private boolean showLvlEnd = false;
    private long lvlEndTime = 0;
    private Map<EnemyTank.TankType, Integer> killCnt;
    
    public GamePanel(GameState gs) {
        this.gs = gs;
        setPreferredSize(new Dimension(GAME_W, GAME_H));
        setMaximumSize(new Dimension(GAME_W, GAME_H));
        setMinimumSize(new Dimension(GAME_W, GAME_H));
        setBackground(Color.BLACK);
        setFocusable(true);
        setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        
        addKeyListener(this);
        
        timer = new Timer(16, this);
        
        killCnt = new HashMap<>();
        resetKills();
    }
    
    private void resetKills() {
        for (EnemyTank.TankType t : EnemyTank.TankType.values()) {
            killCnt.put(t, 0);
        }
    }
    
    public void setParent(TankGame p) {
        this.parent = p;
    }
    
    public void start() {
        timer.start();
    }
    
    public void pause() {
        timer.stop();
    }
    
    public void resume() {
        timer.start();
    }
    
    public void updateGS(GameState newGS) {
        this.gs = newGS;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (gs != null) {
            if (showLvlEnd) {
                if (System.currentTimeMillis() - lvlEndTime > 3000) {
                    showLvlEnd = false;
                    resetKills();
                }
            } else {
                gs.update();
                handleInput();
            }
            
            repaint();
            if (parent != null) {
                parent.updateUI();
            }
        }
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        keys.add(e.getKeyCode());
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        keys.remove(e.getKeyCode());
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}
    
    public void handleKeyPress(int k) {
        keys.add(k);
    }
    
    public void handleKeyRelease(int k) {
        keys.remove(k);
    }
    
    private void handleInput() {
        if (gs == null) return;
        
        PlayerTank p1 = gs.getP1();
        if (p1 != null && p1.isAlive() && !p1.isFrozen()) {
            if (keys.contains(KeyEvent.VK_UP)) {
                p1.setDir(Direction.UP);
                p1.move(gs.getMap(), gs);
            } else if (keys.contains(KeyEvent.VK_DOWN)) {
                p1.setDir(Direction.DOWN);
                p1.move(gs.getMap(), gs);
            } else if (keys.contains(KeyEvent.VK_LEFT)) {
                p1.setDir(Direction.LEFT);
                p1.move(gs.getMap(), gs);
            } else if (keys.contains(KeyEvent.VK_RIGHT)) {
                p1.setDir(Direction.RIGHT);
                p1.move(gs.getMap(), gs);
            }
            
            if (keys.contains(KeyEvent.VK_Z)) {
                Bullet b = p1.shoot();
                if (b != null) {
                    gs.addBullet(b);
                }
            }
        }
        
        if (gs.is2P()) {
            PlayerTank p2 = gs.getP2();
            if (p2 != null && p2.isAlive() && !p2.isFrozen()) {
                if (keys.contains(KeyEvent.VK_W)) {
                    p2.setDir(Direction.UP);
                    p2.move(gs.getMap(), gs);
                } else if (keys.contains(KeyEvent.VK_S)) {
                    p2.setDir(Direction.DOWN);
                    p2.move(gs.getMap(), gs);
                } else if (keys.contains(KeyEvent.VK_A)) {
                    p2.setDir(Direction.LEFT);
                    p2.move(gs.getMap(), gs);
                } else if (keys.contains(KeyEvent.VK_D)) {
                    p2.setDir(Direction.RIGHT);
                    p2.move(gs.getMap(), gs);
                }
                
                if (keys.contains(KeyEvent.VK_SPACE)) {
                    Bullet b = p2.shoot();
                    if (b != null) {
                        gs.addBullet(b);
                    }
                }
            }
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        try {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (gs == null) return;
            
            if (gs.getMap() != null) {
                gs.getMap().draw(g2);
            }
            
            if (gs.getEagle() != null) {
                gs.getEagle().draw(g2);
            }
            
            if (gs.getEnemies() != null) {
                for (EnemyTank e : gs.getEnemies()) {
                    if (e != null && e.isAlive()) {
                        e.drawHidden(g2, gs.getMap());
                    }
                }
            }
            
            drawPlrTank(g2, gs.getP1());
            if (gs.is2P()) {
                drawPlrTank(g2, gs.getP2());
            }
            
            if (gs.getBullets() != null) {
                for (Bullet b : gs.getBullets()) {
                    if (b != null && !b.isDead()) {
                        b.draw(g2);
                    }
                }
            }
            
            if (gs.getPUs() != null) {
                for (PowerUp pu : gs.getPUs()) {
                    if (pu != null && !pu.isExpired()) {
                        pu.draw(g2);
                    }
                }
            }
            
            drawVeg(g2);
            
            if (showLvlEnd) {
                drawLvlEnd(g2);
            }
            
            if (gs.isGameOver()) {
                drawGameOver(g2);
            }
            
        } catch (Exception e) {
            System.err.println("Paint error: " + e.getMessage());
        }
    }
    
    private void drawPlrTank(Graphics2D g, PlayerTank p) {
        if (p != null && p.isAlive()) {
            boolean hidden = p.isHidden(gs.getMap());
            
            if (hidden) {
                Composite old = g.getComposite();
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                p.draw(g);
                g.setComposite(old);
            } else {
                p.draw(g);
            }
        }
    }
    
    private void drawVeg(Graphics2D g) {
        if (gs.getMap() != null) {
            gs.getMap().drawVegOverlay(g);
        }
    }
    
    private void drawLvlEnd(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, getWidth(), getHeight());
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 32));
        String title = "STAGE " + (gs.getLvl() - 1) + " COMPLETE";
        FontMetrics fm = g.getFontMetrics();
        int titleX = (getWidth() - fm.stringWidth(title)) / 2;
        g.drawString(title, titleX, 80);
        
        g.setFont(new Font("Monospaced", Font.BOLD, 24));
        int y = 150;
        
        g.setColor(Color.YELLOW);
        g.drawString("SCORE", 200, y);
        g.drawString(String.valueOf(gs.getScore()), 400, y);
        y += 50;
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 18));
        
        g.drawString("Tank Type", 150, y);
        g.drawString("Destroyed", 300, y);
        g.drawString("Points", 450, y);
        y += 30;
        
        g.drawLine(150, y - 5, 500, y - 5);
        y += 20;
        
        int stgKills = 0;
        int stgPts = 0;
        
        for (EnemyTank.TankType t : EnemyTank.TankType.values()) {
            int cnt = getKills(t);
            int pts = cnt * t.score;
            stgKills += cnt;
            stgPts += pts;
            
            drawTankIcon(g, 150, y - 15, t);
            
            g.setColor(Color.WHITE);
            g.drawString(getName(t), 180, y);
            g.drawString(String.valueOf(cnt), 330, y);
            g.drawString(String.valueOf(pts), 470, y);
            
            y += 35;
        }
        
        g.drawLine(150, y - 10, 500, y - 10);
        y += 20;
        
        g.setFont(new Font("Monospaced", Font.BOLD, 20));
        g.setColor(Color.YELLOW);
        g.drawString("STAGE TOTAL", 150, y);
        g.drawString(String.valueOf(stgKills), 330, y);
        g.drawString(String.valueOf(stgPts), 470, y);
        
        y += 40;
        g.setColor(Color.GREEN);
        g.drawString("STAGE BONUS", 180, y);
        g.drawString("1000", 470, y);
    }
    
    private void drawGameOver(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, getWidth(), getHeight());
        
        g.setColor(Color.RED);
        g.setFont(new Font("Monospaced", Font.BOLD, 48));
        FontMetrics fm = g.getFontMetrics();
        String txt = "GAME OVER";
        int x = (getWidth() - fm.stringWidth(txt)) / 2;
        int y = 100;
        g.drawString(txt, x, y);
        
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Monospaced", Font.BOLD, 24));
        String scoreTxt = "FINAL SCORE: " + gs.getScore();
        x = (getWidth() - g.getFontMetrics().stringWidth(scoreTxt)) / 2;
        g.drawString(scoreTxt, x, y + 60);
        
        y = 220;
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 18));
        
        g.drawString("TOTAL ENEMY STATISTICS", 180, y);
        y += 40;
        
        g.setFont(new Font("Monospaced", Font.BOLD, 16));
        g.drawString("Tank Type", 150, y);
        g.drawString("Destroyed", 300, y);
        g.drawString("Points", 450, y);
        y += 25;
        
        g.drawLine(150, y - 5, 500, y - 5);
        y += 20;
        
        int totKills = 0;
        int totPts = 0;
        
        for (EnemyTank.TankType t : EnemyTank.TankType.values()) {
            int cnt = getTotKills(t);
            int pts = cnt * t.score;
            totKills += cnt;
            totPts += pts;
            
            drawTankIcon(g, 150, y - 15, t);
            
            g.setColor(Color.WHITE);
            g.drawString(getName(t), 180, y);
            g.drawString(String.valueOf(cnt), 330, y);
            g.drawString(String.valueOf(pts), 470, y);
            
            y += 30;
        }
        
        g.drawLine(150, y - 10, 500, y - 10);
        y += 20;
        
        g.setFont(new Font("Monospaced", Font.BOLD, 18));
        g.setColor(Color.YELLOW);
        g.drawString("TOTAL TANKS", 150, y);
        g.drawString(String.valueOf(totKills), 330, y);
        g.drawString(String.valueOf(totPts), 470, y);
        
        y += 40;
        g.setColor(Color.GREEN);
        g.drawString("REACHED STAGE: " + gs.getLvl(), 220, y);
    }
    
    private void drawTankIcon(Graphics2D g, int x, int y, EnemyTank.TankType t) {
        g.setColor(t.clr);
        g.fillRect(x, y, 20, 16);
        
        g.setColor(t.clr.darker());
        g.fillRect(x + 3, y + 3, 14, 10);
        
        g.setColor(Color.BLACK);
        g.fillRect(x + 9, y - 3, 2, 6);
    }
    
    private String getName(EnemyTank.TankType t) {
        switch (t) {
            case BASIC: return "Basic";
            case FAST: return "Fast";
            case POWER: return "Power";
            case ARMOR: return "Armor";
            default: return "Unknown";
        }
    }
    
    private int getKills(EnemyTank.TankType t) {
        if (gs != null && gs.getKillCnt() != null) {
            return gs.getKillCnt().getOrDefault(t, 0);
        }
        return 0;
    }
    
    private int getTotKills(EnemyTank.TankType t) {
        if (gs != null && gs.getTotKillCnt() != null) {
            return gs.getTotKillCnt().getOrDefault(t, 0);
        }
        return 0;
    }
    
    public void recKill(EnemyTank.TankType t) {
        killCnt.put(t, killCnt.getOrDefault(t, 0) + 1);
    }
    
    public void showLvlEnd() {
        showLvlEnd = true;
        lvlEndTime = System.currentTimeMillis();
    }
}