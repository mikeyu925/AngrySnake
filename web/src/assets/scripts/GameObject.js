// 存储所有游戏对象
const GAME_OBJECTRS = [];

// 基类 所有需要不断渲染显示的对象都需要继承该对象
export class GameObject {
    constructor() {
        GAME_OBJECTRS.push(this);
        this.timedelta = 0; // 当前帧距离上一帧的时间间隔
        this.has_called_start = false; // 标志是否执行过
    }

    start() { // start 只执行一次，在创建的时候

    }

    update() { // 除了第一帧，每一帧执行一次

    }

    on_destory() { // 删除对象之前执行

    }

    destory() { // 删除游戏对象
        this.on_destory();
        for (let i in GAME_OBJECTRS) { // in 遍历下标
            {
                const obj = GAME_OBJECTRS[i];
                if (obj == this) {
                    GAME_OBJECTRS.splice(i); // 删除
                    break;
                }
            }
        }
    }
}

let last_timestamp; // 上一次执行时间
const step = timestamp => {
    for (let obj of GAME_OBJECTRS) { // of 取的是值
        if (!obj.has_called_start) { // 如果是第一次执行
            obj.has_called_start = true; // 修改标志
            obj.start();
        } else {
            obj.timedelta = timestamp - last_timestamp; // 计算时间间隔
            obj.update();
        }
    }
    last_timestamp = timestamp; // 更新上一次时间
    requestAnimationFrame(step) //迭代递归运行,在第t+1帧前再次执行该函数
}

// requestAnimationFrame(step)可以使得浏览器在渲染下一帧前执行一下函数 step
requestAnimationFrame(step)