import java.awt.*;
import java.io.Serializable;
import java.util.Random;

public class GameMap implements Serializable {
    public enum TileType {
        EMPTY, BRICK, STEEL, WATER, TREE, ICE, GRASS
    }
    
    public enum HalfType {
        NONE, TOP, BOTTOM, LEFT, RIGHT
    }
    
    private static final int MAP_W = 13;
    private static final int MAP_H = 13;
    private static final int TILE_SZ = 48;
    
    private TileType[][] map;
    private HalfType[][] halfTypes;
    private int[][] brickHP;
    private boolean eagleSteel = false;
    private long steelEnd = 0;
    
    public GameMap() {
        map = new TileType[MAP_H][MAP_W];
        halfTypes = new HalfType[MAP_H][MAP_W];
        brickHP = new int[MAP_H][MAP_W];
        initEmpty();
    }
    
    private void initEmpty() {
        for (int i = 0; i < MAP_H; i++) {
            for (int j = 0; j < MAP_W; j++) {
                map[i][j] = TileType.EMPTY;
                halfTypes[i][j] = HalfType.NONE;
                brickHP[i][j] = 0xFFFF;
            }
        }
    }
    
    public void loadMap1() {
        initEmpty();
        
        int eX = 6;
        int eY = 12;
        map[eY - 1][eX - 1] = TileType.BRICK;
        halfTypes[eY - 1][eX - 1] = HalfType.BOTTOM;
        map[eY - 1][eX] = TileType.BRICK;
        halfTypes[eY - 1][eX] = HalfType.BOTTOM;
        map[eY - 1][eX + 1] = TileType.BRICK;
        halfTypes[eY - 1][eX + 1] = HalfType.BOTTOM;
        map[eY][eX - 1] = TileType.BRICK;
        map[eY][eX + 1] = TileType.BRICK;
        
        map[0][3] = TileType.STEEL;
        map[0][7] = TileType.STEEL;
        map[1][0] = TileType.GRASS;
        map[1][1] = TileType.BRICK;
        map[1][3] = TileType.STEEL;
        map[1][7] = TileType.BRICK;
        map[1][9] = TileType.BRICK;
        map[1][11] = TileType.BRICK;
        map[2][0] = TileType.GRASS;
        map[2][1] = TileType.BRICK;
        map[2][3] = TileType.GRASS;
        map[2][4] = TileType.GRASS;
        map[2][5] = TileType.GRASS;
        map[2][7] = TileType.BRICK;
        map[2][9] = TileType.BRICK;
        map[2][10] = TileType.STEEL;
        map[2][11] = TileType.BRICK;
        map[3][0] = TileType.GRASS;
        map[3][3] = TileType.BRICK;
        map[3][6] = TileType.BRICK;
        map[3][7] = TileType.BRICK;
        map[3][9] = TileType.STEEL;
        map[3][12] = TileType.ICE;
        map[4][0] = TileType.GRASS;
        map[4][3] = TileType.BRICK;
        map[4][5] = TileType.BRICK;
        map[4][9] = TileType.BRICK;
        map[4][10] = TileType.GRASS;
        map[4][11] = TileType.BRICK;
        map[4][12] = TileType.STEEL;
        map[5][0] = TileType.GRASS;
        map[5][1] = TileType.GRASS;
        map[5][5] = TileType.BRICK;
        map[5][8] = TileType.STEEL;
        map[5][9] = TileType.BRICK;
        map[5][10] = TileType.GRASS;
        map[6][1] = TileType.BRICK;
        map[6][2] = TileType.BRICK;
        halfTypes[6][2] = HalfType.BOTTOM;
        map[6][3] = TileType.BRICK;
        halfTypes[6][3] = HalfType.BOTTOM;
        map[6][5] = TileType.GRASS;
        map[6][6] = TileType.GRASS;
        map[6][7] = TileType.STEEL;
        map[6][8] = TileType.BRICK;
        map[6][10] = TileType.GRASS;
        map[6][11] = TileType.BRICK;
        halfTypes[6][11] = HalfType.RIGHT;
        map[7][3] = TileType.STEEL;
        map[7][4] = TileType.GRASS;
        map[7][5] = TileType.BRICK;
        map[7][7] = TileType.BRICK;
        map[7][9] = TileType.BRICK;
        map[7][11] = TileType.BRICK;
        map[8][1] = TileType.BRICK;
        map[8][2] = TileType.WATER;
        map[8][3] = TileType.STEEL;
        map[8][5] = TileType.BRICK;
        map[8][7] = TileType.BRICK;
        map[8][9] = TileType.WATER;
        map[8][10] = TileType.WATER;
        map[8][11] = TileType.BRICK;
        map[8][12] = TileType.WATER;
        map[9][0] = TileType.WATER;
        map[9][1] = TileType.BRICK;
        map[9][3] = TileType.BRICK;
        map[9][5] = TileType.BRICK;
        map[9][6] = TileType.BRICK;
        map[9][7] = TileType.BRICK;
        map[9][9] = TileType.BRICK;
        map[9][10] = TileType.STEEL;
        map[9][11] = TileType.BRICK;
        map[9][12] = TileType.WATER;
        map[10][1] = TileType.BRICK;
        map[10][3] = TileType.BRICK;
        map[10][5] = TileType.BRICK;
        map[10][6] = TileType.BRICK;
        map[10][7] = TileType.BRICK;
        map[10][12] = TileType.WATER;
        map[11][0] = TileType.WATER;
        map[11][1] = TileType.BRICK;
        map[11][9] = TileType.BRICK;
        map[11][11] = TileType.BRICK;
        map[11][12] = TileType.WATER;
        map[12][0] = TileType.WATER;
        map[12][1] = TileType.BRICK;
        map[12][3] = TileType.BRICK;
        map[12][9] = TileType.BRICK;
        map[12][10] = TileType.BRICK;
        map[12][11] = TileType.BRICK;
    }
    
