import requests
from typing import Dict, List, Optional, Any, Union
from dataclasses import dataclass
from enum import Enum

class Dificultad(str, Enum):
    FACIL = "facil"
    MEDIA = "media"
    DIFICIL = "dificil"

@dataclass
class Ingrediente:
    nombre: str
    cantidad: str

class CookMasterError(Exception):
    """Excepción personalizada para errores de la API"""
    def __init__(self, message: str, status_code: Optional[int] = None):
        self.message = message
        self.status_code = status_code
        super().__init__(self.message)

class CookMaster:
    def __init__(self, api_key: str, base_url: str = "https://api.cookmaster.com/v1", 
                 timeout: int = 30, max_retries: int = 3):
        self.api_key = api_key
        self.base_url = base_url.rstrip('/')
        self.timeout = timeout
        self.max_retries = max_retries
        self.session = requests.Session()
        self.session.headers.update({
            "X-API-Key": api_key,
            "Content-Type": "application/json",
            "User-Agent": "CookMaster-Python-SDK/1.0"
        })

    def _request(self, method: str, endpoint: str, **kwargs) -> Any:
        url = f"{self.base_url}/{endpoint.lstrip('/')}"
        for attempt in range(self.max_retries):
            try:
                response = self.session.request(method, url, timeout=self.timeout, **kwargs)
                if response.status_code >= 400:
                    try:
                        error_msg = response.json().get('message', f"Error {response.status_code}")
                    except:
                        error_msg = f"Error {response.status_code}"
                    raise CookMasterError(error_msg, response.status_code)
                return response.json() if response.content else {}
            except requests.exceptions.RequestException as e:
                if attempt == self.max_retries - 1:
                    raise CookMasterError(f"Error de conexión: {str(e)}")
                continue
        return {}

    # ===================== Endpoints =====================
    def get_recetas_todas(self, page: int = 1, limit: int = 50) -> List[Dict[str, Any]]:
        params = {'page': max(1, page), 'limit': min(50, max(1, limit))}
        return self._request('GET', 'recetas/todas', params=params)

    def get_receta(self, id: int) -> Dict[str, Any]:
        if not isinstance(id, int) or id <= 0:
            raise ValueError("ID debe ser un entero positivo")
        return self._request('GET', f'recetas/{id}')

    def eliminar_receta(self, id: int) -> Dict[str, str]:
        if not isinstance(id, int) or id <= 0:
            raise ValueError("ID debe ser un entero positivo")
        return self._request('DELETE', f'recetas/{id}')

    def crear_receta(self, receta: Dict[str, Any]) -> Dict[str, Any]:
        required_fields = ["nombre", "categoria", "ingredientes", "pasos"]
        missing = [f for f in required_fields if f not in receta]
        if missing:
            raise ValueError(f"Faltan campos obligatorios: {', '.join(missing)}")
        
        if not isinstance(receta['ingredientes'], list):
            raise ValueError("'ingredientes' debe ser una lista de diccionarios o Ingrediente")
        if not isinstance(receta['pasos'], list):
            raise ValueError("'pasos' debe ser una lista de strings")
        if len(receta['ingredientes']) > 20:
            raise ValueError("Máximo 20 ingredientes por receta")
        
        # Convertir dataclasses a dict si es necesario
        receta['ingredientes'] = [
            i.__dict__ if isinstance(i, Ingrediente) else i
            for i in receta['ingredientes']
        ]
        
        return self._request('POST', 'recetas', json=receta)

    # ===================== Métodos adicionales =====================
    def buscar_recetas(self, categoria: Optional[str] = None, 
                        dificultad: Optional[Union[str, Dificultad]] = None,
                        tiempo_max: Optional[int] = None) -> List[Dict[str, Any]]:
        params = {}
        if categoria:
            params['categoria'] = categoria
        if dificultad:
            if isinstance(dificultad, Dificultad):
                params['dificultad'] = dificultad.value
            elif dificultad in [d.value for d in Dificultad]:
                params['dificultad'] = dificultad
            else:
                raise ValueError(f"Dificultad inválida: {dificultad}")
        if tiempo_max:
            params['tiempo_max'] = tiempo_max
        return self._request('GET', 'recetas/buscar', params=params)

# ===================== Ejemplo de uso =====================
if __name__ == "__main__":
    api = CookMaster("TU_API_KEY")
    try:
        print(api.get_recetas_todas())
        receta = {
            "nombre": "Pasta al pesto",
            "categoria": "pastas",
            "ingredientes": [Ingrediente("Pasta", "200g"), Ingrediente("Albahaca", "50g")],
            "pasos": ["Hervir pasta", "Preparar pesto", "Mezclar y servir"]
        }
        print(api.crear_receta(receta))
    except CookMasterError as e:
        print(f"Error API: {e.message} (Código {e.status_code})")
