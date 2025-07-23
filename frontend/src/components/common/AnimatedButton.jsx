import React from "react";
import { Button } from "@mui/material";
import { motion } from "framer-motion";

export default function AnimatedButton(props) {
  return (
    <motion.div 
      whileHover={{ scale: 1.05 }} 
      whileTap={{ scale: 0.95 }}
      style={{ display: "inline-block" }}
    >
      <Button {...props} />
    </motion.div>
  );
}