    public void loadMap2() {
        initEmpty();

        int eX = 6;
        int eY = 12;
        map[eY - 1][eX - 1] = TileType.BRICK;
        map[eY - 1][eX] = TileType.BRICK;
        map[eY - 1][eX + 1] = TileType.BRICK;
        map[eY][eX - 1] = TileType.BRICK;
        map[eY][eX + 1] = TileType.BRICK;

        map[0][0] = TileType.TREE;
        map[0][1] = TileType.TREE;
        map[0][4] = TileType.TREE;
        map[0][5] = TileType.TREE;
        map[0][6] = TileType.TREE;
        map[0][7] = TileType.TREE;
        map[0][11] = TileType.TREE;
        map[0][12] = TileType.TREE;
        map[2][0] = TileType.STEEL;
        map[2][1] = TileType.STEEL;
        map[2][2] = TileType.STEEL;
        map[2][3] = TileType.BRICK;
        map[2][4] = TileType.TREE;
        map[2][5] = TileType.STEEL;
        map[2][6] = TileType.STEEL;
        map[2][7] = TileType.BRICK;
        map[2][8] = TileType.TREE;
        map[2][9] = TileType.STEEL;
        map[2][10] = TileType.STEEL;
        map[2][11] = TileType.BRICK;
        map[2][12] = TileType.TREE;
        map[3][0] = TileType.TREE;
        map[3][1] = TileType.TREE;
        map[3][2] = TileType.STEEL;
        map[3][3] = TileType.BRICK;
        map[3][4] = TileType.TREE;
        map[3][5] = TileType.TREE;
        map[3][6] = TileType.STEEL;
        map[3][7] = TileType.BRICK;
        map[3][8] = TileType.TREE;
        map[3][9] = TileType.TREE;
        map[3][10] = TileType.STEEL;
        map[3][11] = TileType.BRICK;
        map[3][12] = TileType.TREE;
        map[4][0] = TileType.TREE;
        map[4][1] = TileType.TREE;
        map[4][2] = TileType.STEEL;
        map[4][3] = TileType.BRICK;
        map[4][4] = TileType.TREE;
        map[4][5] = TileType.TREE;
        map[4][6] = TileType.STEEL;
        map[4][7] = TileType.BRICK;
        map[4][8] = TileType.TREE;
        map[4][9] = TileType.TREE;
        map[4][10] = TileType.STEEL;
        map[4][11] = TileType.BRICK;
        map[4][12] = TileType.TREE;
        map[5][0] = TileType.STEEL;
        map[5][1] = TileType.STEEL;
        map[5][2] = TileType.STEEL;
        map[5][3] = TileType.BRICK;
        map[5][4] = TileType.TREE;
        map[5][5] = TileType.TREE;
        map[5][6] = TileType.STEEL;
        map[5][7] = TileType.BRICK;
        map[5][8] = TileType.TREE;
        map[5][9] = TileType.TREE;
        map[5][10] = TileType.STEEL;
        map[5][11] = TileType.BRICK;
        map[5][12] = TileType.TREE;
        map[6][0] = TileType.STEEL;
        map[6][1] = TileType.BRICK;
        map[6][2] = TileType.TREE;
        map[6][3] = TileType.TREE;
        map[6][4] = TileType.TREE;
        map[6][5] = TileType.TREE;
        map[6][6] = TileType.STEEL;
        map[6][7] = TileType.BRICK;
        map[6][8] = TileType.TREE;
        map[6][9] = TileType.TREE;
        map[6][10] = TileType.STEEL;
        map[6][11] = TileType.BRICK;
        map[6][12] = TileType.TREE;
        map[7][0] = TileType.STEEL;
        map[7][1] = TileType.BRICK;
        map[7][2] = TileType.TREE;
        map[7][3] = TileType.TREE;
        map[7][4] = TileType.TREE;
        map[7][5] = TileType.TREE;
        map[7][6] = TileType.STEEL;
        map[7][7] = TileType.BRICK;
        map[7][8] = TileType.TREE;
        map[7][9] = TileType.TREE;
        map[7][10] = TileType.STEEL;
        map[7][11] = TileType.BRICK;
        map[7][12] = TileType.TREE;
        map[8][0] = TileType.STEEL;
        map[8][1] = TileType.STEEL;
        map[8][2] = TileType.STEEL;
        map[8][3] = TileType.BRICK;
        map[8][4] = TileType.TREE;
        map[8][5] = TileType.STEEL;
        map[8][6] = TileType.STEEL;
        map[8][7] = TileType.STEEL;
        map[8][8] = TileType.BRICK;
        map[8][9] = TileType.STEEL;
        map[8][10] = TileType.STEEL;
        map[8][11] = TileType.STEEL;
        map[8][12] = TileType.BRICK;
        map[9][0] = TileType.TREE;
        map[9][1] = TileType.TREE;
        map[9][11] = TileType.TREE;
        map[9][12] = TileType.TREE;
        map[10][0] = TileType.TREE;
        map[10][1] = TileType.TREE;
        map[10][11] = TileType.TREE;
        map[10][12] = TileType.TREE;
        map[11][1] = TileType.WATER;
        map[11][2] = TileType.WATER;
        map[11][10] = TileType.WATER;
        map[11][11] = TileType.WATER;
        map[11][12] = TileType.WATER;
        map[12][0] = TileType.WATER;
        map[12][1] = TileType.WATER;
        map[12][2] = TileType.WATER;
        map[12][10] = TileType.WATER;
        map[12][11] = TileType.WATER;
        map[12][12] = TileType.WATER;
    }
    
