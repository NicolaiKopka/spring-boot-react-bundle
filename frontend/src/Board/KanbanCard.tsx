import {Status, TaskItem} from "../model";
import {default as axios} from "axios";
import {useState} from "react";
import EditField from "../Edit/EditField";
import {Link, NavLink} from "react-router-dom";
import "./KanbanCard.css"
import {useTranslation} from "react-i18next";
import {deleteTaskFromBackend, moveTaskToNextState, moveTaskToPrevState} from "../API_services/services";

interface KanbanCardProps {
    item: TaskItem
    onTaskChange: Function;
}

export default function KanbanCard(props: KanbanCardProps) {

    const {t} = useTranslation();

    const nextState = () => {
        moveTaskToNextState(props.item)
            .then(() => props.onTaskChange());
    }

    const prevState = async () => {
         moveTaskToPrevState(props.item)
            .then(() => props.onTaskChange());
    }

    const deleteTask = () => {
        deleteTaskFromBackend(props.item.id!)
            .then(() => props.onTaskChange());
    }


    return (
        <div className={"card"} data-testid={props.item.id}>
            <div className={"text-box"}>
                <div className={"titles-box"}>
                    <span>{t("task")}: </span>
                    <span>{t("description")}: </span>
                </div>
                <div className={"content-box"}>
                    <span>{props.item.task}</span>
                    <span>{props.item.description}</span>
                </div>
            </div>

            <div>
                {props.item.status === Status.OPEN?
                    <button onClick={deleteTask}><i className="fa-solid fa-trash-can"></i></button>
                :   <button onClick={prevState}><i className="fa-solid fa-arrow-left-long fa-lg"></i></button>}

                <NavLink to={`/${props.item.id}`}><button><i className="fa-solid fa-pen-to-square"></i></button></NavLink>
                {props.item.status !== Status.DONE && <button onClick={nextState}><i className="fa-solid fa-arrow-right-long fa-lg"></i></button>}
                {props.item.status === Status.DONE && <button onClick={deleteTask}><i className="fa-solid fa-trash-can"></i></button>}
            </div>
        </div>
    )
}