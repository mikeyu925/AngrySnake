// 如果是export 需要用 {} 括起来，如果是export default 不需要{},每个文件只能由一个default
// GameMap 游戏地图组件
import { GameObject } from "./GameObject";
import { Snake } from "./Snake";
import { Wall } from "./Wall";


export class GameMap extends GameObject {
    // ctx 画布,parent 画布的父元素，画布的父元素，用来动态修改画布长宽
    constructor(ctx, parent, store) {
        super(); // 调用基类构造函数
        this.ctx = ctx;
        this.parent = parent;
        this.store = store;
        this.L = 0; // 格子的长度 ==> 会根据页面长宽动态改变
        this.rows = 13; // 游戏地图的行数(行方向格子数) 
        this.cols = 14; // 游戏地图的列数(列方向格子数)

        this.walls = []; // 存储墙的列表
        this.inner_wall_count = 20; // 内部障碍物数量

        this.snakes = [
            new Snake({ id: 0, color: "#4876EC", r: this.rows - 2, c: 1 }, this),
            new Snake({ id: 1, color: "#F94848", r: 1, c: this.cols - 2 }, this),
        ]; // 蛇信息(id，颜色，头部坐标)
    }


    create_walls() {
        const has_wall = this.store.state.pk.gamemap;
        // 将墙添加至存储墙的列表
        for (let r = 0; r < this.rows; r++) {
            for (let c = 0; c < this.cols; c++) {
                if (has_wall[r][c]) {
                    this.walls.push(new Wall(r, c, this));
                }
            }
        }
    }

    // 添加键盘输入监听事件
    add_listening_events() {
        console.log(this.store.state.record);

        if (this.store.state.record.is_record) {
            let k = 0;

            const a_steps = this.store.state.record.a_steps;
            const b_steps = this.store.state.record.b_steps;
            const loser = this.store.state.record.record_loser;
            const [snake0, snake1] = this.snakes;
            const interval_id = setInterval(() => {
                if (k >= a_steps.length - 1) {
                    if (loser === "all" || loser === "A") {
                        snake0.status = "die";
                    }
                    if (loser === "all" || loser === "B") {
                        snake1.status = "die";
                    }
                    clearInterval(interval_id);
                } else {
                    snake0.set_direction(parseInt(a_steps[k]));
                    snake1.set_direction(parseInt(b_steps[k]));
                }
                k++;
            }, 300);
        } else {
            this.ctx.canvas.focus(); // 聚焦
            // 根据键盘输入给每个蛇设定方向
            this.ctx.canvas.addEventListener("keydown", e => {
                let d = -1;
                if (e.key === 'w') d = 0;
                else if (e.key === 'd') d = 1;
                else if (e.key === 's') d = 2;
                else if (e.key === 'a') d = 3;

                if (d >= 0) {
                    this.store.state.pk.socket.send(JSON.stringify({
                        event: "move",
                        direction: d,
                    }));
                }
            });
        }
    }


    start() { // 只执行一次
        this.create_walls()
        this.add_listening_events();
    }

    update_size() { // 在每一帧更新边长
        // 由于画布是动态变化的，因此需要动态更新，求出格子的最大边长
        // 采用相对距离，防止小化页面时游戏界面没有成比例缩小
        this.L = parseInt(Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows));
        this.ctx.canvas.width = this.L * this.cols; // 宽度=格数*格子长度
        this.ctx.canvas.height = this.L * this.rows;
    }

    check_ready() { // 判断蛇是否都准备好下一回合
        // 必须所有蛇均准备好
        for (const snake of this.snakes) {
            if (snake.status !== "idle") return false;
            if (snake.direction === -1) return false;
        }
        return true;
    }

    next_step() { // 让两条蛇进入下一回合
        for (const snake of this.snakes) {
            snake.next_step();
        }
    }
    check_valid(cell) { // 检测目标位置是否合法：没有撞墙或者两条蛇
        for (const wall of this.walls) {
            if (wall.r === cell.r && wall.c === cell.c) {
                return false;
            }
        }
        for (const snake of this.snakes) {
            let k = snake.cells.length;
            if (!snake.check_tail_increasing()) { // 当蛇尾会前进的话，不会判断追尾
                k--;
            }
            for (let i = 0; i < k; i++) {
                if (snake.cells[i].r === cell.r && snake.cells[i].c === cell.c) {
                    return false; // 撞了
                }
            }
        }
        return true;
    }
    update() {
        this.update_size(); // 更新画布边长
        if (this.check_ready()) { // 如果两条蛇都准备好了
            this.next_step(); // 两条蛇进入下一步
        }
        this.render(); // 渲染
    }

    render() { // 游戏画面渲染函数
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