<template>
 <ContentField>
  <div class="game-table">
    <div>
      <span style="font-size: 130%">我的Snake</span>  
      <button type="button" style="float:right" @click="show_add_modal_handler(true)">
        创建Snake
      </button>

      <!-- 创建模态框 Modal 通过id选择并弹出 -->
      <div class="game-modal" id="add-snake-button" tabindex="-1" v-if="show_add_modal">
        <div >
          <h5 style="margin:2px;">创建一个Snake</h5>
        </div>
        <div>
          <!-- 写表单 -->
          <div >
            <label for="add-snake-title">名称</label>
            <input style="width:85%" v-model="snakeadd.title" type="text" id="add-snake-title" placeholder="这里输入Snake的名称">
          </div>
          <div >
            <label for="add-snake-description" >简介</label>
            <textarea style="width:85%;margin-top:10px" v-model="snakeadd.description"  id="add-snake-description" rows="3" placeholder="这里输入Snake的介绍"></textarea>
          </div>
          <div >
            <label for="add-snake-code" >代码</label>
            <VAceEditor
              v-model:value="snakeadd.content"
              @init="editorInit"
              lang="c_cpp"
              theme="textmate"
              style="height: 300px" />
          </div>
        </div>
        <div >
          <div class="error-message">{{snakeadd.error_message}}</div>
          <button type="button"  @click="add_snake">创建</button>  
          <button type="button"  @click="clear_snakeadd">取消</button>
        </div>
      </div>
      
      <table>
        <thead>
          <tr>
            <th>昵称</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="snake in snakes" :key="snake.id">
            <td>{{snake.title}}</td>
            <td>{{snake.createtime}}</td>
            <td>
              <button type="button"   style="margin-right: 10px" @click="show_update_modal_handler(snake.id,true)">修改</button>
              <button type="button"  @click="remova_snake(snake)">删除</button>
            
              <!-- 修改模态框 Modal 通过id选择并弹出 -->
              <div class="game-modal" :id="'update-snake-button-' + snake.id" tabindex="-1" v-if="snake.show_update_modal">
                <div >
                  <h5 style="margin:2px;">修改你的的Snake</h5>
                </div>
                <div>
                  <div>
                    <label for="add-snake-title" >名称</label>
                    <input style="width:85%" v-model="snake.title" type="text" class="form-control" id="add-snake-title" placeholder="这里输入Snake的名称">
                  </div>
                  <div>
                    <label for="add-snake-description">简介</label>
                    <textarea style="width:85%;margin-top:10px" v-model="snake.description" class="form-control" id="add-snake-description" rows="3" placeholder="这里输入Snake的介绍"></textarea>
                  </div>
                  <div>
                    <label for="add-snake-code">代码</label>
                      <VAceEditor
                        v-model:value="snake.content"
                        @init="editorInit"
                        lang="c_cpp"
                        theme="textmate"
                        style="height: 300px" />
                  </div>
                </div>
                <div>
                  <div class="error-message">{{snake.error_message}}</div>
                  <!-- @click 绑定添加事件 -->
                  <button type="button"  @click="update_snake(snake)">保存</button>  
                  <button type="button" @click="show_update_modal_handler(snake.id,false)">取消</button>
                </div>
              </div>

            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
 </ContentField>
</template>

