import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import JobsPage from "./pages/JobsPage";
import "./App.css";

function App() {
  return (
    <div className="App">
      <Routes>
        <Route path="/" element={<LoginPage />} />
        <Route path="/jobs" element={<JobsPage />} />
      </Routes>
    </div>
  );
}

export default App;
