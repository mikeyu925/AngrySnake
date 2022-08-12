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
        this.eps = 1e-2; // 允许误差0.01，当两个点的坐标相差小于eps，认为重合

        this.eye_direction = 0;
        if (this.id === 1) this.eye_direction = 2; // 左下角蛇初始朝上,右上角朝下

        // 不同方向蛇眼睛的偏移量  上右下左
        this.eye_dx = [ // x 方向偏移量
            [-1, 1],
            [1, 1],
            [1, -1],
            [-1, -1],
        ];
        this.eye_dy = [ // y 方向偏移量
            [-1, -1],
            [-1, 1],
            [1, 1],
            [1, -1],
        ];
    }

    start() {

    }

    set_direction(d) { // 设置蛇的方向的接口
        this.direction = d;
    }

    check_tail_increasing() { // 检测当前回合，蛇的长度是否增加
        if (this.step <= 10) return true; // 前10回合每回合长度增加1
        else if (this.step % 3 === 1) return true;
        return false;
    }

    next_step() { // 获取蛇的下一步状态
        const d = this.direction; // 获取方向
        this.next_cell = new Cell(this.cells[0].r + this.dr[d], this.cells[0].c + this.dc[d]);
        this.eye_direction = d; // 眼睛方向
        this.direction = -1; // 清空方向
        this.status = "move"; // 变为移动状态
        this.step++;

        const k = this.cells.length;
        for (let i = k; i > 0; i--) { // 每个小球都向后移动一位
            this.cells[i] = JSON.parse(JSON.stringify(this.cells[i - 1])); // 注意要深拷贝
        } // 此时头部相当于多了一个自己的复制 1234 ==> 1123

        // 转成后端判断
        // if (!this.gamemap.check_valid(this.next_cell)) { // 撞到了，去世
        //     this.status = "die";
        // }
    }

    update_move() { // 移动
        const dx = this.next_cell.x - this.cells[0].x; // 计算水平方向差值
        const dy = this.next_cell.y - this.cells[0].y; // 计算竖直方向差值
        const distance = Math.sqrt(dx * dx + dy * dy); // 计算两点的欧氏距离

        if (distance < this.eps) { //小于一定的阈值说明，已经到目标点了（因为浮点数不能直接比较大小）
            this.cells[0] = this.next_cell; // 更新 头点 为 目标点
            this.next_cell = null; // 清空下一个目标点
            this.status = "idle"; // 设置蛇的状态为静止

            if (!this.check_tail_increasing()) { // 蛇没有变长，砍掉尾巴
                this.cells.pop();
            }
        } else {
            const move_distance = this.speed * this.timedelta / 1000; // 两帧之间的距离
            this.cells[0].x += move_distance * dx / distance; // 水平方向需要移动的距离
            this.cells[0].y += move_distance * dy / distance; // 竖直方向需要移动的距离

            if (!this.check_tail_increasing()) { // 如果蛇的长度不变
                const k = this.cells.length;
                const tail = this.cells[k - 1]; // 尾部
                const tail_target = this.cells[k - 2];
                const tail_dx = tail_target.x - tail.x;
                const tail_dy = tail_target.y - tail.y;
                tail.x += move_distance * tail_dx / distance;
                tail.y += move_distance * tail_dy / distance;
            }
        }

    }



    update() { // 每一帧执行一次
        if (this.status === 'move') { // 仅仅在移动状态下才能执行
            this.update_move();
        }
        this.render();
    }

    render() { // 渲染
        const L = this.gamemap.L;
        const ctx = this.gamemap.ctx; // 获取游戏地图画布

        ctx.fillStyle = this.color;
        if (this.status === "die") { // 如果蛇死了，设置颜色为白色
            ctx.fillStyle = "white";
        }

        // 画蛇的身躯，一个一个圆圈
        for (const cell of this.cells) { // of 遍历值
            ctx.beginPath(); // 开启一个路径
            ctx.arc(cell.x * L, cell.y * L, L / 2 * 0.8, 0, Math.PI * 2);
            ctx.fill();
        }

        for (let i = 1; i < this.cells.length; i++) {
            const a = this.cells[i - 1],
                b = this.cells[i];
            if (Math.abs(a.x - b.x) < this.eps && Math.abs(a.y - b.y) < this.eps) continue;

            if (Math.abs(a.x - b.x) < this.eps) { // 竖直方向
                ctx.fillRect((a.x - 0.4) * L, Math.min(a.y, b.y) * L, L * 0.8, Math.abs(a.y - b.y) * L);
            } else { // 水平方向
                ctx.fillRect(Math.min(a.x, b.x) * L, (a.y - 0.4) * L, Math.abs(a.x - b.x) * L, L * 0.8);
            }
        }

        // 画左右眼睛
        if (this.status === "die") { // 如果蛇死了，设置颜色为白色
            for (let i = 0; i < 2; i++) {
                // 计算眼睛偏移量
                const eye_x = L * (this.cells[0].x + this.eye_dx[this.eye_direction][i] * 0.25);
                const eye_y = L * (this.cells[0].y + this.eye_dy[this.eye_direction][i] * 0.25);

                for (let k = 4; k >= 1; k--) { //眼睛画成眩晕状
                    if (k % 2 === 0) ctx.fillStyle = "black";
                    else ctx.fillStyle = "white";
                    ctx.beginPath();
                    ctx.arc(eye_x, eye_y, L * k * 0.03, 0, Math.PI * 2);
                    ctx.fill();
                }

            }
        } else {
            for (let i = 0; i < 2; i++) {
                // 计算眼睛偏移量
                const eye_x = L * (this.cells[0].x + this.eye_dx[this.eye_direction][i] * 0.25);
                const eye_y = L * (this.cells[0].y + this.eye_dy[this.eye_direction][i] * 0.25);

                ctx.fillStyle = "white"; // 眼白
                ctx.beginPath();
                ctx.arc(eye_x, eye_y, L * 0.1, 0, Math.PI * 2);
                ctx.fill();

                ctx.fillStyle = "black"; // 眼珠
                ctx.beginPath();
                ctx.arc(eye_x, eye_y, L * 0.02, 0, Math.PI * 2);
                ctx.fill();
            }
        }
    }

}