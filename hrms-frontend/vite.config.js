import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import svgr from "vite-plugin-svgr";

export default defineConfig({
  plugins: [
    react(),
    svgr({
      svgrOptions: {
        // svgr options (bạn có thể thêm nếu cần)
        icon: true,
        // This will transform your SVG to a React component
        exportType: "named",
        namedExport: "ReactComponent",
      },
    }),
  ],
  build: {
    sourcemap: true, // Bật source map giúp trace lỗi rõ ràng
  },
  // Bật Vite debug logs
  logLevel: "info", // Hoặc "debug" nếu muốn log chi tiết hơn
});
