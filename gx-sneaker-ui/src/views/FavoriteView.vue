<script setup>

import {ref,onMounted} from "vue"
import {useRouter} from "vue-router"


const router = useRouter()


const favorites = ref([])



const loadFavorite = ()=>{


  favorites.value =
    JSON.parse(
      localStorage.getItem("favorites")
    ) || []


}




const removeFavorite=(id)=>{


  favorites.value =
    favorites.value.filter(
      item=>item.id!==id
    )


  localStorage.setItem(
    "favorites",
    JSON.stringify(favorites.value)
  )


}



const detail=(id)=>{


  router.push(
    `/products/${id}`
  )


}




onMounted(()=>{

  loadFavorite()

})


</script>



<template>


  <div class="favorite-page">


    <h1>
      Sản phẩm yêu thích ❤️
    </h1>



    <div
      v-if="favorites.length===0"
      class="empty"
    >

      Bạn chưa có sản phẩm yêu thích


    </div>





    <div
      v-else
      class="favorite-grid"
    >



      <div

        class="card"

        v-for="item in favorites"

        :key="item.id"

      >



        <img

          :src="`/images/${item.anhDaiDien}`"

          @click="detail(item.id)"

        >



        <h3>

          {{item.tenSanPham}}

        </h3>




        <button

          @click="removeFavorite(item.id)"

        >

          Xóa yêu thích

        </button>



      </div>



    </div>



  </div>


</template>
<style scoped>
.favorite-page{

  padding:50px;

}



.favorite-page h1{

  text-align:center;

  margin-bottom:40px;

}



.favorite-grid{


  display:grid;


  grid-template-columns:

repeat(auto-fill,minmax(250px,1fr));


  gap:25px;


}



.card{


  background:white;


  padding:20px;


  border-radius:20px;


  box-shadow:
    0 10px 30px rgba(0,0,0,.1);


}



.card img{


  width:100%;


  height:250px;


  object-fit:cover;


  cursor:pointer;


}



.card button{


  margin-top:15px;


  width:100%;


  padding:12px;


  border:none;


  background:#111;


  color:white;


  border-radius:10px;


  cursor:pointer;


}



.empty{


  text-align:center;


  font-size:20px;


}
</style>
