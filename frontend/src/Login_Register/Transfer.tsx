import {useNavigate, useParams} from "react-router-dom";
import {useEffect} from "react";
import {loginWithGoogle} from "../API_services/services";
import {AxiosError} from "axios";
import App from "../App";

interface AppProps {
    errorFunction: Function
}

export default function Transfer(props: AppProps) {

    const {token} = useParams()
    const nav = useNavigate()

    useEffect(() => {
        loginWithGoogle(token!).then(data => localStorage.setItem("jwt", data.token))
            .then(() => nav("/board"))
            .catch((e: AxiosError) => {
                props.errorFunction("Seems like you are not verified")
            })
    }, [])



    return (
        <div>Loading....</div>
    )
}