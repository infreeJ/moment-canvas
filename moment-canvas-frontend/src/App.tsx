import { useOutlet } from 'react-router-dom';
import './App.css'

function App() {
  const currentOutlet = useOutlet();

  return (
    <div className='container'>
      {currentOutlet}
    </div>
  )
}

export default App
