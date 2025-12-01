import { useOutlet } from 'react-router-dom';
import './App.css'
import Navbar from './global/components/Navbar';

function App() {
  const currentOutlet = useOutlet();

  return (
    <div className="min-h-screen w-full bg-gray-50">

      <Navbar />

      <main className="w-full">
        {currentOutlet}
      </main>

    </div>
  );
}

export default App
