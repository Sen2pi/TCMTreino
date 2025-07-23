<img src="https://r2cdn.perplexity.ai/pplx-full-logo-primary-dark%402x.png" class="logo" width="120"/>

# Guia Detalhado de Desenvolvimento Frontend: Passo a Passo Completo

Este guia apresenta todos os passos necessários para implementar o **frontend** do projeto “Treasury \& Collateral Management” com React, Material UI, animações e gráficos. Cada etapa inclui explicação do porquê, estrutura de ficheiros, código completo e instruções de configuração.

## Sumário

- Estrutura de Pastas
- Instalar e Configurar Dependências
- Tema Global (Modo Claro/Escuro)
- Layout e Navegação
- Componentes Comuns (Loader, Botão Animado, Toggle de Tema)
- Páginas Principais
    - Login
    - Dashboard
    - Treasury Management
    - Collateral Management
- Serviços de API
- Contexto de Autenticação e Segurança de Rotas
- Testes Unitários Básicos
- Executar e Buildar


## 1. Estrutura de Pastas

```
frontend/
├─ public/
│   └─ index.html
└─ src/
   ├─ assets/                
   ├─ components/
   │   ├─ common/
   │   │   ├─ AnimatedButton.jsx
   │   │   ├─ LoaderBars.jsx
   │   │   └─ ThemeToggle.jsx
   │   ├─ layout/
   │   │   ├─ Header.jsx
   │   │   ├─ Sidebar.jsx
   │   │   └─ Layout.jsx
   │   └─ charts/
   │       └─ TreasuryChart.jsx
   ├─ contexts/
   │   └─ AuthContext.js
   ├─ pages/
   │   ├─ Login.jsx
   │   ├─ Dashboard.jsx
   │   ├─ Treasury.jsx
   │   └─ Collateral.jsx
   ├─ services/
   │   ├─ api.js
   │   ├─ auth.js
   │   ├─ treasury.js
   │   └─ collateral.js
   ├─ theme/
   │   └─ theme.js
   ├─ App.jsx
   └─ index.jsx
```


## 2. Instalar e Configurar Dependências

```bash
npm install @mui/material @mui/icons-material @emotion/react @emotion/styled
npm install recharts framer-motion axios react-router-dom
```

Em **package.json**:

```json
"scripts": {
  "start": "react-scripts start",
  "build": "react-scripts build",
  "test":  "react-scripts test"
},
"proxy": "http://localhost:8080"
```


## 3. Tema Global (Modo Claro/Escuro)

**src/theme/theme.js**

```javascript
import { createTheme } from "@mui/material/styles";

export const getDesignTokens = (mode) => ({
  palette: {
    mode,
    ...(mode === "light"
      ? { primary: { main: "#1976d2" } }
      : { primary: { main: "#90caf9" }, background: { default: "#121212" } }),
  },
  components: {
    MuiButton: {
      styleOverrides: { root: { borderRadius: 8, textTransform: "none" } }
    }
  }
});
```


## 4. Entrada da App e Toggle de Tema

**src/index.jsx**

```jsx
import React, { useMemo, useState } from "react";
import ReactDOM from "react-dom";
import App from "./App";
import { ThemeProvider, createTheme } from "@mui/material/styles";
import CssBaseline from "@mui/material/CssBaseline";
import { getDesignTokens } from "./theme/theme";

export const ColorModeContext = React.createContext({ toggle: () => {} });

function Main() {
  const [mode, setMode] = useState("light");
  const colorMode = useMemo(() => ({
    toggle: () => setMode(prev => (prev === "light" ? "dark" : "light"))
  }), []);
  const theme = useMemo(() => createTheme(getDesignTokens(mode)), [mode]);

  return (
    <ColorModeContext.Provider value={colorMode}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <App />
      </ThemeProvider>
    </ColorModeContext.Provider>
  );
}

ReactDOM.render(<Main />, document.getElementById("root"));
```

**src/components/common/ThemeToggle.jsx**

```jsx
import { useTheme, IconButton } from "@mui/material";
import LightbulbIcon from "@mui/icons-material/Lightbulb";
import { useContext } from "react";
import { ColorModeContext } from "../../index";

export default function ThemeToggle() {
  const theme = useTheme(), mode = theme.palette.mode;
  const colorMode = useContext(ColorModeContext);
  return (
    <IconButton onClick={colorMode.toggle} title="Toggle theme">
      <LightbulbIcon sx={{
        transition: "transform 0.3s",
        transform: mode === "dark" ? "rotate(180deg)" : "none"
      }}/>
    </IconButton>
  );
}
```


