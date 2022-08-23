package com.as.backend.consumer.utils;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    private Integer id;
    private Integer botId; // -1: 人工， 否则表示AI
    private String  botCode;
    private Integer sx;
    private Integer sy;
    // 记录 历史上玩家方向上的序列
    private List<Integer> steps;

    /**
     * 检验当前回合是否增加蛇长度
     * @return
     **/
    private boolean check_tail_increasing(int step){
        if (step <= 10) return true;
        return step % 3 == 1;
    }

    /**
     * 获取蛇的坐标轨迹序列
     * @return
     */
    public List<Cell> getCells(){
        List<Cell> ans = new ArrayList<>();
        int [] dx = {-1,0,1,0},dy = {0,1,0,-1};
        int x = sx,y = sy;
        int step = 0;
        ans.add(new Cell(x,y));
        for (int d : steps){
            x += dx[d];
            y += dy[d];
            ans.add(new Cell(x,y));
            if (!check_tail_increasing(++step)){
                ans.remove(0);
            }
        }
        return ans;
    }

    /**
     * 获得蛇操作序列的字符串
     * @return
     */
    public String getStepsString(){
        StringBuilder sb = new StringBuilder();
        for (int d : this.steps){
            sb.append(d);
        }
        return sb.toString();
    }

}
