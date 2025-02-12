module.exports = {
  root: true,  // ESLint should stop looking in parent directories for configuration
  env: {
    browser: true, // Enables browser globals like `window` and `document`
    es2021: true,  // Enables ES2021 features
  },
  extends: [
    'eslint:recommended',              // Basic ESLint recommended rules
    'plugin:react/recommended',        // React-specific linting rules
    'plugin:@typescript-eslint/recommended', // TypeScript-specific rules
    'plugin:react-hooks/recommended',  // React hooks linting rules
    'prettier',                        // Disables ESLint rules that conflict with Prettier
  ],
  ignorePatterns: ['dist', '.eslintrc.cjs', 'vite.config.ts', 'commitlint.config.cjs'],  // Don't lint build output or node_modules
  parser: '@typescript-eslint/parser', // Specifies the ESLint parser for TypeScript
  parserOptions: {
    ecmaFeatures: {
      jsx: true,  // Enables linting for JSX
    },
    ecmaVersion: 'latest',  // Allows modern ECMAScript syntax
    sourceType: 'module',   // Enables `import`/`export` syntax
  },
  plugins: [
    'react-refresh',                // Adds React Refresh support
    'react',                        // Adds React-specific linting rules
    '@typescript-eslint',           // TypeScript-specific linting rules
    'prettier',                     // Integrates Prettier with ESLint
  ],
  rules: {
    // Add or customize rules here
    'prettier/prettier': 'error',        // Enforces Prettier formatting as ESLint errors
    '@typescript-eslint/no-unused-vars': 'warn', // Warns for unused variables
    'react/react-in-jsx-scope': 'off',   // Turn off React import requirement for JSX (React 17+)
    'react/prop-types': 'off',           // Turns off prop-types validation (we're using TypeScript)
    'react-refresh/only-export-components': [ 'warn', { allowConstantExport: true } ], // Warns if non-component exports are used
  },
  settings: {
    react: {
      version: 'detect',  // Automatically detect the version of React to use
    },
  },
};