<img src="https://r2cdn.perplexity.ai/pplx-full-logo-primary-dark%402x.png" class="logo" width="120"/>

# Guia Completo do Frontend: Treasury \& Collateral Management

Este guia cobre a construção do **frontend** React com Material UI, modos claro/escuro, animações de botões, carregadores, gráficos interativos e transições, seguindo uma sequência lógica de desenvolvimento e TDD.

## Sumário

1. Estrutura de Pastas
2. Dependências e Configuração
3. Tema Global (Modo Dia/Noite)
4. Layout e Navegação
5. Componentes de UI com Animações
6. Páginas: Dashboard, Treasury, Collateral
7. Gráficos Interativos
8. Testes Unitários e de Integração
9. Próximos Passos

## 1. Estrutura de Pastas

```
frontend/
├─ public/
│   └─ index.html
└─ src/
   ├─ assets/                # Imagens, ícones
   ├─ components/
   │   ├─ common/
   │   │   ├─ ThemeToggle.jsx
   │   │   ├─ LoaderBars.jsx
   │   │   └─ AnimatedButton.jsx
   │   ├─ layout/
   │   │   ├─ Header.jsx
   │   │   ├─ Sidebar.jsx
   │   │   └─ Layout.jsx
   │   └─ charts/
   │       └─ TreasuryChart.jsx
   ├─ pages/
   │   ├─ Dashboard.jsx
   │   ├─ Treasury.jsx
   │   └─ Collateral.jsx
   ├─ services/
   │   ├─ api.js
   │   ├─ treasury.js
   │   └─ collateral.js
   ├─ theme/
   │   └─ theme.js
   ├─ App.jsx
   └─ index.jsx
```


## 2. Dependências e Configuração

```bash
npm install @mui/material @mui/icons-material @emotion/react @emotion/styled
npm install recharts            # gráficos
npm install framer-motion       # animações
npm install axios react-router-dom
```

Em **`package.json`**:

```jsonc
"scripts": {
  "start": "react-scripts start",
  "test":  "react-scripts test",
  "build": "react-scripts build"
},
```


## 3. Tema Global (Modo Dia/Noite)

src/theme/theme.js

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
      styleOverrides: {
        root: { borderRadius: 8, textTransform: "none" }
      }
    }
  }
});

```

src/components/common/ThemeToggle.jsx

```jsx
import { useTheme, IconButton } from "@mui/material";
import LightbulbIcon from "@mui/icons-material/Lightbulb";
import { useContext } from "react";
import { ColorModeContext } from "../../App";

export default function ThemeToggle() {
  const theme = useTheme();
  const colorMode = useContext(ColorModeContext);
  return (
    <IconButton onClick={colorMode.toggleColorMode} title="Toggle theme">
      <LightbulbIcon
        sx={{
          transition: "transform 0.3s",
          transform: theme.palette.mode === "dark" ? "rotate(180deg)" : "none"
        }}
      />
    </IconButton>
  );
}
```

src/index.jsx

```jsx
import React, { useMemo, useState } from "react";
import ReactDOM from "react-dom";
import App from "./App";
import { ThemeProvider } from "@mui/material";
import CssBaseline from "@mui/material/CssBaseline";
import { getDesignTokens } from "./theme/theme";

export const ColorModeContext = React.createContext({ toggleColorMode: () => {} });

