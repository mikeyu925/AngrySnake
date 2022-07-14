export class Cell {
    constructor(r, c) {
        this.r = r;
        this.c = c;
        // 由于画布坐标系 和 逻辑坐标系相反,因此需要调换
        this.x = c + 0.5;
        this.y = r + 0.5;
    }
}