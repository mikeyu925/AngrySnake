<template>
    <ContentField>
      <div class="game-table">
        <dir>
          <table style="text-align: center">
            <thead>
              <tr>
                <th>玩家A</th>
                <th>玩家B</th>
                <th>对战结果</th>
                <th>对战时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="record in records" :key="record.record.id" >
                  <td class="game-table-username">
                    <img :src="record.a_photo" alt="" class="record-user-photo" >
                    &nbsp;
                    <span class="record-user-username">{{ record.a_username }}</span>
                  </td>
                  <td class="game-table-username">
                    <img :src="record.b_photo" alt="" class="record-user-photo" >
                    &nbsp;
                    <span class="record-user-username">{{ record.b_username }}</span>
                  </td>
                  <td>{{record.result}}</td>
                  <td>{{record.record.createtime}}</td>
                  <td>
                    <button @click="open_record_content(record.record.id)" type="button">
                      查看录像
                    </button>
                  </td>
              </tr>
            </tbody>
          </table>
          <nav aria-label="...">
            <ul  style="padding : 0;">

              <li class="game-page-item" @click="click_page(-2)">
                <a class="game-page-link" href="#" aria-label="Previous">
                  <span aria-hidden="true">&laquo;</span>
                </a>
              </li>

              <li :class="'game-page-item ' + page.is_active" v-for="page in pages" :key="page.number" @click="click_page(page.number)">
                <a class="game-page-link" href="#">{{page.number}}</a>
              </li>

              <li class="game-page-item" @click="click_page(-1)">
                <a class="game-page-link" href="#" aria-label="Next">
                  <span aria-hidden="true">&raquo;</span>
                </a>
              </li>

            </ul>
          </nav>
        </dir>
      </div>

    </ContentField>
</template>

<script>
import ContentField from '../../components/ContentField.vue'
import {useStore} from 'vuex';
import {ref} from 'vue';
import $ from 'jquery';

export default{
  components: { 
    ContentField
  },
  setup(){
    const store = useStore();
    let records = ref([]);
    let current_page = 1;
    let total_records = 0;

    let pages = ref([]);

    const click_page = page =>{
      if(page === -2) page = current_page - 1;
      else if(page === -1) page = current_page + 1;
      let max_pages = parseInt(Math.ceil(total_records / 10));
      
      if(page >= 1 && page <= max_pages){
        pull_page(page);
      }
    }

    const update_pages = () =>{
      let max_pages = parseInt(Math.ceil(total_records / 10));
      let new_pages = [];
      for(let i = current_page - 2;i <= current_page + 2;i++){
        if(i >= 1 && i <= max_pages){
          new_pages.push({
            number : i,
            is_active : i === current_page ? "active" : "",
          });
        }
      }
      pages.value = new_pages;
    }

    const pull_page = page =>{
        current_page = page;
        $.ajax({
          // url: "http://127.0.0.1:6969/api/record/getlist/",
          url: "https://app3235.acapp.acwing.com.cn/api/record/getlist/",
          data:{
            page,
          },
          type: "get",
          headers: {
              Authorization: "Bearer " + store.state.user.token, // 验证的 token
          },
          success(resp) {
            records.value = resp.records;
            // console.log(records.value);
            total_records = resp.records_count;
            update_pages();
          },
          error(){
            // console.log(resp);
          }
        })
    }
    
    pull_page(current_page);

    const stringTo2D = map => {
      let g = [];
      for(let i = 0, k = 0;i < 13;i++){
        let line = [];
        for(let j = 0;j < 14;j++,k++){
          if(map[k] === '0') line.push(0);
          else line.push(1);
        }
        g.push(line);
      }
      return g;
    }

    const open_record_content = recordId =>{
      for(const record of records.value){
        if(record.record.id === recordId){
          // 更新状态
          store.commit("updateIsRecord",true);
          store.commit("updateGame",{
            map: stringTo2D(record.record.map),
            a_id : record.record.aid,
            a_sx : record.record.asx,
            a_sy : record.record.asy,
            b_id : record.record.bid,
            b_sx : record.record.bsx,
            b_sy : record.record.bsy,
          });
          store.commit("updateSteps",{
            a_steps : record.record.asteps,
            b_steps : record.record.bsteps,
          });
          store.commit("updateRecordLoser", record.record.loser);
          store.commit("updateRouterName","record_content");
          break;
        }
      }
    }

    return {
      records,
      pages,
      open_record_content,
      click_page,
    }

  }
}
</script>

<style scoped>
  img.record-user-photo{
    width: 3vh;
    border: 50%;
  }

  div.game-table{
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
  }

  div.game-table table{
    background-color: rgba(255, 255, 255, 0.5);
    border-radius: 5px;
  }

  .game-table-username{
    text-align: left;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    max-width: 7.7vw;
  }

  td{
    width: 7.5vw;
  }
  
  th{
    text-align: center;
  }

  .game-page-item{
    display: inline-block;
    padding: 8px 12px;
    background-color:rgb(255, 255, 255);
    border: 1px solid #dee2e6;
    cursor: pointer;
    user-select: none;
  }

  .game-page-item:hover{
    background-color: #E9ECEF;
  }

  .game-page-item:active{
    background-color: #0d6efd;
  }
  .game-page-item:active > a{
    color: white;
  }
  .game-page-link{
    color:#0d6efd;
    text-decoration: none;
    cursor: pointer;
  }

  nav{
    display: flex;
    justify-content: center;
    align-items: center;
  }
  
</style>