function Main() {
  const [mode, setMode] = useState("light");
  const colorMode = useMemo(() => ({
    toggleColorMode: () => setMode(prev => prev === "light" ? "dark" : "light")
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


## 4. Layout e Navegação

src/components/layout/Layout.jsx

```jsx
import { Box, AppBar, Toolbar, Drawer } from "@mui/material";
import Header from "./Header";
import Sidebar from "./Sidebar";
import { Outlet } from "react-router-dom";

const drawerWidth = 240;
export default function Layout() {
  return (
    <Box sx={{ display: "flex" }}>
      <AppBar position="fixed" sx={{ ml: `${drawerWidth}px` }}>
        <Toolbar><Header/></Toolbar>
      </AppBar>
      <Drawer
        variant="permanent"
        sx={{ width: drawerWidth, "& .MuiDrawer-paper": { width: drawerWidth } }}
      >
        <Sidebar />
      </Drawer>
      <Box component="main" sx={{ flexGrow: 1, p: 3, mt: 8 }}>
        <Outlet/>
      </Box>
    </Box>
  );
}
```


## 5. Componentes de UI com Animações

### 5.1 Botão Animado

src/components/common/AnimatedButton.jsx

```jsx
import { Button } from "@mui/material";
import { motion } from "framer-motion";

export default function AnimatedButton({ children, ...props }) {
  return (
    <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
      <Button {...props}>{children}</Button>
    </motion.div>
  );
}
```


### 5.2 Loader de Barras

src/components/common/LoaderBars.jsx

```jsx
import "./loader-bars.css";
export default function LoaderBars() {
  return <div className="loader-bars"></div>;
}
```

Em **loader-bars.css** (inspirado de css-loaders.com):

```css
.loader-bars {
  display: flex; align-items: flex-end; gap: 4px; height: 40px;
}
.loader-bars div {
  width: 6px; background: #1976d2; animation: loading 0.8s infinite;
}
.loader-bars div:nth-child(2) { animation-delay: 0.1s; }
.loader-bars div:nth-child(3) { animation-delay: 0.2s; }
@keyframes loading {
  0%,100%{height:6px;}50%{height:40px;}
}
```


## 6. Páginas: Dashboard, Treasury, Collateral

### 6.1 Dashboard.jsx

```jsx
import { Grid, Typography, Paper } from "@mui/material";
import AnimatedButton from "../components/common/AnimatedButton";
import LoaderBars from "../components/common/LoaderBars";
import TreasuryChart from "../components/charts/TreasuryChart";
import { treasuryService, collateralService } from "../services";

export default function Dashboard() {
  const [data, setData] = React.useState(null);
  React.useEffect(() => {
    Promise.all([
      treasuryService.getTreasurySummary(),
      collateralService.getCollateralSummary()
    ]).then(([t, c]) => setData({ t, c }));
  }, []);
  if (!data) return <LoaderBars />;
  return (
    <>
      <Typography variant="h4" gutterBottom>Dashboard</Typography>
      <Grid container spacing={2}>
        <Grid item md={8}><Paper><TreasuryChart data={data.t}/></Paper></Grid>
        <Grid item md={4}>
          <AnimatedButton variant="contained" onClick={() => {}}>Refresh</AnimatedButton>
        </Grid>
      </Grid>
    </>
  );
}
```


### 6.2 Treasury.jsx

```jsx
import { Table, TableHead, TableBody, TableRow, TableCell, Chip } from "@mui/material";
import AnimatedButton from "../components/common/AnimatedButton";
import LoaderBars from "../components/common/LoaderBars";
import { treasuryService } from "../services";

export default function Treasury() {
  const [rows, setRows] = React.useState(null);
  React.useEffect(() => {
    treasuryService.getAll().then(r => setRows(r.data.content));
  }, []);
  if (!rows) return <LoaderBars />;
  return (
    <Table>
      <TableHead>
        <TableRow>
          {["Account","Bank","Balance","Status","Actions"].map(h => <TableCell key={h}>{h}</TableCell>)}
        </TableRow>
      </TableHead>
      <TableBody>
        {rows.map(r => (
          <TableRow key={r.id}>
            <TableCell>{r.accountNumber}</TableCell>
            <TableCell>{r.bankName}</TableCell>
            <TableCell>{r.balance.toLocaleString()}</TableCell>
            <TableCell>
              <Chip label={r.status} color={r.status==="ACTIVE"?"success":"default"}/>
            </TableCell>
            <TableCell>
              <AnimatedButton size="small">Edit</AnimatedButton>
              <AnimatedButton size="small" color="error">Delete</AnimatedButton>
            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  );
}
```


### 6.3 Collateral.jsx

```jsx
import { Grid, Card, CardContent, Typography } from "@mui/material";
import LoaderBars from "../components/common/LoaderBars";
import { collateralService } from "../services";

export default function Collateral() {
  const [items, setItems] = React.useState(null);
  React.useEffect(() => {
    collateralService.getEligible().then(r => setItems(r.data));
  }, []);
  if (!items) return <LoaderBars />;
  return (
    <Grid container spacing={2}>
      {items.map(c => (
        <Grid item md={4} key={c.id}>
          <Card variant="outlined">
            <CardContent>
              <Typography variant="h6">{c.collateralType}</Typography>
              <Typography color="text.secondary">{c.description}</Typography>
              <Typography>Eligible: {c.eligibleValue.toLocaleString()}</Typography>
            </CardContent>
          </Card>
        </Grid>
      ))}
    </Grid>
  );
}
```


## 7. Gráficos Interativos

src/components/charts/TreasuryChart.jsx

```jsx
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer } from "recharts";

export default function TreasuryChart({ data }) {
  return (
    <ResponsiveContainer width="100%" height={300}>
      <BarChart data={data}>
        <XAxis dataKey="currency"/>
        <YAxis/>
        <Tooltip/>
        <Bar dataKey="totalBalance" fill="#1976d2" animationDuration={800}/>
      </BarChart>
    </ResponsiveContainer>
  );
}
```


## 8. Testes Unitários e de Integração

- **Jest + React Testing Library**
- Exemplo: src/components/common/AnimatedButton.test.jsx

```jsx
import { render, fireEvent } from "@testing-library/react";
import AnimatedButton from "./AnimatedButton";

test("calls onClick", () => {
  const onClick = jest.fn();
  const { getByText } = render(<AnimatedButton onClick={onClick}>Click</AnimatedButton>);
  fireEvent.click(getByText("Click"));
  expect(onClick).toHaveBeenCalled();
});
```


## 9. Próximos Passos

- Adicionar autenticação via JWT no frontend.
- Melhorar responsividade e acessibilidade.
- Incorporar gráficos de linhas e pizzas para collateral.
- Pipeline CI/CD com testes e linting.

Com esta sequência, tem um **frontend completo**, moderno e animado, integrado ao backend Spring Boot, pronto para produção.

<div style="text-align: center">⁂</div>

[^1]: Projeto-Treasury-Collateral-Management-Treino.md

