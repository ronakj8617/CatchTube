<script setup>
import { ref } from "vue";
import axios from "axios";

const BASE_API = "http://localhost:8080/api/catchtube/download"
const url = ref('')
const error = ref('')
const loading = ref(false)
const result = ref(null)
const hideButton = ref(true)
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
        Accept: 'application/json'
      }
    })
    const { fileName, downloadUrl } = response.data
    result.value = { fileName, downloadUrl }
    hideButton.value = false
  }
  catch (e) {
    error.value = e.response?.data || "An error occured"
    return
  }
  finally {
    loading.value = false
  }
}
function deleteAllData() {
  axios.post("http://localhost:8080/api/catchtube/deleteDownloads").then((res) => {
    alert('Files are deleted now')
  }).catch((e) => {
    const message = e.response?.data?.message || e.message || 'An error occurred while deleting'
    alert(message)
  })
  hideButton.value = true
}
function deleteFile() {
  console.log(result.value.fileName)
  axios.post("http://localhost:8080/api/catchtube/deleteFile", {
    path: "downloads/" + result.value.fileName
  }).then((res) => {
    alert('Files are deleted now')
    result.value = null
  }).catch((e) => {
    const message = e.response?.data?.message || e.message || 'An error occurred while deleting'
    alert(message)
  })
  hideButton.value = true
}
</script>
<template>
  <div class="container mt-4">
    <h2>CatchTube</h2>
    <div class="form-group">
      <input v-model="url" type="url" class="form-control" placeholder="ENTER A URL" />
    </div>

    <button class="btn btn-primary mt-2" @click="submit" :disabled="loading">
      {{ loading ? 'Downloading...' : 'Download' }}
    </button>

    <div class="mt-3 text-danger" v-if="error">{{ error }}</div>

    <div class="mt-3" v-if="result">
      ✅ Video downloaded!
      <br />
      <a :href="result.downloadUrl" class="btn btn-success mt-2">
        Click to Download: {{ result.fileName }}
      </a>
      <button @click="deleteFile" class="btn btn-danger" v-if="!hideButton">Delete the file</button>
      <button @click="deleteAllData" class="btn btn-danger" v-if="!hideButton">Clean downloads</button>
    </div>
  </div>
</template>