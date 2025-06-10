describe("Member joins a community and likes a post", () => {
    beforeEach(() => {
        cy.fixture("new-user").then((u) => cy.login(u.email, u.password))
    })

    it("joins Eliade Community and likes first post", () => {
        cy.intercept("POST", "/api/memberships").as("join")
        cy.visit("/authors/Mircea%20Eliade")
        cy.contains("Join Community").click()
        cy.wait("@join").its("response.statusCode").should("eq", 200)

        // navbar badge now shows 1 membership
        cy.get('[data-cy="my-communities-link"]').click()
        cy.contains("Eliade Community").click()

        // like first post
        cy.intercept("POST", "/api/posts/*/like").as("like")
        cy.get('[data-cy="post-card"]').first().as("firstPost")
        cy.get("@firstPost").find('[data-cy="like-btn"]').click()
        cy.wait("@like").its("response.statusCode").should("eq", 200)

        cy.get("@firstPost")
            .find("svg")
            .should("have.attr", "fill")
            .and("match", /#f44336/i) // red heart
    })
})
