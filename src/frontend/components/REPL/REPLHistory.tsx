import "../styles/main.css";
import { KeyboardEvent, useState } from "react";
import { CommandOutput } from "./REPL";

/**
 * Represents the history component of the Read-Eval-Print Loop (REPL).
 * It displays a list of previously executed commands and their output.
 *
 * @param {REPLHistoryProps} props - The props for the REPLHistory component.
 * @returns {JSX.Element} The JSX element representing the REPLHistory component.
 */
export function REPLHistory(props: REPLHistoryProps) {
  const [tabIndex, setTabIndex] = useState<number>(-1);

  /**
   * Handle the keydown event.
   *
   * @param {KeyboardEvent<HTMLInputElement>} event - The keydown event.
   * @returns {number}
   */
  const handleKeyDown = (event: KeyboardEvent<HTMLInputElement>) => {
    if (event.key === "ArrowRight") {
      setTabIndex(0);
      console.log("tabIndex: " + tabIndex);
    }
    if (event.key === "ArrowLeft") {
      setTabIndex(-1);
      console.log("tabIndex: " + tabIndex);
    }
  };

  /**
   * Render the REPLHistory component, displaying a list of commands and their output.
   *
   * @returns {JSX.Element} The JSX element representing the REPLHistory component.
   */

  return (
    <div
      className="repl-history overflow-y-scroll"
      data-testid="repl-history"
      tabIndex={0}
      aria-live="assertive"
    >
      {props.history.map((elem, index) => (
        <div
          className="history-element"
          key={index}
          tabIndex={0}
          onKeyDown={handleKeyDown}
        >
          {props.mode && <p>Command: {elem.command}</p>}
          {typeof elem.output === "string" ? (
            <p>Output: {elem.output}</p>
          ) : (
            <div>
              Output:
              <table>
                <tbody>
                  {elem.output?.map((row, rowIndex) => (
                    <tr key={rowIndex}>
                      {row.map((cell, cellIndex) => (
                        <td
                          key={cellIndex}
                          tabIndex={tabIndex}
                          aria-label={cell}
                        >
                          {cell}
                        </td>
                      ))}
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      ))}
    </div>
  );
}

/**
 * Props for the REPLHistory component.
 */
interface REPLHistoryProps {
  history: CommandOutput[];
  mode: boolean;
}
