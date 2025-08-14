import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.awt.*;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int BOARD_W = 832;
    private static final int BOARD_H = 624;
    
    private int score = 0;
    private int lives = 2;
    private int lvl = 1;
    private int remEnemies = 20;
    private int spawnedEnemies = 0;
    private boolean gameOver = false;
    private boolean paused = false;
    
    private PlayerTank p1;
    private PlayerTank p2;
    private boolean twoP = false;
    
    private Eagle eagle;
    private List<EnemyTank> enemies;
    private List<Bullet> bullets;
    private List<PowerUp> pus;
    private GameMap map;
    
    private List<PowerUp.PUType> puOrder;
    private int puIdx = 0;
    
    private Map<EnemyTank.TankType, Integer> killCnt;
    private Map<EnemyTank.TankType, Integer> totKillCnt;
    
    public GameState(boolean twoP) {
        this.twoP = twoP;
        this.killCnt = new HashMap<>();
        this.totKillCnt = new HashMap<>();
        resetKills();
        resetTotKills();
        init();
    }
    
    private void resetKills() {
        for (EnemyTank.TankType t : EnemyTank.TankType.values()) {
            killCnt.put(t, 0);
        }
    }
    
    private void resetTotKills() {
        for (EnemyTank.TankType t : EnemyTank.TankType.values()) {
            totKillCnt.put(t, 0);
        }
    }
    
    private void init() {
        map = new GameMap();
        if (lvl == 1) {
            map.loadMap1();
        } else if (lvl == 2) {
            map.loadMap2();
        } else {
            map.genRndMap();
        }
        
        p1 = createPlayerTank(1);
        if (twoP) {
            p2 = createPlayerTank(2);
        }
        
        eagle = new Eagle(288, 576);
        enemies = new CopyOnWriteArrayList<>();
        bullets = new CopyOnWriteArrayList<>();
        pus = new CopyOnWriteArrayList<>();
        
        initPUOrder();
    }
    
    private PlayerTank createPlayerTank(int plrNum) {
        int[][] spawnPositions = {
            {144, 576}, {192, 576}, {240, 576},
            {336, 576}, {384, 576}, {432, 576}
        };
        
        int startIdx = plrNum == 1 ? 0 : 3;
        
        for (int i = 0; i < 3; i++) {
            int x = spawnPositions[startIdx + i][0];
            int y = spawnPositions[startIdx + i][1];
            
            Rectangle testRect = new Rectangle(x, y, 32, 32);
            if (map.canMove(testRect)) {
                if (plrNum == 2 && p1 != null) {
                    if (!testRect.intersects(p1.getBounds())) {
                        PlayerTank tank = new PlayerTank(x, y, plrNum);
                        tank.setGS(this);
                        return tank;
                    }
                } else {
                    PlayerTank tank = new PlayerTank(x, y, plrNum);
                    tank.setGS(this);
                    return tank;
                }
            }
        }
        
        PlayerTank tank = new PlayerTank(plrNum == 1 ? 192 : 384, 576, plrNum);
        tank.setGS(this);
        return tank;
    }
    
    private void initPUOrder() {
        puOrder = new ArrayList<>();
        puOrder.add(PowerUp.PUType.GRENADE);
        puOrder.add(PowerUp.PUType.HELMET);
        puOrder.add(PowerUp.PUType.SHOVEL);
        puOrder.add(PowerUp.PUType.STAR);
        puOrder.add(PowerUp.PUType.TANK);
        puOrder.add(PowerUp.PUType.TIMER);
    }
    
    public void setPUOrder(List<PowerUp.PUType> order) {
        this.puOrder = order;
        this.puIdx = 0;
    }
    
    public void start() {
        p1.startThread();
        if (twoP && p2 != null) {
            p2.startThread();
        }
        
        spawnInitEnemies();
    }
    
    public void pause() {
        paused = true;
    }
    
    public void resume() {
        paused = false;
    }
    
    private void spawnInitEnemies() {
        Point[] spawnPts = getSpawnPoints();
        int spawned = 0;
        
        for (int i = 0; i < Math.min(4, remEnemies) && i < spawnPts.length; i++) {
            if (spawnEnemyAt(spawnPts[i])) {
                spawned++;
            }
        }
        
        if (spawned < Math.min(4, remEnemies)) {
            for (int tries = 0; tries < 10 && spawned < Math.min(4, remEnemies); tries++) {
                Point pt = getRndSpawn();
                pt.x += (new Random().nextInt(3) - 1) * 48;
                pt.y += new Random().nextInt(2) * 48;
                
                if (pt.x < 0) pt.x = 0;
                if (pt.x > 576) pt.x = 576;
                if (pt.y < 0) pt.y = 0;
                
                if (spawnEnemyAt(pt)) {
                    spawned++;
                }
            }
        }
    }
    
    public void spawnEnemy() {
        if (spawnedEnemies < 20 && enemies.size() < 4) {
            for (int tries = 0; tries < 20; tries++) {
                Point pt = getRndSpawn();
                
                int offset = tries * 16;
                pt.x += (tries % 3 - 1) * offset;
                
                if (pt.x < 0) pt.x = 0;
                if (pt.x > 576) pt.x = 576;
                
                if (spawnEnemyAt(pt)) {
                    break;
                }
            }
        }
    }
    
    private boolean spawnEnemyAt(Point pt) {
        Rectangle spawnRect = new Rectangle(pt.x, pt.y, 32, 32);
        
        if (map != null && !map.canMove(spawnRect)) {
            return false;
        }
        
        for (EnemyTank e : enemies) {
            if (e != null && e.isAlive()) {
                Rectangle enemyRect = new Rectangle(e.getX(), e.getY(), 32, 32);
                if (spawnRect.intersects(enemyRect)) {
                    return false;
                }
            }
        }
        
        if (p1 != null && p1.isAlive()) {
            if (spawnRect.intersects(p1.getBounds())) {
                return false;
            }
        }
        
        if (twoP && p2 != null && p2.isAlive()) {
            if (spawnRect.intersects(p2.getBounds())) {
                return false;
            }
        }
        
        EnemyTank.TankType type = getRndType();
        boolean red = shouldBeRed();
        EnemyTank enemy = new EnemyTank(pt.x, pt.y, type, red, this);
        
        enemies.add(enemy);
        enemy.startThread();
        spawnedEnemies++;
        
        return true;
    }
    
    private Point[] getSpawnPoints() {
        return new Point[] {
            new Point(0, 0),
            new Point(288, 0),
            new Point(576, 0),
            new Point(144, 0),
            new Point(432, 0)
        };
    }
    
    private Point getRndSpawn() {
        Point[] pts = {
            new Point(0, 0),
            new Point(288, 0),
            new Point(576, 0)
        };
        return new Point(pts[new Random().nextInt(3)]);
    }
    
    private EnemyTank.TankType getRndType() {
        EnemyTank.TankType[] types = EnemyTank.TankType.values();
        return types[new Random().nextInt(types.length)];
    }
    
    private boolean shouldBeRed() {
        if (spawnedEnemies == 3 || spawnedEnemies == 10 || spawnedEnemies == 17) {
            return true;
        }
        return new Random().nextInt(100) < 30;
    }
    
    public void update() {
        if (gameOver || paused) return;
        
        try {
            if (map != null) {
                map.update();
            }
            
            List<Bullet> bCopy = new ArrayList<>(bullets);
            for (Bullet b : bCopy) {
                if (b != null) {
                    b.update();
                }
            }
            
            List<PowerUp> puCopy = new ArrayList<>(pus);
            for (PowerUp pu : puCopy) {
                if (pu != null) {
                    pu.update();
                }
            }
            
            checkHits();
            cleanDead();
            
            if (enemies.size() < 4 && spawnedEnemies < 20) {
                spawnEnemy();
            }
            
            checkLvlEnd();
            
        } catch (Exception e) {
            System.err.println("GS update error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void checkHits() {
        try {
            List<Bullet> toRem = new ArrayList<>();
            
            for (Bullet b : bullets) {
                if (b == null || b.isDead()) {
                    toRem.add(b);
                    continue;
                }
                
                if (map != null && map.checkBulletHit(b)) {
                    toRem.add(b);
                    continue;
                }
                
                if (b.isPlayerBullet()) {
                    for (EnemyTank e : new ArrayList<>(enemies)) {
                        if (e != null && !e.isDead() && 
                            b.getBounds().intersects(e.getBounds())) {
                            
                            if (e.isRed() && !e.hasDroppedPU()) {
                                spawnPU(e.getX(), e.getY());
                                e.setDroppedPU(true);
                            }
                            
                            e.hit();
                            toRem.add(b);
                            
                            if (e.isDead()) {
                                score += e.getScore();
                                remEnemies--;
                                killCnt.put(e.getType(), 
                                    killCnt.getOrDefault(e.getType(), 0) + 1);
                                totKillCnt.put(e.getType(), 
                                    totKillCnt.getOrDefault(e.getType(), 0) + 1);
                            }
                            break;
                        }
                    }
                } else {
                    if (p1 != null && p1.isAlive() &&
                        b.getBounds().intersects(p1.getBounds())) {
                        if (!p1.isShielded()) {
                            p1.hit();
                            toRem.add(b);
                            
                            if (!p1.isAlive()) {
                                handleDeath(1);
                            }
                        } else {
                            toRem.add(b);
                        }
                    }
                    
                    if (twoP && p2 != null && p2.isAlive() &&
                        b.getBounds().intersects(p2.getBounds())) {
                        if (!p2.isShielded()) {
                            p2.hit();
                            toRem.add(b);
                            
                            if (!p2.isAlive()) {
                                handleDeath(2);
                            }
                        } else {
                            toRem.add(b);
                        }
                    }
                    
                    if (eagle != null && !eagle.isDestroyed() &&
                        b.getBounds().intersects(eagle.getBounds())) {
                        eagle.destroy();
                        gameOver = true;
                        toRem.add(b);
                    }
                }
                
                for (Bullet b2 : bullets) {
                    if (b2 != b && b2 != null &&
                        b.isPlayerBullet() != b2.isPlayerBullet() &&
                        b.getBounds().intersects(b2.getBounds())) {
                        toRem.add(b);
                        toRem.add(b2);
                        break;
                    }
                }
            }
            
            checkPUHits();
            bullets.removeAll(toRem);
            
        } catch (Exception e) {
            System.err.println("Hit check error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleDeath(int plr) {
        if (lives > 0) {
            lives--;
            if (plr == 1) {
                p1.respawn(192, 528);
            } else {
                p2.respawn(384, 528);
            }
        } else {
            if (!twoP || (!p1.isAlive() && !p2.isAlive())) {
                gameOver = true;
            }
        }
    }
    
    private void checkPUHits() {
        try {
            List<PowerUp> toRem = new ArrayList<>();
            
            for (PowerUp pu : pus) {
                if (pu == null || pu.isExpired()) {
                    toRem.add(pu);
                    continue;
                }
                
                if (p1 != null && p1.isAlive() &&
                    pu.getBounds().intersects(p1.getBounds())) {
                    applyPU(pu.getType(), 1);
                    toRem.add(pu);
                    score += 500;
                    continue;
                }
                
                if (twoP && p2 != null && p2.isAlive() &&
                    pu.getBounds().intersects(p2.getBounds())) {
                    applyPU(pu.getType(), 2);
                    toRem.add(pu);
                    score += 500;
                    continue;
                }
                
                for (EnemyTank e : enemies) {
                    if (e != null && !e.isDead() &&
                        pu.getBounds().intersects(e.getBounds())) {
                        applyPUEnemy(e, pu.getType());
                        toRem.add(pu);
                        break;
                    }
                }
            }
            
            pus.removeAll(toRem);
            
        } catch (Exception e) {
            System.err.println("PU hit error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void applyPU(PowerUp.PUType type, int plr) {
        PlayerTank tank = plr == 1 ? p1 : p2;
        
        switch (type) {
            case GRENADE:
                for (EnemyTank e : enemies) {
                    if (e != null) {
                        e.setAlive(false);
                        e.stopThread();
                        score += e.getScore();
                        killCnt.put(e.getType(), 
                            killCnt.getOrDefault(e.getType(), 0) + 1);
                        totKillCnt.put(e.getType(), 
                            totKillCnt.getOrDefault(e.getType(), 0) + 1);
                    }
                }
                score += 500;
                break;
            case HELMET:
                tank.shield(10000);
                break;
            case STAR:
                tank.addStar();
                break;
            case SHOVEL:
                map.makeEagleSteel(15000);
                break;
            case TANK:
                lives++;
                break;
            case TIMER:
                for (EnemyTank e : enemies) {
                    if (e != null) {
                        e.freeze(8000);
                    }
                }
                break;
        }
    }
    
    private void applyPUEnemy(EnemyTank e, PowerUp.PUType type) {
        switch (type) {
            case GRENADE:
                if (p1 != null && !p1.isShielded()) {
                    p1.hit();
                    if (!p1.isAlive()) {
                        handleDeath(1);
                    }
                }
                if (twoP && p2 != null && !p2.isShielded()) {
                    p2.hit();
                    if (!p2.isAlive()) {
                        handleDeath(2);
                    }
                }
                break;
            case HELMET:
                e.shield(10000);
                break;
            case STAR:
                e.addStar();
                break;
            case SHOVEL:
                map.removeEagle();
                break;
            case TANK:
                break;
            case TIMER:
                if (p1 != null) {
                    p1.freeze(8000);
                }
                if (twoP && p2 != null) {
                    p2.freeze(8000);
                }
                break;
        }
    }
    
    private void spawnPU(int x, int y) {
        try {
            PowerUp.PUType type;
            
            if (puOrder != null && puOrder.size() > 0) {
                type = puOrder.get(puIdx % puOrder.size());
                puIdx++;
            } else {
                PowerUp.PUType[] types = PowerUp.PUType.values();
                type = types[new Random().nextInt(types.length)];
            }
            
            Random rnd = new Random();
            int px = rnd.nextInt(593);
            int py = rnd.nextInt(593);
            
            Rectangle test = new Rectangle(px, py, 32, 32);
            int tries = 0;
            
            while (tries < 20) {
                boolean valid = true;
                
                if (map != null && !map.canMove(test)) {
                    valid = false;
                }
                
                for (PowerUp pu : pus) {
                    if (pu != null && pu.getBounds().intersects(test)) {
                        valid = false;
                        break;
                    }
                }
                
                if (valid) {
                    break;
                }
                
                px = rnd.nextInt(593);
                py = rnd.nextInt(593);
                test = new Rectangle(px, py, 32, 32);
                tries++;
            }
            
            PowerUp newPU = new PowerUp(px, py, type);
            pus.add(newPU);
            
            System.out.println("PU spawned: " + type + " at " + px + "," + py);
            
        } catch (Exception e) {
            System.err.println("PU spawn error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void cleanDead() {
        List<EnemyTank> deadE = new ArrayList<>();
        for (EnemyTank e : enemies) {
            if (e != null && e.isDead()) {
                e.stopThread();
                deadE.add(e);
            }
        }
        enemies.removeAll(deadE);
        
        List<Bullet> deadB = new ArrayList<>();
        for (Bullet b : bullets) {
            if (b != null && b.isDead()) {
                deadB.add(b);
            }
        }
        bullets.removeAll(deadB);
        
        List<PowerUp> expPU = new ArrayList<>();
        for (PowerUp pu : pus) {
            if (pu != null && pu.isExpired()) {
                expPU.add(pu);
            }
        }
        pus.removeAll(expPU);
    }
    
    private void checkLvlEnd() {
        if (spawnedEnemies >= 20 && enemies.isEmpty()) {
            lvl++;
            remEnemies = 20;
            spawnedEnemies = 0;
            
            if (lvl == 1) {
                map.loadMap1();
            }
            else if (lvl == 2) {
                map.loadMap2();
            } else {
                map.genRndMap();
            }
            
            if (p1 != null && p1.isAlive()) {
                p1.setPos(192, 528);
            }
            
            if (twoP && p2 != null && p2.isAlive()) {
                p2.setPos(384, 528);
            }
            
            spawnInitEnemies();
            
            score += 1000;
            resetKills();
        }
    }
    
    public void addBullet(Bullet b) {
        if (b != null) {
            bullets.add(b);
        }
    }
    
    public void save(String file) throws IOException {
        cleanBeforeSave();
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(this);
        }
    }
    
    public static GameState load(String file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            GameState gs = (GameState) ois.readObject();
            
            gs.restoreAfterLoad();
            
            return gs;
        }
    }
    
    private void cleanBeforeSave() {
        cleanDead();
        
        if (p1 != null) {
            p1.stopThread();
        }
        if (p2 != null) {
            p2.stopThread();
        }
        for (EnemyTank enemy : enemies) {
            if (enemy != null) {
                enemy.stopThread();
            }
        }
    }
    
    private void restoreAfterLoad() {
        if (bullets != null) {
            bullets.clear();
        } else {
            bullets = new CopyOnWriteArrayList<>();
        }
        
        int enemyCount = 0;
        if (enemies != null) {
            List<EnemyTank> toRemove = new ArrayList<>();
            for (EnemyTank enemy : enemies) {
                if (enemy == null || enemy.isDead() || !isValidPosition(enemy.getX(), enemy.getY())) {
                    toRemove.add(enemy);
                } else {
                    enemyCount++;
                }
            }
            enemies.removeAll(toRemove);
        } else {
            enemies = new CopyOnWriteArrayList<>();
        }
        
        if (pus != null) {
            pus.clear();
        } else {
            pus = new CopyOnWriteArrayList<>();
        }
        
        for (EnemyTank enemy : enemies) {
            if (enemy != null) {
                enemy.setGameState(this);
            }
        }
        
        if (p1 != null) {
            p1.setGS(this);
            if (!p1.isAlive() && lives > 0) {
                p1.respawn(192, 528);
            }
        }
        
        if (twoP && p2 != null) {
            p2.setGS(this);
            if (!p2.isAlive() && lives > 0) {
                p2.respawn(384, 528);
            }
        }
        
        restartThreads();
        
        int missingEnemies = Math.min(4 - enemyCount, remEnemies);
        for (int i = 0; i < missingEnemies; i++) {
            spawnEnemy();
        }
        
        if (map == null) {
            map = new GameMap();
            if (lvl == 1) {
                map.loadMap1();
            } else if (lvl == 2) {
                map.loadMap2();
            } else {
                map.genRndMap();
            }
        }
        
        if (eagle == null) {
            eagle = new Eagle(288, 576);
        }
    }
    
    private void restartThreads() {
        if (p1 != null && p1.isAlive()) {
            p1.startThread();
        }
        
        if (twoP && p2 != null && p2.isAlive()) {
            p2.startThread();
        }
        
        for (EnemyTank enemy : enemies) {
            if (enemy != null && enemy.isAlive()) {
                enemy.startThread();
            }
        }
    }
    
    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x <= 592 && y >= 0 && y <= 592;
    }
    
    public int getScore() { return score; }
    public int getLives() { return lives; }
    public int getRemEnemies() { return 20 - spawnedEnemies + enemies.size(); }
    public PlayerTank getP1() { return p1; }
    public PlayerTank getP2() { return p2; }
    public Eagle getEagle() { return eagle; }
    public List<EnemyTank> getEnemies() { return enemies; }
    public List<Bullet> getBullets() { return bullets; }
    public List<PowerUp> getPUs() { return pus; }
    public GameMap getMap() { return map; }
    public boolean isGameOver() { return gameOver; }
    public boolean is2P() { return twoP; }
    public int getLvl() { return lvl; }
    public Map<EnemyTank.TankType, Integer> getKillCnt() { return killCnt; }
    public Map<EnemyTank.TankType, Integer> getTotKillCnt() { return totKillCnt; }
}