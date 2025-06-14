describe("Login flow", () => {
    it("logs in as a basic user", () => {
        cy.visit("/login")

        cy.findByLabelText(/email/i).type("dan@gmail.com")
        cy.findByLabelText(/password/i).type("password")
        cy.findByRole("button", { name: /log in/i }).click()

        cy.findByText("dan123").should("be.visible")
    })
})
