import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import svgr from "vite-plugin-svgr";
import rollupNodePolyFill from "rollup-plugin-node-polyfills";

export default defineConfig({
  plugins: [
    react(),
    svgr({
      svgrOptions: {
        icon: true,
        exportType: "named",
        namedExport: "ReactComponent",
      },
    }),
  ],
  define: {
    global: "globalThis",
  },
  build: {
    sourcemap: true,
    rollupOptions: {
      plugins: [
        rollupNodePolyFill(), // Thêm polyfill cho node built-ins
      ],
    },
  },
  optimizeDeps: {
    include: ["buffer", "process", "util"], // Tùy từng lỗi, có thể thêm "crypto-browserify" nếu cần
  },
  logLevel: "info",
});
