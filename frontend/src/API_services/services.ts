import axios, {AxiosResponse} from "axios";
import {LoginResponse, TaskItem} from "../model";

function createHeader() {
   return `Bearer ${localStorage.getItem("jwt")}`
}

export function getAllUserData() {
   return axios.get("/api/kanban", {
      headers: {
         Authorization: createHeader()
      }
   })
        .then(response => response.data)
}

export function getTaskById(id: string) {
   return axios.get(`/api/kanban/${id}`, {
      headers: {
         Authorization: createHeader()
      }
   })
}

export function moveTaskToNextState(item: TaskItem) {
   return axios.put("/api/kanban/next", item, {
      headers: {
         Authorization: createHeader()
      }
   })
}

export function moveTaskToPrevState(item: TaskItem) {
   return axios.put("/api/kanban/prev", item, {
      headers: {
         Authorization: createHeader()
      }
   })
}

export function deleteTaskFromBackend(id: string) {
   return axios.delete(`/api/kanban/${id}`, {
      headers: {
         Authorization: createHeader()
      }
   })
}

export function addTask(item: TaskItem) {
   return axios.post("/api/kanban", item, {
      headers: {
         Authorization: createHeader()
      }
   })
}

export function editTask(item: TaskItem) {
   return axios.put("/api/kanban", item, {
      headers: {
         Authorization: createHeader()
      }
   })
}

export function loginUser(username: string, password: string) {
   return axios.post("/api/login", {
      username: username,
      password: password
   }).then((response: AxiosResponse<LoginResponse>) => response.data)
}

export function loginWithGoogle(googleToken: string) {
   return axios.post("/api/login/google/" + googleToken)
       .then((response: AxiosResponse<LoginResponse>) => response.data)
}

export function registerUser(username: string, password: string, checkPassword: string) {
   return axios.post("/api/user", {
      username: username,
      password: password,
      checkPassword: checkPassword
   }).then(response => response.data)
}