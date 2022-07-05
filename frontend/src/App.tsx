import React, {useState, useEffect} from 'react';
import KanbanBoard from "./Board/KanbanBoard";
import InputField from "./Input/InputField";
import {BrowserRouter, Link, Route, Routes} from "react-router-dom";
import EditField from "./Edit/EditField";
import "./App.css"
import {ErrorBoundary} from "react-error-boundary";
import axios from "axios";
import {TaskItem} from "./model";
import {getAllUserData} from "./API_services/services";
import LoginPage from "./Login_Register/LoginPage";
import RegisterPage from "./Login_Register/RegisterPage";
import Transfer from "./Login_Register/Transfer";

function App() {

    const [errorMessage, setError] = useState("")
    const [taskArray, setTaskArray] = useState<Array<TaskItem>>([]);

    useEffect(() => {
        setTimeout(() => setError(""), 3000)
    }, [errorMessage])

    const fetchTasks = () => {
        getAllUserData()
            .then(data => setTaskArray(data))
            .catch(() => setError("Could not connect to server"))
    }

    return (

        <BrowserRouter>
            <div>
                {errorMessage && <div className={"error-message"}>{errorMessage}</div>}
                <h1>Kanban Board</h1>
                <Routes>
                    <Route path="/board" element={<>
                        <InputField errorFunction={setError} onTaskChange={fetchTasks}/>
                        <KanbanBoard taskArray={taskArray} onTaskChange={fetchTasks}/>
                    </> } />
                    <Route path="/:id" element={<EditField onTaskChange={fetchTasks} errorFunction={setError}/>}/>
                    <Route path="/" element={<LoginPage errorFunction={setError}/>} />
                    <Route path="/register" element={<RegisterPage errorFunction={setError}/>} />
                    <Route path="/transfer/:token" element={<Transfer errorFunction={setError}/>} />
                </Routes>
            </div>
        </BrowserRouter>


    );
}

export default App;
