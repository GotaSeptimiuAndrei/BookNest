/// <reference types="cypress" />

/**
 * Programmatic login: sends POST /api/auth/login and stores the JWT in localStorage,
 * so the next page load is already authenticated.
 */
Cypress.Commands.add("login", (email: string, password: string) => {
    cy.request("POST", "http://localhost:8080/api/auth/login", {
        email,
        password,
    }).then((resp) => {
        // assume the backend sends { token: "..." }
        window.localStorage.setItem("token", resp.body.token)
    })
})

/* ------------------------------------------------------------------ */
/* TS declaration merge so TypeScript knows about the new command     */
declare global {
    namespace Cypress {
        interface Chainable {
            /**
             * Custom command to log in by calling backend API and
             * stashing the JWT in localStorage.
             * @example cy.login('user@mail.com','Pass123!')
             */
            login(email: string, password: string): Chainable<void>
        }
    }
}
export {} // 👈 keep TS file a module