    public void genRndMap() {
        initEmpty();
        Random rnd = new Random();
        
        int eX = 6;
        int eY = 12;
        
        map[eY - 1][eX - 1] = TileType.BRICK;
        halfTypes[eY - 1][eX - 1] = HalfType.BOTTOM;
        map[eY - 1][eX] = TileType.BRICK;
        halfTypes[eY - 1][eX] = HalfType.BOTTOM;
        map[eY - 1][eX + 1] = TileType.BRICK;
        halfTypes[eY - 1][eX + 1] = HalfType.BOTTOM;
        map[eY][eX - 1] = TileType.BRICK;
        map[eY][eX + 1] = TileType.BRICK;
        
        int numStr = 5 + rnd.nextInt(3);
        for (int i = 0; i < numStr; i++) {
            int strType = rnd.nextInt(6);
            int x = rnd.nextInt(MAP_W - 4) + 1;
            int y = rnd.nextInt(MAP_H - 4) + 1;
            
            switch (strType) {
                case 0: makeBrick(x, y, rnd); break;
                case 1: makeSteel(x, y, rnd); break;
                case 2: makeWater(x, y, rnd); break;
                case 3: makeForest(x, y, rnd); break;
                case 4: makeIce(x, y, rnd); break;
                case 5: makeMixed(x, y, rnd); break;
            }
        }
        
        clearSpawns();
    }
    
