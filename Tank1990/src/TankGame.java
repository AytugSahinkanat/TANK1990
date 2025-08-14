import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class TankGame extends JFrame implements ActionListener, KeyListener {
    private static final int BOARD_W = 1000;
    private static final int BOARD_H = 900;
    
    private GamePanel gPanel;
    private JPanel menuPnl;
    private JButton startBtn, stopBtn, saveBtn, loadBtn;
    private JLabel scoreLbl, livesLbl, enemyLbl, lvlLbl;
    
    private GameState gs;
    private boolean running = false;
    private boolean paused = false;
    
    private StartScreen startScr;
    private boolean inStart = true;
    
    private InfoPanel infoPnl;
    
    private static Font RETRO_FNT;
    
    static {
        try {
            RETRO_FNT = new Font("Monospaced", Font.BOLD, 24);
        } catch (Exception e) {
            RETRO_FNT = new Font("Courier New", Font.BOLD, 24);
        }
    }
    
    public TankGame() {
        setTitle("TANK 1990");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        showStart();
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void showStart() {
        startScr = new StartScreen(this);
        setContentPane(startScr);
        revalidate();
        repaint();
    }
    
    public void start1P() {
        inStart = false;
        initGame(false);
    }
    
    public void start2P() {
        inStart = false;
        initGame(true);
    }
    
    private void initGame(boolean twoP) {
        gs = new GameState(twoP);
        gPanel = new GamePanel(gs);
        gPanel.setParent(this);
        
        addKeyListener(this);
        gPanel.addKeyListener(this);
        
        setFocusable(true);
        gPanel.setFocusable(true);
        
        setupUI();
        gPanel.requestFocusInWindow();
    }
    
    private void setupUI() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());
        
        JPanel mainCont = new JPanel();
        mainCont.setLayout(new BorderLayout());
        mainCont.setBackground(Color.BLACK);
        
        menuPnl = new JPanel();
        menuPnl.setBackground(Color.DARK_GRAY);
        
        startBtn = new JButton("Start");
        stopBtn = new JButton("Stop");
        saveBtn = new JButton("Save");
        loadBtn = new JButton("Load");
        
        scoreLbl = new JLabel("Score: 0");
        scoreLbl.setForeground(Color.WHITE);
        livesLbl = new JLabel("Lives: 2");
        livesLbl.setForeground(Color.WHITE);
        enemyLbl = new JLabel("Enemies: 20");
        enemyLbl.setForeground(Color.WHITE);
        lvlLbl = new JLabel("Level: 1");
        lvlLbl.setForeground(Color.WHITE);
        
        startBtn.addActionListener(this);
        stopBtn.addActionListener(this);
        saveBtn.addActionListener(this);
        loadBtn.addActionListener(this);
        
        menuPnl.add(startBtn);
        menuPnl.add(stopBtn);
        menuPnl.add(saveBtn);
        menuPnl.add(loadBtn);
        menuPnl.add(new JSeparator(SwingConstants.VERTICAL));
        menuPnl.add(scoreLbl);
        menuPnl.add(livesLbl);
        menuPnl.add(enemyLbl);
        menuPnl.add(lvlLbl);
        
        JPanel gameCont = new JPanel();
        gameCont.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        gameCont.setBackground(new Color(180, 180, 180));
        gameCont.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        gPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        gameCont.add(gPanel);
        
        infoPnl = new InfoPanel(gs);
        infoPnl.setAlignmentY(Component.TOP_ALIGNMENT);
        gameCont.add(infoPnl);
        
        mainCont.add(menuPnl, BorderLayout.NORTH);
        mainCont.add(gameCont, BorderLayout.CENTER);
        
        add(mainCont);
        
        revalidate();
        repaint();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startBtn) {
            startGame();
        } else if (e.getSource() == stopBtn) {
            stopGame();
        } else if (e.getSource() == saveBtn) {
            saveGame();
        } else if (e.getSource() == loadBtn) {
            loadGame();
        }
    }
    
    private void startGame() {
        if (!running) {
            running = true;
            paused = false;
            
            gs.start();
            gPanel.start();
            gPanel.requestFocusInWindow();
        } else if (paused) {
            paused = false;
            gs.resume();
            gPanel.resume();
            gPanel.requestFocusInWindow();
        }
    }
    
    private void stopGame() {
        if (running && !paused) {
            paused = true;
            gs.pause();
            gPanel.pause();
        }
    }
    
    private void saveGame() {
        try {
            JFileChooser ch = new JFileChooser();
            ch.setSelectedFile(new File("tank_save.dat"));
            if (ch.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                gs.save(ch.getSelectedFile().getPath());
                JOptionPane.showMessageDialog(this, "Oyun kaydedildi!");
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Kaydetme hatası: " + ex.getMessage());
        }
    }
    
    private void loadGame() {
        try {
            JFileChooser ch = new JFileChooser();
            if (ch.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                GameState loaded = GameState.load(ch.getSelectedFile().getPath());
                gs = loaded;
                gPanel.updateGS(gs);
                JOptionPane.showMessageDialog(this, "Oyun yüklendi!");
            }
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Yükleme hatası: " + ex.getMessage());
        }
    }
    
    public void updateUI() {
        if (gs != null) {
            scoreLbl.setText("Score: " + gs.getScore());
            livesLbl.setText("Lives: " + gs.getLives());
            enemyLbl.setText("Enemies: " + gs.getRemEnemies());
            lvlLbl.setText("Level: " + gs.getLvl());
            
            if (infoPnl != null) {
                infoPnl.repaint();
            }
        }
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        if (running && !paused && gPanel != null) {
            gPanel.handleKeyPress(e.getKeyCode());
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        if (running && !paused && gPanel != null) {
            gPanel.handleKeyRelease(e.getKeyCode());
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}
    
    class StartScreen extends JPanel {
        private TankGame game;
        private int selOpt = 0;
        private boolean showTank = false;
        private int selTank = 0;
        private float titleY = BOARD_H + 100;
        private Timer animTmr;
        private long startT;
        
        public StartScreen(TankGame game) {
            this.game = game;
            setPreferredSize(new Dimension(BOARD_W - 100, BOARD_H - 100));
            setBackground(Color.BLACK);
            setFocusable(true);
            
            startT = System.currentTimeMillis();
            
            animTmr = new Timer(30, e -> {
                long elapsed = System.currentTimeMillis() - startT;
                if (elapsed < 2000) {
                    titleY = BOARD_H + 100 - (elapsed / 2000f) * (BOARD_H - 50);
                } else {
                    titleY = 150;
                    animTmr.stop();
                }
                repaint();
            });
            animTmr.start();
            
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (!showTank) {
                        if (e.getKeyCode() == KeyEvent.VK_UP) {
                            selOpt = (selOpt - 1 + 3) % 3;
                            repaint();
                        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                            selOpt = (selOpt + 1) % 3;
                            repaint();
                        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            if (selOpt < 2) {
                                showTank = true;
                                repaint();
                            }
                        }
                    } else {
                        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
                            repaint();
                        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            if (selOpt == 0) {
                                game.start1P();
                            } else {
                                game.start2P();
                            }
                        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                            showTank = false;
                            repaint();
                        }
                    }
                }
            });
            
            requestFocusInWindow();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            
            if (!showTank) {
                if (titleY < BOARD_H) {
                    drawTitle(g2, (int)titleY);
                }
                
                g2.setFont(RETRO_FNT.deriveFont(20f));
                
                if (selOpt == 0) {
                    g2.setColor(Color.YELLOW);
                    drawTank(g2, 160, 280);
                } else {
                    g2.setColor(Color.GRAY);
                }
                g2.drawString("1 PLAYER", 220, 300);
                
                if (selOpt == 1) {
                    g2.setColor(Color.YELLOW);
                    drawTank(g2, 160, 340);
                } else {
                    g2.setColor(Color.GRAY);
                }
                g2.drawString("2 PLAYERS", 220, 360);
                
                if (selOpt == 2) {
                    g2.setColor(Color.YELLOW);
                    drawTank(g2, 160, 400);
                } else {
                    g2.setColor(Color.GRAY);
                }
                g2.drawString("CONSTRUCTION", 220, 420);
                
                g2.setColor(Color.WHITE);
                g2.setFont(RETRO_FNT.deriveFont(16f));
                g2.drawString("© VS. 1990", 250, 500);
                
            } else {
                g2.setColor(Color.WHITE);
                g2.setFont(RETRO_FNT.deriveFont(32f));
                String title = selOpt == 0 ? "1 PLAYER" : "2 PLAYERS";
                FontMetrics fm = g2.getFontMetrics();
                int titleX = (getWidth() - fm.stringWidth(title)) / 2;
                g2.drawString(title, titleX, 100);
                
                g2.setFont(RETRO_FNT.deriveFont(20f));
                String txt = "STAGE 1";
                int txtX = (getWidth() - g2.getFontMetrics().stringWidth(txt)) / 2;
                g2.drawString(txt, txtX, 200);
                
                drawTank(g2, 300, 300);
                
                g2.setFont(RETRO_FNT.deriveFont(16f));
                g2.drawString("Enter: Başla    ESC: Geri", 200, 450);
            }
        }
        
        private void drawTitle(Graphics2D g, int yOff) {
            String[] lines = {
                "████ ████  █  █  █  █",
                "  █  █  █  ██ █  ███ ",
                "  █  ████  █ ██  █ █ ",
                "  █ T█ A█  █ N█  █ K█"
            };
            g.setColor(new Color(180, 70, 30));
            int blkSz = 12;
            int stX = 120;
            
            for (int row = 0; row < lines.length; row++) {
                String line = lines[row];
                for (int col = 0; col < line.length(); col++) {
                    if (line.charAt(col) == '█') {
                        int x = stX + col * blkSz;
                        int y = yOff - 80 + row * blkSz;
                        g.fillRect(x, y, blkSz-1, blkSz-1);
                    }
                }
            }
            
            String[] year = {
                " ██   ███   ███  ████",
                "  █  █   █ █   █ █  █",
                "  █   ████  ████ █  █",
                "  █      █     █ █  █",
                " ███  ████  ████ ████"
            };
            
            int yrStX = 180;
            for (int row = 0; row < year.length; row++) {
                String line = year[row];
                for (int col = 0; col < line.length(); col++) {
                    if (line.charAt(col) == '█') {
                        int x = yrStX + col * blkSz;
                        int y = yOff + row * blkSz;
                        g.fillRect(x, y, blkSz-1, blkSz-1);
                    }
                }
            }
        }
        
        private void drawTank(Graphics2D g, int x, int y) {
            g.fillRect(x, y, 30, 30);
            g.setColor(Color.BLACK);
            g.fillRect(x + 5, y + 5, 20, 20);
            g.setColor(Color.YELLOW);
            g.fillRect(x + 14, y - 5, 4, 10);
        }
    }
    
    public static void main(String[] args) {
        System.out.println("Tank 1990 Oyunu başlatılıyor...");
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Look and Feel ayarlanamadı: " + e.getMessage());
            }
            new TankGame();
        });
    }
}