package com.as.backend.consumer.utils;

import java.util.Arrays;
import java.util.Random;

public class Game {
    final private Integer rows;
    final private Integer cols;
    final private Integer inner_walls_count;
    final private int [][] walls;

    final private static int [] dx = {-1,0,1,0},dy = {0,1,0,-1};

    public  Game(int r,int c,int cnt){
        this.rows = r;
        this.cols = c;
        this.inner_walls_count = cnt;
        this.walls = new int[r][c];
    }

    public int[][] getGameMap(){
        return this.walls;
    }

    private boolean check_connectivity(int sx,int sy,int tx,int ty){
        if (sx == tx && sy == ty) return true;
        this.walls[sx][sy] = 1;

        for (int i = 0;i < 4;i++){
            int x = sx + dx[i],y = sy + dy[i];
            if (x < 0 || x >= this.rows || y < 0 || y >= this.cols || this.walls[x][y] == 1) continue;
            if (check_connectivity(x,y,tx,ty)){
                walls[sx][sy] = 0;
                return true;
            }
        }
        walls[sx][sy] = 0;
        return false;
    }

    private boolean drawGameMap(){  // 画地图
        // 初始化
        for (int i = 0;i < this.rows;i++)
            Arrays.fill(this.walls[i],0);

        // 画四周围墙
        for (int r = 0;r < this.rows;r++){
            this.walls[r][0] = this.walls[r][this.cols-1] = 1;
        }
        for (int c = 0;c < this.cols;c++){
            this.walls[0][c] = this.walls[this.rows-1][c] = 1;
        }
        Random random = new Random();
        // 内部随机产生inner_walls_count个中心对称墙体
        for (int i = 0;i < this.inner_walls_count / 2;i++){
            int r,c;
            do {
                r = random.nextInt(this.rows);
                c = random.nextInt(this.cols);
            }while (this.walls[r][c] == 1 || this.walls[this.rows - 1 - r][this.cols - 1 - c] == 1 || (r == this.rows - 2 && c == 1) || (r == 1 && c == this.cols - 2));

            this.walls[r][c] = this.walls[this.rows - 1 - r][this.cols - 1 - c] = 1;
        }
        return check_connectivity(this.rows-2,1,1,this.cols-2);
    }

    public void createGameMap(){  // 创建地图
        while (!drawGameMap());
    }
}
