import React, { useContext } from "react";
import { useTheme, IconButton } from "@mui/material";
import LightbulbIcon from "@mui/icons-material/Lightbulb";
import { ColorModeContext } from "../../index";

export default function ThemeToggle() {
  const theme = useTheme();
  const mode = theme.palette.mode;
  const colorMode = useContext(ColorModeContext);
  
  return (
    <IconButton onClick={colorMode.toggle} title="Toggle theme" color="inherit">
      <LightbulbIcon sx={{
        transition: "transform 0.3s",
        transform: mode === "dark" ? "rotate(180deg)" : "none"
      }}/>
    </IconButton>
  );
}