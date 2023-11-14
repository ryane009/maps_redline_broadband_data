import "../styles/main.css";
import { useState } from "react";
import { REPLHistory } from "./REPLHistory";
import { REPLInput } from "./REPLInput";

/**
 * This is the top-level component for the Read-Eval-Print Loop (REPL).
 * It serves as the main component that organizes and manages the REPL's functionality.
 */

/**
 * Represents the output of a command.
 */
export type CommandOutput = {
  command: string;
  output: string | string[][];
};

export default function REPL() {
  const [history, setHistory] = useState<CommandOutput[]>([]);
  const [mode, setMode] = useState<boolean>(false);

  /**
   * Render the REPL component, including REPLHistory and REPLInput components.
   *
   * @returns {JSX.Element} The JSX element representing the REPL component.
   */
  return (
    <div className="repl">
      <REPLHistory history={history} mode={mode} />
      <hr></hr>
      <REPLInput
        history={history}
        setHistory={setHistory}
        mode={mode}
        setMode={setMode}
      />
    </div>
  );
}
