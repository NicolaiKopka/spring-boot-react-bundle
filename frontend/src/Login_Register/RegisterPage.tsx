import {FormEvent, useState} from "react";
import {Link, useNavigate} from "react-router-dom";
import {loginUser, registerUser} from "../API_services/services";
import {AxiosError} from "axios";

interface AppProps {
    errorFunction: Function
}

export default function RegisterPage(props: AppProps) {

    const [username, setUsername] = useState("")
    const [password, setPassword] = useState("")
    const [checkPassword, setCheckPassword] = useState("");

    const nav = useNavigate();

    function register(ev: FormEvent) {
        ev.preventDefault()
        registerUser(username, password, checkPassword)
            .then(() => nav("/"))
            .catch((e: AxiosError) => {
                props.errorFunction(e.response?.data)
            })

    }

    return(
        <div>
            <form onSubmit={register}>
                <div>
                    <input value={username} onChange={event => setUsername(event.target.value)}/>
                </div>
                <div>
                    <input type={"password"} value={password} onChange={event => setPassword(event.target.value)}/>
                </div>
                <div>
                    <input type={"password"} value={checkPassword} onChange={event => setCheckPassword((event.target.value))}/>
                </div>
                <button type={"submit"}>Register</button>
            </form>
        </div>
    )
}