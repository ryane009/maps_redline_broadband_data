import "../styles/main.css";
import { Dispatch, SetStateAction, useState } from "react";
import { ControlledInput } from "./ControlledInput";
import { CommandOutput } from "./REPL";
import { loadBaseCommands, commands } from "./commands";

/**
 * Represents the input component of the Read-Eval-Print Loop (REPL).
 * It allows users to enter commands and handles their execution.
 *
 * @param {REPLInputProps} props - The props for the REPLInput component.
 * @returns {JSX.Element} The JSX element representing the REPLInput component.
 */
interface REPLInputProps {
  history: CommandOutput[];
  setHistory: Dispatch<SetStateAction<CommandOutput[]>>;

  mode: boolean;
  setMode: Dispatch<SetStateAction<boolean>>;
}

/**
 * The REPLInput component
 * @param props The props for the REPLInput component
 * @returns The JSX element representing the REPLInput component
 */
export function REPLInput(props: REPLInputProps) {
  const [commandString, setCommandString] = useState<string>("");
  const [currentDataset, setDataset] = useState<string>("file not loaded");

  loadBaseCommands();

  /**
   * Handle the submission of a command.
   *
   * @param {string} commandString - The command entered by the user.
   */
  async function handleSubmit(commandString: string) {
    const regex = new RegExp(/("[^"]+"|\S+)/g);
    const parts =
      commandString.match(regex)?.map((part) => part.replace(/"/g, "")) ?? [];
    const command = parts[0];

    var historyElement: CommandOutput = {
      command: commandString,
      output: "...loading",
    };

    if (commands.has(command)) {
      if (command === "mode") {
        parts.push(props.mode.toString());
      }
      commands
        .get(command)?.(parts.slice(1))
        .then((output) => {
          console.log(output);
          if (typeof output === "string" || typeof output === "object") {
            console.log("string");
            historyElement.output = output;
            setDataset(output);
            setCommandString("");
            props.setHistory([...props.history, historyElement]);
          }

          if (typeof output === "boolean") {
            if (output) {
              props.setMode(true);
              historyElement.output = "mode changed to verbose";
              //Add the history element to the command history
              props.setHistory([...props.history, historyElement]);
              setCommandString("");
            } else {
              props.setMode(false);
              historyElement.output = "mode changed to brief";
              //Add the history element to the command history
              props.setHistory([...props.history, historyElement]);
              setCommandString("");
            }
          }
        });
    } else {
      historyElement.output = "invalid command";
      props.setHistory([...props.history, historyElement]);
      setCommandString("");
    }
  }

  /**
   * Renders the REPLInput component
   */
  return (
    <div className="repl-input">
      <fieldset>
        <legend>Enter a command:</legend>
        <ControlledInput
          value={commandString}
          setValue={setCommandString}
          ariaLabel={"Command input"}
          handleSubmit={() => handleSubmit(commandString)}
          history={props.history}
        />
      </fieldset>
      <div style={{ marginTop: "10px" }}></div>{" "}
      <button
        style={{ marginTop: "10px" }}
        onClick={() => handleSubmit(commandString)}
      >
        Submit
      </button>{" "}
    </div>
  );
}
