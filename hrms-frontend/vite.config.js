import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import svgr from "vite-plugin-svgr";
import nodePolyfills from "rollup-plugin-node-polyfills";
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
  optimizeDeps: {
    esbuildOptions: {
      define: {
        global: "globalThis",
      },
    },

  },
  build: {
    sourcemap: true,
    rollupOptions: {
      plugins: [nodePolyfills()],
    },
  },
  logLevel: "info",
});
