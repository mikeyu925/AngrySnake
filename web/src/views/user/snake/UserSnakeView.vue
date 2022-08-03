<template>
  <div class="container">
    <div class="row">
      <div class="col-3">
        <div class="card" style="margin-top: 20px">
          <div class="card-body">
            <img :src="$store.state.user.photo" alt="" style=" width: 100%;">
          </div>
        </div>
      </div>

      <div class="col-9">
        <div class="card" style="margin-top: 20px">
          <div class="card-header">
              <span style="font-size: 130%">我的Snake</span>  
              <button type="button" class="btn btn-success float-end" data-bs-toggle="modal" data-bs-target="#add-snake-button">
                创建Snake
              </button>

              <!-- 创建模态框 Modal 通过id选择并弹出 -->
              <div class="modal fade" id="add-snake-button" tabindex="-1" >
                <div class="modal-dialog modal-xl">
                  <div class="modal-content">
                    <div class="modal-header">
                      <h5 class="modal-title">创建一个Snake</h5>
                      <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                      <!-- 写表单 -->
                      <div class="mb-3">
                        <label for="add-snake-title" class="form-label">名称</label>
                        <input v-model="snakeadd.title" type="text" class="form-control" id="add-snake-title" placeholder="这里输入Snake的名称">
                      </div>
                      <div class="mb-3">
                        <label for="add-snake-description" class="form-label">简介</label>
                        <textarea v-model="snakeadd.description" class="form-control" id="add-snake-description" rows="3" placeholder="这里输入Snake的介绍"></textarea>
                      </div>
                      <div class="mb-3">
                        <label for="add-snake-code" class="form-label">代码</label>
                        <VAceEditor
                          v-model:value="snakeadd.content"
                          @init="editorInit"
                          lang="c_cpp"
                          theme="textmate"
                          style="height: 300px" />
                      </div>
                    </div>
                    <div class="modal-footer">
                      <div class="error-message">{{snakeadd.error_message}}</div>
                      <!-- @click 绑定添加事件 -->
                      <button type="button" class="btn btn-primary" @click="add_snake">创建</button>  
                      <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" @click="clear_snakeadd">取消</button>
                    </div>
                  </div>
                </div>
              </div>

          </div>
          <div class="card-body">
            <table class="table table-striped table-hover">
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
                      <button type="button" class="btn btn-secondary" style="margin-right: 10px" data-bs-toggle="modal" :data-bs-target="'#update-snake-button-' + snake.id">修改</button>
                      <button type="button" class="btn btn-danger" data-bs-toggle="modal" :data-bs-target="'#remove-snake-button-' + snake.id">删除</button>
                    
                      <!-- 修改模态框 Modal 通过id选择并弹出 -->
                      <div class="modal fade" :id="'update-snake-button-' + snake.id" tabindex="-1" >
                        <div class="modal-dialog modal-xl">
                          <div class="modal-content">
                            <div class="modal-header">
                              <h5 class="modal-title">修改你的的Snake</h5>
                              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                              <!-- 写表单 -->
                              <div class="mb-3">
                                <label for="add-snake-title" class="form-label">名称</label>
                                <input v-model="snake.title" type="text" class="form-control" id="add-snake-title" placeholder="这里输入Snake的名称">
                              </div>
                              <div class="mb-3">
                                <label for="add-snake-description" class="form-label">简介</label>
                                <textarea v-model="snake.description" class="form-control" id="add-snake-description" rows="3" placeholder="这里输入Snake的介绍"></textarea>
                              </div>
                              <div class="mb-3">
                                <label for="add-snake-code" class="form-label">代码</label>
                                  <VAceEditor
                                    v-model:value="snake.content"
                                    @init="editorInit"
                                    lang="c_cpp"
                                    theme="textmate"
                                    style="height: 300px" />
                              </div>
                            </div>
                            <div class="modal-footer">
                              <div class="error-message">{{snake.error_message}}</div>
                              <!-- @click 绑定添加事件 -->
                              <button type="button" class="btn btn-primary" @click="update_snake(snake)">保存</button>  
                              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" >取消</button>
                            </div>
                          </div>
                        </div>
                      </div>

                      <div class="modal" :id="'remove-snake-button-' + snake.id" tabindex="-1">
                        <div class="modal-dialog">
                          <div class="modal-content">
                            <div class="modal-header">
                              <h5 class="modal-title">再次确认！</h5>
                              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                              <p>您确定要删除当前Snake?</p>
                            </div>
                            <div class="modal-footer">
                              <button type="button" class="btn btn-primary" @click="remova_snake(snake)">确定</button>
                              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                            </div>
                          </div>
                        </div>
                      </div>

                    </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useStore } from 'vuex'
import $ from 'jquery'
import {Modal} from 'bootstrap/dist/js/bootstrap'
import { VAceEditor } from 'vue3-ace-editor';
import ace from 'ace-builds';


  export default{
    components:{
      VAceEditor
    },
    setup(){
      ace.config.set(
      "basePath", 
      "https://cdn.jsdelivr.net/npm/ace-builds@" + require('ace-builds').version + "/src-noconflict/")

      const store = useStore();
      let snakes = ref([]);  // 存储snake信息
      // let copy_snakes = [];
      const snakeadd = reactive({
        title : "",
        description : "",
        content : "",
        error_message: "",
      })

      const refresh_snakes = () =>{
        $.ajax({
          url: "http://127.0.0.1:6969/user/snake/getlist/",
          type: "get",
          headers: {
              Authorization: "Bearer " + store.state.user.token, // 验证的 token
          },
          success(resp) {
            console.log(resp)
            snakes.value = resp;
          }
        })
      }
      
      refresh_snakes();

      const add_snake = () =>{
        snakeadd.error_message = "";
        $.ajax({
          url: "http://127.0.0.1:6969/user/snake/add/",
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
              Modal.getInstance("#add-snake-button").hide();
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
      }

      const remova_snake = (snake) => {
        $.ajax({
          url: "http://127.0.0.1:6969/user/snake/remove/",
          type: "POST",
          data:{
            snake_id: snake.id,
          },
          headers: {
              Authorization: "Bearer " + store.state.user.token,
          },
          success(resp) {
            if(resp.error_message === "success"){
              Modal.getInstance('#remove-snake-button-' + snake.id).hide();
              refresh_snakes();
            }
          },
        })
      }

      const update_snake = (snake) => {
        $.ajax({
          url: "http://127.0.0.1:6969/user/snake/update/",
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
              Modal.getInstance('#update-snake-button-' + snake.id).hide();
              refresh_snakes();
            }else{
              snakeadd.error_message = resp.error_message;
            }
          },
        })
      }

      return {
        snakes,
        snakeadd,
        add_snake,
        remova_snake,
        update_snake,
        clear_snakeadd,
      }
    }

  } 
</script>

<style scoped>
  div.error-message{
    color: red;
  }
</style>


