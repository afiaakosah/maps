import './App.css';
import MapElements from './Map';

const TEXT_header_div = "header div"
const TEXT_APP = "app div"

function App() {
 return (
    <div className="App" aria-label = {TEXT_APP}>
      <p className = "header" aria-label = {TEXT_header_div}>
        Welcome to Integration Map!
      </p>
      <MapElements/>      
    </div>
  );
}


export {App, TEXT_header_div};
