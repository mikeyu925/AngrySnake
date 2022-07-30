<template>
    <ContentField>
        我的snake
    </ContentField>
</template>

<script>
import ContentField from '../../../components/ContentField.vue'
import $ from 'jquery'
import {useStore} from 'vuex'


export default{
  components: { ContentField },
  setup(){
    const store = useStore();
    
    // $.ajax({
    //   url: "http://127.0.0.1:6969/user/snake/add/",
    //   type: "POST",
    //   data:{
    //     title: "small snake",
    //     description: "这是一条小蛇",
    //     content: "abcdefg",
    //   },
    //   headers: {
    //       Authorization: "Bearer " + store.state.user.token,
    //   },
    //   success(resp) {
    //     console.log(resp);
    //   },
    //   error(resp){
    //     console.error(resp);
    //   }
    // })

    // $.ajax({
    //   url: "http://127.0.0.1:6969/user/snake/remove/",
    //   type: "POST",
    //   data:{
    //     snake_id: 6,
    //   },
    //   headers: {
    //       Authorization: "Bearer " + store.state.user.token,
    //   },
    //   success(resp) {
    //     console.log(resp);
    //   },
    //   error(resp){
    //     console.error(resp);
    //   }
    // })

    // $.ajax({
    //   url: "http://127.0.0.1:6969/user/snake/update/",
    //   type: "POST",
    //   data:{
    //     snake_id : 7,
    //     title: "big snake",
    //     description: "这是一条大蛇",
    //     content: "ddddd",
    //   },
    //   headers: {
    //       Authorization: "Bearer " + store.state.user.token,
    //   },
    //   success(resp) {
    //     console.log(resp);
    //   },
    //   error(resp){
    //     console.error(resp);
    //   }
    // })

    $.ajax({
      url: "http://127.0.0.1:6969/user/snake/getlist/",
      type: "get",
      headers: {
          Authorization: "Bearer " + store.state.user.token,
      },
      success(resp) {
        console.log(resp);
      },
      error(resp){
        console.error(resp);
      }
    })
}
}
</script>

<style scoped>

</style>
