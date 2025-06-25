import { defineConfig } from "vite"
import react from "@vitejs/plugin-react"
import tsconfigPaths from "vite-tsconfig-paths"

export default defineConfig({
    plugins: [react(), tsconfigPaths({ projects: ["tsconfig.app.json", "tsconfig.node.json"] })],
    server: {
        port: 3000,
    },
    define: {
        global: "window",
    },
    build: {
        commonjsOptions: {
            dynamicRequireTargets: [
                "node_modules/react-country-flag/dist/react-country-flag.js",
                "node_modules/country-flag-icons/react/3x2/index.js",
                "node_modules/i18n-iso-countries/index.js",
            ],
        },
    },
})
