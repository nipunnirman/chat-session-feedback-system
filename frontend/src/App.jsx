import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Dashboard from './pages/Dashboard';
import AdminEditor from './pages/AdminEditor';
import FeedbackPage from './pages/FeedbackPage';
import ResponsesPage from './pages/ResponsesPage';
import './index.css';

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Dashboard />} />
        <Route path="/editor/:enterpriseId" element={<AdminEditor />} />
        <Route path="/responses/:enterpriseId" element={<ResponsesPage />} />
        <Route path="/feedback/:feedbackId" element={<FeedbackPage />} />
      </Routes>
    </BrowserRouter>
  );
}
