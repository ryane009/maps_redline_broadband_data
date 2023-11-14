import "../styles/MapBox.css";

/**
 * Interface of the contents the InfoBox should contain
 */
interface ClickedAreaInfo {
  state?: string | null;
  city?: string | null;
  name?: string | null;
  broadband?: string | null;
}

/**
 * Sets the values to display in the InfoBox.
 * @param param0
 * @returns InfoBox display.
 */
function InfoBox({ area }: { area: ClickedAreaInfo }) {
  return (
    <div className="info-box">
      <p>Clicked Area Information</p>
      <p>State: {area.state || "N/A"}</p>
      <p>City: {area.city || "N/A"}</p>
      <p>Broadband: {area.broadband || "N/A"}</p>
      <p>Name: {area.name || "N/A"}</p>
    </div>
  );
}

export default InfoBox;