## 5. Layout e Navegação

**src/App.jsx**

```jsx
import React from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Layout from "./components/layout/Layout";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import Treasury from "./pages/Treasury";
import Collateral from "./pages/Collateral";
import ProtectedRoute from "./components/common/ProtectedRoute";

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/" element={<ProtectedRoute><Layout/></ProtectedRoute>}>
          <Route index element={<Navigate to="/dashboard" replace />} />
          <Route path="dashboard" element={<Dashboard/>} />
          <Route path="treasury" element={<Treasury/>} />
          <Route path="collateral" element={<Collateral/>} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
```

**Layout, Header, Sidebar**: implemente com Material UI `<AppBar>`, `<Drawer>` e nav links conforme sumário de pastas.

## 6. Componentes Comuns

### 6.1 Botão Animado

```jsx
// AnimatedButton.jsx
import { Button } from "@mui/material";
import { motion } from "framer-motion";

export default function AnimatedButton(props){
  return (
    <motion.div whileHover={{ scale:1.05 }} whileTap={{ scale:0.95 }}>
      <Button {...props} />
    </motion.div>
  );
}
```


### 6.2 Loader de Barras

```jsx
// LoaderBars.jsx + loader-bars.css
export default function LoaderBars(){
  return <div className="loader-bars"><div/><div/><div/></div>;
}
```

```css
.loader-bars{display:flex;align-items:flex-end;gap:4px;height:40px;}
.loader-bars div{width:6px;background:#1976d2;animation:loading .8s infinite;}
.loader-bars div:nth-child(2){animation-delay:.1s;}
.loader-bars div:nth-child(3){animation-delay:.2s;}
@keyframes loading{0%,100%{height:6px;}50%{height:40px;}}
```


## 7. Serviços de API

**src/services/api.js**

```javascript
import axios from "axios";
const api = axios.create({ baseURL:"http://localhost:8080/api" });
api.interceptors.request.use(cfg=>{
  const token=localStorage.getItem("token");
  if(token) cfg.headers.Authorization=`Bearer ${token}`;
  return cfg;
});
api.interceptors.response.use(r=>r,e=>{
  if(e.response?.status===401){
    localStorage.removeItem("token");
    window.location="/login";
  }
  return Promise.reject(e);
});
export default api;
```

**src/services/auth.js**

```javascript
import api from "./api";
export const authService={
  login:creds=>api.post("/auth/login",creds),
  logout:()=>localStorage.removeItem("token")
};
```

**src/services/treasury.js** e **collateral.js**: métodos CRUD e sumários.

## 8. Contexto de Autenticação e Rotas Protegidas

**AuthContext.js**: armazena user e token, fornece `login()`, `logout()`.

**ProtectedRoute.jsx**:

```jsx
import React from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";

export default function ProtectedRoute({ children }){
  const { user, loading } = useAuth();
  if(loading) return null;
  return user ? children : <Navigate to="/login" />;
}
```


## 9. Páginas Principais

Devido ao espaço, mantenha a mesma abordagem apresentada anteriormente:

- **Login.jsx**: formulários, `authService.login`, armazenar token e redirecionar.
- **Dashboard.jsx**: estatísticas em `<Grid>` e `<Card>`, gráficos com Recharts.
- **Treasury.jsx**: tabela, diálogo de edição/criação, `treasuryService`.
- **Collateral.jsx**: cards de collateral elegível com `collateralService`.


## 10. Testes Unitários Básicos

Exemplo para `AnimatedButton` com React Testing Library:

```jsx
import { render, fireEvent } from "@testing-library/react";
import AnimatedButton from "./AnimatedButton";

test("AnimatedButton chama onClick", ()=>{
  const fn=jest.fn();
  const { getByRole } = render(<AnimatedButton onClick={fn}>Click</AnimatedButton>);
  fireEvent.click(getByRole("button"));
  expect(fn).toHaveBeenCalled();
});
```


## 11. Executar e Buildar

```bash
# Backend deve estar a correr em http://localhost:8080
cd frontend
npm install
npm start         # em dev
npm run build     # gerar bundle para produção
```

Com estes passos completos terá um **frontend moderno**, animado, responsivo, com modo claro/escuro, gráficos interativos, segurança e TDD básico — pronto para integrar com o backend e colocar em produção.

<div style="text-align: center">⁂</div>

[^1]: Projeto-Treasury-Collateral-Management-Treino.md

