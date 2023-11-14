import "./styles/App.css";
import MapBox from "./Map/MapBox";
import REPL from "./REPL/REPL";

function App() {
  return (
    <div className="App">
      <MapBox />
      <REPL />
    </div>
  );
}

export default App;
