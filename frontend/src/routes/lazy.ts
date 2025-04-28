import { lazy } from "react"
export const lazyImport = <T extends React.ComponentType<any>>(factory: () => Promise<{ default: T }>) => lazy(factory)
