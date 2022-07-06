import axios from "axios";
import {Status, TaskItem} from "../model";
import {render, screen, waitFor} from "@testing-library/react";
import {MemoryRouter} from "react-router-dom";
import App from "../App";
import KanbanBoard from "./KanbanBoard";

// jest.mock("../Login_Register/LoginPage.tsx", () => <div>FakeLogin</div>)

jest.mock("../Login_Register/LoginPage.tsx", () => {
    return function DummyLoginPage() {
        return (
            <div>
               FakeLogin
            </div>
        );
    };
});

test("that server connection established and items rendered properly", async () => {

    const task1: TaskItem = {
        id: "01",
        task: "Hallo",
        description: "Task",
        status: Status.OPEN,
        userId: "1234"
    }

    const task2: TaskItem = {
        id: "02",
        task: "Hallo2",
        description: "Task2",
        status: Status.OPEN,
        userId: "1234"
    }

    jest.spyOn(axios, "get").mockImplementation((url: string) => {
        expect(url).toEqual("/api/kanban")
        return Promise.resolve({
            status: 200,
            data: [task1, task2]
        })
    })

    // const url = location.href;
    // console.log(url)
    // global.window = Object.create(window);
    // Object.defineProperty(window, 'location', {
    //     value: {
    //         href: url + "board"
    //     }
    // });
    // console.log(location.href)

    window.history.pushState({}, "", "/board")

    render(<App/>)

    await waitFor(() => {
        expect(screen.getByTestId("01")).toBeDefined();
        expect(screen.getByTestId("02")).toBeDefined();
    })
})