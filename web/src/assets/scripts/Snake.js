// 由于蛇需要实时刷新,因此要需要继承于GameObject
import { GameObject } from "./GameObject";
import { Cell } from "./Cell";

export class Snake extends GameObject {
    // 传进来蛇的信息 和 游戏地图
    constructor(info, gamemap) {
        super();

        this.id = info.id; // 蛇的id
        this.color = info.color; // 颜色
        this.gamemap = gamemap; // 地图引用,用于调用函数和地图参数

        this.cells = [new Cell(info.r, info.c)]; // 存放蛇的身体,cells[0]存放蛇头
        this.next_cell = null; // 下一步的目标位置

        this.speed = 5; // 每秒钟走 speed 个格子
        this.direction = -1; // -1表示没有指令 0~3 表示上右下左
        this.status = "idle"; // 蛇的状态: idle 静止 move 移动 die 死亡

        this.dr = [-1, 0, 1, 0]; // 四个方向 行的偏移量
        this.dc = [0, 1, 0, -1]; // 四个方向 列的偏移量

        this.step = 0; // 蛇的回合数
    }

    start() {

    }

    set_direction(d) { // 设置蛇的方向的接口
        this.direction = d;
    }

    next_step() { // 获取蛇的下一步状态
        const d = this.direction; // 获取方向
        this.next_cell = new Cell(this.cells[0].r + this.dr[d], this.cells[0] + this.dc[d]);
        this.direction = -1; // 清空方向
        this.status = "move";
    }

    update_move() {
        // this.cells[0].x += this.speed * this.timedelta / 1000; // this.timedelta 记录的是两帧之间的间隔


    }

    update() { // 每一帧执行一次
        if (this.status === 'move') {
            this.update_move();
        }
        this.render();
    }

    render() {
        const L = this.gamemap.L;
        const ctx = this.gamemap.ctx;

        ctx.fillStyle = this.color;
        for (const cell of this.cells) { // of 遍历值
            ctx.beginPath(); // 开启一个路径
            ctx.arc(cell.x * L, cell.y * L, L / 2, 0, Math.PI * 2);
            ctx.fill();
        }
    }
}