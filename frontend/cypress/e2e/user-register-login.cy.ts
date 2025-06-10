describe("User registration / verification / login", () => {
    it("registers, verifies email and logs in", () => {
        cy.visit("/register")

        cy.intercept("POST", "/api/auth/register").as("register")
        cy.get('[data-cy="username"]').type("john123")
        cy.get('[data-cy="email"]').type("john@Test.com")
        cy.get('[data-cy="password"]').type("Secret123!")
        cy.get('[data-cy="register-btn"]').click()

        cy.wait("@register").its("response.statusCode").should("eq", 201)

        // simulate backend verification link
        cy.visit("/verify-email?token=fake-code")
        cy.contains("Email verified").should("exist")

        cy.visit("/login")
        cy.get('[data-cy="email"]').type("john@Test.com")
        cy.get('[data-cy="password"]').type("Secret123!")
        cy.get('[data-cy="login-btn"]').click()

        cy.url().should("eq", Cypress.config("baseUrl") + "/")
        cy.get('[data-cy="notification-bell"] .badge').should("not.exist")
    })
})
