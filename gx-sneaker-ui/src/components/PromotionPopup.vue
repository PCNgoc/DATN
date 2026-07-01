<template>

  <transition name="popup">

    <div v-if="show" class="overlay">


      <div class="popup">


        <!-- Nút đóng -->
        <button
          class="close"
          @click="close"
        >
          ✕
        </button>



        <!-- Ảnh popup -->
        <img
          src="/images/popup-sale.jpg"
          alt="Khuyến mãi"
          class="banner"
        />



        <!-- Nội dung -->
        <div class="content">


          <span class="tag">
            🔥 HOT SALE
          </span>



          <h1>
            GIẢM ĐẾN 50%
          </h1>



          <h3>
            GX SNEAKER
          </h3>



          <p>
            Freeship toàn quốc • Voucher 100K
          </p>



          <button
            class="buy"
            @click="shopNow"
          >
            MUA NGAY
          </button>



          <label class="checkbox">

            <input
              type="checkbox"
              v-model="dontShowToday"
            >

            Không hiển thị lại hôm nay

          </label>



        </div>


      </div>


    </div>


  </transition>


</template>



<script setup>

import { ref, onMounted } from "vue"



const show = ref(false)


const dontShowToday = ref(false)




// Hiện popup khi load trang

onMounted(()=>{


  const hidden =
    localStorage.getItem(
      "hidePopupToday"
    )



  if(!hidden){


    setTimeout(()=>{

      show.value = true

    },800)


  }


})





// Đóng popup

const close = ()=>{


  show.value = false



  if(dontShowToday.value){


    localStorage.setItem(
      "hidePopupToday",
      "true"
    )


  }


}





// Chuyển sang cửa hàng

const shopNow = ()=>{


  window.location.href="/products"


}



</script>





<style scoped>


.overlay{

  position:fixed;

  inset:0;

  /* giảm độ tối phía sau */
  background:rgba(0,0,0,.25);

  /* làm mờ nhẹ thôi */
  backdrop-filter:blur(2px);

  display:flex;

  justify-content:center;

  align-items:center;

  z-index:99999;

}




.popup{


  width:650px;

  height:430px;

  position:relative;

  border-radius:18px;

  overflow:hidden;

  box-shadow:
    0 25px 60px rgba(0,0,0,.45);


}




.banner{


  width:100%;

  height:100%;

  object-fit:cover;


}




.content{


  position:absolute;

  inset:0;


  background:
    linear-gradient(
      rgba(0,0,0,.25),
      rgba(0,0,0,.55)
    );


  display:flex;

  flex-direction:column;

  justify-content:flex-end;


  padding:35px;


}





.tag{


  width:max-content;


  background:#ff2d2d;


  color:white;


  padding:6px 15px;


  border-radius:999px;


  font-size:14px;


  font-weight:bold;


  margin-bottom:12px;


}





.content h1{


  color:white;


  font-size:38px;


  font-weight:900;


  line-height:1;


  margin:0;


  text-shadow:
    0 3px 10px black;


}





.content h3{


  color:#ffe600;


  font-size:25px;


  margin:8px 0;


  text-shadow:
    0 3px 10px black;


}





.content p{


  color:white;


  font-size:16px;


  margin:0 0 18px;


}





.buy{


  width:170px;


  height:45px;


  border:none;


  border-radius:999px;


  background:#ff2d2d;


  color:white;


  font-size:16px;


  font-weight:bold;


  cursor:pointer;


  transition:.3s;


}





.buy:hover{


  transform:scale(1.08);


  background:#d90000;


}






.checkbox{


  margin-top:12px;


  color:white;


  font-size:14px;


}





.checkbox input{


  margin-right:7px;


}






.close{


  position:absolute;


  top:12px;


  right:12px;


  width:38px;


  height:38px;


  border:none;


  border-radius:50%;


  background:white;


  cursor:pointer;


  font-size:17px;


  z-index:10;


  box-shadow:
    0 5px 15px rgba(0,0,0,.3);


}





/* Animation */


.popup-enter-active{

  animation:
    zoom .35s;

}



.popup-leave-active{

  animation:
    zoomOut .25s forwards;

}





@keyframes zoom{


  from{

    opacity:0;

    transform:scale(.8);

  }


  to{

    opacity:1;

    transform:scale(1);

  }


}





@keyframes zoomOut{


  from{

    opacity:1;

    transform:scale(1);

  }


  to{

    opacity:0;

    transform:scale(.85);

  }


}






@media(max-width:768px){



  .popup{


    width:90%;


    height:380px;


  }




  .content{


    padding:25px;


  }




  .content h1{


    font-size:30px;


  }





  .content h3{


    font-size:22px;


  }



  .content p{


    font-size:14px;


  }



}



</style>
