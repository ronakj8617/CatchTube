<script setup>
import { ref } from "vue";
import axios from "axios";

const BASE_API = "http://localhost:8080/api/catchtube/download"
const url = ref('')
const error = ref('')
const loading = ref(false)
const result = ref('')
const downloadLink = ref('')

const submit = async () => {
  if (!url.value) {
    error.value = "Please provide a URL"
    return
  }
  try {
    loading.value = true
    const response = await axios.get(BASE_API, {
      params: {
        url: url.value
      },
      headers: {
        Accept: 'text/plain'
      }
    })

    downloadLink.value = response.data

  }
  catch (e) {
    error.value = e.response?.data || "An error occured"
    return
  }
  finally {
    loading.value = false
  }
}
</script>
<template>
  <div class="container mt-4">
    <h2 style="font-family: 'Times New Roman', Times, serif;">CatchTube</h2>
    <div class="form-group">
      <input type="url" class="form-control" v-model="url" placeholder="ENTER THE URL" />
    </div>
    <button class="btn btn-primary mt4" @click="submit">
      {{ loading ? "Downloading" : "Submit" }}
    </button>
    <div class="mt-3 text-danger" v-if="error">{{ error }}</div>
    <div class="mt-3" v-if="result">{{ result }}</div>
    <a :href="downloadLink" v-if="downloadLink">Click here to download!!</a>
  </div>
</template>