import "@testing-library/cypress/add-commands"

/**
 * Logs in through the real API and saves the JWT in localStorage.
 */
Cypress.Commands.add("loginViaApi", (email: string, password: string) => {
    cy.request("POST", "http://localhost:8080/api/auth/login", {
        email,
        password,
    }).then(({ body }) => {
        // 👉 adjust the next three lines to match YOUR response
        console.log("LOGIN RESPONSE >>>", body)
        let token: string | undefined

        if (typeof body === "string")
            token = body // raw string
        else if (body.token)
            token = body.token // { token: "..." }
        else if (body.results?.token) token = body.results.token // { results:{token} }

        // Bail early with a helpful message
        if (!token) {
            throw new Error(`loginViaApi: could not find JWT in response body: ${JSON.stringify(body, null, 2)}`)
        }

        window.localStorage.setItem("token", token)

        // (Optional) store user info if you expose it
        const userObj = body.user ?? body.results?.user
        if (userObj) window.localStorage.setItem("user", JSON.stringify(userObj))
    })
})

Cypress.Commands.add("searchBookAndOpen", (title: string) => {
    cy.visit("/books")

    cy.findByPlaceholderText(/search/i).type(`${title}{enter}`)

    /* locate the <h6>Dune</h6> heading of the card */
    cy.contains("h6", new RegExp(`^${title}$`, "i"))
        .parentsUntil("body") // climb to the card root
        .filter("[data-discover], .MuiCard-root") // any unique card selector
        .first()
        .within(() => {
            // click the View Details link *inside this card only*
            cy.findByRole("link", { name: /view details/i }).click()
        })
})

declare global {
    // eslint-disable-next-line @typescript-eslint/no-namespace
    namespace Cypress {
        interface Chainable {
            /** Programmatic login that sets localStorage token */
            loginViaApi(email: string, password: string): Chainable<void>
            searchBookAndOpen(title: string): Chainable<void>
        }
    }
}