<script>
import { ref, reactive } from 'vue'
import { useStore } from 'vuex'
import $ from 'jquery'
import { VAceEditor } from 'vue3-ace-editor';
import ace from 'ace-builds';
import ContentField from '../../../components/ContentField.vue'

  export default{
    components:{
      VAceEditor,
      ContentField,
    },
    setup(){
      ace.config.set(
      "basePath", 
      "https://cdn.jsdelivr.net/npm/ace-builds@" + require('ace-builds').version + "/src-noconflict/")

      const store = useStore();
      let snakes = ref([]);  // 存储snake信息
      let copy_snakes = ref([]);
      let show_add_modal = ref(false);

      const snakeadd = reactive({
        title : "",
        description : "",
        content : "",
        error_message: "",
      })

      const refresh_snakes = () =>{
        $.ajax({
          url: "https://app3235.acapp.acwing.com.cn/api/user/snake/getlist/",
          type: "get",
          headers: {
              Authorization: "Bearer " + store.state.user.token, // 验证的 token
          },
          success(resp) {
            for(const snake of resp){
              snake.show_update_modal = false;
            }
            snakes.value = resp;
            copy_snakes.value = JSON.parse(JSON.stringify(snakes.value));
          }
        })
      }
      
      refresh_snakes();

      const add_snake = () =>{
        snakeadd.error_message = "";
        $.ajax({
          url: "https://app3235.acapp.acwing.com.cn/api/user/snake/add/",
          type: "POST",
          data:{
            title: snakeadd.title,
            description: snakeadd.description,
            content: snakeadd.content,
          },
          headers: {
              Authorization: "Bearer " + store.state.user.token,
          },
          success(resp) {
            if(resp.error_message === "success"){
              // 清空数据
              snakeadd.title = "";
              snakeadd.description = "";
              snakeadd.content = "";
              show_add_modal.value = false;
              refresh_snakes();
            }else{
              snakeadd.error_message = resp.error_message;
            }
          },
        })
      }
      // 如果创建时取消输入，则 清空之前的输入
      const clear_snakeadd = () => {
        snakeadd.title = "";
        snakeadd.description = "";
        snakeadd.content = "";
        show_add_modal_handler(false);
      }

      const goback_update = () =>{
          snakes.value = JSON.parse(JSON.stringify(copy_snakes.value));
      }

      const remova_snake = (snake) => {
        $.ajax({
          url: "https://app3235.acapp.acwing.com.cn/api/user/snake/remove/",
          type: "POST",
          data:{
            snake_id: snake.id,
          },
          headers: {
              Authorization: "Bearer " + store.state.user.token,
          },
          success(resp) {
            if(resp.error_message === "success"){
              refresh_snakes();
            }
          },
        })
      }

      const update_snake = (snake) => {
        $.ajax({
          // url: "http://127.0.0.1:6969/api/user/snake/update/",
          url: "https://app3235.acapp.acwing.com.cn/api/user/snake/update/",
          type: "POST",
          data:{
            snake_id : snake.id,
            title: snake.title,
            description: snake.description,
            content: snake.content,
          },
          headers: {
              Authorization: "Bearer " + store.state.user.token,
          },
          success(resp) {
            if(resp.error_message === "success"){
              refresh_snakes();
            }else{
              snakeadd.error_message = resp.error_message;
            }
          },
        })
      }

      const show_add_modal_handler = is_show =>{
        show_add_modal.value = is_show;
      }

      const show_update_modal_handler = (snake_id,is_show) =>{
        const new_snakes = [];
        for(const snake of snakes.value){
          if(snake_id === snake.id){
            snake.show_update_modal = is_show;
          }
          new_snakes.push(snake)
        }
        snakes.value = new_snakes;
      }

      return {
        snakes,
        snakeadd,
        copy_snakes,
        show_add_modal,
        add_snake,
        remova_snake,
        update_snake,
        clear_snakeadd,
        goback_update,
        show_add_modal_handler,
        show_update_modal_handler,
      }
    }

  } 
</script>

<style scoped>
  div.error-message{
    color: red;
  }

  div.game-table{
    display: flex;
    justify-content: center;
    padding: 5vh;
    width: 100%;
    height: cacl(100%-5vh);
    
  }

  div.game-table table{
    background-color: rgba(255, 255, 255, 0.5);
    border-radius: 5px;
  }

  td{
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    width: 12vw;
    max-width: 12vw;
    text-align: center;
  }
  
  th{
    text-align: center;
  }

  .game-modal{
    background-color: white;
    padding: 10px;
    border-radius: 5px;
    position: absolute;
    width: 40vw;
    height: 60vh;
    left: 0;
    right: 0;
    top: 0;
    bottom: 0;
    margin: auto;
    text-align: left;
  }


</style>


