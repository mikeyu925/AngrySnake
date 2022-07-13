// 如果是export 需要用 {} 括起来，如果是export default 不需要{},每个文件只能由一个default
// GameMap 游戏地图组件
import { GameObject } from "./GameObject";
import { Wall } from "./Wall";


export class GameMap extends GameObject {
    // ctx 画布,parent 画布的父元素，画布的父元素，用来动态修改画布长宽
    constructor(ctx, parent) {
        super(); // 调用基类构造函数
        this.ctx = ctx;
        this.parent = parent;

        this.L = 0; // 格子的长度
        this.rows = 13; // 游戏地图的行数(行方向格子数) 
        this.cols = 13; // 游戏地图的列数(列方向格子数)

        this.walls = []; // 存储墙的列表
        this.inner_wall_count = 30; // 内部障碍物数量
    }

    check_connectivity(g, sx, sy, tx, ty) { // 深度优先搜索 判断是否联通
        if (sx == tx && sy == ty) {
            return true;
        }
        g[sx][sy] = true;
        let dx = [-1, 0, 1, 0],
            dy = [0, 1, 0, -1];
        for (let i = 0; i < 4; i++) {
            let x = sx + dx[i],
                y = sy + dy[i];
            if (!g[x][y] && this.check_connectivity(g, x, y, tx, ty)) return true;
        }
        return false;
    }

    create_walls() {
        const has_wall = []; // 标记是否有墙
        // 初始化
        for (let r = 0; r < this.rows; r++) {
            has_wall[r] = [];
            for (let c = 0; c < this.cols; c++) {
                has_wall[r][c] = false;
            }
        }
        // 给四周加上障碍物
        for (let r = 0; r < this.rows; r++) {
            has_wall[r][0] = has_wall[r][this.cols - 1] = true;
        }
        for (let c = 0; c < this.cols; c++) {
            has_wall[0][c] = has_wall[this.rows - 1][c] = true;
        }
        // 创建随机内部障碍物
        for (let i = 0; i < this.inner_wall_count / 2; i++) {
            let r = -1;
            let c = -1;
            do {
                r = parseInt(Math.random() * this.rows);
                c = parseInt(Math.random() * this.cols);
            } while (has_wall[r][c] == true || (r == this.rows - 2 && c == 1) || (r == 1 && c == this.cols - 2));
            has_wall[r][c] = true;
            has_wall[c][r] = true;
        }

        // 判断两条蛇是否更够联通
        const copy_g = JSON.parse(JSON.stringify(has_wall)); // 深度拷贝
        if (!this.check_connectivity(copy_g, this.rows - 2, 1, 1, this.cols - 2)) return false;

        // 将墙添加至存储墙的列表
        for (let r = 0; r < this.rows; r++) {
            for (let c = 0; c < this.cols; c++) {
                if (has_wall[r][c]) {
                    this.walls.push(new Wall(r, c, this));
                }
            }
        }

        return true;
    }

    start() {
        // while(!this.create_walls()); // 不建议使用死循环
        for (let i = 0; i < 10000; i++) {
            if (this.create_walls())
                break;
        }
    }

    update_size() { // 在每一帧更新边长
        // 由于画布是动态变化的，因此需要动态更新，求出格子的最大边长
        // 采用相对距离，防止小化页面时游戏界面没有成比例缩小
        this.L = parseInt(Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows));
        this.ctx.canvas.width = this.L * this.cols; // 宽度=格数*格子长度
        this.ctx.canvas.height = this.L * this.rows;
    }

    update() {
        this.update_size(); // 更新画布边长
        this.render();
    }

    render() { // 渲染函数
        // this.ctx.fillStyle = 'green';
        // this.ctx.fillRect(0, 0, this.ctx.canvas.width, this.ctx.canvas.height);
        const color_even = "#AAD751"; // 偶数格颜色
        const color_odd = "#A2D048"; // 奇数格颜色
        for (let r = 0; r < this.rows; r++) {
            for (let c = 0; c < this.cols; c++) {
                if ((r + c) % 2 == 0) {
                    this.ctx.fillStyle = color_even;
                } else {
                    this.ctx.fillStyle = color_odd;
                }
                this.ctx.fillRect(c * this.L, r * this.L, this.L, this.L); // 填充，注意 列在前 行在后
            }
        }
    }
}