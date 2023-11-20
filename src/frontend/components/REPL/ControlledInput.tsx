import "../styles/main.css";
import React, {
  Dispatch,
  SetStateAction,
  KeyboardEvent,
  useState,
} from "react";
import { CommandOutput } from "./REPL";

/**
 * Props for controlled input
 */
interface ControlledInputProps {
  value: string;
  setValue: Dispatch<SetStateAction<string>>;
  ariaLabel: string;
  handleSubmit: () => void;
  history: CommandOutput[];
}

/**
 * A controlled input component that wraps an HTML input element.
 * It ensures that React manages the input's state.
 *
 * @param {ControlledInputProps} props - The props for the ControlledInput component.
 * @returns {JSX.Element} The JSX element representing the controlled input.
 */
export function ControlledInput({
  value,
  setValue,
  ariaLabel,
  handleSubmit,
  history,
}: ControlledInputProps) {
  const [historyIndex, setHistoryIndex] = useState<number>(history.length);

  const handleKeyDown = (event: KeyboardEvent<HTMLInputElement>) => {
    if (event.key === "Enter") {
      handleSubmit();
      setHistoryIndex(history.length);
    } else if (event.key === "ArrowUp") {
      if (historyIndex > -1) {
        setHistoryIndex(historyIndex - 1);
        setValue(history[historyIndex].command);
      }
    } else if (event.key === "ArrowDown") {
      if (historyIndex < history.length) {
        setHistoryIndex(historyIndex + 1);
        if (historyIndex === history.length - 1) {
          setValue("");
        } else {
          setValue(history[historyIndex + 1].command);
        }
      }
    }
  };

  return (
    <input
      type="text"
      className="repl-command-box"
      value={value}
      placeholder="Enter command here!"
      onChange={(ev) => setValue(ev.target.value)}
      onKeyDown={handleKeyDown}
      aria-label={ariaLabel}
    />
  );
}
