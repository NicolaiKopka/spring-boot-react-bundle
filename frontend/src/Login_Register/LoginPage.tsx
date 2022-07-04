import {FormEvent, useEffect, useState} from "react";
import {Link, useNavigate} from "react-router-dom";
import {loginUser} from "../API_services/services";
import {AxiosError} from "axios";
import App from "../App";

interface AppProps {
    errorFunction: Function
}

export default function LoginPage(props: AppProps) {

    const [username, setUsername] = useState("")
    const [password, setPassword] = useState("")
    const [googleToken, setGoogleToken] = useState("")

    const nav = useNavigate();

    function handleCallbackResponse(response: any) {
        setGoogleToken(response.credential)
        nav("/transfer/" + response.credential)
    }

    useEffect(() => {
        /* global google */
        // @ts-ignore
        google.accounts.id.initialize({
            client_id: "1019999010766-lld8krspracip7l0gp13oi1jr1ifcgpg.apps.googleusercontent.com",
            callback: handleCallbackResponse
        })
        // @ts-ignore
        google.accounts.id.renderButton(
            document.getElementById("signInDiv"),
            { theme: "outline", size: "large"}
        )
    }, [])

    function login(ev: FormEvent) {
        ev.preventDefault()
        loginUser(username, password)
            .then(data => localStorage.setItem("jwt", data.token))
            .then(() => nav("/board"))
            .catch((e: AxiosError) => {
                props.errorFunction("Sorry, user not found")
            })
    }

    return(
        <div>
            <form onSubmit={login}>
                <div>
                    <input value={username} onChange={event => setUsername(event.target.value)}/>
                </div>
                <div>
                    <input type={"password"} value={password} onChange={event => setPassword(event.target.value)}/>
                </div>
                <button type={"submit"}>Login</button>
            </form>
            <div id={"signInDiv"}></div>
            <Link to={"/register"}>Create an account</Link>
        </div>
    )
}