// 如果是export 需要用 {} 括起来，如果是export default 不需要{},每个文件只能由一个default
import { GameObject } from "./GameObject";

export class GameMap extends GameObject {
    // ctx 画布,parent 画布的父元素，画布的父元素，用来动态修改画布长宽
    constructor(ctx, parent) {
        super(); // 调用基类构造函数
        this.ctx = ctx;
        this.parent = parent;

        this.L = 0; // 格子的长度
        this.rows = 13; // 游戏地图的行数(行方向格子数) 
        this.cols = 13; // 游戏地图的列数(列方向格子数)
        // 采用相对距离，防止小化页面时游戏界面没有成比例缩小
    }

    start() {

    }


    update_size() { // 在每一帧更新边长
        // 由于画布是动态变化的，因此需要动态更新，求出格子的最大边长
        this.L = Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows);
        this.ctx.canvas.width = this.L * this.cols; // 宽度=格数*格子长度
        this.ctx.canvas.height = this.L * this.rows;
    }

    update() {
        this.update_size(); // 更新画布边长
        this.render();
    }

    render() { // 渲染函数
        this.ctx.fillStyle = 'green';
        this.ctx.fillRect(0, 0, this.ctx.canvas.width, this.ctx.canvas.height);
    }
}