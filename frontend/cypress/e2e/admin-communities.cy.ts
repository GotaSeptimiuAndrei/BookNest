describe("Admin communities grid", () => {
    beforeEach(() => {
        cy.login("admin@gmail.com", "Admin123!")
    })

    it("lists communities and navigates", () => {
        cy.fixture("communities.json").as("communities")
        cy.intercept("GET", "/api/communities", { fixture: "communities.json" })

        cy.visit("/")
        cy.contains("Manage Library") // sanity admin
        cy.get('[data-cy="admin-communities-link"]').click()

        cy.get("@communities").then((data: any) => {
            cy.get('[data-cy="community-card"]').should("have.length", data.results.length)
        })

        cy.get('[data-cy="community-card"]').first().click()
        cy.url().should("match", /\/communities\/\d+$/)
    })
})