    private void makeBrick(int x, int y, Random rnd) {
        int w = 3 + rnd.nextInt(3);
        int h = 2 + rnd.nextInt(3);
        boolean horz = rnd.nextBoolean();
        
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int px = horz ? x + j : x + i;
                int py = horz ? y + i : y + j;
                
                if (px < MAP_W && py < MAP_H && px >= 0 && py >= 0) {
                    map[py][px] = TileType.BRICK;
                    if (rnd.nextInt(100) < 20) {
                        HalfType[] types = {HalfType.TOP, HalfType.BOTTOM, 
                                          HalfType.LEFT, HalfType.RIGHT};
                        halfTypes[py][px] = types[rnd.nextInt(types.length)];
                    }
                }
            }
        }
    }
    
    private void makeSteel(int x, int y, Random rnd) {
        int sz = 2 + rnd.nextInt(2);
        for (int i = 0; i < sz; i++) {
            for (int j = 0; j < sz; j++) {
                if (x + j < MAP_W && y + i < MAP_H) {
                    map[y + i][x + j] = TileType.STEEL;
                }
            }
        }
    }
    
    private void makeWater(int x, int y, Random rnd) {
        int w = 2 + rnd.nextInt(3);
        int h = 2 + rnd.nextInt(3);
        
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                if (x + j < MAP_W && y + i < MAP_H) {
                    map[y + i][x + j] = TileType.WATER;
                }
            }
        }
    }
    
    private void makeForest(int x, int y, Random rnd) {
        int sz = 3 + rnd.nextInt(3);
        for (int i = 0; i < sz; i++) {
            for (int j = 0; j < sz; j++) {
                if (x + j < MAP_W && y + i < MAP_H && rnd.nextInt(100) < 70) {
                    map[y + i][x + j] = rnd.nextBoolean() ? TileType.TREE : TileType.GRASS;
                }
            }
        }
    }
    
    private void makeIce(int x, int y, Random rnd) {
        int w = 3 + rnd.nextInt(2);
        int h = 2 + rnd.nextInt(2);
        
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                if (x + j < MAP_W && y + i < MAP_H) {
                    map[y + i][x + j] = TileType.ICE;
                }
            }
        }
    }
    
    private void makeMixed(int x, int y, Random rnd) {
        int sz = 4;
        TileType[] types = {TileType.BRICK, TileType.STEEL, TileType.WATER};
        
        for (int i = 0; i < sz; i++) {
            for (int j = 0; j < sz; j++) {
                if (x + j < MAP_W && y + i < MAP_H) {
                    if ((i == 0 || i == sz-1 || j == 0 || j == sz-1) && rnd.nextInt(100) < 80) {
                        map[y + i][x + j] = types[rnd.nextInt(types.length)];
                    }
                }
            }
        }
    }
    
    private void clearSpawns() {
        for (int i = 10; i < MAP_H; i++) {
            for (int j = 3; j < 6; j++) {
                if (!isEagleWall(i, j)) {
                    map[i][j] = TileType.EMPTY;
                    halfTypes[i][j] = HalfType.NONE;
                }
            }
        }
        
        for (int i = 10; i < MAP_H; i++) {
            for (int j = 7; j < 10; j++) {
                if (!isEagleWall(i, j)) {
                    map[i][j] = TileType.EMPTY;
                    halfTypes[i][j] = HalfType.NONE;
                }
            }
        }
        
        map[0][0] = TileType.EMPTY;
        map[0][1] = TileType.EMPTY;
        map[1][0] = TileType.EMPTY;
        map[1][1] = TileType.EMPTY;
        
        map[0][6] = TileType.EMPTY;
        map[0][5] = TileType.EMPTY;
        map[0][7] = TileType.EMPTY;
        map[1][5] = TileType.EMPTY;
        map[1][6] = TileType.EMPTY;
        map[1][7] = TileType.EMPTY;
        
        map[0][11] = TileType.EMPTY;
        map[0][12] = TileType.EMPTY;
        map[1][11] = TileType.EMPTY;
        map[1][12] = TileType.EMPTY;
    }
    
    private boolean isEagleWall(int row, int col) {
        return (row == 11 && col >= 5 && col <= 7) || 
               (row == 12 && (col == 5 || col == 7));
    }
    
    public void update() {
        if (eagleSteel && System.currentTimeMillis() > steelEnd) {
            restoreEagle();
        }
    }
    
    public void makeEagleSteel(int dur) {
        eagleSteel = true;
        steelEnd = System.currentTimeMillis() + dur;
        
        int eX = 6;
        int eY = 12;
        
        if (map[eY - 1][eX - 1] == TileType.BRICK) {
            map[eY - 1][eX - 1] = TileType.STEEL;
        }
        if (map[eY - 1][eX] == TileType.BRICK) {
            map[eY - 1][eX] = TileType.STEEL;
        }
        if (map[eY - 1][eX + 1] == TileType.BRICK) {
            map[eY - 1][eX + 1] = TileType.STEEL;
        }
        if (map[eY][eX - 1] == TileType.BRICK) {
            map[eY][eX - 1] = TileType.STEEL;
        }
        if (map[eY][eX + 1] == TileType.BRICK) {
            map[eY][eX + 1] = TileType.STEEL;
        }
    }
    
    public void removeEagle() {
        int eX = 6;
        int eY = 12;
        
        map[eY - 1][eX - 1] = TileType.EMPTY;
        map[eY - 1][eX] = TileType.EMPTY;
        map[eY - 1][eX + 1] = TileType.EMPTY;
        map[eY][eX - 1] = TileType.EMPTY;
        map[eY][eX + 1] = TileType.EMPTY;
        
        halfTypes[eY - 1][eX - 1] = HalfType.NONE;
        halfTypes[eY - 1][eX] = HalfType.NONE;
        halfTypes[eY - 1][eX + 1] = HalfType.NONE;
    }
    
    private void restoreEagle() {
        eagleSteel = false;
        int eX = 6;
        int eY = 12;
        
        if (map[eY - 1][eX - 1] == TileType.STEEL) {
            map[eY - 1][eX - 1] = TileType.BRICK;
        }
        if (map[eY - 1][eX] == TileType.STEEL) {
            map[eY - 1][eX] = TileType.BRICK;
        }
        if (map[eY - 1][eX + 1] == TileType.STEEL) {
            map[eY - 1][eX + 1] = TileType.BRICK;
        }
        if (map[eY][eX - 1] == TileType.STEEL) {
            map[eY][eX - 1] = TileType.BRICK;
        }
        if (map[eY][eX + 1] == TileType.STEEL) {
            map[eY][eX + 1] = TileType.BRICK;
        }
    }
    
    private void drawBrickPat(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(new Color(160, 82, 45));
        
        int bH = 12;
        int bW = 12;
        
        for (int row = 0; row < h; row += bH) {
            for (int col = 0; col < w; col += bW * 2) {
                int off = (row / bH) % 2 == 0 ? 0 : bW;
                g.drawLine(x + col + off, y + row, x + col + off, y + row + bH);
            }
            g.drawLine(x, y + row, x + w, y + row);
        }
    }
    
    public boolean canMove(Rectangle bounds) {
        int startX = bounds.x / TILE_SZ;
        int startY = bounds.y / TILE_SZ;
        int endX = (bounds.x + bounds.width - 1) / TILE_SZ;
        int endY = (bounds.y + bounds.height - 1) / TILE_SZ;
        
        for (int i = startY; i <= endY && i < MAP_H; i++) {
            for (int j = startX; j <= endX && j < MAP_W; j++) {
                if (i >= 0 && j >= 0 && i < MAP_H && j < MAP_W) {
                    TileType tile = map[i][j];
                    if (tile == TileType.BRICK) {
                        if (brickHP[i][j] > 0 && brickHP[i][j] < 0xFFFF) {
                            if (!canPassDmgBrick(bounds, j * TILE_SZ, i * TILE_SZ, brickHP[i][j])) {
                                return false;
                            }
                        } else if (brickHP[i][j] == 0xFFFF) {
                            if (halfTypes[i][j] != HalfType.NONE) {
                                if (!canPassHalf(bounds, j * TILE_SZ, i * TILE_SZ, halfTypes[i][j])) {
                                    return false;
                                }
                            } else {
                                return false;
                            }
                        }
                    } else if (tile == TileType.STEEL || tile == TileType.WATER) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    private boolean canPassDmgBrick(Rectangle bounds, int tX, int tY, int hp) {
        int subSz = TILE_SZ / 4;
        
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int bitIdx = i * 4 + j;
                if ((hp & (1 << bitIdx)) != 0) {
                    Rectangle brickPart = new Rectangle(tX + j * subSz, tY + i * subSz, subSz, subSz);
                    if (bounds.intersects(brickPart)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    private boolean canPassHalf(Rectangle bounds, int tX, int tY, HalfType type) {
        switch (type) {
            case TOP:
                Rectangle topHalf = new Rectangle(tX, tY, TILE_SZ, TILE_SZ/2);
                return !bounds.intersects(topHalf);
                
            case BOTTOM:
                Rectangle btmHalf = new Rectangle(tX, tY + TILE_SZ/2, TILE_SZ, TILE_SZ/2);
                return !bounds.intersects(btmHalf);
                
            case LEFT:
                Rectangle leftHalf = new Rectangle(tX, tY, TILE_SZ/2, TILE_SZ);
                return !bounds.intersects(leftHalf);
                
            case RIGHT:
                Rectangle rightHalf = new Rectangle(tX + TILE_SZ/2, tY, TILE_SZ/2, TILE_SZ);
                return !bounds.intersects(rightHalf);
                
            default:
                return true;
        }
    }
    
    public boolean checkBulletHit(Bullet bullet) {
        int tX = bullet.getX() / TILE_SZ;
        int tY = bullet.getY() / TILE_SZ;
        
        if (tX >= 0 && tX < MAP_W && tY >= 0 && tY < MAP_H) {
            TileType tile = map[tY][tX];
            
            if (tile == TileType.BRICK) {
                if (halfTypes[tY][tX] != HalfType.NONE) {
                    if (hitHalf(bullet, tX * TILE_SZ, tY * TILE_SZ, halfTypes[tY][tX])) {
                        map[tY][tX] = TileType.EMPTY;
                        halfTypes[tY][tX] = HalfType.NONE;
                        return true;
                    }
                    return false;
                } else {
                    dmgBrick(tX, tY, bullet);
                    return true;
                }
            } else if (tile == TileType.STEEL) {
                if (bullet.isPowerful()) {
                    map[tY][tX] = TileType.EMPTY;
                }
                return true;
            }
        }
        return false;
    }
    
    private void dmgBrick(int tX, int tY, Bullet bullet) {
        Direction bDir = bullet.getDirection();
        int dmg = bullet.isPowerful() ? 2 : 1;
        int curHP = brickHP[tY][tX];
        int bitsRem = 0;
        
        switch (bDir) {
            case UP:
                for (int row = 3; row >= 0 && dmg > 0; row--) {
                    boolean rowHas = false;
                    for (int col = 0; col < 4; col++) {
                        int bit = row * 4 + col;
                        if ((curHP & (1 << bit)) != 0) {
                            rowHas = true;
                            break;
                        }
                    }
                    
                    if (rowHas) {
                        for (int col = 0; col < 4; col++) {
                            int bit = row * 4 + col;
                            bitsRem |= (1 << bit);
                        }
                        dmg--;
                    }
                }
                break;
                
            case DOWN:
                for (int row = 0; row < 4 && dmg > 0; row++) {
                    boolean rowHas = false;
                    for (int col = 0; col < 4; col++) {
                        int bit = row * 4 + col;
                        if ((curHP & (1 << bit)) != 0) {
                            rowHas = true;
                            break;
                        }
                    }
                    
                    if (rowHas) {
                        for (int col = 0; col < 4; col++) {
                            int bit = row * 4 + col;
                            bitsRem |= (1 << bit);
                        }
                        dmg--;
                    }
                }
                break;
                
            case LEFT:
                for (int col = 3; col >= 0 && dmg > 0; col--) {
                    boolean colHas = false;
                    for (int row = 0; row < 4; row++) {
                        int bit = row * 4 + col;
                        if ((curHP & (1 << bit)) != 0) {
                            colHas = true;
                            break;
                        }
                    }
                    
                    if (colHas) {
                        for (int row = 0; row < 4; row++) {
                            int bit = row * 4 + col;
                            bitsRem |= (1 << bit);
                        }
                        dmg--;
                    }
                }
                break;
                
            case RIGHT:
                for (int col = 0; col < 4 && dmg > 0; col++) {
                    boolean colHas = false;
                    for (int row = 0; row < 4; row++) {
                        int bit = row * 4 + col;
                        if ((curHP & (1 << bit)) != 0) {
                            colHas = true;
                            break;
                        }
                    }
                    
                    if (colHas) {
                        for (int row = 0; row < 4; row++) {
                            int bit = row * 4 + col;
                            bitsRem |= (1 << bit);
                        }
                        dmg--;
                    }
                }
                break;
        }
        
        brickHP[tY][tX] = curHP & ~bitsRem;
        
        if (brickHP[tY][tX] == 0) {
            map[tY][tX] = TileType.EMPTY;
            halfTypes[tY][tX] = HalfType.NONE;
        }
    }
    
    private boolean hitHalf(Bullet bullet, int tX, int tY, HalfType type) {
        Rectangle bBounds = bullet.getBounds();
        
        switch (type) {
            case TOP:
                return bBounds.intersects(new Rectangle(tX, tY, TILE_SZ, TILE_SZ/2));
            case BOTTOM:
                return bBounds.intersects(new Rectangle(tX, tY + TILE_SZ/2, TILE_SZ, TILE_SZ/2));
            case LEFT:
                return bBounds.intersects(new Rectangle(tX, tY, TILE_SZ/2, TILE_SZ));
            case RIGHT:
                return bBounds.intersects(new Rectangle(tX + TILE_SZ/2, tY, TILE_SZ/2, TILE_SZ));
            default:
                return true;
        }
    }
    
    public void draw(Graphics2D g) {
        for (int i = 0; i < MAP_H; i++) {
            for (int j = 0; j < MAP_W; j++) {
                int x = j * TILE_SZ;
                int y = i * TILE_SZ;
                
                switch (map[i][j]) {
                    case BRICK:
                        drawBrick(g, x, y, i, j);
                        break;
                    case STEEL:
                        g.setColor(Color.LIGHT_GRAY);
                        g.fillRect(x, y, TILE_SZ, TILE_SZ);
                        g.setColor(Color.GRAY);
                        for (int k = 0; k < TILE_SZ; k += 8) {
                            g.drawLine(x + k, y, x + k, y + TILE_SZ);
                            g.drawLine(x, y + k, x + TILE_SZ, y + k);
                        }
                        break;
                    case WATER:
                        g.setColor(new Color(0, 0, 139));
                        g.fillRect(x, y, TILE_SZ, TILE_SZ);
                        g.setColor(new Color(65, 105, 225));
                        g.drawOval(x + 8, y + 8, 12, 12);
                        g.drawOval(x + 28, y + 28, 12, 12);
                        g.drawOval(x + 8, y + 28, 12, 12);
                        g.drawOval(x + 28, y + 8, 12, 12);
                        break;
                    case TREE:
                        g.setColor(new Color(34, 139, 34));
                        g.fillRect(x, y, TILE_SZ, TILE_SZ);
                        g.setColor(new Color(0, 100, 0));
                        for (int m = 0; m < 3; m++) {
                            for (int n = 0; n < 3; n++) {
                                g.fillOval(x + m * 16 + 4, y + n * 16 + 4, 8, 8);
                            }
                        }
                        break;
                    case ICE:
                        g.setColor(new Color(175, 238, 238));
                        g.fillRect(x, y, TILE_SZ, TILE_SZ);
                        g.setColor(Color.WHITE);
                        g.drawLine(x + 12, y + 12, x + 36, y + 36);
                        g.drawLine(x + 36, y + 12, x + 12, y + 36);
                        break;
                    case GRASS:
                        g.setColor(new Color(124, 252, 0));
                        g.fillRect(x, y, TILE_SZ, TILE_SZ);
                        g.setColor(new Color(34, 139, 34));
                        for (int k = 6; k < TILE_SZ; k += 12) {
                            g.drawLine(x + k, y + 6, x + k, y + TILE_SZ - 6);
                        }
                        break;
                }
            }
        }
    }
    
    private void drawBrick(Graphics2D g, int x, int y, int row, int col) {
        if (halfTypes[row][col] != HalfType.NONE) {
            g.setColor(new Color(139, 69, 19));
            
            switch (halfTypes[row][col]) {
                case TOP:
                    g.fillRect(x, y, TILE_SZ, TILE_SZ/2);
                    drawBrickPat(g, x, y, TILE_SZ, TILE_SZ/2);
                    break;
                case BOTTOM:
                    g.fillRect(x, y + TILE_SZ/2, TILE_SZ, TILE_SZ/2);
                    drawBrickPat(g, x, y + TILE_SZ/2, TILE_SZ, TILE_SZ/2);
                    break;
                case LEFT:
                    g.fillRect(x, y, TILE_SZ/2, TILE_SZ);
                    drawBrickPat(g, x, y, TILE_SZ/2, TILE_SZ);
                    break;
                case RIGHT:
                    g.fillRect(x + TILE_SZ/2, y, TILE_SZ/2, TILE_SZ);
                    drawBrickPat(g, x + TILE_SZ/2, y, TILE_SZ/2, TILE_SZ);
                    break;
            }
        } else {
            int hp = brickHP[row][col];
            if (hp == 0xFFFF) {
                g.setColor(new Color(139, 69, 19));
                g.fillRect(x, y, TILE_SZ, TILE_SZ);
                drawBrickPat(g, x, y, TILE_SZ, TILE_SZ);
            } else if (hp > 0) {
                int subSz = TILE_SZ / 4;
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        int bitIdx = i * 4 + j;
                        if ((hp & (1 << bitIdx)) != 0) {
                            g.setColor(new Color(139, 69, 19));
                            g.fillRect(x + j * subSz, y + i * subSz, subSz, subSz);
                            
                            g.setColor(new Color(160, 82, 45));
                            if (i % 2 == 0) {
                                g.drawLine(x + j * subSz, y + i * subSz + subSz/2, 
                                         x + (j+1) * subSz, y + i * subSz + subSz/2);
                            }
                            if ((i + j) % 2 == 0) {
                                g.drawLine(x + j * subSz + subSz/2, y + i * subSz, 
                                         x + j * subSz + subSz/2, y + (i+1) * subSz);
                            }
                        }
                    }
                }
            }
        }
    }
    
    public boolean isHideTile(int x, int y) {
        int tX = x / TILE_SZ;
        int tY = y / TILE_SZ;
        
        if (tX >= 0 && tX < MAP_W && tY >= 0 && tY < MAP_H) {
            TileType tile = map[tY][tX];
            return tile == TileType.TREE || tile == TileType.GRASS;
        }
        return false;
    }
    
    public boolean isIceTile(int x, int y) {
        int tX = x / TILE_SZ;
        int tY = y / TILE_SZ;
        
        if (tX >= 0 && tX < MAP_W && tY >= 0 && tY < MAP_H) {
            return map[tY][tX] == TileType.ICE;
        }
        return false;
    }
    
    public void drawVegOverlay(Graphics2D g) {
        for (int i = 0; i < MAP_H; i++) {
            for (int j = 0; j < MAP_W; j++) {
                int x = j * TILE_SZ;
                int y = i * TILE_SZ;
                
                if (map[i][j] == TileType.TREE) {
                    g.setColor(new Color(0, 100, 0, 150));
                    g.fillOval(x + 1, y + 1, TILE_SZ - 2, TILE_SZ - 2);
                } else if (map[i][j] == TileType.GRASS) {
                    g.setColor(new Color(34, 139, 34, 100));
                    for (int k = 3; k < TILE_SZ; k += 3) {
                        g.drawLine(x + k, y + 3, x + k, y + TILE_SZ - 3);
                    }
                }
            }
        }
    }
}