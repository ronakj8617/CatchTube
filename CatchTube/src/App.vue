<script setup>
import { ref } from "vue";
const url = ref('url')
const buttonText = ref('Click Me!')
function submit() {
  buttonText.value = buttonText.value + '!'
}
</script>
<template>
  <div class="container-fluid">
    <div class="row">
      <div class="col-6">
        <h2 class="">CatchTube</h2>

        <input type="url" id="url" />
        <button class="btn btn-primary" style="width: 150px;" id="submit" @click="submit">{{ buttonText }}</button>
      </div>
    </div>
  </div>
</template>